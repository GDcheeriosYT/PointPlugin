package org.pointsPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {
    private final PointPlugin plugin;
    private final PointScoreboard scoreboard;

    public PlayerListener(PointPlugin plugin, PointScoreboard scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
    }

    @EventHandler
    public void OnPlayerHurt(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) PublishPlayerData((Player) entityDamageEvent.getEntity());
    }

    @EventHandler
    public void OnPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) PublishPlayerData((Player) event.getEntity());
    }

    @EventHandler
    public void OnFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            PublishPlayerData(player);
        }
    }

    @EventHandler
    public void OnInventoryChange(InventoryPickupItemEvent event) {
        if (event.getInventory().getHolder() instanceof Player player) {
            PublishPlayerData(player);
        }
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        PublishPlayerData(event.getPlayer());
    }

    

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent event) {
        PublishPlayerData(event.getPlayer());
    }
    
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        DisconnectPlayer(event.getPlayer());
    }


    private void highlightPlayer(Player player) {
        PotionEffect highlight = new PotionEffect(
                PotionEffectType.GLOWING,
                Integer.MAX_VALUE,
                0,
                false,
                false
        );

        player.addPotionEffect(highlight);
    }

    public void PublishPlayerData(Player player) {
        PlayerData playerData = getPlayerData(player);
        highlightPlayer(player);
        plugin.UpdatePlayerStats(playerData);
        scoreboard.setPoint(player, (int) playerData.points);
    }

    public void DisconnectPlayer(Player player) {
        plugin.RemovePlayerStats(player.getName());
        scoreboard.setPoint(player, 0);
    }

    private double calculateArmorPoints(Player player) {
        double armorPoints = 0;

        // Get the armor contents: boots, leggings, chestplate, helmet
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece != null) {
                Material type = armorPiece.getType();

                // Assign armor points based on armor type
                switch (type) {
                    case DIAMOND_HELMET:
                        armorPoints += 3;
                        break;
                    case DIAMOND_CHESTPLATE:
                        armorPoints += 8;
                        break;
                    case DIAMOND_LEGGINGS:
                        armorPoints += 6;
                        break;
                    case DIAMOND_BOOTS:
                        armorPoints += 3;
                        break;

                    case IRON_HELMET:
                        armorPoints += 2;
                        break;
                    case IRON_CHESTPLATE:
                        armorPoints += 6;
                        break;
                    case IRON_LEGGINGS:
                        armorPoints += 5;
                        break;
                    case IRON_BOOTS:
                        armorPoints += 2;
                        break;

                    case CHAINMAIL_HELMET:
                        armorPoints += 2;
                        break;
                    case CHAINMAIL_CHESTPLATE:
                        armorPoints += 5;
                        break;
                    case CHAINMAIL_LEGGINGS:
                        armorPoints += 4;
                        break;
                    case CHAINMAIL_BOOTS:
                        armorPoints += 1;
                        break;

                    case GOLDEN_HELMET:
                        armorPoints += 2;
                        break;
                    case GOLDEN_CHESTPLATE:
                        armorPoints += 5;
                        break;
                    case GOLDEN_LEGGINGS:
                        armorPoints += 3;
                        break;
                    case GOLDEN_BOOTS:
                        armorPoints += 1;
                        break;

                    case LEATHER_HELMET:
                        armorPoints += 1;
                        break;
                    case LEATHER_CHESTPLATE:
                        armorPoints += 3;
                        break;
                    case LEATHER_LEGGINGS:
                        armorPoints += 2;
                        break;
                    case LEATHER_BOOTS:
                        armorPoints += 1;
                        break;

                    default:
                        // No points for other materials or empty slots
                        break;
                }
            }
        }

        return armorPoints; // Total armor points
    }

    private PlayerData getPlayerData(Player player) {
        return new PlayerData(
                player.getHealth(),
                player.getFoodLevel(),
                calculateArmorPoints(player),
                player.getName(),
                player.getUniqueId(),
                player.isOnline(),
                player.getLocation()
        );
    }
}
