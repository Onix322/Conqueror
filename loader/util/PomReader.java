package loader.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PomReader {

    private final DocumentBuilder documentBuilder;

    private PomReader(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    private static class Holder{
        private static PomReader INSTANCE = null;
    }

    public static synchronized void init(DocumentBuilder documentBuilder){
        if(Holder.INSTANCE == null){
            Holder.INSTANCE = new PomReader(documentBuilder);
        }
    }

    public static PomReader getInstance(){
        if(Holder.INSTANCE == null){
            throw new IllegalStateException("PomReader is not initialized. Use PomReader.init().");
        }
        return Holder.INSTANCE;
    }

    public Document readFile(File file) {
        try {
            return this.documentBuilder.parse(file);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document readStream(InputStream stream) {
        try {
            return this.documentBuilder.parse(stream);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document readString(String string) {
        try {
            return this.documentBuilder.parse(string);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dependency> extractDependencies(Document document) {
        Node dependenciesNode = document.getElementsByTagName("dependencies").item(0);
        return this.extract(dependenciesNode, "dependency", this::buildDependency);
    }

    public Map<String, String> extractAttributes(Node node){
        Map<String, String> attr = new HashMap<>();
        NamedNodeMap rawAttrs = node.getAttributes();
        for (int i = 0; i < rawAttrs.getLength(); i++) {
            Node a = rawAttrs.item(i);
            attr.put(a.getNodeName(), a.getNodeValue());
        }

        return attr;
    }

    public <T> List<T> extract(Node node, String nameTag, Function<NodeAttributes, T> buildFunction){
        List<T> result = new ArrayList<>();
        NodeList nodeListDep = node.getChildNodes();
        for (int i = 0; i < nodeListDep.getLength(); i++) {
            Node childNode = nodeListDep.item(i);
            if(childNode.getNodeName().equals(nameTag)){
                Map<String, String> attributes = this.extractAttributes(childNode);
                NodeAttributes nodeAttributes = new NodeAttributes(childNode, attributes);
                result.add(buildFunction.apply(nodeAttributes));
            }
        }
        return result;
    }

    public Dependency buildDependency(NodeAttributes nodeAttributes){
        Node node = nodeAttributes.getNode();
        Map<String, String> attributes = nodeAttributes.getAttributes();

        List<Exclusion> exclusions = this.extract(node, "exclusion", this::buildExclusion);

        if (attributes.get("groupId") == null || attributes.get("artifactId") == null) {
            throw new IllegalArgumentException("Dependency must have groupId and artifactId (mandatory)");
        }
        return Dependency.builder()
                .groupId(attributes.get("groupId"))
                .artifactId(attributes.get("artifactId"))
                .version(attributes.getOrDefault("version", null))
                .type(attributes.getOrDefault("type", null))
                .classifier(attributes.getOrDefault("classifier", null))
                .scope(attributes.getOrDefault("scope", null))
                .optional(Boolean.valueOf(attributes.getOrDefault("optional", null)))
                .exclusions(exclusions)
                .build();
    }

    public Exclusion buildExclusion(NodeAttributes nodeAttributes){
        Map<String, String> attributes = nodeAttributes.getAttributes();
        if (attributes.get("groupId") == null || attributes.get("artifactId") == null) {
            throw new IllegalArgumentException("Dependency must have groupId and artifactId (mandatory)");
        }
        return new Exclusion(
                attributes.get("groupId"),
                attributes.get("artifactId")
        );
    }

    public List<Node> toList(NodeList nodeList){
        List<Node> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            list.add(node);
        }
        return list;
    }

    public Map<String, String> toMap(List<Node> list){
        Map<String, String> map = new HashMap<>();

        for (Node n : list){
            Map.Entry<String, String> entry = this.toEntry(n);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public Map.Entry<String, String> toEntry(Node node){
        return Map.entry(node.getNodeName(), node.getTextContent());
    }

    public Map<String, String> extractTags(String tagName, Document document){
        NodeList nodeList = document.getElementsByTagName(tagName);
        List<Node> list = this.toList(nodeList);
        return this.toMap(list);
    }
}
