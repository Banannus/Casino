package dk.banannus;

import dk.banannus.commands.CasinoAdmin;
import dk.banannus.events.PlayerArmorStandManipulateListener;
import dk.banannus.events.PlayerInteractListener;
import dk.banannus.utils.Config;
import dk.banannus.utils.CasinoConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Casino extends JavaPlugin {

	public static Config config, data;
	public static FileConfiguration configYML;
	public static FileConfiguration dataYML;
	public static Casino instance;
	public static CasinoConfig cConfig;

	@Override
	public void onEnable() {

		instance = this;

		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerArmorStandManipulateListener(), this);
		getCommand("ca").setExecutor(new CasinoAdmin());

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

		cConfig = new CasinoConfig();
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
