package loader.utilities.linkGenerator.link;

/**
 * Enum representing the possible file extensions for links.
 * Used to specify the type of artifact associated with a link,
 * such as JAR, POM, or XML files.
 */
public enum LinkExtension {
    JAR("jar"),
    POM("pom"),
    XML("xml");

    private final String ex;

    LinkExtension(String ex) {
        this.ex = ex;
    }

    public String getValue() {
        return ex;
    }
}
