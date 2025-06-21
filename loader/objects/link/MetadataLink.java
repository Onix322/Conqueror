package loader.objects.link;

import loader.objects.Dependency;

import java.net.URI;

public class MetadataLink extends Link {
    public MetadataLink(Dependency dependency, URI link, LinkExtension linkExtension) {
        super(dependency, link, linkExtension);
    }
}
