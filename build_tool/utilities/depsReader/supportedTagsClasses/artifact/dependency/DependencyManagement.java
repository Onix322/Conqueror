package build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency;

import java.util.Objects;

/*
 * Represents the dependency management section of a Maven POM file.
 * This class encapsulates a collection of dependencies and provides
 * methods to access and modify them.
 */
public class DependencyManagement {
    private Dependencies dependencies;

    public DependencyManagement(Builder builder) {
        this.dependencies = builder.getDependencies();
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public void setDependencies(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        DependencyManagement that = (DependencyManagement) object;
        return Objects.equals(getDependencies(), that.getDependencies());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDependencies());
    }

    @Override
    public String toString() {
        return "DependencyManagement{" +
                "dependencies=" + dependencies +
                '}';
    }

    public static class Builder {
        private Dependencies dependencies;

        private Builder() {
            this.dependencies = new Dependencies();
        }

        public Dependencies getDependencies() {
            return dependencies;
        }

        public Builder setDependencies(Dependencies dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public DependencyManagement build() {
            return new DependencyManagement(this);
        }
    }
}
