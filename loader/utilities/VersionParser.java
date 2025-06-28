package loader.utilities;

import loader.objects.link.Version;

import java.util.Arrays;
import java.util.Map;

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

    private VersionParser() {
    }

    private static class Holder {
        private static VersionParser INSTANCE = null;
    }

    public static void init() {
        if (VersionParser.Holder.INSTANCE == null) {
            VersionParser.Holder.INSTANCE = new VersionParser();
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

    public Version split(String rawVersion) {
        VersionIntervalDirection direction = this.extractIntervalDirection(rawVersion);
        String[] version = rawVersion.replace(direction.getValue(), "")
                .split("\\.");
        int rank = this.rankCalculator(version);
        return new Version(version, rank, direction);
    }

    public int rankCalculator(String[] version) {
        if(version[0].isEmpty()) return 0;
        //find qualifier / gather data
        String qualifier = this.findQualifier(version);
        Integer iteration = this.findQualifierIteration(version);
        int majorVersion = this.findMajorVersion(version);
        int minorVersion = this.findMinorVersion(version);
        int last = version.length > 2 ? Integer.parseInt(version[2]) : 0;
        //calculate rank
        return (majorVersion * 1000)
                + (minorVersion * 100) + iteration
                + last
                + version.length
                + this.DEFAULT_RANK_POINTS
                - this.QUALIFIER_ORDER.get(qualifier)
                + iteration;
    }

    public String findQualifier(String[] version){
        String key = "final";
        for (String sb : version){
            if(!sb.matches("\\d+-\\w+(?:-\\d+|)")) continue;
            key = sb.replaceAll("(\\d|\\W)", "");
        }
        return key;
    }

    public Integer findQualifierIteration(String[] version) {

        StringBuilder rawIteration = new StringBuilder();

        for (String subN : version) {
            if (!subN.matches("\\d+-\\w+(?:-\\d+|)")) continue;
            for (int i = subN.length() - 1; i >= 0; i--) {
                if (!Character.isDigit(subN.charAt(i))) break;
                rawIteration.append(subN.charAt(i));
            }
        }

        return rawIteration.isEmpty() ? 0 : Integer.parseInt(rawIteration.reverse().toString());
    }

    public int findMajorVersion(String[] version){
        return Integer.parseInt(version[0]);
    }

    public int findMinorVersion(String[] version){
        StringBuilder sb = new StringBuilder();
        String rawVersion = version[1];

        for (int i = 0; i < rawVersion.length(); i++) {
            if (!Character.isDigit(rawVersion.charAt(i))) break;
            sb.append(rawVersion.charAt(i));
        }
        return Integer.parseInt(sb.toString());
    }

    public VersionIntervalDirection extractIntervalDirection(String rawVersion) {
        String start = rawVersion.substring(0, 1);
        String end = rawVersion.substring(rawVersion.length() - 1);

        VersionIntervalDirection dirStart = VersionIntervalDirection.getDirection(start);
        if (dirStart != null) return dirStart;

        VersionIntervalDirection dirEnd = VersionIntervalDirection.getDirection(end);
        if (dirEnd != null) return dirEnd;

        return VersionIntervalDirection.EQUAL;
    }

    public Integer[] parse(String[] numbersLike) {
        return Arrays.stream(numbersLike).map(Integer::parseInt)
                .toArray(Integer[]::new);
    }


}
