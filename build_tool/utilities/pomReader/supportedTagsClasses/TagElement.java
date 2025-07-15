package build_tool.utilities.pomReader.supportedTagsClasses;

/*
 * This enum represents the various XML tags that can be found in a Maven POM file.
 * Each tag corresponds to a specific element in the POM structure, such as project,
 * dependencies, dependency, etc. The enum provides a method to find a tag by its name.
 */
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

    /**
     * Finds a TagElement by its tag name.
     *
     * @param tagName the name of the tag to find
     * @return the TagElement corresponding to the tag name, or NONE if not found
     */
    public static TagElement find(String tagName){
        for (TagElement te : TagElement.values()){
            if(te.getTagName().equals(tagName)){
                return te;
            }
        }
        return TagElement.NONE;
    }
}
