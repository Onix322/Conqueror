package loader.utilities.linkGenerator.link;


import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;

import java.net.URI;
import java.util.Objects;

/**
 * Represents a versioned link associated with a {@link VersionedArtifact}, a URI, and a link extension.
 * Extends the {@link Link} class to include version-specific artifact details.
 */
public class VersionedLink extends Link {
    private VersionedArtifact artifact;

    public VersionedLink(VersionedArtifact versionedArtifact, URI uri, LinkExtension extension) {
        super(versionedArtifact, uri, extension);
        this.artifact = versionedArtifact;
    }

    @Override
    public VersionedArtifact getArtifact() {
        return artifact;
    }

    public VersionedLink setArtifact(VersionedArtifact artifact) {
        this.artifact = artifact;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        VersionedLink that = (VersionedLink) object;
        return Objects.equals(getArtifact(), that.getArtifact());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getArtifact());
    }

    @Override
    public String toString() {
        return "VersionedLink{" +
                "artifact=" + artifact +
                '}';
    }
}
