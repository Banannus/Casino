package dk.banannus.tasks;

import java.util.List;
import java.util.Map;
import java.util.Random;

import dk.banannus.utils.Chat;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import dk.banannus.Casino;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import static dk.banannus.events.PlayerInteractListener.resetOpenStatus;

public class Rewards {

	@SuppressWarnings("unchecked")
	public Rewards(Player player, Location hologramLoc, Material clickedBlock) {

		// Hologram
		ArmorStand hologram = hologramLoc.getWorld().spawn(hologramLoc, ArmorStand.class);
		hologram.setGravity(false);
		hologram.setCanPickupItems(false);
		hologram.setCustomNameVisible(true);
		hologram.setVisible(false);

		double totalChance = 0.0;
		for (Map<?, ?> name : Casino.configYML.getMapList("casino.crates")) {
			String block = (String) name.get("block");
			Material blockMaterial = Material.valueOf(block);
			if (clickedBlock.equals(blockMaterial)) {
				List<Map<?, ?>> rewardList = (List<Map<?, ?>>) name.get("rewards");
				for (Map<?, ?> rewards : rewardList) {
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
			if (clickedBlock.equals(blockMaterial)) {
				List<Map<?, ?>> rewardList = (List<Map<?, ?>>) name.get("rewards");
				for (Map<?, ?> rewards : rewardList) {
					String material = (String) rewards.get("material");
					String itemName = (String) rewards.get("guiName");
					int antal = (int) rewards.get("antal");
					String antalReward = String.valueOf(antal);
					int chance = (int) rewards.get("chance");
					short itemData;
					if (rewards.containsKey("data")) {
						itemData = ((Integer) rewards.get("data")).shortValue();
					} else {
						itemData = 0;
					}
					double normalizedChance = chance / totalChance;
					cumulativeChance += normalizedChance;
					if (randomValue < cumulativeChance) {
						ItemStack rewardItem = new ItemStack(Material.valueOf(material), antal, itemData);
						Enchantment enchant = null;
						int enchantLevel = 0;
						if (rewards.containsKey("enchants")) {
							Map<?, ?> enchants = (Map<?, ?>) rewards.get("enchants");
							for (Map.Entry<?, ?> entry : enchants.entrySet()) {

								String[] enchant_split = entry.getValue().toString().split(" ");
								String enchantName = enchant_split[0];
								enchantLevel = Integer.parseInt(enchant_split[1]);
								enchant = Enchantment.getByName(enchantName);

								rewardItem.addUnsafeEnchantment(enchant, enchantLevel);
							}
						}
						ItemMeta itemMeta = rewardItem.getItemMeta();
						itemMeta.setDisplayName(Chat.colored(itemName));
						rewardItem.setItemMeta(itemMeta);
						player.getInventory().addItem(rewardItem);
						ItemStack i = new ItemStack(Material.valueOf(material), 1, itemData);
						if (!(enchant == null)) {
							i.addUnsafeEnchantment(enchant, enchantLevel);
						}
						Item item = hologramLoc.getWorld().dropItem(hologramLoc, i);
						item.setPickupDelay(Integer.MAX_VALUE);
						String rewardmsg = Casino.configYML.getString("casino.reward-msg").replace("%item%", itemName).replace("%antal%", antalReward);
						hologram.setCustomName(Chat.colored(rewardmsg));
						hologram.setPassenger(item);
						Location fireworkLoc = hologramLoc.clone().add(0,1,0);
						shootFirework(fireworkLoc);
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


	public void shootFirework(Location loc) {
		FireworkEffect effect = FireworkEffect.builder()
				.withColor(Color.fromRGB(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)))
				.with(FireworkEffect.Type.BALL_LARGE)
				.build();
		Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(1);
		firework.setFireworkMeta(meta);
	}
}