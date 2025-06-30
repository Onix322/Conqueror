package loader.utilities.version.versionHandler;


import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;

public interface VersionHandlerContract {
    Project handleVersion(Project param);
}
