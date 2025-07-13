package loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata;

import loader.utilities.version.FixedVersion;
import loader.utilities.version.Version;

import java.util.LinkedList;

/**
 * Represents a collection of fixed versions in a Maven POM file.
 * This class extends LinkedList to hold FixedVersion objects,
 * providing a convenient way to manage multiple fixed versions
 * in a single structure.
 */
public class Versions extends LinkedList<FixedVersion> {
}
