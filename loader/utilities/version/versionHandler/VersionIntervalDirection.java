package loader.utilities.version.versionHandler;

/*
 * Enum representing the direction of version intervals.
 * This enum is used to specify how versions are compared in a range,
 * such as whether the range includes or excludes the endpoints.
 * The values correspond to the common mathematical notation for intervals.
 */
public enum VersionIntervalDirection {
    /*
     * Represents the direction of version intervals.
     * The values correspond to the common mathematical notation for intervals:
     * - BIGGER_OR_EQUAL: '[' (inclusive lower bound)
     * - LESS_OR_EQUAL: ']' (inclusive upper bound)
     * - EQUAL: '' (exact match)
     * - BIGGER: '(' (exclusive lower bound)
     * - LESS: ')' (exclusive upper bound)
     */
    BIGGER_OR_EQUAL("["),
    LESS_OR_EQUAL("]"),
    EQUAL(""),
    BIGGER("("),
    LESS(")");

    private final String value;

    VersionIntervalDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns the VersionIntervalDirection corresponding to the given string value.
     * If no matching direction is found, returns null.
     *
     * @param value the string representation of the direction
     * @return the corresponding VersionIntervalDirection or null if not found
     */
    public static VersionIntervalDirection getDirection(String value){
        VersionIntervalDirection v = null;
        for(VersionIntervalDirection vid : values()){
            if(vid.getValue().equals(value)){
                v = vid;
                break;
            }
        }
        return v;
    }
}
