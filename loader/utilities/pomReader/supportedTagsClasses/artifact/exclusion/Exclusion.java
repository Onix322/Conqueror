package loader.utilities.pomReader.supportedTagsClasses.artifact.exclusion;

import loader.utilities.pomReader.supportedTagsClasses.artifact.Artifact;

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
                "groupId='" + super.groupId + '\'' +
                ", artifactId='" + super.artifactId + '\'' +
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
