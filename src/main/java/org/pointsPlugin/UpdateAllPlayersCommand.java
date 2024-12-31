package org.pointsPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateAllPlayersCommand implements CommandExecutor {
    private final PlayerListener playerListener;

    public UpdateAllPlayersCommand(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("refresh")) {
            playerListener.PublishAllPlayersData();
            sender.sendMessage("Updated data for all online players.");
            return true;
        }

        return false;
    }
}