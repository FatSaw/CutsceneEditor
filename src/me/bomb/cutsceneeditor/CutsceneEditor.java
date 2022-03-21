package me.bomb.cutsceneeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.bomb.cutscene.Route;

public class CutsceneEditor extends JavaPlugin implements Listener {
	HashMap<UUID,ArrayList<Location>> locations = new HashMap<UUID,ArrayList<Location>>();
	
	public void onEnable() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(UUID playeruuid : locations.keySet()) {
					Player player = Bukkit.getPlayer(playeruuid);
					if(player!=null&&player.isOnline()) {
						locations.get(player.getUniqueId()).add(player.getEyeLocation());
					}
				}
			}
		}.runTaskTimer(this, 0L, 1L);
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("recordscene")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("cutscene.recordscene")) {
					if(locations.containsKey(player.getUniqueId())) {
						new Route(player.getName(), locations.get(player.getUniqueId()).toArray(new Location[locations.get(player.getUniqueId()).size()]));
						locations.remove(player.getUniqueId());
						player.sendMessage("§aRecord stopped");
					} else {
						locations.put(player.getUniqueId(), new ArrayList<Location>(Arrays.asList(player.getEyeLocation())));
						player.sendMessage("§aRecord started");
					}
				} else player.sendMessage("§cNo permission");
			} else {
				sender.sendMessage("Command only for players");
			}
		}
		return true;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(locations.containsKey(player.getUniqueId())) {
			new Route(player.getName(), locations.get(player.getUniqueId()).toArray(new Location[locations.get(player.getUniqueId()).size()]));
			locations.remove(player.getUniqueId());
		}
	}
	
	
	
}
