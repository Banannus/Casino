package dk.banannus.tasks;

import dk.banannus.Casino;
import dk.banannus.events.openCrate;
import dk.banannus.utils.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class crateAnimation {

	private Location armorStandLoc;
	public void Animation(Location loc, String head, Player player, BlockFace blockface) {
		Location startLoc = loc.clone().add(0.5,-0.5,0.5);
		Location endLoc = startLoc.clone().add(0,2,0);
		armorStandLoc = loc.add(0.5, -0.5, 0.5);

		ItemStack crateHead = GUI.getSkull(head);

		ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setArms(true);
		stand.setHelmet(crateHead);
		stand.setMarker(false);
		stand.setMetadata("unstealable", new FixedMetadataValue(Casino.getInstance(), "unstealable"));

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
		double rotationRadians = Math.toRadians(angleY);
		AtomicReference<EulerAngle> rotation = new AtomicReference<>(new EulerAngle(0, rotationRadians, 0));

		AtomicReference<Double> y = new AtomicReference<>(armorStandLoc.getY());
		double x = armorStandLoc.getX();
		double z = armorStandLoc.getZ();

		long delay = 10L;
		AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

		new Thread(() -> {
			int i = 0;

			while (i < 41) {

				y.updateAndGet(v -> (double) (v + 2.0 / 40.0));
				rotation.updateAndGet(r -> r.setY(r.getY() + Math.toRadians(8.7)));
				Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
					stand.teleport(new Location(loc.getWorld(), x, y.get(), z));
					stand.setHeadPose(rotation.get());
				}, 1L);

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				long timePassed = System.currentTimeMillis() - startTime.get();
				int framesPassed = (int) (timePassed / delay);
				int framesToSkip = framesPassed - i;
				if (framesToSkip > 0) {
					try {
						Thread.sleep(framesToSkip * delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startTime.set(System.currentTimeMillis() - (framesPassed * delay));
				}

				i++;
			}

			Bukkit.getScheduler().runTaskLater(Casino.getInstance(), () -> {
				stand.remove();
				openCrate.resetOpenStatus(player);
				player.sendMessage(String.valueOf(startLoc));

				Vector direction = endLoc.clone().subtract(startLoc).toVector().normalize();

				double distance = startLoc.distance(endLoc);
				for (double j = 0; j < distance; j += 0.2) {
					Location particleLoc = startLoc.clone().add(direction.clone().multiply(j));
					new ParticleBuilder(ParticleEffect.FLAME, player.getLocation())
							.setOffsetY(1f)
							.setSpeed(0.1f)
							.display();
				}
			}, 20L);
		}).start();
	}
}
