package me.nanigans.pandorabounties.Inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

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
        return null;
    }
}
