package miner82.bananosuite.classes;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.Location;

public class TeleportPremiumCalculator {

    public static double calculateTeleportCost(ConfigEngine configEngine, Location currentLocation, Location destination) {

        double distance = DistanceCalculator.calculateDistance(currentLocation, destination);

        if(distance > 0) {

            final double baseCost = configEngine.getTeleportBaseCost();
            final double growthRate = configEngine.getTeleportGrowthRate();
            final double costCap = configEngine.getTeleportMaximumCost();

            double teleportCost;

            try {

                teleportCost = baseCost * (java.lang.Math.pow(1 + growthRate, distance / 100));

            }
            catch (Exception e) {

                teleportCost = baseCost;

            }

            if(teleportCost > costCap) {

                teleportCost = costCap;

            }

            return Math.Round(teleportCost, 2);

        }

        return 0;

    }

}
