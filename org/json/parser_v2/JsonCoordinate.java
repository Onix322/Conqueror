package org.json.parser_v2;

public class JsonCoordinate {

    private int startIndex;
    private int endIndex;

    public JsonCoordinate(JsonCoordinateBuilder jsonCoordinateBuilder) {
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

    public static class JsonCoordinateBuilder {
        private int startIndex;
        private int endIndex;

        public JsonCoordinateBuilder(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public JsonCoordinateBuilder setStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public JsonCoordinateBuilder setEndIndex(int endIndex) {
            this.endIndex = endIndex;
            return this;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public JsonCoordinate build() {
            return new JsonCoordinate(this);
        }
    }
}
