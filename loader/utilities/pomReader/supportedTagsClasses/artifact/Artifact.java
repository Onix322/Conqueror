package loader.utilities.pomReader.supportedTagsClasses.artifact;

import java.util.Objects;

/**
 * Represents a generic artifact with a group ID and an artifact ID.
 * This class serves as a base class for more specific artifact types.
 * It provides methods to access and modify the group ID and artifact ID,
 * as well as methods for equality checks, hash code generation, and string representation.
 */
public abstract class Artifact {
    private String groupId;
    private String artifactId;

    public Artifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Artifact artifact = (Artifact) object;
        return Objects.equals(groupId, artifact.groupId) && Objects.equals(artifactId, artifact.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                '}';
    }
}
