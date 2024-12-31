package org.pointsPlugin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PointPlugin extends JavaPlugin {

    private final List<PlayerData> playerStats = new ArrayList<>();
    private HttpServer server;
    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        try {
            // Register Bukkit event listener
            this.playerListener = new PlayerListener(this, new PointScoreboard());
            registerEvents();
            getLogger().info("PlayerListener registered successfully.");

            // Start the HTTP server
            startHttpServer();
            getLogger().info("HTTP Server started successfully.");

            // Register the update all players command
            if (getCommand("refresh") != null) {
                getCommand("refresh").setExecutor(new UpdateAllPlayersCommand(playerListener));
                getLogger().info("Command '/refresh' registered successfully.");
            } else {
                getLogger().severe("Command '/refresh' not found in plugin.yml!");
            }

            // Confirm successful plugin enablement
            getLogger().info("PointPlugin has been enabled!");
        } catch (Exception e) {
            // Log any errors to identify issues
            getLogger().severe("Failed to enable PointPlugin! Error: " + e.getMessage());
            e.printStackTrace();

            // Gracefully disable the plugin
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        stopHttpServer();
    }

    // Update player stats dynamically
    public synchronized void UpdatePlayerStats(PlayerData playerData) {
        for (int i = 0; i < playerStats.size(); i++) {
            if (playerStats.get(i).name.equals(playerData.name)) {
                playerStats.set(i, playerData);
                return;
            }
        }
        playerStats.add(playerData);
    }

    public synchronized void RemovePlayerStats(String name) {
        for (int i = 0; i < playerStats.size(); i++) {
            if (playerStats.get(i).name.equals(name)) {
                playerStats.remove(i);
                return;
            }
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        getLogger().info("PlayerListener registered successfully.");
    }

    private void startHttpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(25567), 0);
            server.createContext("/", new PlayerHandler());
            server.createContext("/stats", new StatsHandler());
            server.setExecutor(null); // Creates a default executor
            server.start();

            getLogger().info("HTTP server started on http://127.0.0.1:25567");
        } catch (IOException e) {
            getLogger().severe("Failed to start HTTP server.");
            e.printStackTrace();
        }
    }

    private void stopHttpServer() {
        if (server != null) {
            server.stop(0);
            getLogger().info("HTTP server stopped.");
        }
    }

    // Handler for serving the HTML page
    private static class PlayerHandler implements HttpHandler {
        private static String generateHtmlPage() {
            return """
                    <!DOCTYPE html>
<html>
<head>
    <title>Player Info</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .container {
            padding: 16px;
        }
        .player {
            border: 1px solid #ddd;
            margin-bottom: 10px;
            padding: 10px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            transition: transform 0.5s ease, opacity 0.5s ease;
            opacity: 1;
        }
        .player.sorting {
            transform: translateY(10px); /* Smooth movement for re-sorting */
            opacity: 0; /* Fading effect */
        }
        .player.new {
            opacity: 0;
            transform: translateY(-20px);
            animation: fadeIn 0.5s ease forwards;
        }
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Player Statistics</h1>
        <div id="updates"></div>
    </div>
    <script>
        const updatesContainer = document.getElementById('updates');

        // Poll server for player statistics and update the UI
        function pollServer() {
            fetch('/stats')
                .then(response => response.json())
                .then(data => {
                    // Sort players by points in descending order
                    data.sort((a, b) => b.points - a.points);

                    const existingPlayers = new Set(); // Track IDs of existing players

                    // Step 1: Capture initial positions
                    const positionsBefore = {};
                    Array.from(updatesContainer.children).forEach(playerDiv => {
                        positionsBefore[playerDiv.getAttribute('data-id')] =
                            playerDiv.getBoundingClientRect().top;
                    });

                    // Step 2: Add or update players and reorder DOM
                    data.forEach((player, index) => {
                        const playerId = player.id;
                        existingPlayers.add(playerId); // Mark player as existing

                        let playerDiv = updatesContainer.querySelector(`[data-id="${playerId}"]`);

                        if (!playerDiv) {
                            // If the player doesn't already exist, create a new one
                            playerDiv = document.createElement('div');
                            playerDiv.className = 'player new'; // Trigger fade-in animation
                            playerDiv.setAttribute('data-id', playerId);
                            updatesContainer.appendChild(playerDiv);
                        } else {
                            // Update existing players (trigger animation)
                            playerDiv.classList.remove('new'); // Remove fade-in class if not new
                        }

                        // Update player's content
                        playerDiv.innerHTML = `
                            <h3>${player.name}</h3>
                            <p>Points: ${player.points}</p>
                            <p>Health: ${player.health}</p>
                            <p>Food Level: ${player.foodLevel}</p>
                            <p>Armor: ${player.armor}</p>
                            <p>Connected: ${player.isConnected}</p>
                            ${
                                player.location
                                    ? `<p>Location: X=${player.location.x}, Y=${player.location.y}, Z=${player.location.z}</p>`
                                    : ''
                            }
                        `;

                        // Correct the order in the DOM
                        updatesContainer.appendChild(playerDiv);
                    });

                    // Step 3: Recalculate positions and animate the movement
                    const positionsAfter = {};
                    Array.from(updatesContainer.children).forEach(playerDiv => {
                        const playerId = playerDiv.getAttribute('data-id');
                        positionsAfter[playerId] = playerDiv.getBoundingClientRect().top;

                        // Animate the position change
                        const deltaY = (positionsBefore[playerId] || positionsAfter[playerId]) - positionsAfter[playerId];
                        playerDiv.style.transition = 'none'; // Disable current transition
                        playerDiv.style.transform = `translateY(${deltaY}px)`; // Apply initial delta
                        requestAnimationFrame(() => {
                            playerDiv.style.transition = 'transform 0.5s ease'; // Re-enable transition
                            playerDiv.style.transform = ''; // Reset transform to animate
                        });
                    });

                    // Step 4: Remove players not in the latest data
                    Array.from(updatesContainer.children).forEach(playerDiv => {
                        const playerId = playerDiv.getAttribute('data-id');
                        if (!existingPlayers.has(playerId)) {
                            playerDiv.classList.add('sorting'); // Trigger fade-out animation
                            setTimeout(() => playerDiv.remove(), 500); // Remove after animation ends
                        }
                    });
                })
                .catch(error => console.error('Error fetching stats:', error));
        }

        // Poll server for updates every 2 seconds
        setInterval(pollServer, 2000);
    </script>
</body>
</html>
                    """;
        }

        private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, byte[] response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = generateHtmlPage();
            sendResponse(exchange, 200, "text/html", response.getBytes());
        }
    }

    // Handler for sending player stats as JSON
    private class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = generatePlayerStatsJson();
                sendResponse(exchange, 200, "application/json", response.getBytes());
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }

        private String generatePlayerStatsJson() {
            StringBuilder json = new StringBuilder("[");
            synchronized (playerStats) {
                for (PlayerData playerData : playerStats) {
                    json.append("{")
                            .append("\"name\":\"").append(playerData.name).append("\",")
                            .append("\"id\":\"").append(playerData.id).append("\",")
                            .append("\"health\":").append(playerData.health).append(",")
                            .append("\"foodLevel\":").append(playerData.foodLevel).append(",")
                            .append("\"armor\":").append(playerData.armor).append(",")
                            .append("\"isConnected\":").append(playerData.isConnected).append(",")
                            .append("\"points\":").append(playerData.points).append(",");

                    // Include position (location)
                    if (playerData.location != null) {
                        json.append("\"location\":{")
                                .append("\"x\":").append(playerData.location.getX()).append(",")
                                .append("\"y\":").append(playerData.location.getY()).append(",")
                                .append("\"z\":").append(playerData.location.getZ()).append("},");
                    } else {
                        json.append("\"location\":null,");
                    }

                    // Remove last comma in the object's JSON structure
                    if (json.charAt(json.length() - 1) == ',') {
                        json.setLength(json.length() - 1);
                    }
                    json.append("},");
                }

                // Remove trailing comma from the array
                if (json.length() > 1 && json.charAt(json.length() - 1) == ',') {
                    json.setLength(json.length() - 1);
                }
            }
            json.append("]");
            return json.toString();
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String contentType, byte[] response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}