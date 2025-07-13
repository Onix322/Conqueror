package loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project;

import loader.utilities.pomReader.supportedTagsClasses.artifact.Artifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.parent.Parent;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.version.Version;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a Maven project with its associated metadata.
 * This class extends Artifact to include additional fields
 * such as version, modelVersion, name, packaging, dependencies,
 * parent, dependencyManagement, and properties.
 * It provides a builder pattern for easy instantiation and modification.
 */
public class Project extends Artifact implements XMLParsed {

    private Version version;
    private Version modelVersion;
    private String name;
    private String packaging;
    private Dependencies dependencies;
    private Parent parent;
    private DependencyManagement dependencyManagement;
    private Map<String, String> proprieties;

    private Project(Builder builder) {
        super(builder.getGroupId(), builder.getArtifactId());
        this.version = builder.getVersion();
        this.modelVersion = builder.getModelVersion();
        this.name = builder.getName();
        this.packaging = builder.getPackaging();
        this.dependencies = builder.getDependencies();
        this.parent = builder.getParent();
        this.dependencyManagement = builder.getDependencyManagement();
        this.proprieties = builder.getProprieties();
    }

    public Map<String, String> getProprieties() {
        return proprieties;
    }

    public void setProprieties(Map<String, String> proprieties) {
        this.proprieties = proprieties;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public void setDependencies(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Version getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(Version modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public DependencyManagement getDependencyManagement() {
        return dependencyManagement;
    }

    public void setDependencyManagement(DependencyManagement dependencyManagement) {
        this.dependencyManagement = dependencyManagement;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Project project = (Project) object;
        return Objects.equals(getGroupId(), project.getGroupId()) && Objects.equals(getArtifactId(), project.getArtifactId()) && Objects.equals(getVersion(), project.getVersion()) && Objects.equals(getModelVersion(), project.getModelVersion()) && Objects.equals(getName(), project.getName()) && Objects.equals(getPackaging(), project.getPackaging()) && Objects.equals(getDependencies(), project.getDependencies()) && Objects.equals(getParent(), project.getParent()) && Objects.equals(getDependencyManagement(), project.getDependencyManagement()) && Objects.equals(getProprieties(), project.getProprieties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion(), getModelVersion(), getName(), getPackaging(), getDependencies(), getParent(), getDependencyManagement(), getProprieties());
    }

    @Override
    public String toString() {
        return "Project{" +
                "groupId='" + super.getGroupId() + '\'' +
                ", artifactId='" + super.getArtifactId() + '\'' +
                ", version='" + version + '\'' +
                ", modelVersion='" + modelVersion + '\'' +
                ", name='" + name + '\'' +
                ", packaging='" + packaging + '\'' +
                ", dependencies=" + dependencies +
                ", parent=" + parent +
                ", dependencyManagement=" + dependencyManagement +
                ", proprieties=" + proprieties +
                '}';
    }

    @Override
    public Project getAs() {
        return this;
    }

    public static class Builder {
        private String groupId;
        private String artifactId;
        private Version version;
        private Version modelVersion;
        private String name;
        private String packaging;
        private Dependencies dependencies;
        private Parent parent;
        private DependencyManagement dependencyManagement;
        private Map<String, String> proprieties;

        private Builder() {
        }

        public Map<String, String> getProprieties() {
            return proprieties;
        }

        public Builder setProprieties(Map<String, String> proprieties) {
            this.proprieties = proprieties;
            return this;
        }

        public Dependencies getDependencies() {
            return dependencies;
        }

        public Builder setDependencies(Dependencies dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public String getPackaging() {
            return packaging;
        }

        public Builder setPackaging(String packaging) {
            this.packaging = packaging;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Version getModelVersion() {
            return modelVersion;
        }

        public Builder setModelVersion(Version modelVersion) {
            this.modelVersion = modelVersion;
            return this;
        }

        public Version getVersion() {
            return version;
        }

        public Builder setVersion(Version version) {
            this.version = version;
            return this;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Builder setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public String getGroupId() {
            return groupId;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Parent getParent() {
            return parent;
        }

        public Builder setParent(Parent parent) {
            this.parent = parent;
            return this;
        }

        public DependencyManagement getDependencyManagement() {
            return dependencyManagement;
        }

        public Builder setDependencyManagement(DependencyManagement dependencyManagement) {
            this.dependencyManagement = dependencyManagement;
            return this;
        }

        public Project build() {
            return new Project(this);
        }
    }
}
