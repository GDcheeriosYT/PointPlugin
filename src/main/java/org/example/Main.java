package org.example;

public class Main {
    public static void main(String[] args) {
        PointPlugin plugin = new PointPlugin();
        System.out.println(plugin.getDescription().getVersion());
    }
}