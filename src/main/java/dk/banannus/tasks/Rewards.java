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

import static dk.banannus.events.PlayerInteractListener.resetOpenStatus;

public class Rewards {

	// TODO: ADD CHECKS
	@SuppressWarnings("unchecked")
	public Rewards(Player player, Location hologramLoc, Material clickedBlock) {

		// TODO: Gør teksten pæn og add til config.
		// Hologram
		ArmorStand hologram = hologramLoc.getWorld().spawn(hologramLoc, ArmorStand.class);
		hologram.setGravity(false);
		hologram.setCanPickupItems(false);
		hologram.setCustomName("test");
		hologram.setCustomNameVisible(true);
		hologram.setVisible(false);

		double totalChance = 0.0;
		for (Map<?, ?> name : Casino.configYML.getMapList("casino.crates")) {
			String block = (String) name.get("block");
			Material blockMaterial = Material.valueOf(block);
			if(clickedBlock.equals(blockMaterial)) {
				List<Map<?, ?>> rewardList = (List<Map<?, ?>>) name.get("rewards");
				for(Map<?, ?> rewards : rewardList) {
					int chance = (int) rewards.get("chance");
					totalChance += chance;
				}
			}
		}

		double randomValue = Math.random();
		double cumulativeChance = 0.0;
		for (Map<?, ?> name : Casino.configYML.getMapList("casino.crates")) {
			String block = (String) name.get("block");
			Material blockMaterial = Material.valueOf(block);
			if(clickedBlock.equals(blockMaterial)) {
				List<Map<?, ?>> rewardList = (List<Map<?, ?>>) name.get("rewards");
				for(Map<?, ?> rewards : rewardList) {
					String material = (String) rewards.get("material");
					int antal = (int) rewards.get("antal");
					int chance = (int) rewards.get("chance");
					double normalizedChance = chance / totalChance;
					cumulativeChance += normalizedChance;
					if (randomValue < cumulativeChance) {
						ItemStack rewardItem = new ItemStack(Material.valueOf(material), antal);
						player.getInventory().addItem(rewardItem);
						ItemStack i = new ItemStack(Material.valueOf(material), 1);
						Item item = hologramLoc.getWorld().dropItem(hologramLoc, i);
						item.setPickupDelay(Integer.MAX_VALUE);
						hologram.setPassenger(item);
						Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
							hologram.remove();
							item.remove();
							resetOpenStatus(player);
						}, 100L);
						break;
					}
				}
			}
		}
	}
}