package org.pointsPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public final class PlayerData {
    public double health;
    public double foodLevel;
    public double armor;
    public String name;
    public UUID id;
    public boolean isConnected;
    public double points;
    public Location location;

    public PlayerData(
            double health,
            double foodLevel,
            double armor,
            String name,
            UUID id,
            boolean isConnected,
            Location location
    ) {
        this.health = health;
        this.foodLevel = foodLevel;
        this.armor = armor;
        this.name = name;
        this.id = id;
        this.isConnected = isConnected;
        this.location = location;
        this.points = calculatePoints();
    }
    
    
    private double calculatePoints() {
        double points = 0;

        if (isConnected) {
            points += foodLevel;
            points += armor;

            // we need to scale points based on health
            points = points * health / 20;
        }

        
        return Math.round(points * 100.0) / 100.0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PlayerData) obj;
        return Double.doubleToLongBits(this.health) == Double.doubleToLongBits(that.health) &&
                Double.doubleToLongBits(this.foodLevel) == Double.doubleToLongBits(that.foodLevel) &&
                Double.doubleToLongBits(this.armor) == Double.doubleToLongBits(that.armor) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.id, that.id) &&
                this.isConnected == that.isConnected &&
                Double.doubleToLongBits(this.points) == Double.doubleToLongBits(that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(health, foodLevel, armor, name, id, isConnected, points);
    }

    @Override
    public String toString() {
        return "PlayerData[" +
                "health=" + health + ", " +
                "foodLevel=" + foodLevel + ", " +
                "armor=" + armor + ", " +
                "name=" + name + ", " +
                "id=" + id + ", " +
                "isConnected=" + isConnected + ", " +
                "points=" + points + " " +
                "position: " + location + "]";
    }

}

