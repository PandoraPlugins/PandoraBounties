package me.nanigans.pandorabounties.Inventories;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.Config.Config;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import me.nanigans.pandorabounties.Utils.NBTData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class BountyInventory extends me.nanigans.pandorabounties.Inventories.Inventory{

    private int page = 0;
    private static final PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

    public BountyInventory(Player player){
        super(player);
        super.inv = this.createInventory();
        super.player.openInventory(this.inv);
        methods.put("bountiesOnPlayer", this::bountiesOnPlayer);
        methods.put("allBounties", this::allBounties);

    }

    private void bountiesOnPlayer(){
        new PlayerBounty(this.player);
        unregister();
    }

    @Override
    protected void pageForward(){

        File file = new File(plugin.path);
        File[] files = file.listFiles();
        if(files != null && files.length >= 45*(this.page+1)) {
            this.page++;
            final Inventory inventory = this.createInventory();
            this.inv = swapInvs(inventory);

        }

    }

    @Override
    protected void pageBackwards(){

        this.setPage(this.getPage()-1);
        final Inventory inv = this.createInventory();
        this.inv = swapInvs(inv);

    }

    protected void allBounties(){
        new AllBounties(this.player);
        unregister();
    }


    @Override
    protected Inventory createInventory(){

        Inventory inv = Bukkit.createInventory(this.player, 54, "Your Placed Bounties");

        File file = new File(plugin.path);
        File[] files = file.listFiles();
        if(files != null) {
            for (int i = (45*this.page); i < files.length*(this.page+1); i++) {
                File listFile = files[i];
                if (listFile.getAbsolutePath().endsWith(".yml")) {
                    YamlGenerator yaml = new YamlGenerator(listFile.getAbsolutePath());
                    final Map<String, Object> bounty = Config.yamlContainsPlayer(this.player, yaml);
                    if (bounty != null) {

                        if (inv.getItem(44) == null) {

                            final FileConfiguration data = yaml.getData();
                            final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
                            double total = bounties.stream().mapToDouble(j -> Double.parseDouble(j.get("amount").toString())).reduce(0, Double::sum);

                            String uuidStr = listFile.getName().replace(".yml", "");
                            OfflinePlayer uuid = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                            final SkullMeta itemMeta = ((SkullMeta) skull.getItemMeta());
                            itemMeta.setOwner(uuid.getName());
                            itemMeta.setDisplayName(uuid.getName() + "'s Bounties");
                            itemMeta.setLore(Arrays.asList("Press Q to remove", ChatColor.GRAY + "Your bounty: $" + ChatColor.GREEN + bounty.get("amount"),
                                    ChatColor.GRAY + "Total Amount: $" + ChatColor.GREEN + total));
                            skull.setItemMeta(itemMeta);
                            inv.addItem(skull);
                        } else break;

                    }
                }
            }
        }

        inv.setItem(inv.getSize()-9, createItem(Material.COMPASS, "Page Backwards", "METHOD~pageBack"));
        inv.setItem(inv.getSize()-1, createItem(Material.COMPASS, "Page Forward", "METHOD~pageForward"));
        inv.setItem(inv.getSize()-6, createItem(Material.PAPER, "Balance: $"+ChatColor.GREEN+
                Essentials.getPlugin(Essentials.class).getUser(this.player).getMoney()));
        inv.setItem(inv.getSize()-4, createItem(Material.BOOK, "Bounties placed on You", "METHOD~bountiesOnPlayer"));
        inv.setItem(inv.getSize()-3, createItem(Material.BOOKSHELF, "All bounties", "METHOD~allBounties"));

        return inv;
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public Player getPlayer() {
        return player;
    }

    public static PandoraBounties getPlugin() {
        return plugin;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }
}
