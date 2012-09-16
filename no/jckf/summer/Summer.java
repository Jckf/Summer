package no.jckf.summer;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Summer extends JavaPlugin implements Listener {
	private FileConfiguration conf;

	public void onEnable() {
		conf = getConfig();

		getServer().getPluginManager().registerEvents(this,this);

		getServer().getScheduler().scheduleSyncRepeatingTask(this,new Runnable() {
			public void run() {
				for (World w : getServer().getWorlds()) {
					Object time = conf.get(w.getName() + ".time");

					if (time != null) {
						w.setTime(Long.valueOf(time.toString()));
					}
				}
			}
		},0,200);
	}

	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used in-game.");
			return true;
		}

		if (!sender.isOp()) {
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		World world = ((Player) sender).getWorld();

		if (args[0].equalsIgnoreCase("weather")) {
			String path = world.getName() + ".weather";

			conf.set(path,conf.getBoolean(path,false) ? false : true);

			saveConfig();

			sender.sendMessage(ChatColor.GREEN + "Weather in " + world.getName() + " is " + (conf.getBoolean(path) ? "now" : "no longer") + " frozen.");

			return true;
		} else if (args[0].equalsIgnoreCase("time")) {
			String path = world.getName() + ".time";

			if (conf.get(path) != null) {
				conf.set(path,null);
				sender.sendMessage(ChatColor.GREEN + "Time in " + world.getName() + " is no longer frozen.");
			} else {
				conf.set(path,world.getTime());
				sender.sendMessage(ChatColor.GREEN + "Time in " + world.getName() + " is now frozen.");
			}

			saveConfig();

			return true;
		}

		return false;
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		if (conf.getBoolean(event.getWorld().getName() + ".weather",false)) {
			event.setCancelled(true);
		}
	}
}
