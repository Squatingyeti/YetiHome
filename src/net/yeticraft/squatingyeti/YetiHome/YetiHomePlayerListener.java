package net.yeticraft.squatingyeti.YetiHome;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class YetiHomePlayerListener implements Listener {
	YetiHome plugin;

	public YetiHomePlayerListener(YetiHome plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (YetiPermissions.has(player, "yetihome.homeondeath") && Settings.isHomeOnDeathEnabled()) {
			Location location = plugin.homes.getHome(player, "");

			if (location != null) {
				event.setRespawnLocation(location);
			}
		}
	}
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (Settings.loadChunks() == true) {
            World world = event.getPlayer().getWorld();
            Chunk chunk = world.getChunkAt(event.getTo());
            int x = chunk.getX();
            int z = chunk.getZ();
            world.refreshChunk(x, z);
        }
    } 
}