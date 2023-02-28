package dk.banannus.utils;


import dk.banannus.Casino;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class createLocation {

	public static void addCCrate(Location loc, String name){
		Casino.dataYML.set("casino." + name + ".world", loc.getWorld().getName());
		Casino.dataYML.set("casino." + name + ".x", loc.getX());
		Casino.dataYML.set("casino." + name + ".y", loc.getY());
		Casino.dataYML.set("casino." + name + ".z", loc.getZ());
		Casino.data.saveConfig();
		Casino.cConfig.reloadCasinoConfig();
	}

	public static void removeCcrate(String name) {
		Casino.dataYML.set("casino."+ name, null);
		Casino.data.saveConfig();
		Casino.cConfig.reloadCasinoConfig();
	}

	public static Location getLocations (String name) {
		double x = Casino.dataYML.getDouble("casino." + name + ".x");
		double y = Casino.dataYML.getDouble("casino." + name + ".y");
		double z = Casino.dataYML.getDouble("casino." + name + ".z");
		String w = Casino.dataYML.getString("casino." + name + ".world");
		World world = Bukkit.getWorld(w.toLowerCase());

		return new Location(world, x, y, z);
	}

	public static List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<>();
		ConfigurationSection casinoSection = Casino.dataYML.getConfigurationSection("casino");
		if (casinoSection != null) {
			for (String name : casinoSection.getKeys(false)) {
				if (Casino.dataYML.contains("casino." + name + ".x")
						&& Casino.dataYML.contains("casino." + name + ".y")
						&& Casino.dataYML.contains("casino." + name + ".z")
						&& Casino.dataYML.contains("casino." + name + ".world")) {
					double x = Casino.dataYML.getDouble("casino." + name + ".x");
					double y = Casino.dataYML.getDouble("casino." + name + ".y");
					double z = Casino.dataYML.getDouble("casino." + name + ".z");
					String worldName = Casino.dataYML.getString("casino." + name + ".world");
					World world = Bukkit.getWorld(worldName);
					if (world != null) {
						Location location = new Location(world, x, y, z);
						locations.add(location);
					}
				}
			}
		}
		return locations;
	}
}