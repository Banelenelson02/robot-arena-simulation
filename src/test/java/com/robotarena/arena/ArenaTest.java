package com.robotarena.arena;

import com.robotarena.model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Arena class.
 *
 * These tests verify the composition relationship between Arena and Robot:
 * that the Arena correctly manages its robots, enforces its rules, and
 * responds accurately to queries about grid state.
 */
@DisplayName("Arena Unit Tests")
class ArenaTest {

    private Arena arena;
    private Robot alpha;
    private Robot bravo;

    @BeforeEach
    void setUp() {
        arena = new Arena(10, 8);
        alpha = new Robot("R001", "Alpha", 100, new Position(1, 1));
        bravo = new Robot("R002", "Bravo",  80, new Position(5, 5));
    }

    // --- Constructor Tests ---

    @Test
    @DisplayName("Arena initialises with correct dimensions")
    void arenaInitialisesCorrectly() {
        assertEquals(10, arena.getWidth());
        assertEquals(8,  arena.getHeight());
        assertEquals(0,  arena.getTickCount());
        assertTrue(arena.getRobots().isEmpty());
    }

    @Test
    @DisplayName("Constructor rejects dimensions smaller than 2x2")
    void constructorRejectsSmallDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Arena(1, 8));
        assertThrows(IllegalArgumentException.class, () -> new Arena(10, 1));
    }

    // --- addRobot() Tests ---

    @Test
    @DisplayName("addRobot() adds a robot successfully")
    void addRobotSuccess() {
        arena.addRobot(alpha);
        assertEquals(1, arena.getRobots().size());
    }

    @Test
    @DisplayName("addRobot() rejects a null robot")
    void addRobotRejectsNull() {
        assertThrows(NullPointerException.class, () -> arena.addRobot(null));
    }

    @Test
    @DisplayName("addRobot() rejects out-of-bounds starting position")
    void addRobotRejectsOutOfBounds() {
        Robot outOfBounds = new Robot("R099", "Ghost", 50, new Position(99, 99));
        assertThrows(IllegalArgumentException.class, () -> arena.addRobot(outOfBounds));
    }

    @Test
    @DisplayName("addRobot() rejects duplicate robot ID")
    void addRobotRejectsDuplicateId() {
        Robot duplicate = new Robot("R001", "Clone", 50, new Position(3, 3));
        arena.addRobot(alpha);
        assertThrows(IllegalArgumentException.class, () -> arena.addRobot(duplicate));
    }

    // --- removeRobot() Tests ---

    @Test
    @DisplayName("removeRobot() removes the correct robot by ID")
    void removeRobotById() {
        arena.addRobot(alpha);
        arena.addRobot(bravo);
        boolean removed = arena.removeRobot("R001");
        assertTrue(removed);
        assertEquals(1, arena.getRobots().size());
        assertEquals("R002", arena.getRobots().get(0).getId());
    }

    @Test
    @DisplayName("removeRobot() returns false for unknown ID")
    void removeRobotUnknownId() {
        arena.addRobot(alpha);
        assertFalse(arena.removeRobot("UNKNOWN"));
    }

    // --- isPositionValid() Tests ---

    @Test
    @DisplayName("isPositionValid() returns true for corners")
    void positionValidAtCorners() {
        assertTrue(arena.isPositionValid(new Position(0, 0)));
        assertTrue(arena.isPositionValid(new Position(9, 7)));
        assertTrue(arena.isPositionValid(new Position(0, 7)));
        assertTrue(arena.isPositionValid(new Position(9, 0)));
    }

    @Test
    @DisplayName("isPositionValid() returns false outside grid")
    void positionInvalidOutsideGrid() {
        assertFalse(arena.isPositionValid(new Position(-1, 0)));
        assertFalse(arena.isPositionValid(new Position(0, -1)));
        assertFalse(arena.isPositionValid(new Position(10, 0)));
        assertFalse(arena.isPositionValid(new Position(0, 8)));
    }

    @Test
    @DisplayName("isPositionValid() returns false for null")
    void positionInvalidNull() {
        assertFalse(arena.isPositionValid(null));
    }

    // --- getRobotsAt() Tests ---

    @Test
    @DisplayName("getRobotsAt() returns robot at correct position")
    void getRobotsAtCorrectPosition() {
        arena.addRobot(alpha);
        List<Robot> found = arena.getRobotsAt(new Position(1, 1));
        assertEquals(1, found.size());
        assertEquals("R001", found.get(0).getId());
    }

    @Test
    @DisplayName("getRobotsAt() returns empty list for empty cell")
    void getRobotsAtEmptyCell() {
        arena.addRobot(alpha);
        List<Robot> found = arena.getRobotsAt(new Position(9, 7));
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("getRobotsAt() detects two robots on the same cell")
    void getRobotsAtDetectsCollision() {
        Robot samePos = new Robot("R005", "Crasher", 50, new Position(1, 1));
        arena.addRobot(alpha);
        arena.addRobot(samePos);
        List<Robot> found = arena.getRobotsAt(new Position(1, 1));
        assertEquals(2, found.size());
    }

    // --- findRobotById() Tests ---

    @Test
    @DisplayName("findRobotById() returns correct robot")
    void findRobotByIdFound() {
        arena.addRobot(alpha);
        Optional<Robot> result = arena.findRobotById("R001");
        assertTrue(result.isPresent());
        assertEquals("Alpha", result.get().getName());
    }

    @Test
    @DisplayName("findRobotById() returns empty Optional for unknown ID")
    void findRobotByIdNotFound() {
        arena.addRobot(alpha);
        Optional<Robot> result = arena.findRobotById("UNKNOWN");
        assertFalse(result.isPresent());
    }

    // --- removeDeadRobots() Tests ---

    @Test
    @DisplayName("removeDeadRobots() removes only dead robots")
    void removeDeadRobotsOnlyRemovesDead() {
        arena.addRobot(alpha);
        arena.addRobot(bravo);
        alpha.takeDamage(100); // Kill alpha
        arena.removeDeadRobots();
        assertEquals(1, arena.getRobots().size());
        assertEquals("R002", arena.getRobots().get(0).getId());
    }

    // --- getMostDamagedRobot() Tests ---

    @Test
    @DisplayName("getMostDamagedRobot() returns the robot with lowest health %")
    void getMostDamagedRobotReturnsCorrect() {
        arena.addRobot(alpha);
        arena.addRobot(bravo);
        alpha.takeDamage(80); // Alpha at 20%
        bravo.takeDamage(10); // Bravo at ~87.5%
        Optional<Robot> result = arena.getMostDamagedRobot();
        assertTrue(result.isPresent());
        assertEquals("R001", result.get().getId());
    }

    @Test
    @DisplayName("getMostDamagedRobot() returns empty Optional for empty arena")
    void getMostDamagedRobotEmptyArena() {
        Optional<Robot> result = arena.getMostDamagedRobot();
        assertFalse(result.isPresent());
    }

    // --- tick() Tests ---

    @Test
    @DisplayName("tick() increments the tick counter")
    void tickIncrementsCounter() {
        arena.addRobot(alpha);
        arena.tick();
        arena.tick();
        assertEquals(2, arena.getTickCount());
    }

    @Test
    @DisplayName("tick() returns a non-empty log")
    void tickReturnsLog() {
        arena.addRobot(alpha);
        List<String> log = arena.tick();
        assertFalse(log.isEmpty());
    }

    @Test
    @DisplayName("tick() removes dead robots automatically")
    void tickRemovesDeadRobots() {
        arena.addRobot(alpha);
        alpha.takeDamage(100); // Kill before tick
        arena.tick();
        assertEquals(0, arena.getRobots().size());
    }

    // --- getRobots() immutability test ---

    @Test
    @DisplayName("getRobots() returns an unmodifiable list")
    void getRobotsIsUnmodifiable() {
        arena.addRobot(alpha);
        List<Robot> robots = arena.getRobots();
        assertThrows(UnsupportedOperationException.class,
                () -> robots.add(bravo)
        );
    }
}