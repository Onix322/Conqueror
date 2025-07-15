package build_tool.utilities.version;

/**
 * Interface representing a version in a software system.
 * This interface defines methods to check the type of version,
 * retrieve its string representation, and get the version object.
 * It also provides a method to safely cast the version to a specific type.
 */
public interface Version {
    boolean isFixed();

    boolean isInterval();

    String asString();

    <R> R getVersion();

    //BETTER TO BE USED WITH TYPE VERIFYING METHODS AS isFixed() or isInterval
    /**
     * Safely casts the version to a specific type.
     * If the version is not an instance of the specified class,
     * it throws an IllegalArgumentException.
     *
     * @param clazz the class to cast the version to its implementation type.
     * @param <T>   the type of the version to cast to.
     * @return the version cast to the specified type
     * */
    @SuppressWarnings("unchecked")
    default <T extends Version> T getAs(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return (T) this;
        } else {
            throw new IllegalArgumentException("Not an instance of " + clazz.getSimpleName());
        }
    }

    /**
     * Checks if the version is unknown.
     * For FixedVersion, it checks if the ranking points are -1.
     * For other versions, it checks if the version is null.
     *
     * @param version the version to check
     * @return true if the version is unknown, false otherwise
     */
    default boolean isUnknown(Version version) {
        if (this.isFixed()) {
            return ((FixedVersion) version).getRankingPoints() == -1;
        } else return version == null;
    }
}
