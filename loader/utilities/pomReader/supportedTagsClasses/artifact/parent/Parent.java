package loader.utilities.pomReader.supportedTagsClasses.artifact.parent;

import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import loader.utilities.version.Version;

import java.util.Objects;

public class Parent extends VersionedArtifact {
    private String relativePath;

    private Parent(Builder builder) {
        super(builder.groupId, builder.artifactId, builder.version);
        this.relativePath = builder.getRelativePath();
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Parent parent = (Parent) object;
        return Objects.equals(getRelativePath(), parent.getRelativePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRelativePath());
    }

    @Override
    public String toString() {
        return "Parent{" +
                "relativePath='" + relativePath + '\'' +
                ", groupId='" + super.getGroupId() + '\'' +
                ", artifactId='" + super.getArtifactId() + '\'' +
                '}';
    }

    public static class Builder {
        private String groupId;
        private String artifactId;
        private Version version;
        private String relativePath;

        private Builder() {
        }

        public String getGroupId() {
            return groupId;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Builder setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Version getVersion() {
            return version;
        }

        public Builder setVersion(Version version) {
            this.version = version;
            return this;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public Builder setRelativePath(String relativePath) {
            this.relativePath = relativePath;
            return this;
        }

        public Parent build() {
            return new Parent(this);
        }
    }
}
