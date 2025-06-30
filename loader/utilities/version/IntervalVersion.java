package loader.utilities.version;

import java.util.Objects;

public class IntervalVersion implements Version {

    private FixedVersion first;
    private FixedVersion second;

    public IntervalVersion(FixedVersion first, FixedVersion second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean isInterval() {
        return true;
    }

    @Override
    public String asString() {
        return first.getDirection().getValue()
                + first.asString()
                + ','
                + second.asString()
                + second.getDirection().getValue();
    }

    public FixedVersion getFirst() {
        return first;
    }

    public void setFirst(FixedVersion first) {
        this.first = first;
    }

    public FixedVersion getSecond() {
        return second;
    }

    public void setSecond(FixedVersion second) {
        this.second = second;
    }

    @Override
    public FixedVersion[] getVersion() {
        return new FixedVersion[]{first, second};
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        IntervalVersion that = (IntervalVersion) object;
        return Objects.equals(getFirst(), that.getFirst()) && Objects.equals(getSecond(), that.getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond());
    }

    @Override
    public String toString() {
        return "IntervalVersion{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
