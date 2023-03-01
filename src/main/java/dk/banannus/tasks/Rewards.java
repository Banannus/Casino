package dk.banannus.tasks;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dk.banannus.Casino;


public class Rewards {
	@SuppressWarnings("unchecked")
	public Rewards(Player player, Location hologramLoc, Material clickedBlock) {

		// Hologram
		ArmorStand hologram = hologramLoc.getWorld().spawn(hologramLoc, ArmorStand.class);
		hologram.setGravity(false);
		hologram.setCanPickupItems(false);
		hologram.setCustomName("test");
		hologram.setCustomNameVisible(true);
		hologram.setVisible(false);


		for (Map<?, ?> name : Casino.configYML.getMapList("casino.crates")) {
			String block = (String) name.get("block");
			Material blockMaterial = Material.valueOf(block);
			if(clickedBlock.equals(blockMaterial)) {
				List<Map<?, ?>> rewardList = (List<Map<?, ?>>) name.get("rewards");
				for(Map<?, ?> rewards : rewardList) {
					String material = (String) rewards.get("material");
					int antal = (int) rewards.get("antal");
					int chance = (int) rewards.get("chance");
					Bukkit.broadcastMessage(material);
					Bukkit.broadcastMessage(String.valueOf(antal));
					Bukkit.broadcastMessage(String.valueOf(chance));
				}
			}
		}

		Bukkit.broadcastMessage("start");
		ItemStack i = new ItemStack(Material.INK_SACK, 1);
		Item item = hologramLoc.getWorld().dropItem(hologramLoc, i);
		hologram.setPassenger(item);

		// Fjern alt
		Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
			hologram.remove();
			item.remove();
		}, 100L);

	}
}