package loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata;

import loader.utilities.version.FixedVersion;

import java.util.Objects;

/**
 * Represents the versioning information of a Maven artifact.
 * This class encapsulates the latest and release versions, as well as a collection of all versions.
 * It provides methods to access and modify these properties, along with a builder pattern for instantiation.
 */
public class Versioning {
    private FixedVersion latest;
    private FixedVersion release;
    private Versions versions;
    private long lastUpdated;

    public Versioning(Builder builder) {
        this.latest = builder.latest;
        this.release = builder.release;
        this.versions = builder.versions;
        this.lastUpdated = builder.lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public FixedVersion getLatest() {
        return latest;
    }

    public boolean add(FixedVersion version) {
        return this.versions.add(version);
    }

    public void setLatest(FixedVersion latest) {
        this.latest = latest;
    }

    public FixedVersion getRelease() {
        return release;
    }

    public void setRelease(FixedVersion release) {
        this.release = release;
    }

    public Versions getVersions() {
        return versions;
    }

    public void setVersions(Versions versions) {
        this.versions = versions;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Versioning that = (Versioning) object;
        return lastUpdated == that.lastUpdated && Objects.equals(getLatest(), that.getLatest()) && Objects.equals(getRelease(), that.getRelease()) && Objects.equals(getVersions(), that.getVersions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatest(), getRelease(), getVersions(), lastUpdated);
    }

    @Override
    public String toString() {
        return "Versioning{" +
                "latest=" + latest +
                ", release=" + release +
                ", versions=" + versions +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    public static class Builder {
        private FixedVersion latest;
        private FixedVersion release;
        private Versions versions;
        private long lastUpdated;

        private Builder() {
        }

        public long getLastUpdated() {
            return lastUpdated;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Versions getVersions() {
            return versions;
        }

        public Builder setVersions(Versions versions) {
            this.versions = versions;
            return this;
        }

        public FixedVersion getRelease() {
            return release;
        }

        public Builder setRelease(FixedVersion release) {
            this.release = release;
            return this;
        }

        public FixedVersion getLatest() {
            return latest;
        }

        public Builder setLatest(FixedVersion latest) {
            this.latest = latest;
            return this;
        }

        public Versioning build(){
            return new Versioning(this);
        }
    }
}
