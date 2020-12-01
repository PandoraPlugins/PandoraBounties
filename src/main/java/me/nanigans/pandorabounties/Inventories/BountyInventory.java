package me.nanigans.pandorabounties.Inventories;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.Config.Config;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import me.nanigans.pandorabounties.Utils.NBTData;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class BountyInventory extends me.nanigans.pandorabounties.Inventories.Inventory implements Listener{

    private int page = 0;
    private static final PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);
    private Inventory inv;

    public BountyInventory(Player player){
        super(player);
        this.inv = createInventory();
        super.player.openInventory(this.inv);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        methods.put("bountiesOnPlayer", this::bountiesOnPlayer);

    }


    @EventHandler
    public void onInvClick(InventoryClickEvent event){

        if(event.getInventory().equals(inv)){
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.valueOf("CLICK"), 2, 1);
            event.setCancelled(true);
            if(event.getCurrentItem() != null){

                ItemStack item = event.getCurrentItem();
                final String method = NBTData.getNBT(item, "METHOD");
                if(method != null){

                    super.execute(method);

                }

            }

        }

    }

    private void bountiesOnPlayer(){



    }

    @Override
    protected void pageForward(){

        File file = new File(plugin.path);
        File[] files = file.listFiles();
        System.out.println("this.page = " + this.page);
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

        return inv;
    }

    private static ItemStack createItem(Material mat, String name, String... nbt){

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        if(nbt.length > 0)
            item = NBTData.setNBT(item, nbt);
        return item;

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
