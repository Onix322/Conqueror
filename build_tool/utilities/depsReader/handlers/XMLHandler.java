package build_tool.utilities.depsReader.handlers;

import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.XMLParsed;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata.Metadata;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata.Versioning;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata.Versions;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.project.Project;
import build_tool.utilities.depsReader.supportedTagsClasses.TagElement;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.Dependencies;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.Dependency;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.exclusion.Exclusion;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.parent.Parent;
import build_tool.utilities.version.FixedVersion;
import build_tool.utilities.version.Version;
import build_tool.utilities.version.versionHandler.VersionParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/** XMLHandler is a SAX handler for parsing XML files related to Maven artifacts.
    * It processes XML elements to build a structured representation of the artifact's metadata and project information.
    * The handler supports both <project> and <metadata> XML types, extracting relevant data such as groupId, artifactId, version,
    * dependencies, exclusions, parent information, and properties.

    * This class is designed as a singleton and should be initialized with a VersionParser instance.
* */
public class XMLHandler extends DefaultHandler {

// Final parsed XML product
// This variable holds the final parsed representation of the XML structure, either a <project> or <metadata> type.
private XMLParsed xmlParsed;

// XMLType <project>
// Variables used to build and store information related to Maven <project> XML elements.
private Project.Builder projectBuilder;
private Dependencies dependencies;
private Dependency.Builder dependency;
private Exclusion.Builder exclusion;
private Parent.Builder parent;
private DependencyManagement.Builder dependencyManagement;
private Map<String, String> properties;

// XMLType <metadata>
// Variables used to build and store information related to Maven <metadata> XML elements.
private Metadata.Builder metadataBuilder;
private Versioning.Builder versioning;
private Versions versions;

// Extracted characters (value)
// A buffer to store the character data extracted from XML elements.
private StringBuilder elementValue = new StringBuilder();

// Turn-dictating variables
// to track the current XML tag, its parent context, and the overall XML type being processed.
private TagElement current = TagElement.NONE; // Keeps track of the current start tag.
private TagElement context = TagElement.NONE; // Tracks the parent tag for nested elements.
private TagElement xmlType = TagElement.NONE; // Identifies the type of XML being processed (e.g., <project>, <metadata>).

// Injections
// External dependencies injected into the handler, such as the VersionParser for handling version-related logic.
private final VersionParser versionParser;
    public XMLHandler(VersionParser versionParser) {
        super();
        this.versionParser = versionParser;
    }

    /** Handles character data within XML elements.
     * This method accumulates character data into the elementValue StringBuilder,
     * which is later processed in the endElement method to extract the value of the XML tag.
     *
     * @param ch The characters from the XML element.
     * @param start The start position in the character array.
     * @param length The number of characters to process.
    * */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        }
        elementValue.append(ch, start, length);
    }

    /** Handles the start of the XML document.
     * This method is called at the beginning of the XML parsing process.
     * It initializes any necessary structures or variables before processing the XML elements.
     *
     * @throws SAXException if an error occurs during parsing. */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /**
     * Handles the end of the XML document.
     * This method is called at the end of the XML parsing process.
     * It finalizes the parsing and builds the final XMLParsed object based on the XML type.
     *
     * @throws SAXException if an error occurs during parsing.
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        switch (this.xmlType){
            case PROJECT -> xmlParsed = projectBuilder.build();
            case METADATA -> xmlParsed = metadataBuilder.build();
        }
    }

    /**
     * Handles the start of an XML element by identifying the tag and initializing the appropriate builder.
     * It sets the current context and prepares to collect data for the element.
     *
     * @param uri The namespace URI, or the empty string if the element has no namespace.
     * @param localName The local name (without prefix), or the empty string if not available.
     * @param qName The qualified name (with prefix), or the empty string if not available.
     * @param attributes The attributes of the element, or null if there are none.
    * */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        TagElement tagElement = TagElement.find(qName);
        switch (tagElement) {
            case METADATA:
                this.metadataBuilder = Metadata.builder();
                this.xmlType = TagElement.METADATA;
                this.current = TagElement.METADATA;
                break;
            case VERSIONING:
                this.versioning = Versioning.builder();
                this.current = TagElement.VERSIONING;
                break;
            case VERSIONS:
                this.versions = new Versions();
                this.versioning.setVersions(versions);
                this.current = TagElement.VERSIONS;
                break;
            case PROJECT:
                this.projectBuilder = Project.builder();
                this.xmlType = TagElement.PROJECT;
                this.context = TagElement.PROJECT;
                this.current = TagElement.PROJECT;
                break;
            case PARENT:
                this.parent = Parent.builder();
                this.current = TagElement.PARENT;
                break;
            case PROFILE:
                this.context = TagElement.PROFILE;
                this.current = TagElement.PROFILE;
                break;
            case DEPENDENCY_MANAGEMENT:
                dependencyManagement = DependencyManagement.builder();
                this.context = TagElement.DEPENDENCY_MANAGEMENT;
                this.current = TagElement.DEPENDENCY_MANAGEMENT;
                break;
            case DEPENDENCIES:
                this.dependencies = new Dependencies();
                this.current = TagElement.DEPENDENCIES;
                break;
            case EXCLUSION:
                this.exclusion = Exclusion.builder();
                this.current = TagElement.EXCLUSION;
                break;
            case DEPENDENCY:
                this.dependency = Dependency.builder();
                this.current = TagElement.DEPENDENCY;
                break;
            case PLUGIN:
                this.context = TagElement.PLUGIN;
                this.current = TagElement.PLUGIN;
                break;
            case PROPERTIES:
                if(properties == null){
                    properties = new HashMap<>();
                }
                this.current = TagElement.PROPERTIES;
                break;
            case PREREQUISITES:
                if(properties == null){
                    properties = new HashMap<>();
                }
                this.current = TagElement.PREREQUISITES;
                break;
        }
    }

    /**
     * Handles the end of an XML element by processing the collected data and updating the appropriate builder.
     * It finalizes the current context and updates the metadata or project information as needed.
     *
     * @param uri The namespace URI, or the empty string if the element has no namespace.
     * @param localName The local name (without prefix), or the empty string if not available.
     * @param qName The qualified name (with prefix), or the empty string if not available.
    * */
    @Override
    public void endElement(String uri, String localName, String qName) {
        TagElement tagElement = TagElement.find(qName);
        switch (tagElement) {
            case METADATA:
                metadataBuilder.setVersioning(versioning.build());
                break;
            case LAST_UPDATED:
                if(current == TagElement.VERSIONING){
                    versioning.setLastUpdated(System.currentTimeMillis());
                }
                break;
            case LATEST:
                if(current == TagElement.VERSIONING){
                    Version version = this.versionParser.parse(elementValue.toString().trim());
                    if(version instanceof FixedVersion fixedVersion){
                        versioning.setLatest(fixedVersion);
                    }
                }
            case RELEASE:
                if(current == TagElement.VERSIONING){
                    Version version = this.versionParser.parse(elementValue.toString().trim());
                    if(version instanceof FixedVersion fixedVersion){
                        versioning.setRelease(fixedVersion);
                    }
                }
                break;
            case PROJECT:
                if(properties == null){
                    projectBuilder.setProprieties(new HashMap<>());
                }
                if(dependencies == null){
                    projectBuilder.setDependencies(new Dependencies());
                }
                if(dependencyManagement == null){
                    projectBuilder.setDependencyManagement(DependencyManagement.builder().build());
                }
                break;
            case PROFILE, PLUGIN:
                this.current = TagElement.PROJECT;
                this.context = TagElement.PROJECT;
                break;
            case DEPENDENCY:
                switch(context){
                    case DEPENDENCY_MANAGEMENT -> dependencyManagement.getDependencies().add(dependency.build());
                    case PROJECT -> dependencies.add(dependency.build());
                }
                break;
            case DEPENDENCIES:
                switch(context){
                    case DEPENDENCY_MANAGEMENT -> dependencyManagement.setDependencies(dependencies);
                    case PROJECT -> projectBuilder.setDependencies(dependencies);
                }
                break;
            case PROPERTIES:
                projectBuilder.setProprieties(properties);
                this.current = TagElement.PROJECT;
                break;
            case EXCLUSION:
                dependency.addExclusion(exclusion.build());
                break;
            case PARENT:
                projectBuilder.setParent(parent.build());
                this.current = TagElement.PROJECT;
                break;
            case DEPENDENCY_MANAGEMENT:
                projectBuilder.setDependencyManagement(dependencyManagement.build());
                this.context = TagElement.PROJECT;
                break;
            case GROUP_ID:
                switch (current) {
                    case PROJECT -> projectBuilder.setGroupId(elementValue.toString().trim());
                    case METADATA -> metadataBuilder.setGroupId(elementValue.toString().trim());
                    case DEPENDENCY -> dependency.groupId(elementValue.toString().trim());
                    case EXCLUSION -> exclusion.setGroupId(elementValue.toString().trim());
                    case PARENT -> parent.setGroupId(elementValue.toString().trim());
                }
                break;
            case ARTIFACT_ID:
                switch (current) {
                    case PROJECT -> projectBuilder.setArtifactId(elementValue.toString().trim());
                    case METADATA -> metadataBuilder.setArtifactId(elementValue.toString().trim());
                    case DEPENDENCY -> dependency.artifactId(elementValue.toString().trim());
                    case EXCLUSION -> exclusion.setArtifactId(elementValue.toString().trim());
                    case PARENT -> parent.setArtifactId(elementValue.toString().trim());
                }
                break;
            case VERSION:
                switch (current) {
                    case DEPENDENCY -> {
                        Version version = this.versionParser.handleVariable(
                                elementValue.toString().trim(),
                                this.properties
                        );
                        dependency.version(version);
                    }
                    case PROJECT -> {
                        Version version = this.versionParser.handleVariable(
                                elementValue.toString().trim(),
                                this.properties
                        );
                        projectBuilder.setVersion(version);
                    }
                    case PARENT -> {
                        Version version = this.versionParser.handleVariable(
                                elementValue.toString().trim(),
                                this.properties
                        );
                        parent.setVersion(version);
                    }
                    case VERSIONS -> {
                        Version version = this.versionParser.parse(elementValue.toString().trim());
                        if(version instanceof FixedVersion fixedVersion){
                            versions.add(fixedVersion);
                        }
                    }
                }
                break;
            case TYPE:
                if (current.equals(TagElement.DEPENDENCY)) {
                    dependency.type(elementValue.toString().trim());
                }
                break;
            case CLASSIFIER:
                if (current.equals(TagElement.DEPENDENCY)) {
                    dependency.classifier(elementValue.toString().trim());
                }
                break;
            case SCOPE:
                if (current.equals(TagElement.DEPENDENCY)) {
                    dependency.scope(elementValue.toString().trim());
                }
                break;
            case OPTIONAL:
                if (current.equals(TagElement.DEPENDENCY)) {
                    dependency.optional(Boolean.parseBoolean(elementValue.toString().trim()));
                }
                break;
            case RELATIVE_PATH:
                if (current.equals(TagElement.PARENT)) {
                    parent.setRelativePath(elementValue.toString().trim());
                }
                break;
            case NAME:
                if (context.equals(TagElement.PROJECT)) {
                    projectBuilder.setName(elementValue.toString().trim());
                }
                break;
            case MODEL_VERSION: {
                if (current.equals(TagElement.PROJECT)) {
                    Version version = versionParser.parse(elementValue.toString().trim());
                    projectBuilder.setModelVersion(version);
                }
                break;
            }
            case PACKAGING: {
                if (context.equals(TagElement.PROJECT)) {
                    projectBuilder.setPackaging(elementValue.toString().trim());
                }
                break;
            }
            default:
                //added in default because properties can contain custom tags
                switch (current){
                    case PROPERTIES , PREREQUISITES -> {
                        switch (tagElement){
                            case TagElement.NONE -> properties.put(qName, elementValue.toString().trim());
                            case TagElement.MAVEN -> properties.put(
                                    "project.prerequisites.maven",
                                    elementValue.toString().trim()
                            );
                        }
                    }
                }
                break;
        }
        elementValue.setLength(0);
    }
    /**
     * Returns the final parsed XML structure.
     *
     * @return the parsed XML structure
     */
    public XMLParsed getXmlParsed() {
        return xmlParsed;
    }
}
