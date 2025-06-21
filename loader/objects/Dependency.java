package loader.objects;

import java.util.ArrayList;
import java.util.List;

public class Dependency {

    private final String groupId;
    private final String artifactId;
    private String version;
    private final String type;
    private final String classifier;
    private final String scope;
    private final Boolean optional;
    private final List<Exclusion> exclusions;

    private Dependency(Builder builder) {
        this.groupId = builder.groupId;
        this.artifactId = builder.artifactId;
        this.version = builder.version;
        this.type = builder.type;
        this.classifier = builder.classifier;
        this.scope = builder.scope;
        this.optional = builder.optional;
        this.exclusions = builder.exclusions;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
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

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", classifier='" + classifier + '\'' +
                ", scope='" + scope + '\'' +
                ", optional=" + optional +
                ", exclusions=" + exclusions +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String groupId;
        private String artifactId;
        private String version;
        private String type;
        private String classifier;
        private String scope;
        private Boolean optional;
        private List<Exclusion> exclusions = new ArrayList<>();

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Builder version(String version) {
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

        public Dependency build() {
            return new Dependency(this);
        }
    }
}