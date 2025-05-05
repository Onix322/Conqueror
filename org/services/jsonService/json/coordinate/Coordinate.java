package org.services.jsonService.json.coordinate;

public class Coordinate {

    private int startIndex;
    private int endIndex;

    public Coordinate(CoordinateBuilder jsonCoordinateBuilder) {
        this.startIndex = jsonCoordinateBuilder.getStartIndex();
        this.endIndex = jsonCoordinateBuilder.getEndIndex();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public static CoordinateBuilder builder(){
        return new CoordinateBuilder();
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }

    public static class CoordinateBuilder {
        private int startIndex = 0;
        private int endIndex = 0;

        private CoordinateBuilder() {}

        public CoordinateBuilder setStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public CoordinateBuilder setEndIndex(int endIndex) {
            this.endIndex = endIndex;
            return this;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public Coordinate build() {
            return new Coordinate(this);
        }
    }
}
