package me.nanigans.pandorabounties.Inventories;

import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import me.nanigans.pandorabounties.Utils.NBTData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AllBounties extends Inventory{
    private int page = 0;
    private Sortings sort;
    public AllBounties(Player player) {
        super(player);
        sort = Sortings.HIGHEST_PRICE;
        super.inv = swapInvs(createInventory());
        methods.put("back", this::back);
        methods.put("sort", this::sort);
    }


    protected void sort(){
        if(click == ClickType.LEFT) {
            if (sort.getNumVal() + 1 > Sortings.values().length) {
                sort = Sortings.HIGHEST_PRICE;
            } else sort = Sortings.getSortByNum(sort.getNumVal() + 1);
        }else if(click == ClickType.RIGHT){
            if(sort.getNumVal() - 1 < 1)
                sort = Sortings.OLDEST_BIDS;
            else sort = Sortings.getSortByNum(sort.getNumVal()-1);
        }
        this.inv = swapInvs(createInventory());
    }

    @Override
    protected void pageForward() {

        File file = new File(plugin.path);
        final File[] files = file.listFiles();
        if(files != null)
        if(45*(page+1) >= files.length-1)
            this.page++;
    }

    @Override
    protected void pageBackwards() {
        this.page = Math.max(this.page-1, 0);
    }

    protected void back(){
        new BountyInventory(this.player);
    }

    @Override
    protected org.bukkit.inventory.Inventory createInventory() {
        this.inv = Bukkit.createInventory(this.player, 54, "All Bounties");

        final File[] filesA = sortFiles();
        if(filesA != null){

            for (int i = page*45; i < 45*(page+1); i++) {
                if(filesA.length > i){
                    File file = filesA[i];
                    if(file != null){
                        if(file.getAbsolutePath().endsWith(".yml")) {
                            String uuid = file.getName().replace(".yml", "");
                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
                            final FileConfiguration data = yaml.getData();
                            final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
                            final double amt = bounties.stream().mapToDouble(j -> Double.parseDouble(j.get("amount").toString()))
                                    .reduce(0, Double::sum);

                            final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                            final SkullMeta meta = ((SkullMeta) item.getItemMeta());
                            meta.setOwner(player.getName());
                            meta.setDisplayName(player.getName());
                            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Total Amount: $" + ChatColor.GREEN + amt));
                            item.setItemMeta(meta);
                            inv.addItem(item);
                        }

                    }else break;
                }
            }

        }
        inv.setItem(inv.getSize()-9, createItem(Material.COMPASS, "Page Backwards", "METHOD~pageBackwards"));
        inv.setItem(inv.getSize()-1, createItem(Material.COMPASS, "Page Forward", "METHOD~pageForward"));
        inv.setItem(inv.getSize()-8, createItem(Material.BARRIER, ChatColor.RED+"Back", "METHOD~back"));
        ItemStack sorting = new ItemStack(Material.PAPER);
        ItemMeta meta = sorting.getItemMeta();
        meta.setDisplayName("Sort By:");
        List<String> lore = new ArrayList<>();
        for (Sortings value : Sortings.values()) {
            lore.add((this.sort == value ? ChatColor.GOLD : ChatColor.GRAY) + value.toString());
        }
        meta.setLore(lore);
        sorting.setItemMeta(meta);
        inv.setItem(inv.getSize()-2, NBTData.setNBT(sorting, "METHOD~sort"));

        return inv;
    }

    private File[] sortFiles(){

        switch (sort) {
            case HIGHEST_PRICE: return sortByNum(true);
            case LOWEST_PRICE: return sortByNum(false);
            case NEWEST_BIDS: return sortByFileChange(true);
            case OLDEST_BIDS: return sortByFileChange(false);
        }
        return new File[0];
    }

    /**
     * Sorts a directory with bounty yaml data by the highest total bounty
     * @param inverse weather to inverse the data or not
     * @return a array of data;
     */
    private static File[] sortByNum(boolean inverse){

        final File[] files = new File(plugin.path).listFiles();
        if(files != null) {
            final Map<Double, File> fileNums = new HashMap<>(files.length);
            for (File file : files) {
                if(file.getAbsolutePath().endsWith(".yml")) {
                    YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
                    final FileConfiguration data = yaml.getData();
                    final List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
                    final double amt = bounties.stream().mapToDouble(j -> Double.parseDouble(j.get("amount").toString()))
                            .reduce(0, Double::sum);
                    fileNums.put(amt, file);
                }
            }

            List<Double> sorted = new ArrayList<>(fileNums.keySet());
            Collections.sort(sorted);
            if(inverse) Collections.reverse(sorted);

            File[] files1 = new File[sorted.size()];
            for (int i = 0; i < sorted.size(); i++) {
                files1[i] = fileNums.get(sorted.get(i));
            }
            return files1;

        }

        return new File[0];

    }

    /**
     * Sorts file directory by the time it was last modified
     * @param inverse weather to inverse the data or not
     * @return a array of data
     */
    private static File[] sortByFileChange(boolean inverse){

        File[] files = new File(plugin.path).listFiles();
        if(files != null){

            final List<File> collect = Arrays.stream(files).sorted(Comparator.comparingLong(File::lastModified)).collect(Collectors.toList());
            collect.remove(new File(plugin.path+"/.DS_Store"));
            if(inverse) collect.sort(Comparator.reverseOrder());
            return collect.toArray(new File[0]);
        }
        return new File[0];
    }

    enum Sortings{
        HIGHEST_PRICE(1, "Highest Bounty"),
        LOWEST_PRICE(2, "Lowest Bounty"),
        NEWEST_BIDS(3, "Newest Bounty"),
        OLDEST_BIDS(4, "Oldest Bounty");
        private final int numVal;
        private final String s;
        private static final Map<Integer, Sortings> data = new HashMap<>();

        static{
            for (Sortings value : Sortings.values()) {
                data.put(value.getNumVal(), value);
            }
        }

        Sortings(int numVal, String s){
            this.numVal = numVal;
            this.s = s;
        }

        public String toString(){
            return s;
        }

        public static Sortings getSortByNum(int num){
            return data.get(num);
        }
        public int getNumVal(){
            return numVal;
        }
    }

}
