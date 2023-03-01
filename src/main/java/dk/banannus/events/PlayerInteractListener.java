package dk.banannus.events;

import dk.banannus.Casino;

import dk.banannus.tasks.Animation;
import dk.banannus.utils.CreateLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerInteractListener implements Listener {

	// TODO: Færdiggør tekst
	// TODO: ADD CHECKS
	private static final List<Player> openingCrate = new ArrayList<>();

	@EventHandler
	public void onOpenCrate(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			for (String name : Casino.dataYML.getConfigurationSection("casino").getKeys(false)) {
				Location loc = CreateLocation.getLocations(name);

				if (e.getClickedBlock().getLocation().equals(loc)) {
					List<Map<?, ?>> crates = Casino.configYML.getMapList("casino.crates");

					String blockType = String.valueOf(e.getClickedBlock().getType());

					String headTexture = "";
					for (Map<?, ?> item : crates) {
						String itemBlockType = (String) item.get("block");
						assert false;
						if (blockType.equals(itemBlockType)) {
							headTexture = (String) item.get("head");
							break;
						}
					}

					BlockFace blockface = e.getBlockFace();
					Location blockLoc = e.getClickedBlock() != null ? e.getClickedBlock().getLocation() : null;
					Material block = e.getClickedBlock().getType();

					if (!openingCrate.contains(player)) {
						openingCrate.add(player);
						e.setCancelled(true);
						player.sendMessage("Opening crate");
						assert blockLoc != null;
						new Animation().AnimationCreate(blockLoc, headTexture, player, blockface, block);
					} else {
						openingCrate.add(player);
						e.setCancelled(true);
						player.sendMessage("Opening crate");
						assert blockLoc != null;
						new Animation().AnimationCreate(blockLoc, headTexture, player, blockface, block);
						player.sendMessage("You are already opening a crate!");
						e.setCancelled(true);
					}
				}
			}
		}
	}
	public static void resetOpenStatus(Player player) {
		openingCrate.remove(player);
	}
}