package dk.banannus.events;

import dk.banannus.Casino;

import dk.banannus.tasks.crateAnimation;
import dk.banannus.utils.createLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class openCrate implements Listener {

	private static final List<Player> openingCrate = new ArrayList<>();
	@EventHandler
	public void onOpenCrate(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block clickedBlock = e.getClickedBlock();
		Location blockLoc = clickedBlock != null ? clickedBlock.getLocation() : null;
		List<Location> crateLocs = createLocation.getAllLocations();
		BlockFace blockface = e.getBlockFace();
		String blockType = String.valueOf(e.getClickedBlock().getType());

		if (blockLoc != null && crateLocs.contains(blockLoc)) {
			List<Map<?, ?>> crates = Casino.configYML.getMapList("casino.crates");


			String headTexture = null;
			for (Map<?, ?> item : crates) {
				String itemBlockType = (String) item.get("block");
				assert false;
				if (blockType.equals(itemBlockType)) {
					headTexture = (String) item.get("head");
					break;
				}
			}

			if (!openingCrate.contains(player)) {
				openingCrate.add(player);
				e.setCancelled(true);
				player.sendMessage("Opening crate");
				new crateAnimation().Animation(blockLoc, headTexture, player, blockface);
			} else {
				player.sendMessage("You are already opening a crate!");
				e.setCancelled(true);
			}
		}
	}

	public static void resetOpenStatus(Player player) {
		openingCrate.remove(player);
	}
}