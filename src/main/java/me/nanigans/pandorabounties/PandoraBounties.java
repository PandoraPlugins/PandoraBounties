package me.nanigans.pandorabounties;

import me.nanigans.pandorabounties.Commands.Bounty;
import me.nanigans.pandorabounties.Utils.Config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class PandoraBounties extends JavaPlugin {

    public final String path = getDataFolder().getAbsolutePath()+"/Bounties";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("bounty").setExecutor(new Bounty());
        try {
            Config.createAHConfigFolder("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
