package it.giospezia.spicyqol.sit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitManager {

    private final Map<UUID, ArmorStand> seated = new HashMap<>();
    private static final double SIT_OFFSET_Y = 0.25;

    public boolean isSeated(Player p) {
        return seated.containsKey(p.getUniqueId());
    }

    public void toggleSit(Player p) {
        if (isSeated(p)) standUp(p);
        else sit(p);
    }

    public void sit(Player p) {
        if (isSeated(p)) return;

        Location base = safeBase(p);
        if (base == null) {
            p.sendMessage("§cNon puoi sederti qui.");
            return;
        }

        Location spawn = base.clone().add(0, SIT_OFFSET_Y, 0);

        // ✅ sempre valido
        ArmorStand stand = spawnStand(spawn, p.getLocation().getYaw());

        stand.addPassenger(p);
        seated.put(p.getUniqueId(), stand);
    }

    public void standUp(Player p) {
        ArmorStand stand = seated.remove(p.getUniqueId());
        if (stand == null) return;

        if (!stand.isDead()) {
            for (Entity e : stand.getPassengers()) {
                stand.removePassenger(e);
            }
            stand.remove();
        }
    }

    public void cleanup(Player p) {
        standUp(p);
    }

    public void cleanupAll() {
        for (ArmorStand stand : seated.values()) {
            if (stand != null && !stand.isDead()) stand.remove();
        }
        seated.clear();
    }

    private Location safeBase(Player p) {
        Location l = p.getLocation();
        World w = l.getWorld();

        int x = l.getBlockX();
        int z = l.getBlockZ();
        int groundY = l.getBlockY() - 1;

        Block ground = w.getBlockAt(x, groundY, z);
        Material g = ground.getType();

        if (!g.isSolid()) return null;
        if (g == Material.WATER || g == Material.LAVA) return null;

        return new Location(w, x + 0.5, groundY + 1.0, z + 0.5, l.getYaw(), 0f);
    }

    private ArmorStand spawnStand(Location loc, float yaw) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        as.setVisible(false);
        as.setMarker(true);
        as.setGravity(false);
        as.setSmall(true);
        as.setInvulnerable(true);
        as.setRotation(yaw, 0f);

        return as;
    }
}