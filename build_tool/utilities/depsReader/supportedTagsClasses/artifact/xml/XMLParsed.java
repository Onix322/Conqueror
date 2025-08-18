package build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml;

/*
 * Interface representing a parsed XML structure.
 * This interface defines a method to retrieve the parsed object
 * as a specific type, allowing for type-safe access to the parsed data.
 */
public interface XMLParsed {
    <R extends XMLParsed> R getAs();
}
