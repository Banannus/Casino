package dk.banannus.tasks;

import dk.banannus.Casino;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class crateRewards {
	public crateRewards(Player player, Location hologramLoc) {
		ArmorStand hologram = hologramLoc.getWorld().spawn(hologramLoc, ArmorStand.class);
		hologram.setGravity(false);
		hologram.setCanPickupItems(false);
		hologram.setCustomName("test");
		hologram.setCustomNameVisible(true);
		hologram.setVisible(false);

		Bukkit.getScheduler().runTaskLater(Casino.getInstance(), hologram::remove, 100);
		player.sendMessage("craterewards");

	}
}