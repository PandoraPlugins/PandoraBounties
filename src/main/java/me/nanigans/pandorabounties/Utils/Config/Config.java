package me.nanigans.pandorabounties.Utils.Config;

import me.nanigans.pandorabounties.PandoraBounties;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
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
    public static void setPlayerBounty(Player playerSet, OfflinePlayer bounty, double amount){

        PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

        final YamlGenerator yaml = new YamlGenerator(plugin.path+"/"+bounty.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();

        List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
        bounties = bounties == null ? new ArrayList<>() : bounties;
        final Map<String, Object> setBy = yamlContainsPlayer(playerSet, yaml);
        if(setBy != null){
            if(amount > 0) {
                setBy.put("amount", amount);
                playerSet.sendMessage(ChatColor.GREEN+"Updated the amount on this player!");
            }else if(amount == 0){
                bounties.remove(setBy);
                playerSet.sendMessage(ChatColor.GREEN+"Removed your bounty from this player");
            }
        }else{
            Map<String, Object> newBounty = new HashMap<>();
            newBounty.put("setBy", playerSet.getUniqueId().toString());
            newBounty.put("amount", amount);
            bounties.add(newBounty);
            playerSet.sendMessage(ChatColor.GREEN+"Bounty Added!");
        }
        data.set("bounties", bounties);

        yaml.save();

    }

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



    /**
     * Returns a {@link Map} representative of the passed Object that represents
     * a section of a YAML file. This method neglects the implementation of the
     * section (whether it be {@link ConfigurationSection} or just a
     * {@link Map}), and returns the appropriate value.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param o The object to interpret
     * @param deep If an object is a {@link ConfigurationSection}, {@code true} to do a deep search
     * @return A {@link Map} representing the section
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getConfigSectionValue(Object o, boolean deep) {
        if (o == null) {
            return null;
        }
        Map<String, T> map;
        if (o instanceof ConfigurationSection) {
            map = (Map<String, T>) ((ConfigurationSection) o).getValues(deep);
        } else if (o instanceof Map) {
            map = (Map<String, T>) o;
        } else {
            return null;
        }
        return map;
    }


}
