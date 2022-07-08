package miner82.bananosuite.classes;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;

public class DeathInsurancePremiumCalculator {

    public static double CalculateNextPremium(ConfigEngine configEngine, Player player, DeathInsuranceOption insuranceOption) {

        double last24HrUses = DB.getPlayerPremiumUseCountInLast24Hours(player);

        if(insuranceOption == DeathInsuranceOption.None) {
            return 0;
        }

        final double baseCost = configEngine.getBaseDeathInsurancePremium();
        final double growthRate = configEngine.getDeathInsuranceGrowthRate();
        final double costCap = configEngine.getDeathInsuranceMaximumCost();

        double premium = baseCost;

        try {
            if(last24HrUses > 0) {

                premium = baseCost * (java.lang.Math.pow(1 + growthRate, last24HrUses));

            }
            else if(DB.getPlayerPremiumUseCountSinceDate(player, DateCalculator.GetDateOfLastDayOfWeek(DayOfWeek.SUNDAY)) == 0
                  && DB.getPlayerIsCitizen(player)) {

                // The first death per week is free if the player is a citizen.
                return 0;

            }

        }
        catch (Exception e) {

        }

        if(premium > costCap) {

            premium = costCap;

        }

        if(insuranceOption == DeathInsuranceOption.Inventory) {
            premium *= 0.75;
        }

        return Math.Round(premium, 2);

    }
}
