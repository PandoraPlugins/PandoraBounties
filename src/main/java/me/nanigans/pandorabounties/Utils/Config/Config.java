package me.nanigans.pandorabounties.Utils.Config;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.JsonUtils;
import net.ess3.api.MaxMoneyException;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {


    /**
     * Sets a players bounty on another player.
     * @param playerSet the player putting a bounty on another player
     * @param bounty the player being put a bounty on
     * @param amount the amount to put the bounty
     */
    public static void setPlayerBounty(Player playerSet, OfflinePlayer bounty, double amount) {

        PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

        final YamlGenerator yaml = new YamlGenerator(plugin.path+"/"+bounty.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();

        List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
        bounties = bounties == null ? new ArrayList<>() : bounties;
        final Map<String, Object> setBy = yamlContainsPlayer(playerSet, yaml);
        if(setBy != null){
            if(amount > 0) {
                setBy.put("amount", amount);
                playerSet.sendMessage(JsonUtils.getData("messages.private.bountyAdd")
                        .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", String.valueOf(amount)));
                playerSet.sendMessage(JsonUtils.getData("messages.global.bountyAdd")
                        .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", String.valueOf(amount))
                        .replaceAll("\\{bounty_player}", bounty.getName()));

            }else if(amount == 0){
                bounties.remove(setBy);
                playerSet.sendMessage(JsonUtils.getData("messages.private.bountyRemove")
                        .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", setBy.get("amount").toString()));
                playerSet.sendMessage(JsonUtils.getData("messages.global.bountyRemove")
                        .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", setBy.get("amount").toString())
                        .replaceAll("\\{bounty_player}", bounty.getName()));

            }
        }else{
            Map<String, Object> newBounty = new HashMap<>();
            newBounty.put("setBy", playerSet.getUniqueId().toString());
            newBounty.put("amount", amount);
            bounties.add(newBounty);
            playerSet.sendMessage(JsonUtils.getData("messages.private.bountyAdd")
                    .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", String.valueOf(amount)));
            playerSet.sendMessage(JsonUtils.getData("messages.global.bountyAdd")
                    .replaceAll("\\{player}", playerSet.getName()).replaceAll("\\{amount}", String.valueOf(amount))
                    .replaceAll("\\{bounty_player}", bounty.getName()));

        }
        data.set("bounties", bounties);

        yaml.save();

    }

    /**
     * Removes a bounty listing from a player
     * @param playerToRemove the player to remove
     * @param removingFrom the player that is being removed from
     * @return weather it was successful or not
     */
    public static boolean removePlayerBounty(Player playerToRemove, OfflinePlayer removingFrom){
        PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

        final YamlGenerator yaml = new YamlGenerator(plugin.path+"/"+removingFrom.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
        final Map<String, Object> bounty = yamlContainsPlayer(playerToRemove, yaml);
        if(bounty != null) {
            try {
                Essentials.getPlugin(Essentials.class).getUser(playerToRemove).giveMoney(BigDecimal.valueOf(Double.parseDouble(bounty.get("amount").toString())));
            } catch (MaxMoneyException ignored) {
            }
            bounties.remove(bounty);
            data.set("bounties", bounties);
            yaml.save();
            playerToRemove.sendMessage(JsonUtils.getData("messages.private.bountyRemove")
                    .replaceAll("\\{player}", playerToRemove.getName()).replaceAll("\\{amount}", bounty.get("amount").toString()));
            playerToRemove.sendMessage(JsonUtils.getData("messages.global.bountyRemove")
                    .replaceAll("\\{player}", playerToRemove.getName()).replaceAll("\\{amount}", bounty.get("amount").toString())
                    .replaceAll("\\{bounty_player}", removingFrom.getName()));
            return true;
        }
        return false;
    }

    /**
     * Checks if a yaml file contains a bounty set by a player. If so, it'll return that object
     * @param player the player that might be in a file
     * @param yaml the yaml file to check
     * @return the player object if it exists else null
     */

    public static Map<String, Object> yamlContainsPlayer(Player player, YamlGenerator yaml){
        final FileConfiguration data = yaml.getData();
        final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
        if(bounties != null) {
            for (Map<String, Object> bounty : bounties) {
                if (bounty.get("setBy").toString().equals(player.getUniqueId().toString())) {
                    return bounty;
                }
            }
        }
        return null;
    }

    /**
     * Creates a new directory with respect to the path
     * @param path the path to make the directory
     * @throws IOException error for when it fails
     */
    public static void createAHConfigFolder(String path) throws IOException {
        PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

        File file = new File(plugin.path+path);
        if(!file.exists()) {
            Path paths = Paths.get(plugin.path+path);
            Files.createDirectories(paths);
        }

    }

}
