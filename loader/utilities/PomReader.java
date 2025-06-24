package loader.utilities;

import loader.objects.NodeAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PomReader {

    private final DocumentBuilder documentBuilder;

    private PomReader(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    private static class Holder {
        private static PomReader INSTANCE = null;
    }

    public static synchronized void init(DocumentBuilder documentBuilder) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new PomReader(documentBuilder);
        }
    }

    public static PomReader getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("PomReader is not initialized. Use PomReader.init().");
        }
        return Holder.INSTANCE;
    }

    public Document readFile(File file) {
        try {
            this.documentBuilder.reset();
            return this.documentBuilder.parse(file);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document readStream(InputStream stream) {
        try {
            this.documentBuilder.reset();
            return this.documentBuilder.parse(stream);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document readString(String string) {
        try {
            this.documentBuilder.reset();
            return this.documentBuilder.parse(string);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        this.documentBuilder.reset();
    }

    public List<NodeAttributes> extractDependencies(Document document) {
        NodeList dependenciesList = document.getElementsByTagName("dependencies");

        List<Node> dpsNodeList = this.toList(dependenciesList);
        Optional<Node> dependencies = dpsNodeList.stream()
                .filter(n -> n.getParentNode().getNodeName().equals("project"))
                .findFirst();

        if (dependencies.isEmpty()) {
            return List.of();
        }
        if (dependenciesList.getLength() == 0) {
            return List.of();
        }

        return this.extract(dependencies.get(), "dependency");
    }

    public List<NodeAttributes> extractExclusions(NodeAttributes dependency) {

        NodeList exNodeList = dependency.getNode().getChildNodes();
        List<Node> exList = this.toList(exNodeList);
        List<NodeAttributes> allExclusions = new ArrayList<>();
        for (Node n : exList) {
            if (!n.getNodeName().equals("exclusions")) continue;
            List<NodeAttributes> nodeAttributes = this.extract(n, "exclusion");
            allExclusions.addAll(nodeAttributes);
        }

        return allExclusions;
    }

    public Map<NodeAttributes, List<NodeAttributes>> extractFullDependencies(Document document) {
        Map<NodeAttributes, List<NodeAttributes>> fullDependencies = new HashMap<>();
        List<NodeAttributes> dependencies = this.extractDependencies(document);

        for (NodeAttributes d : dependencies) {
            List<NodeAttributes> exclusions = this.extractExclusions(d);
            fullDependencies.put(d, exclusions);
        }

        return fullDependencies;
    }

    public Map<String, String> extractAttributes(Node node) {
        Map<String, String> attr = new HashMap<>();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node a = nodeList.item(i);
            String key = a.getNodeName();
            String value = this.resolveVariable(a.getTextContent(), node.getOwnerDocument());
            attr.put(key, value);
        }

        return attr;
    }

    public List<NodeAttributes> extract(Node node, String nameTag) {
        List<NodeAttributes> result = new ArrayList<>();
        NodeList nodeListDep = node.getChildNodes();
        for (int i = 0; i < nodeListDep.getLength(); i++) {
            Node childNode = nodeListDep.item(i);
            if (childNode.getNodeName().equals(nameTag)) {
                Map<String, String> attributes = this.extractAttributes(childNode);
                NodeAttributes nodeAttributes = new NodeAttributes(childNode, attributes);
                result.add(nodeAttributes);
            }
        }
        return result;
    }

    public List<Node> toList(NodeList nodeList) {
        List<Node> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            list.add(node);
        }
        return list;
    }

    public Map<String, String> toMap(List<Node> list) {
        Map<String, String> map = new HashMap<>();

        for (Node n : list) {
            Map.Entry<String, String> entry = this.toEntry(n);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public Map<Node, String> toMapNode(List<Node> list) {
        Map<Node, String> map = new HashMap<>();

        for (Node n : list) {
            Map.Entry<Node, String> entry = this.toEntryNode(n);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public Map.Entry<String, String> toEntry(Node node) {
        return Map.entry(node.getNodeName(), node.getTextContent());
    }

    public Map.Entry<Node, String> toEntryNode(Node node) {
        return Map.entry(node, node.getTextContent());
    }

    public Map<String, String> extractTagsAsMap(String tagName, Document document) {
        NodeList nodeList = document.getElementsByTagName(tagName);
        List<Node> list = this.toList(nodeList);
        return this.toMap(list);
    }

    public List<Map.Entry<String, String>> extractTagsAsList(String tagName, Node node) {
        List<Map.Entry<String, String>> listEntries = new LinkedList<>();
        NodeList nodeList = node.getChildNodes();
        List<Node> list = this.toList(nodeList);
        for(Node n : list){
            if(n.getNodeName().equals(tagName)){
                listEntries.add(this.toEntry(n));
            }
        }
        return listEntries;
    }

    public List<Map.Entry<Node, String>> extractTagsAsListNode(String tagName, Node node) {
        List<Map.Entry<Node, String>> listEntries = new LinkedList<>();
        NodeList nodeList = node.getChildNodes();
        List<Node> list = this.toList(nodeList);
        for(Node n : list){
            if(n.getNodeName().equals(tagName)){
                listEntries.add(this.toEntryNode(n));
            }
        }
        return listEntries;
    }

    public String resolveVariable(String var, Document document) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(var);
        if (!matcher.matches()) return var;

        return this.find(var.replaceAll("[${}]", ""), document);
    }

    public String find(String var, Document document) {

        String[] parts = var.split("\\.");
        List<String> partsList = Arrays.stream(parts)
                .collect(Collectors.toCollection(ArrayList::new));

        ListIterator<String> partsToFind = partsList.listIterator(0);

        String endpoint = parts[parts.length - 1];

        Node currentNode = document;
        Node endpointNode = null;

        while (partsToFind.hasNext()) {

            String p = partsToFind.next();
            var parentNode = this.extract(currentNode, p);
            if (!parentNode.isEmpty()) {
                currentNode = parentNode.getFirst().getNode();
            }

            var extractedEndpoint = this.extract(currentNode, endpoint);
            if (!extractedEndpoint.isEmpty()) {
                endpointNode = extractedEndpoint.getFirst().getNode();
                break;
            }
            partsList.add("parent");
            partsList.remove(p);
            partsToFind = partsList.listIterator(partsToFind.nextIndex());
        }

        if (endpointNode == null) {
            return null;
        }
        return endpointNode.getTextContent();
    }
}
