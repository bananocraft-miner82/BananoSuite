package miner82.bananosuite.commands.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandTabCompleter implements TabCompleter{
    private ConfigEngine configEngine;

    public AdminCommandTabCompleter(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(sender instanceof Player) {

            Player player = (Player) sender;

            if(!player.isOp()
                && !player.hasPermission(command.getPermission())) {

                return results;

            }

        }

        if(args.length == 1) {

            results.add("deathinsurance");
            results.add("donate");
            results.add("monkeymaps");
            results.add("pvptoggle");
            results.add("rain");
            results.add("teleport");
            results.add("reloadconfig");

        }
        else if(args.length == 2
                 && !args[0].equalsIgnoreCase("reloadconfig")) {

            results.add("enable");
            results.add("disable");
            results.add("status");

            if(args[0].equalsIgnoreCase("deathinsurance")) {

                results.add("setbaserate");

            }
            else if(args[0].equalsIgnoreCase("monkeymaps")) {

                results.add("setmmprice");
                results.add("setqrprice");
                results.add("reload");

            }

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}