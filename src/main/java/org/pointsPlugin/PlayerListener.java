package org.pointsPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event) {
        event.getPlayer().sendMessage("You moved!");
    }

    private PlayerData getPlayerData(Player player) {
        return new PlayerData(
                player.getHealth(),
                player.getFoodLevel(),
                calculateArmorPoints(player)
        );
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

    private double getPoints(Player player) {
        double points = 0;

        points += player.getHealth();
        points *= 1 + (double) player.getFoodLevel() / 20;
        points *= 1 + (double) calculateArmorPoints(player) / 20;

        // we need to scale points based on health
        points = points * player.getHealth() / 20;

        return points;
    }
}
