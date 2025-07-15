package build_tool.utilities.version.versionHandler;


import build_tool.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;

/**
 * VersionHandlerContract defines a contract for handling versioning in a project.
 * Implementations of this interface should provide logic to process and manage
 * the version of a given project.
 */
public interface VersionHandlerContract {
    Project handleVersion(Project param);
}
