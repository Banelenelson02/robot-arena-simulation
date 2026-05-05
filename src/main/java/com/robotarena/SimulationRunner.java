package com.robotarena;

import com.robotarena.arena.Arena;
import com.robotarena.arena.ArenaRenderer;
import com.robotarena.arena.Position;
import com.robotarena.model.Robot;

/**
 * The entry point for the Robot Arena Simulation.
 *
 * <p>This class wires together all the components and runs a short demo.
 * Its only job is bootstrapping — creating objects and starting the loop.
 * All logic lives in Arena, Robot, and ArenaRenderer.
 *
 * <p>In Phase 5 this will read config from a file or CLI arguments.
 * For now, it hardcodes a demo scenario so we can see everything working.
 */
public class SimulationRunner {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("     ROBOT ARENA SIMULATION — Phase 2     ");
        System.out.println("===========================================");

        // --- Build the arena (10 columns x 8 rows) ---
        Arena arena = new Arena(10, 8);
        ArenaRenderer renderer = new ArenaRenderer();

        // --- Create robots and place them on the grid ---
        Robot alpha   = new Robot("R001", "Alpha",   100, new Position(1, 1));
        Robot bravo   = new Robot("R002", "Bravo",    80, new Position(7, 1));
        Robot charlie = new Robot("R003", "Charlie",  60, new Position(4, 4));
        Robot delta   = new Robot("R004", "Delta",   120, new Position(8, 6));

        arena.addRobot(alpha);
        arena.addRobot(bravo);
        arena.addRobot(charlie);
        arena.addRobot(delta);

        // --- Show initial state ---
        System.out.println("\nInitial Arena State:");
        renderer.render(arena);
        renderer.renderRobotStatus(arena);

        // --- Simulate damage and repair (demonstrating Robot messaging) ---
        System.out.println("\n  >> Simulating combat exchange...");
        alpha.takeDamage(35);
        bravo.takeDamage(60);
        charlie.repair(20);   // Charlie was damaged before entering arena
        delta.takeDamage(110); // Delta is destroyed

        // --- Run 3 ticks ---
        for (int i = 0; i < 3; i++) {
            renderer.renderTickLog(arena.tick());
        }

        // --- Show final state ---
        System.out.println("\nFinal Arena State (Delta removed — destroyed):");
        renderer.render(arena);
        renderer.renderRobotStatus(arena);

        System.out.println("\n===========================================");
        System.out.println("           Simulation complete.            ");
        System.out.println("===========================================");
    }
}