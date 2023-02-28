package dk.banannus;

import dk.banannus.commands.casinoAdmin;
import dk.banannus.events.armorstandClick;
import dk.banannus.events.openCrate;
import dk.banannus.utils.Config;
import dk.banannus.utils.casinoConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Casino extends JavaPlugin {

	public static Config config, data;
	public static FileConfiguration configYML;
	public static FileConfiguration dataYML;
	public static Casino instance;
	public static casinoConfig cConfig;

	@Override
	public void onEnable() {

		instance = this;

		getServer().getPluginManager().registerEvents(new openCrate(), this);
		getServer().getPluginManager().registerEvents(new armorstandClick(), this);
		getCommand("ca").setExecutor(new casinoAdmin());

		if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
			this.saveResource("config.yml", false);
		}

		if (!(new File(this.getDataFolder(), "data.yml")).exists()) {
			this.saveResource("data.yml", false);
		}

		config = new Config(this, null, "config.yml");
		configYML = config.getConfig();
		config.saveConfig();

		data = new Config(this, null, "data.yml");
		dataYML = data.getConfig();

		cConfig = new casinoConfig();
		cConfig.reloadCasinoConfig();

	}

	@Override
	public void onDisable() {
		config.reloadConfig();
	}

	public static Casino getInstance() {
		return instance;
	}

}
