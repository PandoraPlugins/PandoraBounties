package me.nanigans.pandorabounties.Inventories;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerBounty extends me.nanigans.pandorabounties.Inventories.Inventory implements Listener {

    private int page = 0;

    public PlayerBounty(Player player) {
        super(player);
        inv = swapInvs(createInventory());
        methods.put("back", this::back);
    }

    @Override
    protected void pageForward() {

        File file = new File(plugin.path+"/"+this.player.getUniqueId()+".yml");
        if(file.exists()) {
            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
            final FileConfiguration data = yaml.getData();
            final List<?> bounties = data.getList("bounties");
            if(bounties.size() >= 45*(this.page+1)) {
                this.page++;
                inv = swapInvs(createInventory());
            }
        }
    }

    @Override
    protected void pageBackwards() {
        this.page = Math.max(this.page-1, 0);
        inv = swapInvs(createInventory());
    }

    private void back(){
        new BountyInventory(this.player);
        unregister();
    }

    @Override
    protected Inventory createInventory() {

        Inventory inv = Bukkit.createInventory(this.player, 54, "Bounties on you");
        File file = new File(plugin.path+"/"+this.player.getUniqueId()+".yml");
        if(file.exists()){
            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
            final FileConfiguration data = yaml.getData();
            final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");

            for (int i = page*45; i < bounties.size()*(page+1); i++) {

                if(inv.getItem(44) == null) {
                    OfflinePlayer bounti = Bukkit.getOfflinePlayer(UUID.fromString(bounties.get(i).get("setBy").toString()));
                    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    SkullMeta meta = ((SkullMeta) item.getItemMeta());
                    meta.setOwner(bounti.getName());
                    meta.setDisplayName(bounti.getName());
                    meta.setLore(Collections.singletonList(ChatColor.GRAY + "Amount: $" + ChatColor.GREEN + bounties.get(i).get("amount")));
                    item.setItemMeta(meta);
                    inv.addItem(item);

                }else break;

            }
        }
        inv.setItem(inv.getSize()-9, createItem(Material.COMPASS, "Page Backwards", "METHOD~pageBack"));
        inv.setItem(inv.getSize()-1, createItem(Material.COMPASS, "Page Forward", "METHOD~pageForward"));
        inv.setItem(inv.getSize()-6, createItem(Material.PAPER, "Balance: $"+ChatColor.GREEN+
                Essentials.getPlugin(Essentials.class).getUser(this.player).getMoney()));
        inv.setItem(inv.getSize()-4, createItem(Material.BARRIER, ChatColor.RED+"Back", "METHOD~back"));
        return inv;
    }
}
