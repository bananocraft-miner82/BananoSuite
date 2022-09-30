package miner82.bananosuite.commands;

import miner82.bananosuite.configuration.ConfigEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableCommand extends BaseCommand implements CommandExecutor {

    private final ConfigEngine configEngine;

    public EnableCommand(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if(!CanCommand(sender)) {

            return false;

        }

        if(sender instanceof Player) {

            player = (Player) sender;

        }

        if(args.length != 2
             && !(args.length == 1 && args[0].equalsIgnoreCase("reloadconfig"))) {

            SendMessage(player, "Incorrect arguments. Expected 2, received " + args.length, ChatColor.RED);

            return false;

        }
        else if(!args[0].equalsIgnoreCase("reloadconfig")
                 && !args[1].equalsIgnoreCase("enable")
                 && !args[1].equalsIgnoreCase("disable")
                 && !args[1].equalsIgnoreCase("status")) {

            SendMessage(player, "Invalid argument. Valid values are 'enable' and 'disable'.", ChatColor.RED);

            return false;

        }

        // Apply the setting
        try {

            if(args[0].equalsIgnoreCase("reloadconfig")) {

                SendMessage(player, "Configuration reload command acknowledged.", ChatColor.GREEN);
                this.configEngine.reload();

            }
            else if(args[1].equalsIgnoreCase("status")) {

                if (args[0].equalsIgnoreCase("deathinsurance")) {

                    SendMessage(player, "Death insurance is currently " + getEnabledOrDisabled(this.configEngine.getDeathInsuranceEnabled()) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("donate")) {

                    SendMessage(player, "The Donate command is currently " + getEnabledOrDisabled(this.configEngine.getDonateCommandIsEnabled()) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("monkeymaps")) {

                    SendMessage(player, "The MonKeyMaps command is currently " + getEnabledOrDisabled(this.configEngine.getMonkeyMapsEnabled()) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("pvptoggle")) {

                    SendMessage(player, "The PvP Toggle command is currently " + getEnabledOrDisabled(this.configEngine.getPvpToggleEnabled()) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("rain")) {

                    SendMessage(player, "The Rain command is currently " + getEnabledOrDisabled(this.configEngine.getRainEnabled()) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("teleport")) {

                    SendMessage(player, "The Teleport commands are currently " + getEnabledOrDisabled(this.configEngine.getTeleportEnabled()) + ".", ChatColor.GREEN);

                } else {

                    SendMessage(player, "The command '" + args[0] + "' could not be identified.", ChatColor.RED);

                }

            }
            else {

                boolean enabled = args[1].equalsIgnoreCase("enable");

                if (args[0].equalsIgnoreCase("deathinsurance")) {

                    this.configEngine.setDeathInsuranceEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "Death insurance is now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("donate")) {

                    this.configEngine.setDonateCommandIsEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "The Donate command is now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("monkeymaps")) {

                    this.configEngine.setMonkeyMapsEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "The MonKeyMaps command is now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("pvptoggle")) {

                    this.configEngine.setPvpToggleEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "The PvP Toggle command is now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("rain")) {

                    this.configEngine.setRainEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "The Rain command is now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else if (args[0].equalsIgnoreCase("teleport")) {

                    this.configEngine.setTeleportEnabled(enabled);
                    this.configEngine.save();

                    SendMessage(player, "The Teleport commands are now " + getEnabledOrDisabled(enabled) + ".", ChatColor.GREEN);

                } else {

                    SendMessage(player, "The command '" + args[0] + "' could not be identified.", ChatColor.RED);
                    return false;

                }

            }

        }
        catch (Exception e) {

            System.out.println("An error occurred while attempting to enable/disable the specified command: ");
            e.printStackTrace();

        }

        return true;

    }

    private String getEnabledOrDisabled(boolean enabled) {
        if(enabled) {
            return "enabled";
        }
        else {
            return "disabled";
        }
    }

}
