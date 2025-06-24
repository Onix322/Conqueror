package loader.utilities;

import java.util.Arrays;
import java.util.Map;

public class VersionComparator {

    private static final Map<String, Integer> QUALIFIER_ORDER = Map.ofEntries(
            Map.entry("alpha", 5),
            Map.entry("a", 5),
            Map.entry("beta", 4),
            Map.entry("b", 4),
            Map.entry("milestone", 3),
            Map.entry("m", 3),
            Map.entry("rc", 2),
            Map.entry("cr", 2),
            Map.entry("snapshot", 1),
            Map.entry("final", 0),
            Map.entry("ga", 0),
            Map.entry("release", 0),
            Map.entry("", 0)
    );

    private VersionComparator() {
    }

    private static class Holder {
        private static VersionComparator INSTANCE = null;
    }

    public static synchronized void init() {
        if (VersionComparator.Holder.INSTANCE == null) {
            VersionComparator.Holder.INSTANCE = new VersionComparator();
        }
    }

    public static VersionComparator getInstance() {
        if (VersionComparator.Holder.INSTANCE == null) {
            throw new IllegalStateException("VersionComparator is not initialized. Use VersionComparator.init().");
        }
        return VersionComparator.Holder.INSTANCE;
    }

    public int compare(Map.Entry<Integer[], VersionIntervalDirection> v1, Map.Entry<Integer[], VersionIntervalDirection> v2) {

        Integer[][] equalizedVersions = equalizeLength(v1.getKey(), v2.getKey());

        System.out.println(Arrays.deepToString(equalizedVersions));

        Integer[] first = equalizedVersions[0];
        Integer[] second = equalizedVersions[1];

        for (int i = 0; i < first.length; i++) {
            if (first[i] < second[i]) return -1;
            else if (first[i] > (second[i])) return 1;
        }

        return 0;
    }

    private Integer[][] equalizeLength(Integer[] v1, Integer[] v2) {
        if (v1.length < v2.length) {
            return new Integer[][]{this.resize(v1, v2.length), v2};
        }
        return new Integer[][]{v1, this.resize(v2, v1.length)};
    }

    private Integer[] resize(Integer[] array, int newSize){
        return Arrays.stream(Arrays.copyOf(array, newSize))
                .map(e -> e == null ? 0 : e)
                .toArray(Integer[]::new);
    }
}
