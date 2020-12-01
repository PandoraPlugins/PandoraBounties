package me.nanigans.pandorabounties.Inventories;

import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.NBTData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface Actions{
    void execute();
}


public abstract class Inventory implements Listener {
    protected static Map<String, Actions> methods = new HashMap<>();
    protected Player player;
    protected boolean swapInvs = false;
    protected org.bukkit.inventory.Inventory inv;
    protected PandoraBounties plugin;

    public Inventory(Player player){
        this.player = player;
        methods.put("pageForward", this::pageForward);
        methods.put("pageBackwards", this::pageBackwards);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected org.bukkit.inventory.Inventory swapInvs(org.bukkit.inventory.Inventory newInv){
        if(!swapInvs)
        this.player.openInventory(newInv);
        swapInvs = true;
        return this.player.getOpenInventory().getTopInventory();
    }

    protected void unregister(){
        HandlerList.unregisterAll(this);
    }


    protected abstract void pageForward();
    protected abstract void pageBackwards();

    protected void execute(String method){
        if(methods.containsKey(method))
            methods.get(method).execute();
    }

    @EventHandler
    protected void onInvClose(InventoryCloseEvent event) throws Throwable {
        if(!swapInvs){
            this.unregister();
            this.finalize();
        }
    }

    @EventHandler
    protected void onInvClick(InventoryClickEvent event){
        if(event.getInventory().equals(this.inv)){
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.valueOf("CLICK"), 2, 1);
            event.setCancelled(true);
            if(event.getCurrentItem() != null){

                ItemStack item = event.getCurrentItem();
                final String method = NBTData.getNBT(item, "METHOD");
                if(method != null){

                    this.execute(method);

                }

            }

        }
    }

    protected abstract org.bukkit.inventory.Inventory createInventory();

}
