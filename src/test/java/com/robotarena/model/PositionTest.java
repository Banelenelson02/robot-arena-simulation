package com.robotarena.arena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Position Unit Tests")
class PositionTest {

    @Test
    @DisplayName("equals() returns true for same coordinates")
    void equalsSameCoordinates() {
        assertEquals(new Position(2, 3), new Position(2, 3));
    }

    @Test
    @DisplayName("equals() returns false for different coordinates")
    void notEqualDifferentCoordinates() {
        assertNotEquals(new Position(2, 3), new Position(3, 2));
    }

    @Test
    @DisplayName("hashCode() matches for equal positions")
    void hashCodeMatchesForEqualPositions() {
        assertEquals(
                new Position(5, 5).hashCode(),
                new Position(5, 5).hashCode()
        );
    }

    @Test
    @DisplayName("distanceTo() returns 0 for same position")
    void distanceToSelf() {
        Position p = new Position(3, 3);
        assertEquals(0.0, p.distanceTo(new Position(3, 3)), 0.001);
    }

    @Test
    @DisplayName("distanceTo() returns correct Euclidean distance")
    void distanceToKnownValue() {
        // (0,0) to (3,4) = 5.0 (classic 3-4-5 triangle)
        Position origin = new Position(0, 0);
        Position target = new Position(3, 4);
        assertEquals(5.0, origin.distanceTo(target), 0.001);
    }
}