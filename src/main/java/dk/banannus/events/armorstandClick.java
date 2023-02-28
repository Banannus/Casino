package dk.banannus.events;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class armorstandClick implements Listener {

	@EventHandler
	public void onPlayerInteractArmorStand(PlayerArmorStandManipulateEvent event) {

		if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			ArmorStand armorStand = event.getRightClicked();
			if (armorStand.hasMetadata("unstealable")) {
				event.setCancelled(true);
			}
		}
	}
}

