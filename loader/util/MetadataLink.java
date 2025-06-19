package loader.util;

import java.net.URI;
import java.util.Objects;

public class MetadataLink {
    private Dependency dependency;
    private URI link;

    public MetadataLink(Dependency dependency, URI link) {
        this.dependency = dependency;
        this.link = link;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "MetadataLink{" +
                "dependency=" + dependency +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        MetadataLink that = (MetadataLink) object;
        return Objects.equals(getDependency(), that.getDependency()) && Objects.equals(getLink(), that.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDependency(), getLink());
    }
}
