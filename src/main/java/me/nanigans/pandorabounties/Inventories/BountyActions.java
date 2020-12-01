package me.nanigans.pandorabounties.Inventories;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;


public class BountyActions {
    private static Map<String, Actions> methods = new HashMap<>();
    private BountyInventory info;

    public BountyActions(BountyInventory inv){
        this.info = inv;
        methods.put("pageForward", this::pageForward);
        methods.put("pageBackwards", this::pageBackwards);
        methods.put("bountiesOnPlayer", this::bountiesOnPlayer);

    }

    public void execute(String method){
        if(methods.containsKey(method))
            methods.get(method).execute();
    }

    private void bountiesOnPlayer(){



    }

    private void pageForward(){

        final Inventory inv = info.getInv();


    }

    private void pageBackwards(){

    }

}
