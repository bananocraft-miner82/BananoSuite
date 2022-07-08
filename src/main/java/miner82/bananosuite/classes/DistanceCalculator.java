package miner82.bananosuite.classes;

import org.bukkit.Location;

public class DistanceCalculator {

    public static double calculateDistance(Location fromLocation, Location toLocation) {

        // d = ((x2 - x1)2 + (y2 - y1)2 + (z2 - z1)2)1/2

        return java.lang.Math.pow(
                                   java.lang.Math.pow(toLocation.getBlockX() - fromLocation.getBlockX(), 2)
                                    + java.lang.Math.pow(toLocation.getBlockY() - fromLocation.getBlockY(), 2)
                                    + java.lang.Math.pow(toLocation.getBlockZ() - fromLocation.getBlockZ(), 2),
                                   0.5);

    }

}
