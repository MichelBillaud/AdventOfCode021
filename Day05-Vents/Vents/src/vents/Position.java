package vents;

record Position(int row, int col) {

    static Position from(String string1, String string2) {
        return new Position(Integer.parseInt(string1),
                Integer.parseInt(string2));
    }

    int distance(Position other) {
        return Math.max(Math.abs(this.col - other.col),
                Math.abs(this.row - other.row));
    }

    int dRow(Position other) {
        return Integer.signum(other.row - row);
    }

    int dCol(Position other) {
        return Integer.signum(other.col - col);
    }
}
