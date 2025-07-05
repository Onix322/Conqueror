package loader.utilities.pomReader.supportedTagsClasses.artifact.dependency;


import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.exclusion.Exclusion;
import loader.utilities.version.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dependency extends VersionedArtifact {

    private Version version;
    private String type;
    private String classifier;
    private String scope;
    private Boolean optional;
    private List<Exclusion> exclusions;

    public Dependency(Builder builder) {
        super(builder.groupId, builder.artifactId, builder.version);
        this.version = builder.version;
        this.type = builder.type;
        this.classifier = builder.classifier;
        this.scope = builder.scope;
        this.optional = builder.optional;
        this.exclusions = builder.exclusions;
    }

    // Getters

    public Version getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getScope() {
        return scope;
    }

    public Boolean getOptional() {
        return optional;
    }

    public List<Exclusion> getExclusions() {
        return exclusions;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public void setExclusions(List<Exclusion> exclusions) {
        this.exclusions = exclusions;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + super.getGroupId() + '\'' +
                ", artifactId='" + super.getArtifactId() + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", classifier='" + classifier + '\'' +
                ", scope='" + scope + '\'' +
                ", optional=" + optional +
                ", exclusions=" + exclusions +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Dependency that = (Dependency) object;
        return Objects.equals(getVersion(), that.getVersion()) && Objects.equals(getType(), that.getType()) && Objects.equals(getClassifier(), that.getClassifier()) && Objects.equals(getScope(), that.getScope()) && Objects.equals(getOptional(), that.getOptional()) && Objects.equals(getExclusions(), that.getExclusions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVersion(), getType(), getClassifier(), getScope(), getOptional(), getExclusions());
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String groupId;
        private String artifactId;
        private Version version;
        private String type = "jar";
        private String classifier = null;
        private String scope = "compile";
        private Boolean optional = false;
        private List<Exclusion> exclusions = new ArrayList<>();

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder classifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder optional(Boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder exclusions(List<Exclusion> exclusions) {
            this.exclusions = exclusions;
            return this;
        }

        public Builder addExclusion(Exclusion exclusion) {
            this.exclusions.add(exclusion);
            return this;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Version getVersion() {
            return version;
        }

        public String getType() {
            return type;
        }

        public String getClassifier() {
            return classifier;
        }

        public String getScope() {
            return scope;
        }

        public Boolean getOptional() {
            return optional;
        }

        public List<Exclusion> getExclusions() {
            return exclusions;
        }

        public Dependency build() {
            return new Dependency(this);
        }
    }
}
