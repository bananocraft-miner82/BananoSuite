package miner82.bananosuite.commands.tabcompleters;

import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.classes.MonKeyType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MonKeyMapTabCompleter implements TabCompleter {

    private ConfigEngine configEngine;

    public MonKeyMapTabCompleter(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> results = new ArrayList<>();

        if(args.length == 1) {

            results.add("buy");
            results.add("collect");
            results.add("query");
            results.add("retry");
            results.add("help");

        }
        else if(args.length > 1
                  && args[0].equalsIgnoreCase("buy")) {

            if (args.length == 2) {

                results.add(MonKeyType.MonKey.toString());
                results.add(MonKeyType.QRCode.toString());

            } else if (args.length == 3
                    && args[2] == null || args[2] == "") {

                results.add("ban_");

            } else if (args.length == 4
                    && args[1].equalsIgnoreCase(MonKeyType.MonKey.toString())
                    && this.configEngine.getApplyFrame().length() > 0) {

                results.add("none");

                for (String key : this.configEngine.getAvailableFrames().keySet()) {

                    results.add(key);

                }

            }

        }

        return StringUtil.copyPartialMatches(args[args.length - 1], results, new ArrayList<>());
    }
}