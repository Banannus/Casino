package dk.banannus.tasks;

import dk.banannus.Casino;
import dk.banannus.events.openCrate;
import dk.banannus.utils.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.concurrent.atomic.AtomicReference;


public class crateAnimation {

	public void Animation(Location loc, String head, Player player, BlockFace blockface) {

		// Diverse variabler
		Location startLoc = loc.clone().add(0.5, 1, 0.5);
		Location endLoc = startLoc.clone().add(0, 2, 0);
		Location hologramLoc = loc.clone().add(0.5,-0.75,0.5);
		World world = player.getWorld();
		Location armorStandLoc = loc.add(0.5, -0.5, 0.5);

		ItemStack crateHead = GUI.getSkull(head);

		// Spawn armor standen med forskellige værdier
		ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setArms(true);
		stand.setHelmet(crateHead);
		stand.setMarker(false);
		stand.setMetadata("unstealable", new FixedMetadataValue(Casino.getInstance(), "unstealable"));

		// Tjek hvad retning den skal vende ift. hvor spilleren har klikket
		double angleY;
		switch (blockface) {
			case EAST:
				angleY = -90;
				break;
			case WEST:
				angleY = 90;
				break;
			case NORTH:
				angleY = 180;
				break;
			default:
				angleY = 0;
		}

		// Variabler til armor stand rotation og lokation.
		double rotationRadians = Math.toRadians(angleY);
		AtomicReference<EulerAngle> rotation = new AtomicReference<>(new EulerAngle(0, rotationRadians, 0));

		AtomicReference<Double> y = new AtomicReference<>(armorStandLoc.getY());
		double x = armorStandLoc.getX();
		double z = armorStandLoc.getZ();

		// Delay
		long delay = 40L;

		new Thread(() -> {
			int i = 0;

			// Start af animation
			while (i < 41) {

				// Regn rotation osv ud samt teleport og ryk armor standen.
				y.updateAndGet(v -> (v + 2.0 / 40.0));
				rotation.updateAndGet(r -> r.setY(r.getY() + Math.toRadians(8.7)));
				Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
					stand.teleport(new Location(loc.getWorld(), x, y.get(), z));
					stand.setHeadPose(rotation.get());
				}, 1L);


				// Partikler og lyde mens animationen forgår.
				if (i == 0 || i == 20 || i == 40) {
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
					Location standLoc = stand.getEyeLocation().clone();
					new ParticleBuilder(ParticleEffect.ENCHANTMENT_TABLE, standLoc)
							.setAmount(30)
							.setOffset(0.5F, 0.5F, 0.5F)
							.setSpeed(0.05f)
							.display();
				}

				// Delay
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				i++;
			}

			// Efter Animation - Partikler -> CrateReward
			Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
				openCrate.resetOpenStatus(player);

				Vector direction = endLoc.clone().subtract(startLoc).toVector().normalize();

				double distance = startLoc.distance(endLoc);
				for (double j = 0; j < distance; j += 0.2) {
					Location particleLoc = startLoc.clone().add(direction.clone().multiply(j));
					new ParticleBuilder(ParticleEffect.FLAME, particleLoc)
							.setOffsetY(1f)
							.setSpeed(0.1f)
							.display();

				}
				try {
					Thread.sleep(70);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				stand.remove();
				world.createExplosion(endLoc.getX(), endLoc.getY(), endLoc.getZ(), 1, false, false);
				new ParticleBuilder(ParticleEffect.SMOKE_NORMAL, endLoc)
						.setAmount(50)
						.setOffset(1, 1, 1)
						.setSpeed(0.05f)
						.display();

				new crateRewards(player, hologramLoc);

			}, 15L);
		}).start();
	}
}
