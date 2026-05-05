package com.robotarena.model;

import com.robotarena.arena.Position;

/**
 * Represents a single robot in the Arena simulation.
 *
 * <h2>Encapsulation in Practice</h2>
 * <p>Every field is {@code private}. No external class can reach in and set
 * {@code robot.health = -999}. All state changes flow through public methods
 * that enforce the class's rules (invariants):
 * <ul>
 *   <li>Health is always between 0 and maxHealth (inclusive)</li>
 *   <li>A robot with 0 health is always marked as not alive</li>
 *   <li>A dead robot cannot be damaged further or repaired</li>
 * </ul>
 *
 * <h2>Class Responsibility</h2>
 * <p>This class is responsible for ONE thing: managing the state of a single
 * robot. It does not know about the Arena, other robots, or the display.
 * That separation is intentional and is called the
 * <em>Single Responsibility Principle</em>.
 *
 * <h2>Object Identity vs Equality</h2>
 * <p>Two Robot objects are considered the same robot if they share the same
 * {@code id}. This models the real-world idea that a robot is identified by
 * its serial number, not by where it happens to live in memory.
 */
public class Robot {

    // -------------------------------------------------------------------------
    // Fields — all private. This IS encapsulation.
    // -------------------------------------------------------------------------

    /** Unique identifier for this robot. Used to determine equality. */
    private final String id;

    /** Display name shown in the simulation output. */
    private final String name;

    /** Current health points. Always between 0 and maxHealth. */
    private int health;

    /** The ceiling on health. A robot cannot be repaired above this value. */
    private final int maxHealth;

    /** This robot's location on the Arena grid. */
    private Position position;

    /** Tracks whether this robot can still participate in the simulation. */
    private boolean alive;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new Robot at full health at the given position.
     *
     * @param id        unique identifier (e.g. "R001")
     * @param name      display name (e.g. "Ironclad")
     * @param maxHealth maximum and starting health points (must be > 0)
     * @param position  starting position on the grid (must not be null)
     * @throws IllegalArgumentException if maxHealth is not positive
     * @throws NullPointerException     if position or id or name is null
     */
    public Robot(String id, String name, int maxHealth, Position position) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Robot id must not be null or blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Robot name must not be null or blank.");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException(
                    "maxHealth must be positive, but got: " + maxHealth
            );
        }
        if (position == null) {
            throw new NullPointerException("Position must not be null.");
        }

        this.id        = id;
        this.name      = name;
        this.maxHealth = maxHealth;
        this.health    = maxHealth; // Robots start at full health
        this.position  = position;
        this.alive     = true;
    }

    // -------------------------------------------------------------------------
    // Core Behaviour Methods
    // These are the "verbs" of the Robot class — what it can DO.
    // -------------------------------------------------------------------------

    /**
     * Applies damage to this robot.
     *
     * <p>This is the <strong>only</strong> public way to reduce a robot's
     * health. The method enforces all rules internally:
     * <ul>
     *   <li>Negative damage values are ignored (cannot heal via damage)</li>
     *   <li>Health cannot drop below 0</li>
     *   <li>If health reaches 0, the robot is marked as dead</li>
     * </ul>
     *
     * <p><strong>Design note:</strong> Callers do not set health directly.
     * They say "take 30 damage" and trust this class to handle it correctly.
     * This is the essence of encapsulation — hiding the <em>how</em>.
     *
     * @param amount the damage to apply (negative values are ignored)
     */
    public void takeDamage(int amount) {
        if (!alive) {
            return; // Dead robots cannot be damaged further
        }
        if (amount <= 0) {
            return; // Ignore zero or negative damage values
        }

        this.health = Math.max(0, this.health - amount);

        if (this.health == 0) {
            this.alive = false;
        }
    }

    /**
     * Repairs this robot, restoring some health.
     *
     * <p>Rules:
     * <ul>
     *   <li>A dead robot cannot be repaired</li>
     *   <li>Health cannot exceed maxHealth</li>
     *   <li>Negative repair values are ignored</li>
     * </ul>
     *
     * @param amount the health points to restore (negative values are ignored)
     */
    public void repair(int amount) {
        if (!alive) {
            return; // Dead robots cannot be repaired
        }
        if (amount <= 0) {
            return;
        }

        this.health = Math.min(maxHealth, this.health + amount);
    }

    /**
     * Moves this robot to a new position on the grid.
     *
     * <p>The Arena is responsible for validating that the position is within
     * bounds before calling this method. The robot itself just accepts the move.
     *
     * @param newPosition the target position (must not be null)
     * @throws NullPointerException if newPosition is null
     */
    public void moveTo(Position newPosition) {
        if (newPosition == null) {
            throw new NullPointerException("Cannot move to a null position.");
        }
        this.position = newPosition;
    }

    // -------------------------------------------------------------------------
    // Getters — controlled read access to private state
    // -------------------------------------------------------------------------

    /** @return this robot's unique identifier */
    public String getId() {
        return id;
    }

    /** @return this robot's display name */
    public String getName() {
        return name;
    }

    /** @return the current health points */
    public int getHealth() {
        return health;
    }

    /** @return the maximum possible health for this robot */
    public int getMaxHealth() {
        return maxHealth;
    }

    /** @return this robot's current position on the grid */
    public Position getPosition() {
        return position;
    }

    /** @return {@code true} if this robot is still active in the simulation */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Returns the health as a percentage of maximum health.
     * Useful for the MedicBot to find the most damaged ally.
     *
     * @return health percentage as a value between 0.0 and 100.0
     */
    public double getHealthPercentage() {
        return ((double) health / maxHealth) * 100.0;
    }

    // -------------------------------------------------------------------------
    // Object contract overrides
    // -------------------------------------------------------------------------

    /**
     * Two robots are equal if and only if they share the same {@code id}.
     *
     * <p><strong>OOP Note:</strong> This separates <em>object identity</em>
     * (same reference in memory) from <em>object equality</em> (same logical
     * robot). In a simulation, the same robot might be rebuilt from a save
     * file into a different memory address — it's still the same robot.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Robot other = (Robot) obj;
        return this.id.equals(other.id);
    }

    /**
     * Consistent with equals(): robots with the same id get the same hash code.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a formatted summary of this robot's current state.
     *
     * <p>Example: {@code [R001] Ironclad | HP: 70/100 (70.0%) | Pos: (3, 5) | ALIVE}
     */
    @Override
    public String toString() {
        String status = alive ? "ALIVE" : "DESTROYED";
        return String.format(
                "[%s] %s | HP: %d/%d (%.1f%%) | Pos: %s | %s",
                id, name, health, maxHealth, getHealthPercentage(), position, status
        );
    }
}