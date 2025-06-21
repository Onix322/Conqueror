package loader.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlPrinter {

    public static void printTextContent(Document document) {
        printNode(document.getDocumentElement());
    }

    public static void printNode(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            String text = node.getTextContent().trim();
            if (!text.isEmpty()) {
                System.out.println(text);
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            printNode(children.item(i));
        }
    }
}