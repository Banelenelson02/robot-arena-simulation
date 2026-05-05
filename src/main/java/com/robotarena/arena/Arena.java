package com.robotarena.arena;

import com.robotarena.model.Robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the grid-based arena in which robots operate.
 *
 * <h2>Composition in Practice</h2>
 * <p>An Arena <strong>has</strong> robots — it does not extend Robot or
 * pretend to be one. This "has-a" relationship is called <em>composition</em>.
 * The Arena owns the list; it controls who gets added, who gets removed,
 * and who gets a turn. The robots themselves know nothing about the Arena
 * unless we explicitly pass it to them.
 *
 * <h2>Single Responsibility</h2>
 * <p>This class is responsible for <strong>grid state management only</strong>:
 * <ul>
 *   <li>Tracking which robots are present and where</li>
 *   <li>Validating grid boundaries</li>
 *   <li>Advancing the simulation one tick at a time</li>
 * </ul>
 * It does NOT render output (that is ArenaRenderer's job) and it does NOT
 * decide how a robot behaves on its turn (that will be each robot's job
 * in Phase 3 via {@code performAction()}).
 *
 * <h2>Messages and Methods</h2>
 * <p>When {@code tick()} runs, the Arena sends a message to each robot.
 * In Phase 2 that message is simple ("log your status"). In Phase 3 it
 * becomes {@code robot.performAction(this)} — the Arena still sends the
 * same message; each robot type responds differently. That difference in
 * response is polymorphism.
 */
public class Arena {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Number of columns on the grid. */
    private final int width;

    /** Number of rows on the grid. */
    private final int height;

    /**
     * The robots currently registered in this arena.
     *
     * <p>This list is the core of the Arena-Robot composition relationship.
     * It is private — external code cannot reach in and add or remove robots
     * directly. All mutations go through {@code addRobot()} and
     * {@code removeRobot()}, which enforce the arena's rules.
     */
    private final List<Robot> robots;

    /** Tracks how many simulation steps have elapsed. */
    private int tickCount;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new Arena with the given dimensions.
     *
     * @param width  the number of columns (must be >= 2)
     * @param height the number of rows (must be >= 2)
     * @throws IllegalArgumentException if width or height is less than 2
     */
    public Arena(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException(
                    "Arena dimensions must be at least 2x2. Got: " + width + "x" + height
            );
        }
        this.width     = width;
        this.height    = height;
        this.robots    = new ArrayList<>();
        this.tickCount = 0;
    }

    // -------------------------------------------------------------------------
    // Robot Management — controlled mutation of the composition
    // -------------------------------------------------------------------------

    /**
     * Adds a robot to the arena.
     *
     * <p>Rules enforced:
     * <ul>
     *   <li>The robot must not be null</li>
     *   <li>The robot's starting position must be within grid bounds</li>
     *   <li>No two robots may share the same ID</li>
     * </ul>
     *
     * @param robot the robot to add
     * @throws IllegalArgumentException if position is out of bounds or ID is duplicate
     * @throws NullPointerException     if robot is null
     */
    public void addRobot(Robot robot) {
        if (robot == null) {
            throw new NullPointerException("Cannot add a null robot to the arena.");
        }
        if (!isPositionValid(robot.getPosition())) {
            throw new IllegalArgumentException(
                    "Robot '" + robot.getName() + "' has an out-of-bounds starting position: "
                            + robot.getPosition() + ". Arena is " + width + "x" + height + "."
            );
        }
        if (findRobotById(robot.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "A robot with ID '" + robot.getId() + "' is already in the arena."
            );
        }
        robots.add(robot);
    }

    /**
     * Removes a robot from the arena by its ID.
     *
     * @param robotId the ID of the robot to remove
     * @return {@code true} if the robot was found and removed, {@code false} otherwise
     */
    public boolean removeRobot(String robotId) {
        return robots.removeIf(r -> r.getId().equals(robotId));
    }

    /**
     * Removes all robots that are no longer alive from the arena.
     *
     * <p>Called at the end of each tick to clean up destroyed robots.
     */
    public void removeDeadRobots() {
        robots.removeIf(r -> !r.isAlive());
    }

    // -------------------------------------------------------------------------
    // Simulation — the tick loop
    // -------------------------------------------------------------------------

    /**
     * Advances the simulation by one step.
     *
     * <p>In Phase 2 each robot simply announces its status — the Arena sends
     * each robot a message and reports back. In Phase 3 this will call
     * {@code robot.performAction(this)}, and each robot subclass will respond
     * with its own behaviour. The Arena's tick() code will not change at all —
     * that stability under extension is the point of polymorphism.
     *
     * @return a log of what happened this tick, one line per robot
     */
    public List<String> tick() {
        tickCount++;
        List<String> log = new ArrayList<>();
        log.add("--- Tick " + tickCount + " ---");

        for (Robot robot : robots) {
            if (robot.isAlive()) {
                // Phase 2: send a simple status message to each robot.
                // Phase 3: replace this line with robot.performAction(this)
                String entry = robot.getName() + " holds position at "
                        + robot.getPosition() + ". " + robot.getStatusSummary();
                log.add(entry);
            }
        }

        removeDeadRobots();
        log.add("Active robots remaining: " + robots.size());
        return log;
    }

    // -------------------------------------------------------------------------
    // Querying — read-only access to arena state
    // -------------------------------------------------------------------------

    /**
     * Checks whether a given position lies within the arena boundaries.
     *
     * @param position the position to check
     * @return {@code true} if the position is on the grid
     */
    public boolean isPositionValid(Position position) {
        if (position == null) return false;
        return position.getX() >= 0
                && position.getX() < width
                && position.getY() >= 0
                && position.getY() < height;
    }

    /**
     * Returns all robots currently at the given position.
     *
     * <p>In normal play this returns 0 or 1 robot. A list is returned
     * because collision rules (two robots landing on the same cell) are
     * handled in Phase 3.
     *
     * @param position the grid cell to inspect
     * @return a list of robots at that position (may be empty, never null)
     */
    public List<Robot> getRobotsAt(Position position) {
        List<Robot> result = new ArrayList<>();
        for (Robot robot : robots) {
            if (robot.getPosition().equals(position)) {
                result.add(robot);
            }
        }
        return result;
    }

    /**
     * Finds a robot by its unique ID.
     *
     * <p>Returns an {@link Optional} — the caller must handle the case where
     * the robot is not found, rather than risking a NullPointerException.
     *
     * @param id the robot ID to search for
     * @return an Optional containing the robot, or empty if not found
     */
    public Optional<Robot> findRobotById(String id) {
        for (Robot robot : robots) {
            if (robot.getId().equals(id)) {
                return Optional.of(robot);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the robot with the lowest health percentage currently alive.
     *
     * <p>Used by MedicBot in Phase 3 to find the most damaged ally.
     *
     * @return an Optional containing the most damaged robot, or empty if
     *         no robots are present
     */
    public Optional<Robot> getMostDamagedRobot() {
        return robots.stream()
                .filter(Robot::isAlive)
                .min((a, b) -> Double.compare(
                        a.getHealthPercentage(),
                        b.getHealthPercentage()
                ));
    }

    /**
     * Returns an unmodifiable view of all robots in the arena.
     *
     * <p>Callers can read the list but cannot add or remove from it directly.
     * This protects the arena's internal state while still allowing read access.
     *
     * @return an unmodifiable list of all robots
     */
    public List<Robot> getRobots() {
        return Collections.unmodifiableList(robots);
    }

    /**
     * Returns the number of robots currently alive in the arena.
     *
     * @return count of living robots
     */
    public int getAliveRobotCount() {
        int count = 0;
        for (Robot robot : robots) {
            if (robot.isAlive()) count++;
        }
        return count;
    }

    /** @return the arena width in columns */
    public int getWidth()  { return width; }

    /** @return the arena height in rows */
    public int getHeight() { return height; }

    /** @return the number of ticks that have elapsed */
    public int getTickCount() { return tickCount; }

    /**
     * Returns a short summary of the arena's current state.
     *
     * <p>Example: {@code Arena[10x8 | Tick: 3 | Robots alive: 4]}
     */
    @Override
    public String toString() {
        return String.format("Arena[%dx%d | Tick: %d | Robots alive: %d]",
                width, height, tickCount, getAliveRobotCount());
    }
}
