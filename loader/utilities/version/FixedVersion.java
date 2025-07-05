package loader.utilities.version;

import loader.utilities.version.versionHandler.VersionIntervalDirection;

import java.util.Arrays;
import java.util.Objects;

public class FixedVersion implements Version {

    private String[] version;
    private int rankingPoints;
    private VersionIntervalDirection direction;

    public FixedVersion(String[] version, int rankingPoints, VersionIntervalDirection direction) {
        this.version = version;
        this.rankingPoints = rankingPoints;
        this.direction = direction;
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

    public int compareTo(FixedVersion version){
        return Integer.compare(this.getRankingPoints(), version.getRankingPoints());
    }

    public static FixedVersion unknown() {
        return new FixedVersion(
                new String[]{"0", "0", "0"},
                -1,
                VersionIntervalDirection.EQUAL
        );
    }

    @Override
    public String asString(){
        return String.join(".", this.version);
    }

    @Override
    public String[] getVersion() {
        return version;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public boolean isInterval(){
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        FixedVersion version1 = (FixedVersion) object;
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