package org.example;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event){
        event.getPlayer().sendMessage("You moved!");
    }
}
