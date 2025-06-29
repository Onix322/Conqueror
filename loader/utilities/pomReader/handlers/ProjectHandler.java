package loader.utilities.pomReader.handlers;

import loader.utilities.pomReader.supportedTagsClasses.artifact.project.Project;
import loader.utilities.pomReader.supportedTagsClasses.TagElement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.exclusion.Exclusion;
import loader.utilities.pomReader.supportedTagsClasses.artifact.parent.Parent;
import loader.utilities.version.Version;
import loader.utilities.version.versionHandler.VersionParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class ProjectHandler extends DefaultHandler {

    private Project project;
    private Project.Builder projectBuilder;
    private Dependencies dependencies;
    private Dependency.Builder dependency;
    private Exclusion.Builder exclusion;
    private Parent.Builder parent;
    private DependencyManagement.Builder dependencyManagement;
    private Map<String, String> properties;

    //value
    private StringBuilder elementValue = new StringBuilder();

    //changes the turn so the end tags will not get confused
    private TagElement turn = TagElement.NONE;
    private TagElement context = TagElement.NONE;

    private final VersionParser versionParser;

    private ProjectHandler(VersionParser versionParser) {
        super();
        this.versionParser = versionParser;
    }

    private static class Holder {
        private static ProjectHandler INSTANCE = null;
    }

    public static synchronized void init(VersionParser versionParser) {
        if (ProjectHandler.Holder.INSTANCE == null) {
            ProjectHandler.Holder.INSTANCE = new ProjectHandler(versionParser);
        }
    }

    public static ProjectHandler getInstance() {
        if (ProjectHandler.Holder.INSTANCE == null) {
            throw new IllegalStateException("ProjectHandler is not initialized. Use ProjectHandler.init().");
        }
        return ProjectHandler.Holder.INSTANCE;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        }
        elementValue.append(ch, start, length);
    }

    @Override
    public void startDocument() {
        this.projectBuilder = Project.builder();
        this.context = TagElement.PROJECT;
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
//        projectBuilder = versionParser.fillVersions(projectBuilder);
        System.out.println(projectBuilder.build());
        project = projectBuilder.build();
    }

    //Defined objects
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        TagElement tagElement = TagElement.find(qName);
        switch (tagElement) {
            case PROJECT:
                turn = TagElement.PROJECT;
                break;
            case PARENT:
                parent = Parent.builder();
                turn = TagElement.PARENT;
                break;
            case DEPENDENCY_MANAGEMENT:
                dependencyManagement = DependencyManagement.builder();
                context = TagElement.DEPENDENCY_MANAGEMENT;
                turn = TagElement.DEPENDENCY_MANAGEMENT;
                break;
            case DEPENDENCIES:
                dependencies = new Dependencies();
                switch (context) {
                    case DEPENDENCY_MANAGEMENT:
                        dependencyManagement.setDependencies(dependencies);
                        break;
                    case PROJECT:
                        projectBuilder.setDependencies(dependencies);
                        break;
                }
                turn = TagElement.DEPENDENCIES;
                break;
            case EXCLUSION:
                exclusion = Exclusion.builder();
                turn = TagElement.EXCLUSION;
                break;
            case DEPENDENCY:
                dependency = Dependency.builder();
                turn = TagElement.DEPENDENCY;
                break;
            case PROPERTIES:
                properties = new HashMap<>();
                turn = TagElement.PROPERTIES;
                break;
        }
        elementValue.setLength(0);
    }


    /// /Get end elements values
    @Override
    public void endElement(String uri, String localName, String qName) {
        TagElement tagElement = TagElement.find(qName);
        switch (tagElement) {
            case PROJECT:
                projectBuilder.setDependencies(dependencies);
                projectBuilder.setProprieties(properties);
                break;
            case DEPENDENCY:
                if (context == TagElement.DEPENDENCY_MANAGEMENT) {
                    dependencyManagement.getDependencies().add(dependency.build());
                } else {
                    dependencies.add(dependency.build());
                }
                break;
            case EXCLUSION:
                dependency.addExclusion(exclusion.build());
                break;
            case PARENT:
                projectBuilder.setParent(parent.build());
                break;
            case DEPENDENCY_MANAGEMENT:
                projectBuilder.setDependencyManagement(dependencyManagement.build());
                context = TagElement.NONE;
                break;
            case GROUP_ID:
                switch (turn) {
                    case PROJECT -> projectBuilder.setGroupId(elementValue.toString().trim());
                    case DEPENDENCY -> dependency.groupId(elementValue.toString().trim());
                    case EXCLUSION -> exclusion.setGroupId(elementValue.toString().trim());
                    case PARENT -> parent.setGroupId(elementValue.toString().trim());
                }
                break;
            case ARTIFACT_ID:
                switch (turn) {
                    case PROJECT -> projectBuilder.setArtifactId(elementValue.toString().trim());
                    case DEPENDENCY -> dependency.artifactId(elementValue.toString().trim());
                    case EXCLUSION -> exclusion.setArtifactId(elementValue.toString().trim());
                    case PARENT -> parent.setArtifactId(elementValue.toString().trim());
                }
                break;
            case VERSION:
                switch (turn) {
                    case DEPENDENCY -> {
                        Version version = this.versionParser.handleVariable(
                                elementValue.toString().trim(),
                                this.properties
                        );
                        dependency.version(version);
                    }
                    case PROJECT -> {
                        Version version = versionParser.split(elementValue.toString().trim());
                        projectBuilder.setVersion(version);
                    }
                    case PARENT -> {
                        Version version = versionParser.split(elementValue.toString().trim());
                        parent.setVersion(version);
                    }
                }
                break;
            case TYPE:
                if (turn.equals(TagElement.DEPENDENCY)) {
                    dependency.type(elementValue.toString().trim());
                }
                break;
            case CLASSIFIER:
                if (turn.equals(TagElement.DEPENDENCY)) {
                    dependency.classifier(elementValue.toString().trim());
                }
                break;
            case SCOPE:
                if (turn.equals(TagElement.DEPENDENCY)) {
                    dependency.scope(elementValue.toString().trim());
                }
                break;
            case OPTIONAL:
                if (turn.equals(TagElement.DEPENDENCY)) {
                    dependency.optional(Boolean.parseBoolean(elementValue.toString().trim()));
                }
                break;
            case RELATIVE_PATH:
                if (turn.equals(TagElement.PARENT)) {
                    parent.setRelativePath(elementValue.toString().trim());
                }
                break;
            case NAME:
                if (turn.equals(TagElement.PROJECT)) {
                    projectBuilder.setName(elementValue.toString().trim());
                }
                break;
            case MODEL_VERSION: {
                if (turn.equals(TagElement.PROJECT)) {
                    Version version = versionParser.split(elementValue.toString().trim());
                    projectBuilder.setModelVersion(version);
                }
                break;
            }
            case PACKAGING: {
                if (turn.equals(TagElement.PROJECT)) {
                    projectBuilder.setPackaging(elementValue.toString().trim());
                }
                break;
            }
            default:
                switch (turn) {
                    //added in default because properties can contain
                    //custom tags
                    case PROPERTIES -> {
                        if (tagElement == TagElement.NONE) {
                            properties.put(qName, elementValue.toString().trim());
                        }
                    }
                }
        }
        elementValue.setLength(0);
    }

    public Project getProjectBuilder() {
        return project;
    }
}
