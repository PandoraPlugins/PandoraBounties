package me.nanigans.pandorabounties.Inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface Actions{
    void execute();
}


public abstract class Inventory implements Listener {
    protected static Map<String, Actions> methods = new HashMap<>();
    protected BountyInventory info;
    protected Player player;

    public Inventory(Player player){
        this.player = player;
        methods.put("pageForward", this::pageForward);
        methods.put("pageBackwards", this::pageBackwards);
    }

    protected abstract void pageForward();
    protected abstract void pageBackwards();
    protected void execute(String method){
        if(methods.containsKey(method))
            methods.get(method).execute();
    }

    @EventHandler
    protected abstract void onInvClick(InventoryClickEvent event);

    protected abstract org.bukkit.inventory.Inventory createInventory();

}
