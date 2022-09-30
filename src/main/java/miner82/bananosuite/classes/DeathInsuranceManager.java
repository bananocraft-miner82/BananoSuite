package miner82.bananosuite.classes;

import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.interfaces.IDBConnection;

import java.time.*;

public class DeathInsuranceManager {

    public static double CalculateNextPremium(IDBConnection db, ConfigEngine configEngine, PlayerRecord playerRecord, DeathInsuranceOption insuranceOption) {

        if(insuranceOption == DeathInsuranceOption.None) {
            return 0;
        }

        double last24HrUses = db.getPlayerDIUseCountInLast24Hours(playerRecord.getUUID());

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
            else if(db.getPlayerDIUseCountSinceDate(playerRecord.getUUID(), DateCalculator.GetDateOfLastDayOfWeek(DayOfWeek.SUNDAY)) == 0
                    && playerRecord.getPlayerRank().getPerks()) {

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
