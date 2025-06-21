package loader.objects;

public class Exclusion {
    private final String groupId;
    private final String artifactId;

    public Exclusion(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String toString() {
        return "Exclusion{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                '}';
    }
}