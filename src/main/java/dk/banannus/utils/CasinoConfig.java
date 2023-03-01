package dk.banannus.utils;

import dk.banannus.Casino;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class CasinoConfig {
	public static ArrayList<Location> casinoLocations = new ArrayList<>();

	public CasinoConfig() {

	}

	public void reloadCasinoConfig(){
		casinoLocations.clear();
		try {
			for (String id : Casino.dataYML.getConfigurationSection("casino").getKeys(false)) {
				double x = Casino.dataYML.getDouble("casino."+id+".x");
				double y = Casino.dataYML.getDouble("casino."+id+".y");
				double z = Casino.dataYML.getDouble("casino."+id+".z");
				String w = Casino.dataYML.getString("casino."+id+".world");
				World world = Bukkit.getWorld(w.toLowerCase());
				casinoLocations.add(new Location(world, x, y, z));
			}
		} catch(NullPointerException exception){
			Bukkit.getConsoleSender().sendMessage("No Locations found! C some cs!");
		}
	}

	public ArrayList<Location> getCasinoLocations() {
		return casinoLocations;
	}

}