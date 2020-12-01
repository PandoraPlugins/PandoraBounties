package me.nanigans.pandorabounties.Inventories;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class PlayerBounty extends me.nanigans.pandorabounties.Inventories.Inventory implements Listener {

    public PlayerBounty(Player player) {
        super(player);
    }

    @Override
    protected void pageForward() {

    }

    @Override
    protected void pageBackwards() {

    }

    @EventHandler
    @Override
    protected void onInvClick(InventoryClickEvent event) {

    }

    @Override
    protected Inventory createInventory() {

        Inventory inv = Bukkit.createInventory(this.player, 54, "Bounties on you");
        File file = new File(plugin.path+"/"+this.player.getUniqueId()+".yml");
        if(file.exists()){
            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
            final FileConfiguration data = yaml.getData();
            final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");

            for (Map<String, Object> bounty : bounties) {
                if(inv.getItem(44) == null) {
                    OfflinePlayer bounti = Bukkit.getOfflinePlayer(UUID.fromString(bounty.get("setBy").toString()));
                    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    SkullMeta meta = ((SkullMeta) item.getItemMeta());
                    meta.setOwner(bounti.getName());
                    meta.setDisplayName(bounti.getName());
                    meta.setLore(Collections.singletonList(ChatColor.GRAY + "Amount: $" + ChatColor.GREEN +
                            Essentials.getPlugin(Essentials.class).getUser(bounti.getUniqueId()).getMoney()));
                    item.setItemMeta(meta);
                    inv.addItem(item);
                }else break;

            }
        }

        return inv;
    }
}
