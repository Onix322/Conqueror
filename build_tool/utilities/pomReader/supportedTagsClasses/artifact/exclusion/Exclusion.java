package build_tool.utilities.pomReader.supportedTagsClasses.artifact.exclusion;

import build_tool.utilities.pomReader.supportedTagsClasses.artifact.Artifact;

/**
 * Represents an exclusion in a Maven POM file.
 * This class extends Artifact to include groupId and artifactId
 * for the excluded artifact.
 * It provides a builder pattern for easy instantiation and modification.
 */
public class Exclusion extends Artifact {

    private Exclusion(Builder builder) {
        super(builder.getGroupId(), builder.getArtifactId());
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String toString() {
        return "Exclusion{" +
                "groupId='" + super.getGroupId() + '\'' +
                ", artifactId='" + super.getArtifactId() + '\'' +
                '}';
    }

    public static class Builder {
        private String groupId;
        private String artifactId;

        public Builder() {}

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

        public Exclusion build(){
            return new Exclusion(this);
        }
    }
}
