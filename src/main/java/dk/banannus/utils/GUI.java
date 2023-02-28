package dk.banannus.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUI {
	public static ItemStack createMaterial(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(Chat.colored(name));
		List<String> coloredLore = new ArrayList<>();
		for (String line : lore) {
			coloredLore.add(Chat.colored(line));
		}

		meta.setLore(coloredLore);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack createItemStack(final boolean enabled, final ItemStack item, final String name, final String... lore) {
		if(!enabled) {
			ItemStack disabled_item = new ItemStack(Material.INK_SACK, 1, (byte)8);
			ItemMeta meta = disabled_item.getItemMeta();
			meta.setDisplayName(Chat.colored(name));
			List<String> coloredLore = new ArrayList<>();
			for (String line : lore) {
				coloredLore.add(Chat.colored(line));
			}
			meta.setLore(coloredLore);

			disabled_item.setItemMeta(meta);
			return disabled_item;
		}


		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(Chat.colored(name));
		List<String> coloredLore = new ArrayList<>();
		for (String line : lore) {
			coloredLore.add(Chat.colored(line));
		}

		meta.setLore(coloredLore);

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack createItemGlass(boolean enabled, Material material, int GlassColor, String displayName, String... loreString) {
		List<String> lore = new ArrayList<>();
		ItemStack item = new ItemStack(material, 1, (short) GlassColor);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Chat.colored(displayName));

		for (String s : loreString)
			lore.add(Chat.colored(s));

		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createDyedItem(DyeColor color) {
		ItemStack ink = new ItemStack(Material.INK_SACK);
		ink.setData(new Dye(color));
		return ink;
	}

	public static ItemStack getSkull(String url) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		if(url.isEmpty())return head;


		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}


	public static ItemStack getPlayerSkull(String name) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwner(name);
		head.setItemMeta(meta);
		return head;
	}


}