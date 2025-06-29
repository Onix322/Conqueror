package loader.utilities.version.versionHandler;


import loader.utilities.pomReader.supportedTagsClasses.artifact.project.Project;

public interface VersionHandlerContract {
    Project handleVersion(Project param);
}
