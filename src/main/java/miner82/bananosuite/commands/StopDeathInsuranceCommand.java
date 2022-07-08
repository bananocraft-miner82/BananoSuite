package miner82.bananosuite.commands;

import miner82.bananosuite.DB;
import miner82.bananosuite.classes.DeathInsuranceOption;
import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopDeathInsuranceCommand extends BaseCommand implements CommandExecutor {

    private ConfigEngine configEngine;

    public StopDeathInsuranceCommand(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(DB.getPlayerDeathInsurance(player) != DeathInsuranceOption.None) {

            DB.setPlayerDeathInsuranceOption(player, DeathInsuranceOption.None);

            SendMessage(player, "Your Death Insurance policy has been terminated.", ChatColor.GREEN);

        }
        else {

            SendMessage(player, "You do not have an active Death Insurance policy to terminate.", ChatColor.GOLD);

        }

        return true;

    }

}
