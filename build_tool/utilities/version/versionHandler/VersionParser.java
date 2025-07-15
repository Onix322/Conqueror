package build_tool.utilities.version.versionHandler;

import build_tool.utilities.pomReader.PomReader;
import build_tool.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import build_tool.utilities.version.FixedVersion;
import build_tool.utilities.version.IntervalVersion;
import build_tool.utilities.version.Version;

import java.util.*;

/**
 * VersionParser is a utility class for parsing and handling version strings.
 * It supports both fixed and interval versions, allowing for complex version
 * handling in projects.
 * <p>
 * This class is designed to be a singleton, ensuring that only one instance
 * exists throughout the application.
 * It provides methods to parse version strings,
 * handle variables, and calculate version ranks based on predefined rules.
 * </p>
 */
public class VersionParser {

    public final int DEFAULT_RANK_POINTS = 100;

    private final Map<String, Integer> QUALIFIER_ORDER = Map.ofEntries(
            Map.entry("alpha", 50),
            Map.entry("a", 50),
            Map.entry("beta", 40),
            Map.entry("b", 40),
            Map.entry("milestone", 30),
            Map.entry("m", 30),
            Map.entry("rc", 20),
            Map.entry("cr", 20),
            Map.entry("snapshot", 10),
            Map.entry("final", 0),
            Map.entry("ga", 0),
            Map.entry("release", 0),
            Map.entry("", 0)
    );

    private final VersionHandler versionHandler;

    private VersionParser(VersionHandler versionHandler) {
        this.versionHandler = versionHandler;
    }

    private static class Holder {
        private static VersionParser INSTANCE = null;
    }

    public static void init(VersionHandler versionHandler) {
        if (VersionParser.Holder.INSTANCE == null) {
            VersionParser.Holder.INSTANCE = new VersionParser(versionHandler);
        } else {
            throw new IllegalStateException("VersionParser has already been initialized.");
        }
    }

    public static VersionParser getInstance() {
        if (VersionParser.Holder.INSTANCE == null) {
            throw new IllegalStateException("VersionParser not initialized. Call init() first.");
        }
        return VersionParser.Holder.INSTANCE;
    }

    /**
     * Handles a raw version string, determining if it is an interval or fixed version.
     * If the version starts with "[${" or "(${", it is treated as an interval variable.
     * Otherwise, it is treated as a fixed variable.
     *
     * @param rawVersion The raw version string to handle.
     * @param properties A map of properties for variable resolution.
     * @return A Version object representing the parsed version.
     */
    public Version handleVariable(String rawVersion, Map<String, String> properties) {
        if (rawVersion.startsWith("[${") || rawVersion.startsWith("(${")) {
            return handleIntervalVariable(rawVersion, properties);
        }
        return handleFixedVariable(rawVersion, properties);
    }

    /**
     * Handles an interval variable version string, parsing it into an IntervalVersion object.
     * The version string is expected to be in the format "[v1,v2]" or "(v1,v2)".
     *
     * @param rawVersion The raw version string to handle.
     * @param properties A map of properties for variable resolution.
     * @return An IntervalVersion object representing the parsed interval version.
     */
    public Version handleIntervalVariable(String rawVersion, Map<String, String> properties) {
        String[] split = rawVersion.split(",");

        String v1Raw = split[0].substring(1);
        FixedVersion v1 = this.handleFixedVariable(
                        v1Raw.isEmpty() ? String.valueOf(0) : v1Raw,
                        properties
        ).getAs(FixedVersion.class);
        v1.setDirection(VersionIntervalDirection.getDirection(String.valueOf(rawVersion.charAt(0))));

        String v2Raw = split[1].replaceAll("[)]]", "");
        FixedVersion v2 = this.handleFixedVariable(
                v2Raw.isEmpty() ? String.valueOf(0) : v2Raw,
                properties
        ).getAs(FixedVersion.class);
        v2.setDirection(VersionIntervalDirection.getDirection(String.valueOf(rawVersion.charAt(rawVersion.length() - 1))));

        return new IntervalVersion(v1, v2);
    }

    /**
     * Handles a fixed variable version string, parsing it into a FixedVersion object.
     * If the version starts with "${", it resolves the variable using the provided properties.
     *
     * @param rawVersion The raw version string to handle.
     * @param properties A map of properties for variable resolution.
     * @return A FixedVersion object representing the parsed fixed version, or null if not found.
     */
    public Version handleFixedVariable(String rawVersion, Map<String, String> properties) {
        if (!rawVersion.startsWith("${")) return this.parse(rawVersion);
        if (properties == null) {
            properties = new HashMap<>();
        }
        String replaced = rawVersion.replaceAll("[${}]", "");
        String prop = properties.getOrDefault(replaced, DefaultProperties.get(replaced));
        if (prop == null) {
            return null;
        }
        if (prop.startsWith("${")) {
            return this.handleFixedVariable(prop, properties);
        }
        return this.parse(prop);
    }

    /**
     * Parses a string into an Integer.
     * If the string is blank, it returns 0.
     * If the string cannot be parsed, it throws a NumberFormatException with a descriptive message.
     *
     * @param integer The string to parse.
     * @return The parsed Integer value.
     */
    public Integer parseInt(String integer) {
        try {
            if(integer.isBlank()) return 0;
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("String could not be parsed: " + integer + " -> integer");
        }
    }

    /**
     * Handles the versioning of a Project object using a PomReader.
     * This method delegates the version handling to the VersionHandler.
     *
     * @param project The Project object to handle versions for.
     * @param pomReader The PomReader used to read the project's POM file.
     * @return The Project object with handled versions.
     */
    public Project handleVersions(Project project, PomReader pomReader) {
        return this.versionHandler.handleVersion(project, pomReader);
    }

    /**
     * Parses a raw version string into a Version object.
     * It determines whether the version is an interval or fixed version based on the presence of brackets.
     *
     * @param rawVersion The raw version string to parse.
     * @return A Version object representing the parsed version.
     */
    public Version parse(String rawVersion) {
        if (rawVersion.contains("[") || rawVersion.contains("(")) {
            return parseInterval(rawVersion);
        } else {
            return parseFixed(rawVersion);
        }
    }

    /**
     * Parses a raw version string into a FixedVersion object.
     * It extracts the interval direction and calculates the rank based on the version components.
     *
     * @param rawVersion The raw version string to parse.
     * @return A FixedVersion object representing the parsed fixed version.
     */
    public FixedVersion parseFixed(String rawVersion) {
        VersionIntervalDirection direction = this.extractIntervalDirection(rawVersion);
        String[] version = rawVersion.replace(direction.getValue(), "")
                .split("\\.");
        int rank = this.rankCalculator(version);
        return new FixedVersion(version, rank, direction);
    }

    /**
     * Parses a raw interval version string into an IntervalVersion object.
     * It splits the string by commas and parses each part into a FixedVersion.
     *
     * @param rawInterval The raw interval version string to parse.
     * @return An IntervalVersion object representing the parsed interval version.
     */
    public IntervalVersion parseInterval(String rawInterval) {
        String[] rawVersions = rawInterval.split(",");
        List<FixedVersion> list = new LinkedList<>();

        for (String rv : rawVersions) {
            list.add(this.parseFixed(rv.trim()));
        }
        return new IntervalVersion(list.getFirst(), list.getLast());
    }

    /**
     * Calculates the rank of a version based on its components.
     * The rank is calculated using the major version, minor version, iteration,
     * last component, and qualifier points.
     *
     * @param version An array of strings representing the version components.
     * @return The calculated rank as an integer.
     */
    public int rankCalculator(String[] version) {
        if (version[0].isEmpty()) return 0;
        //find qualifier / gather data
        String qualifier = this.findQualifier(version);
        int qualifierPoints = this.QUALIFIER_ORDER.get(qualifier) == null ? 1 : this.QUALIFIER_ORDER.get(qualifier);
        Integer iteration = this.findQualifierIteration(version);
        int majorVersion = this.findMajorVersion(version);
        int minorVersion = this.findMinorVersion(version);
        int last = findLast(version);
        //calculate rank
        return (majorVersion * 1000)
                + (minorVersion * 100) + iteration
                + last
                + version.length
                + this.DEFAULT_RANK_POINTS
                - qualifierPoints
                + iteration;
    }

    /**
     * Finds the qualifier from a version array.
     * It checks each component of the version and returns the last valid qualifier found.
     * If no valid qualifier is found, it defaults to "final".
     *
     * @param version An array of strings representing the version components.
     * @return The qualifier as a string.
     */
    public String findQualifier(String[] version) {
        String key = "final";
        for (String sb : version) {
            if (!sb.matches("\\d+-\\w+(?:-\\d+|)")) continue;
            key = sb.replaceAll("(\\d|\\W)", "").toLowerCase(Locale.ROOT);
        }
        return key;
    }

    /**
     * Finds the iteration number from a version array.
     * It extracts the numeric part from components that match the pattern "digit-word(-digit)".
     * If no valid iteration is found, it defaults to 0.
     *
     * @param version An array of strings representing the version components.
     * @return The iteration number as an Integer, or 0 if not found.
     */
    public Integer findQualifierIteration(String[] version) {

        StringBuilder rawIteration = new StringBuilder();

        for (String subN : version) {
            if (!subN.matches("\\d+-\\w+(?:-\\d+|)")) continue;
            for (int i = subN.length() - 1; i >= 0; i--) {
                if (!Character.isDigit(subN.charAt(i))) break;
                rawIteration.append(subN.charAt(i));
            }
        }

        return rawIteration.isEmpty() ? 0 : this.parseInt(rawIteration.reverse().toString());
    }

    /**
     * Finds the major version from a version array.
     * It extracts the numeric part from the first component of the version.
     *
     * @param version An array of strings representing the version components.
     * @return The major version as an integer.
     */
    public int findMajorVersion(String[] version) {
//        System.out.println(version[0]);
        return this.parseInt(version[0].replaceAll("\\D", ""));
    }

    /**
     * Finds the minor version from a version array.
     * It extracts the numeric part from the second component of the version.
     * If the second component is not present, it defaults to 0.
     *
     * @param version An array of strings representing the version components.
     * @return The minor version as an integer, or 0 if not found.
     */
    public int findMinorVersion(String[] version) {
        StringBuilder sb = new StringBuilder();
        if (version.length <= 1) return 0;
        String rawVersion = version[1];

        for (int i = 0; i < rawVersion.length(); i++) {
            if (!Character.isDigit(rawVersion.charAt(i))) break;
            sb.append(rawVersion.charAt(i));
        }
        return this.parseInt(sb.toString());
    }

    /**
     * Finds the last component of a version array.
     * It extracts the numeric part from the third component of the version.
     * If the third component is not present, it defaults to 0.
     *
     * @param version An array of strings representing the version components.
     * @return The last component as an integer, or 0 if not found.
     */
    public int findLast(String[] version) {
        if (version.length <= 2) return 0;
        String rawVersion = version[2].replaceAll("\\D+", "");
//        System.out.println(Arrays.toString(version));
        return this.parseInt(rawVersion);
    }

    /**
     * Extracts the interval direction from a raw version string.
     * It checks the first and last characters of the string to determine the direction.
     * If neither character indicates a direction, it defaults to EQUAL.
     *
     * @param rawVersion The raw version string to extract the direction from.
     * @return The VersionIntervalDirection representing the direction of the interval.
     */
    public VersionIntervalDirection extractIntervalDirection(String rawVersion) {
        String start = rawVersion.substring(0, 1);
        String end = rawVersion.substring(rawVersion.length() - 1);

        VersionIntervalDirection dirStart = VersionIntervalDirection.getDirection(start);
        if (dirStart != null) return dirStart;

        VersionIntervalDirection dirEnd = VersionIntervalDirection.getDirection(end);
        if (dirEnd != null) return dirEnd;

        return VersionIntervalDirection.EQUAL;
    }
}
