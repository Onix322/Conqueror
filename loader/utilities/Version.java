package loader.utilities;

import java.util.Arrays;
import java.util.Objects;

public class Version {

    private String[] version;
    private int rankingPoints;
    private VersionIntervalDirection direction;

    public Version(String[] version, int rankingPoints, VersionIntervalDirection direction) {
        this.version = version;
        this.rankingPoints = rankingPoints;
        this.direction = direction;
    }

    public String[] getVersion() {
        return version;
    }

    public String asString(){
        return String.join(".", this.version);
    }

    public void setVersion(String[] version) {
        this.version = version;
    }

    public int getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(int rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    public VersionIntervalDirection getDirection() {
        return direction;
    }

    public void setDirection(VersionIntervalDirection direction) {
        this.direction = direction;
    }

    public int compareTo(Version version){
        return Integer.compare(this.getRankingPoints(), version.getRankingPoints());
    }

    public boolean isInterval(){
        return direction != VersionIntervalDirection.EQUAL;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Version version1 = (Version) object;
        return getRankingPoints() == version1.getRankingPoints() && Objects.deepEquals(getVersion(), version1.getVersion()) && getDirection() == version1.getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getVersion()), getRankingPoints(), getDirection());
    }

    @Override
    public String toString() {
        return "Version{" +
                "version=" + Arrays.toString(version) +
                ", rankingPoints=" + rankingPoints +
                ", direction=" + direction +
                '}';
    }
}