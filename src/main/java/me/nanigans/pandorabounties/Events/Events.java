package me.nanigans.pandorabounties.Events;

import com.earth2me.essentials.Essentials;
import me.nanigans.pandorabounties.PandoraBounties;
import me.nanigans.pandorabounties.Utils.Config.YamlGenerator;
import net.ess3.api.MaxMoneyException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Events implements Listener {
    protected static PandoraBounties plugin = PandoraBounties.getPlugin(PandoraBounties.class);

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws MaxMoneyException {

        Player died = event.getEntity();
        Player killer = died.getKiller();

        File file = new File(plugin.path+"/"+died.getUniqueId()+".yml");
        if(file.exists() && killer != null){

            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
            final FileConfiguration data = yaml.getData();
            List<Map<String, Object>> bounties = (List<Map<String, Object>>) data.getList("bounties");
            double amt = bounties.stream().mapToDouble(i -> Double.parseDouble(i.get("amount").toString())).reduce(0, Double::sum);
            Essentials.getPlugin(Essentials.class).getUser(killer).giveMoney(BigDecimal.valueOf(amt));
            file.delete();
            killer.sendMessage(ChatColor.GREEN+"Bounty from player: " + died.getName() + " received!");

        }

    }

}
