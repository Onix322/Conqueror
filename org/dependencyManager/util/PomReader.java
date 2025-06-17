package org.dependencyManager.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PomReader {

    private final DocumentBuilder documentBuilder;
    private final Document document;

    public PomReader(File file, DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
        this.document = this.read(file);
    }

    public Document read(File file) {
        try {
            return this.documentBuilder.parse(file);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NodeList getTagName(String tagName) {
        return this.document.getElementsByTagName(tagName);
    }

    public List<Node> transform(NodeList nodeList){
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }

        return nodes;
    }
}
