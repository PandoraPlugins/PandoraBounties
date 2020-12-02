package me.nanigans.pandorabounties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.nanigans.pandorabounties.Commands.Bounty;
import me.nanigans.pandorabounties.Events.Events;
import me.nanigans.pandorabounties.Utils.Config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class PandoraBounties extends JavaPlugin {

    public final String path = getDataFolder().getAbsolutePath()+"/Bounties";
    GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    public Map<String, Object> map = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("bounty").setExecutor(new Bounty());
        getServer().getPluginManager().registerEvents(new Events(), this);
        try {
            Config.createAHConfigFolder("");

        } catch (IOException e) {
            e.printStackTrace();
        }

        File configFile = new File(getDataFolder(), "config.json");

        if(!configFile.exists()) {

            saveResource(configFile.getName(), false);
            try {
                Gson gson = gsonBuilder.create();

                map = gson.fromJson(new FileReader(configFile), HashMap.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
