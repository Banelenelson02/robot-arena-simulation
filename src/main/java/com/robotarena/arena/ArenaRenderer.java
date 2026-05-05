package com.robotarena.arena;

import com.robotarena.model.Robot;

import java.util.List;

/**
 * Renders the Arena and its robots to the console.
 *
 * <h2>Single Responsibility Principle</h2>
 * <p>This class exists for ONE reason: turning Arena state into text on screen.
 * It does not manage robots, it does not run ticks, it does not validate
 * positions. If we later want to render to a file or a GUI instead of the
 * console, we create a new renderer — we do not touch Arena or Robot at all.
 *
 * <h2>Why separate from Arena?</h2>
 * <p>Consider the alternative: putting a {@code print()} method inside Arena.
 * That would mean Arena has two reasons to change — one if the game logic
 * changes, and one if the display format changes. Separating them means each
 * class changes for exactly one reason.
 *
 * <h2>Composition</h2>
 * <p>ArenaRenderer <strong>uses</strong> an Arena — it reads from it via
 * public getters. It does not extend Arena and it does not store a reference
 * to one as a field. It receives the Arena as a method parameter, making it
 * stateless and easy to test.
 */
public class ArenaRenderer {

    // Visual characters used to draw the grid
    private static final String EMPTY_CELL    = " . ";
    private static final String ROBOT_CELL    = " R ";
    private static final String MULTI_CELL    = " X "; // 2+ robots on same cell
    private static final String BORDER_CORNER = "+";
    private static final String BORDER_H      = "---";
    private static final String BORDER_V      = "|";

    /**
     * Renders the full arena grid with all robot positions marked.
     *
     * <p>Each cell is 3 characters wide. Robots are shown as {@code R},
     * cells with multiple robots as {@code X}, empty cells as {@code .}.
     * Robot IDs are printed in a legend below the grid.
     *
     * @param arena the arena to render (must not be null)
     */
    public void render(Arena arena) {
        System.out.println(renderToString(arena));
    }

    /**
     * Builds the full grid rendering as a String (useful for testing).
     *
     * @param arena the arena to render
     * @return the complete grid as a multi-line String
     */
    public String renderToString(Arena arena) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(arena).append("\n");
        sb.append(buildTopBorder(arena.getWidth())).append("\n");

        for (int y = 0; y < arena.getHeight(); y++) {
            sb.append(BORDER_V);
            for (int x = 0; x < arena.getWidth(); x++) {
                List<Robot> here = arena.getRobotsAt(new Position(x, y));
                if (here.isEmpty()) {
                    sb.append(EMPTY_CELL);
                } else if (here.size() == 1) {
                    sb.append(ROBOT_CELL);
                } else {
                    sb.append(MULTI_CELL);
                }
            }
            sb.append(BORDER_V).append("\n");
        }

        sb.append(buildTopBorder(arena.getWidth())).append("\n");
        sb.append(buildLegend(arena));

        return sb.toString();
    }

    /**
     * Prints the tick log returned by {@link Arena#tick()} to the console.
     *
     * @param tickLog the list of log lines from a tick
     */
    public void renderTickLog(List<String> tickLog) {
        System.out.println();
        for (String line : tickLog) {
            System.out.println("  " + line);
        }
    }

    /**
     * Prints a full status table of all robots in the arena.
     *
     * @param arena the arena whose robots to list
     */
    public void renderRobotStatus(Arena arena) {
        System.out.println("\n  === Robot Status ===");
        if (arena.getRobots().isEmpty()) {
            System.out.println("  (no robots in arena)");
            return;
        }
        for (Robot robot : arena.getRobots()) {
            System.out.println("  " + robot);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildTopBorder(int width) {
        StringBuilder sb = new StringBuilder(BORDER_CORNER);
        for (int i = 0; i < width; i++) {
            sb.append(BORDER_H);
        }
        sb.append(BORDER_CORNER);
        return sb.toString();
    }

    private String buildLegend(Arena arena) {
        StringBuilder sb = new StringBuilder("  Legend:\n");
        for (Robot robot : arena.getRobots()) {
            sb.append(String.format("    R = [%s] %s | %s%n",
                    robot.getId(),
                    robot.getName(),
                    robot.getStatusSummary()
            ));
        }
        return sb.toString();
    }
}
