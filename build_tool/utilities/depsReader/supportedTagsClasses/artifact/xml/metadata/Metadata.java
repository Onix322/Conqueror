package build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata;

import build_tool.utilities.depsReader.supportedTagsClasses.artifact.Artifact;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.XMLParsed;

import java.util.Objects;

/**
 * Represents the metadata of an artifact in a Maven POM file.
 * This class encapsulates the group ID, artifact ID, and versioning information.
 * It implements the XMLParsed interface to indicate that it can be parsed from XML.
 */
public class Metadata extends Artifact implements XMLParsed {
    private Versioning versioning;
    public Metadata(Builder builder) {
        super(builder.groupId, builder.artifactId);
        this.versioning = builder.versioning;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Metadata getAs() {
        return this;
    }

    public Versioning getVersioning() {
        return versioning;
    }

    public void setVersioning(Versioning versioning) {
        this.versioning = versioning;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Metadata metadata = (Metadata) object;
        return Objects.equals(getVersioning(), metadata.getVersioning());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVersioning());
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "versioning=" + versioning +
                ", groupId='" + super.getGroupId() + '\'' +
                ", artifactId='" + super.getArtifactId() + '\'' +
                '}';
    }

    public static class Builder{
        private String groupId;
        private String artifactId;
        private Versioning versioning;

        private Builder() {
        }

        public String getGroupId() {
            return groupId;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Versioning getVersioning() {
            return versioning;
        }

        public Builder setVersioning(Versioning versioning) {
            this.versioning = versioning;
            return this;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Builder setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Metadata build(){
            return new Metadata(this);
        }
    }
}
