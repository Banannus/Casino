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

	// TODO: Command til at sætte items der skal i kisten, med chance og antal.
	// TODO: Færdiggør øvrige commands
	// TODO: ADD CHECKS

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (!player.hasPermission(Casino.configYML.getString("casino.staff-permission"))) {
			player.sendMessage(Chat.colored(Casino.configYML.getString("casino.prefix") + "&cDu har ikke adgang til dette."));
			return true;
		}


		if (args.length == 0) {
			player.sendMessage(Casino.configYML.getString("casino.prefix"));
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
				sender.sendMessage(Chat.colored(Casino.configYML.getString("casino.prefix") + "&aConfig reloaded."));
			if (!success)
				sender.sendMessage(Chat.colored(Casino.configYML.getString("casino.prefix") + "&cDer opstod en fejl. Tjek din console."));
		}

		if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("list")) {
			if (args[0].equalsIgnoreCase("set")) {
				if (args.length == 1) {
					player.sendMessage(Casino.configYML.getString("casino.prefix"));
					player.sendMessage(Chat.colored("&8┃ &f/ca set <navn>"));
					return true;
				}
			}

			if (Casino.dataYML.contains("casino." + args[1])) {
				player.sendMessage(Casino.configYML.getString("casino.prefix") + Chat.colored("&cDette navn er allerede i brug!"));
				return true;
			}

			Block target = player.getTargetBlock((Set<Material>) null, 5);
			Location cLoc = target.getLocation();
			if (Casino.cConfig.getCasinoLocations().contains(cLoc)) {
				player.sendMessage(Casino.configYML.getString("casino.prefix") + Chat.colored("&cDenne lokation er allerede i brug!"));
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
					player.sendMessage(Chat.colored(Casino.configYML.getString("casino.prefix") + "&7Du placerede en crate ved&8: &7x: &a" + cLoc.getX() + " &7y: &a" + cLoc.getY() + " &7z: &a" + cLoc.getZ() + " &8(&a" + cLoc.getWorld().getName() + "&8)"));
					foundMatchingCrate = true;
					break;
				}
			}

			if (!foundMatchingCrate) {
				player.sendMessage(Chat.colored(Casino.configYML.getString("casino.prefix") + "&7Du skal kigge på en &ccrate block."));
			}
		}
		return false;
	}
}