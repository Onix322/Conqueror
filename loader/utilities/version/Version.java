package loader.utilities.version;

public interface Version {
    boolean isFixed();
    boolean isInterval();
    String asString();
    <R> R getVersion();

    //BETTER TO BE USED WITH TYPE VERIFYING METHODS AS isFixed() or isInterval
    @SuppressWarnings("unchecked")
    default <T extends Version> T getAs(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return (T) this;
        } else {
            throw new IllegalArgumentException("Not an instance of " + clazz.getSimpleName());
        }
    }

    default boolean isUnknown(Version version){
        if(this.isFixed()){
            return ((FixedVersion) version).getRankingPoints() == -1;
        } else return version == null;
    }
}
