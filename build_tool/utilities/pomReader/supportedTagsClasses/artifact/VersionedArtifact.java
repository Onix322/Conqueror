package build_tool.utilities.pomReader.supportedTagsClasses.artifact;

import build_tool.utilities.version.Version;

import java.util.Objects;

/**
 * Represents a versioned artifact in a Maven POM file.
 * This class extends the Artifact class to include a version field,
 * providing a way to encapsulate both the artifact's identity and its version.
 */
public class VersionedArtifact extends Artifact {
    private Version version;

    public VersionedArtifact(String groupId, String artifactId, Version version) {
        super(groupId, artifactId);
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VersionedArtifact{" +
                "version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        VersionedArtifact that = (VersionedArtifact) object;
        return Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVersion());
    }

}
