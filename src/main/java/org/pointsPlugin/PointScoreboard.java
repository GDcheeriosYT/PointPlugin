package org.pointsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PointScoreboard {

    private final org.bukkit.scoreboard.ScoreboardManager manager;
    private final Scoreboard board;
    private final Objective objective;

    public PointScoreboard() {
        // Initialize the server's scoreboard manager
        this.manager = Bukkit.getScoreboardManager();

        // Create a new scoreboard
        assert manager != null;
        this.board = manager.getNewScoreboard();

        // Register an Objective if it doesn't exist
        this.objective = board.registerNewObjective("points", "dummy", "Player Points");
        this.objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR); // Display on the sidebar
    }

    // Method to set points for a specific player
    public void setPoint(Player player, int points) {
        // Retrieve the player's score on the scoreboard
        Score score = objective.getScore(player.getName());

        // Update the score with the new points value
        score.setScore(points);

        // Assign the updated scoreboard to the player
        player.setScoreboard(board);
    }
}