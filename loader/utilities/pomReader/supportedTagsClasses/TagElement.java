package loader.utilities.pomReader.supportedTagsClasses;

public enum TagElement {
    NONE(""),
    PROJECT("project"),
    DEPENDENCIES("dependencies"),
    DEPENDENCY("dependency"),
    EXCLUSION("exclusion"),
    EXCLUSIONS("exclusions"),
    GROUP_ID("groupId"),
    ARTIFACT_ID("artifactId"),
    VERSION("version"),
    SCOPE("scope"),
    TYPE("type"),
    CLASSIFIER("classifier"),
    OPTIONAL("optional"),
    PACKAGING("packaging"),
    NAME("name"),
    MODEL_VERSION("modelVersion"),
    PARENT("parent"),
    DEPENDENCY_MANAGEMENT("dependencyManagement"),
    PROPERTIES("properties"),
    PREREQUISITES("prerequisites"),
    MAVEN("maven"),
    RELATIVE_PATH("relativePath"),
    METADATA("metadata"),
    PLUGIN("plugin"),
    VERSIONING("versioning"),
    LATEST("latest"),
    RELEASE("release"),
    VERSIONS("versions"),
    LAST_UPDATED("lastUpdated"),
    PROFILE("profile");


    private final String tagName;

    TagElement(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public static TagElement find(String tagName){
        for (TagElement te : TagElement.values()){
            if(te.getTagName().equals(tagName)){
                return te;
            }
        }
        return TagElement.NONE;
    }
}
