package loader.utilities;

/**
 * [ -> '>='
 * ] -> '<='
 * ( -> '>'
 * ) -> '<'
 * none of them means '=' -> output = input
 * */
public enum VersionIntervalDirection {
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
