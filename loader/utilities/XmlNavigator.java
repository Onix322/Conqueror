package loader.utilities;

import loader.objects.NodeAttributes;
import org.w3c.dom.Document;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlNavigator {

    private final PomReader pomReader;

    // Constructor privat – Singleton clasic
    private XmlNavigator(PomReader pomReader) {
        this.pomReader = pomReader;
    }

    private static class Holder{
        private static XmlNavigator INSTANCE = null;
    }

    public static void init(PomReader pomReader) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new XmlNavigator(pomReader);
        } else {
            throw new IllegalStateException("XmlNavigator has already been initialized.");
        }
    }

    public static XmlNavigator getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("XmlNavigator not initialized. Call init() first.");
        }
        return Holder.INSTANCE;
    }

    public NodeAttributes getFirst(Document doc, String path) {
        return getNodeAtIndex(doc, path, false).firstNode;
    }

    public List<NodeAttributes> getAll(Document doc, String path) {
        return getNodeAtIndex(doc, path, true).lastExtracted;
    }

    public NodeAttributes getFirst(String xmlUrl, String path) {
        Document doc = pomReader.readString(xmlUrl);
        return getFirst(doc, path);
    }

    public List<NodeAttributes> getAll(String xmlUrl, String path) {
        Document doc = pomReader.readString(xmlUrl);
        return getAll(doc, path);
    }

    private ExtractedNodes getNodeAtIndex(Document doc, String path, boolean returnAllAtEnd) {
        String[] parts = path.split("\\.");
        NodeAttributes current = null;
        List<NodeAttributes> extracted = null;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            Matcher matcher = Pattern.compile("([a-zA-Z_-]+)(\\d+)?").matcher(part);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid tag format: " + part);
            }

            String tagName = matcher.group(1);
            int index = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) - 1 : 0;

            if (i == 0) {
                extracted = pomReader.extract(doc, tagName);
            } else {
                extracted = pomReader.extract(current.getNode(), tagName);
            }

            if (extracted.isEmpty() || index >= extracted.size()) {
                throw new IndexOutOfBoundsException("Tag <" + tagName + "> index " + (index + 1) + " not found.");
            }

            current = extracted.get(index);
        }

        return new ExtractedNodes(current, extracted);
    }

    private record ExtractedNodes(NodeAttributes firstNode, List<NodeAttributes> lastExtracted) {
    }
}