package dk.banannus.commands;

import dk.banannus.Casino;
import dk.banannus.utils.Chat;
import dk.banannus.utils.CasinoConfig;
import dk.banannus.utils.CreateLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static dk.banannus.Casino.config;


public class CasinoAdmin implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = Casino.configYML.getString("casino.prefix");
		Player player = (Player) sender;
		if (!player.hasPermission(Casino.configYML.getString("casino.staff-permission"))) {
			player.sendMessage(Chat.colored(prefix) + "&cDu har ikke adgang til dette.");
			return true;
		}


		if (args.length == 0) {
			player.sendMessage(Chat.colored(prefix));
			player.sendMessage(Chat.colored("&8┃ &f/ca set/remove/list/reload"));
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			boolean success;
			try {
				config.reloadConfig();
				Casino.configYML = config.getConfig();
				success = true;

			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}
			if (success)
				sender.sendMessage(Chat.colored(prefix) + "&a&CONFIG RELOADED.");
			if (!success)
				sender.sendMessage(Chat.colored(prefix) + "&cDer opstod en fejl. Tjek din console.");
		}

		if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("list")) {
			Block target = player.getTargetBlock((Set<Material>) null, 5);
			Location cLoc = target.getLocation();
			if (args[0].equalsIgnoreCase("set")) {
				if (args.length == 1) {
					player.sendMessage(Chat.colored(prefix));
					player.sendMessage(Chat.colored("&8┃ &f/ca set <navn>"));
					return true;
				}

				if (Casino.dataYML.contains("casino." + args[1])) {
					player.sendMessage(Chat.colored(prefix) + Chat.colored("&cDette navn er allerede i brug!"));
					return true;
				}

				if (Casino.cConfig.getCasinoLocations().contains(cLoc)) {
					player.sendMessage(Chat.colored(prefix) + Chat.colored("&cDenne lokation er allerede i brug!"));
					return true;
				}

				List<Map<?, ?>> crates = Casino.configYML.getMapList("casino.crates");
				boolean foundMatchingCrate = false;

				for (Map<?, ?> item : crates) {
					String blockName = (String) item.get("block");
					Material blockType = Material.getMaterial(blockName);

					if (target.getType() == blockType) {
						CreateLocation.addCCrate(cLoc, args[1]);
						CasinoConfig.casinoLocations.add(cLoc);
						player.sendMessage(Chat.colored(prefix) + Chat.colored("&7Du placerede en crate ved&8: &7x: &a" + cLoc.getX() + " &7y: &a" + cLoc.getY() + " &7z: &a" + cLoc.getZ() + " &8(&a" + cLoc.getWorld().getName() + "&8)"));
						foundMatchingCrate = true;
						break;
					}
				}
				if (!foundMatchingCrate) {
					player.sendMessage(Chat.colored(prefix) + Chat.colored("&cDu skal kigge på en &4crate block!"));
				}
			}


			if(args[0].equalsIgnoreCase("remove")) {

				if (args.length == 1) {
					player.sendMessage(Chat.colored(prefix));
					player.sendMessage(Chat.colored("&8┃ &f/ca remove <navn>"));
					return true;
				}

				if (Casino.cConfig.getCasinoLocations().size() == 0) {
					player.sendMessage(Chat.colored(prefix + "&cDer findes ikke nogle crates!"));
					return true;
				}

				if (args.length == 3 && args[2].equalsIgnoreCase("--force")) {
					CreateLocation.removeCcrate(args[1]);
					player.sendMessage(Chat.colored(prefix + "&7Fjernede craten &c" + args[1] + "!"));
				} else {
					if (!Casino.dataYML.contains("casino." + args[1])) {
						player.sendMessage(Chat.colored(prefix + "&cDenne crate findes ikke"));
						player.sendMessage(Chat.colored(prefix + "&8┃ Tror du at det en fejl, så add --force til kommanden"));
						return true;
					}
					CreateLocation.removeCcrate(args[1]);
					player.sendMessage(Chat.colored(prefix + "&7Fjernede craten &c" + args[1] + "!"));
				}

			}

			if(args[0].equalsIgnoreCase("list")) {
				if(Casino.cConfig.getCasinoLocations().size() == 0) {
					player.sendMessage(Chat.colored(prefix + "&cDer findes ikke nogle crates!"));
					return true;
				}

				player.sendMessage(Chat.colored(prefix));
				for(String name : Casino.dataYML.getConfigurationSection("casino").getKeys(false)) {
					player.sendMessage(Chat.colored("&8┃ " + "&7" + name));
				}
				return true;
			}

		}
		return false;
	}
}