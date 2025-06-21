package loader.objects;

import org.w3c.dom.Node;

import java.util.Map;
import java.util.Objects;

public class NodeAttributes {
    private Node node;
    private Map<String, String> attributes;

    public NodeAttributes(Node node, Map<String, String> attributes) {
        this.node = node;
        this.attributes = attributes;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "NodeAttributes{" +
                "node=" + node +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        NodeAttributes that = (NodeAttributes) object;
        return Objects.equals(getNode(), that.getNode()) && Objects.equals(getAttributes(), that.getAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode(), getAttributes());
    }
}
