package dk.banannus.events;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dk.banannus.Casino;

import dk.banannus.tasks.Animation;
import dk.banannus.utils.Chat;
import dk.banannus.utils.CreateLocation;
import dk.banannus.utils.GUI;
import dk.banannus.utils.GlassColor;
import dk.nydt.sscore.api.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerInteractListener implements Listener {

	private static final List<Player> openingCrate = new ArrayList<>();

	@EventHandler
	@SuppressWarnings("unchecked")
	public void onOpenCrate(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			String prefix = Casino.configYML.getString("casino.prefix");

				List<Location> loc = CreateLocation.getAllLocations();

				if (loc.contains(e.getClickedBlock().getLocation())) {
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
						assert blockLoc != null;
						new Animation().AnimationCreate(blockLoc, headTexture, player, blockface, block);
					} else {
						player.sendMessage(Chat.colored(prefix) + Chat.colored(Casino.configYML.getString("casino.crateopen-msg")));
						e.setCancelled(true);
					}
				}
		}
		List<GuiItem> antal = new ArrayList<>();

		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

			List<Location> loc = CreateLocation.getAllLocations();

			if (loc.contains(e.getClickedBlock().getLocation())) {
				e.setCancelled(true);
				for (Map<?, ?> blockName : Casino.configYML.getMapList("casino.crates")) {
					String block = (String) blockName.get("block");
					Material blockMaterial = Material.valueOf(block);
					if (e.getClickedBlock().getType().equals(blockMaterial)) {
						List<Map<?, ?>> rewardList = (List<Map<?, ?>>) blockName.get("rewards");
						for (Map<?, ?> rewards : rewardList) {
							String material = (String) rewards.get("material");
							String itemName = (String) rewards.get("guiName");
							int chance = (int) rewards.get("chance");
							short itemData;
							if (rewards.containsKey("data")) {
								itemData = ((Integer) rewards.get("data")).shortValue();
							} else {
								itemData = 0;
							}
							ItemStack itemStack = new ItemStack(Material.valueOf(material), 1, itemData);
							if (rewards.containsKey("enchants")) {
								Map<?, ?> enchants = (Map<?, ?>) rewards.get("enchants");
								for (Map.Entry<?, ?> entry : enchants.entrySet()) {

									//int level = Integer.parseInt(entry.getKey().toString());
									String[] enchant_split = entry.getValue().toString().split(" ");
									String enchantName = enchant_split[0];
									int enchantLevel = Integer.parseInt(enchant_split[1]);
									Enchantment enchant = Enchantment.getByName(enchantName);

									itemStack.addUnsafeEnchantment(enchant, enchantLevel);
								}
							}
							ItemMeta itemMeta = itemStack.getItemMeta();
							List<String> loreList = Casino.configYML.getStringList("casino.lore");
							List<String> lt = new ArrayList<>();
							for (String lore : loreList) {
								lore = lore.replace("%chance%", String.valueOf(chance));
								lt.add(lore);
							}
							itemMeta.setDisplayName(Chat.colored(itemName));
							itemMeta.setLore(ColorUtils.getColored(lt));
							itemStack.setItemMeta(itemMeta);
							GuiItem guiItem = ItemBuilder.from(itemStack).asGuiItem(event -> event.setCancelled(true));
							antal.add(guiItem);
						}
					}
				}
				String top_row = Casino.configYML.getString("casino.top_row");
				String bottom_row = Casino.configYML.getString("casino.bottom_row");
				String guiName = Casino.configYML.getString("casino.prefix");
				Gui CrateGUI = Gui.gui()
						.rows(5)
						.title(Component.text(Chat.colored(guiName))).create();

				for (int i = 0; i < 9; i++) {
					ItemStack it = GUI.createItemGlass(Material.STAINED_GLASS_PANE, GlassColor.getGlassColor(top_row), "&7" );
					GuiItem item =  ItemBuilder.from(it).asGuiItem(event -> event.setCancelled(true));
					CrateGUI.setItem(i, item);
				}

				for (int i = 36; i < 45; i++) {
					ItemStack it = GUI.createItemGlass(Material.STAINED_GLASS_PANE, GlassColor.getGlassColor(bottom_row), "&7" );
					GuiItem item =  ItemBuilder.from(it).asGuiItem(event -> event.setCancelled(true));
					CrateGUI.setItem(i, item);
				}

				for(GuiItem material : antal) {
					CrateGUI.addItem(material);
				}

				CrateGUI.open(player);
			}
		}
	}



	public static void resetOpenStatus(Player player) {
		openingCrate.remove(player);
	}
}