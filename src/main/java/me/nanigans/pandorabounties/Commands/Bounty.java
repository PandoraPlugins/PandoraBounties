package me.nanigans.pandorabounties.Commands;

import me.nanigans.pandorabounties.Inventories.BountyInventory;
import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.Config.Config;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Bounty implements CommandExecutor {
    private final PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("bounty")){

            if(sender instanceof Player){

                Player player = (Player) sender;

                switch (args.length) {
                    case 0:{

                        new BountyInventory(player);

                        return true;
                    }
                    case 2:{

                        OfflinePlayer bounty = Bukkit.getOfflinePlayer(args[0]);
                        if(bounty.getPlayer() != null && bounty.hasPlayedBefore()){

                            if(NumberUtils.isNumber(args[1])){

                                double number = Double.parseDouble(args[1]);
                                Config.setPlayerBounty(player, bounty, number);
                                return true;

                            }else{
                                player.sendMessage(ChatColor.RED+"Please enter a valid number");
                                return false;
                            }

                        }else{
                            player.sendMessage(ChatColor.RED+"Could not find this player");
                            return true;
                        }

                    }
                    default:{
                        player.sendMessage(ChatColor.RED+"Please specify who to place a bounty on and the amount");
                        return false;
                    }
                }


            }

        }

        return false;
    }
}
