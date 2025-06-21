package loader.objects.link;

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
