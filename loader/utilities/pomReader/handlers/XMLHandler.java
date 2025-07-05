package loader.utilities.pomReader.handlers;

import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata.Metadata;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata.Versioning;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata.Versions;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import loader.utilities.pomReader.supportedTagsClasses.TagElement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.exclusion.Exclusion;
import loader.utilities.pomReader.supportedTagsClasses.artifact.parent.Parent;
import loader.utilities.version.FixedVersion;
import loader.utilities.version.Version;
import loader.utilities.version.versionHandler.VersionParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLHandler extends DefaultHandler {

    // final product
    private XMLParsed xmlParsed;

    // XMLType <project>
    private Project.Builder projectBuilder;
    private Dependencies dependencies;
    private Dependency.Builder dependency;
    private Exclusion.Builder exclusion;
    private Parent.Builder parent;
    private DependencyManagement.Builder dependencyManagement;
    private Map<String, String> properties;

    // XMLType <metadata>
    private Metadata.Builder metadataBuilder;
    private Versioning.Builder versioning;
    private Versions versions;

    // Extracted characters (value)
    private StringBuilder elementValue = new StringBuilder();

    // Turn-dictating variables
    private TagElement current = TagElement.NONE; // keeps the counting of each start tag
    private TagElement context = TagElement.NONE; // keeps parent counted, because some variables have the same children.
    private TagElement xmlType = TagElement.NONE; // used for telling the XML type

    // Injections
    private final VersionParser versionParser;

    public XMLHandler(VersionParser versionParser) {
        super();
        this.versionParser = versionParser;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        }
        elementValue.append(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        switch (this.xmlType){
            case PROJECT -> xmlParsed = projectBuilder.build();
            case METADATA -> xmlParsed = metadataBuilder.build();
        }
    }

    /// Make actions at the START of the element. E.g., start element <element>
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

    /// Make actions at the END of the element. E.g., end element </element>
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

    public XMLParsed getXmlParsed() {
        return xmlParsed;
    }
}
