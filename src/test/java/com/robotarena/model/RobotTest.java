package com.robotarena.model;

import com.robotarena.arena.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Robot class.
 *
 * Each test method targets one specific behaviour or invariant.
 * Tests are named to read as sentences: "robot takes damage correctly".
 *
 * The @BeforeEach method runs before EVERY test, giving each test
 * a fresh Robot to work with so tests cannot interfere with each other.
 */
@DisplayName("Robot Unit Tests")
class RobotTest {

    private Robot robot;

    @BeforeEach
    void setUp() {
        // A standard test robot: 100 max HP, starting at position (0,0)
        robot = new Robot("R001", "Ironclad", 100, new Position(0, 0));
    }

    // --- Construction Tests ---

    @Test
    @DisplayName("Robot initialises at full health")
    void robotStartsAtFullHealth() {
        assertEquals(100, robot.getHealth());
        assertEquals(100, robot.getMaxHealth());
    }

    @Test
    @DisplayName("Robot initialises as alive")
    void robotStartsAlive() {
        assertTrue(robot.isAlive());
    }

    @Test
    @DisplayName("Constructor throws on non-positive maxHealth")
    void constructorRejectsNonPositiveMaxHealth() {
        assertThrows(IllegalArgumentException.class, () ->
                new Robot("R002", "Broken", 0, new Position(0, 0))
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Robot("R003", "Broken", -10, new Position(0, 0))
        );
    }

    @Test
    @DisplayName("Constructor throws on null position")
    void constructorRejectsNullPosition() {
        assertThrows(NullPointerException.class, () ->
                new Robot("R002", "Ghost", 50, null)
        );
    }

    @Test
    @DisplayName("Constructor throws on blank id")
    void constructorRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Robot("", "Ghost", 50, new Position(0, 0))
        );
    }

    // --- takeDamage() Tests ---

    @Test
    @DisplayName("takeDamage() reduces health by the correct amount")
    void takeDamageReducesHealth() {
        robot.takeDamage(30);
        assertEquals(70, robot.getHealth());
    }

    @Test
    @DisplayName("takeDamage() cannot reduce health below 0")
    void takeDamageDoesNotGoBelowZero() {
        robot.takeDamage(999);
        assertEquals(0, robot.getHealth());
    }

    @Test
    @DisplayName("takeDamage() marks robot as dead when health reaches 0")
    void takeDamageKillsRobotAtZeroHealth() {
        robot.takeDamage(100);
        assertFalse(robot.isAlive());
    }

    @Test
    @DisplayName("takeDamage() is ignored for a dead robot")
    void takeDamageIgnoredWhenDead() {
        robot.takeDamage(100); // Kill the robot
        robot.takeDamage(50);  // This should be ignored
        assertEquals(0, robot.getHealth());
    }

    @Test
    @DisplayName("takeDamage() ignores zero and negative values")
    void takeDamageIgnoresNonPositiveAmount() {
        robot.takeDamage(0);
        robot.takeDamage(-10);
        assertEquals(100, robot.getHealth()); // Health unchanged
    }

    // --- repair() Tests ---

    @Test
    @DisplayName("repair() increases health by the correct amount")
    void repairIncreasesHealth() {
        robot.takeDamage(50);   // HP: 50
        robot.repair(20);       // HP: 70
        assertEquals(70, robot.getHealth());
    }

    @Test
    @DisplayName("repair() cannot exceed maxHealth")
    void repairDoesNotExceedMaxHealth() {
        robot.takeDamage(10);   // HP: 90
        robot.repair(999);      // Should cap at 100
        assertEquals(100, robot.getHealth());
    }

    @Test
    @DisplayName("repair() is ignored for a dead robot")
    void repairIgnoredWhenDead() {
        robot.takeDamage(100);  // Kill
        robot.repair(50);       // Should do nothing
        assertEquals(0, robot.getHealth());
        assertFalse(robot.isAlive());
    }

    @Test
    @DisplayName("repair() ignores zero and negative values")
    void repairIgnoresNonPositiveAmount() {
        robot.takeDamage(40);   // HP: 60
        robot.repair(0);
        robot.repair(-10);
        assertEquals(60, robot.getHealth());
    }

    // --- moveTo() Tests ---

    @Test
    @DisplayName("moveTo() updates the robot's position")
    void moveToUpdatesPosition() {
        Position newPos = new Position(3, 4);
        robot.moveTo(newPos);
        assertEquals(newPos, robot.getPosition());
    }

    @Test
    @DisplayName("moveTo() throws on null position")
    void moveToRejectsNull() {
        assertThrows(NullPointerException.class, () -> robot.moveTo(null));
    }

    // --- getHealthPercentage() Tests ---

    @Test
    @DisplayName("getHealthPercentage() returns 100.0 at full health")
    void healthPercentageAtFull() {
        assertEquals(100.0, robot.getHealthPercentage(), 0.001);
    }

    @Test
    @DisplayName("getHealthPercentage() returns 50.0 at half health")
    void healthPercentageAtHalf() {
        robot.takeDamage(50);
        assertEquals(50.0, robot.getHealthPercentage(), 0.001);
    }

    // --- equals() and hashCode() Tests ---

    @Test
    @DisplayName("equals() returns true for robots with the same id")
    void equalsByIdSameId() {
        // Different name, different health, different position — same ID
        Robot sameId = new Robot("R001", "Different Name", 50, new Position(9, 9));
        assertEquals(robot, sameId);
    }

    @Test
    @DisplayName("equals() returns false for robots with different ids")
    void notEqualByIdDifferentId() {
        Robot differentId = new Robot("R002", "Ironclad", 100, new Position(0, 0));
        assertNotEquals(robot, differentId);
    }

    @Test
    @DisplayName("equals() returns true when comparing a robot to itself")
    void equalsReflexive() {
        assertEquals(robot, robot);
    }

    @Test
    @DisplayName("hashCode() is consistent with equals()")
    void hashCodeConsistentWithEquals() {
        Robot sameId = new Robot("R001", "Other", 50, new Position(1, 1));
        assertEquals(robot.hashCode(), sameId.hashCode());
    }

    // --- toString() Test ---

    @Test
    @DisplayName("toString() contains id, name, and status")
    void toStringContainsKeyInfo() {
        String output = robot.toString();
        assertTrue(output.contains("R001"));
        assertTrue(output.contains("Ironclad"));
        assertTrue(output.contains("ALIVE"));
    }
}
