package com.robotarena.arena;

/**
 * Represents a 2D coordinate on the Arena grid.
 *
 * <p>This is a <strong>value object</strong> — two Position instances are
 * considered equal if they hold the same x and y values, regardless of
 * whether they are the same object in memory. This directly addresses
 * the curriculum concept of <em>Object Identity vs Object Equality</em>:
 * identity is the JVM reference (==), equality is our defined meaning (equals()).
 *
 * <p>Fields are final because a Position does not change once created.
 * To "move" a robot, you create a new Position — you do not mutate this one.
 * This immutability makes Position safe to share between objects without
 * defensive copying.
 */
public class Position {

    private final int x;
    private final int y;

    /**
     * Constructs a new Position at the given coordinates.
     *
     * @param x the column on the grid (0-indexed, left to right)
     * @param y the row on the grid (0-indexed, top to bottom)
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** @return the x (column) coordinate */
    public int getX() {
        return x;
    }

    /** @return the y (row) coordinate */
    public int getY() {
        return y;
    }

    /**
     * Calculates the Euclidean distance between this position and another.
     *
     * <p>Used by robots to find nearby targets or allies.
     *
     * @param other the other position to measure to
     * @return the straight-line distance as a double
     */
    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Two Positions are equal if and only if their x and y coordinates match.
     *
     * <p><strong>OOP Note:</strong> Without overriding this, Java compares
     * object references — {@code new Position(2,3) == new Position(2,3)}
     * would be {@code false}, which is logically wrong for a coordinate.
     * We override to define equality by <em>value</em>, not <em>identity</em>.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Must be overridden whenever equals() is overridden.
     *
     * <p>The contract: if two objects are equal, they MUST have the same
     * hash code. Violating this breaks HashMap, HashSet, and any
     * hash-based collection.
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    /**
     * Returns a human-readable coordinate string, e.g. "(3, 5)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
