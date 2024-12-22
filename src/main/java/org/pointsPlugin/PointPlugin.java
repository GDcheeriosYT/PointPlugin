package org.pointsPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class PointPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getLogger().info("enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("disabled");
    }
}