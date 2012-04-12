package net.yeticraft.squatingyeti.YetiHome;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandExecutor {
	YetiHome plugin;
	
	public CommandExecutor(YetiHome plugin) {
		this.plugin = plugin;
	}
	
	public void goDefaultHome(Player player) {
		if (player.hasPermission("yetihome.defaulthome.go")) {
		}
		
		Date cooldown = plugin.cooldowns.getCooldown(player.getName());
		
		if (cooldown != null && !player.hasPermission("yetihome.ignore.cooldown")) {
			Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getTime() - new Date().getTime()), 1000) / 1000);
			return;
		}
		
		Location teleport = plugin.homes.getHome(player, "");
		
		if (teleport != null) {
			Util.teleportPlayer(player, teleport, plugin);
			
			int cooldownTime = Settings.getSettingCooldown(player);
			if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
		
	} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to default home location. Permission denied.", plugin);
		}
	}
	
	public void goNamedHome(Player player, String home) {
		if (YetiPermissions.has(player, "yetihome.namedhome.go")) {
			
			Date cooldown = plugin.cooldowns.getCooldown(player.getName());
			
			if (cooldown != null && !YetiPermissions.has(player, "yetihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getTime() - new Date().getTime()), 1000) / 1000);
				return;
			}
			
			Location teleport = plugin.homes.getHome(player, home);
			
			if (teleport != null) {
				Util.teleportPlayer(player, teleport, plugin);
				
				int cooldownTime = Settings.getSettingCooldown(player);
				if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to teleport to home location [" + home + "]. Permission denied.", plugin);
		}
	}
	
	public void setDefaultHome(Player player) {
		if (player.hasPermission("yetihome.defaulthome.set")) {
			int numHomes = plugin.homes.getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			
			if (numHomes < maxHomes || plugin.homes.getHome(player, "") != null) {
				plugin.homes.addHome(player, "", player.getLocation());
				Settings.sendMessageDefaultHomeSet(player);
				Messaging.logInfo("Player " + player.getName() + " set default home location", plugin);
			}
		}else {
				Messaging.logInfo("Player " + player.getName() + " tried to set default home location, but was denied.", plugin);
				
			}
		}
	
	public void setNamedHome(Player player, String home) {
		if (player.hasPermission("yetihome.namedhome.set")) {
			int numHomes = plugin.homes.getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			
			if (numHomes < maxHomes || plugin.homes.getHome(player, home) != null) {
			
			plugin.homes.addHome(player, home, player.getLocation());
			Settings.sendMessageHomeSet(player, home);
			Messaging.logInfo("Player " + player.getName() + " set home location [" + home + "]", plugin);
		} else {
			Settings.sendMessageMaxHomes(player, numHomes, maxHomes);
			Messaging.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Player has too many already.", plugin);
		}
	} else {
		Messaging.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Permission denied.", plugin);
		}
	}
	
	public void deleteDefaultHome(Player player) {
		if (plugin.homes.getHome(player, "") != null) {
			Settings.sendMessageCannotDeleteDefaultHome(player);
		} else {
				Settings.sendMessageNoDefaultHome(player);
			}
			Messaging.logInfo("Player " + player.getName() + " tried to delete deafult home location. Denied.", plugin);
	}
	
	public void deleteNamedHome(Player player, String home) {
		if (YetiPermissions.has(player, "yetihome.namedhome.delete")) {
			if (plugin.homes.getHome(player, home) != null) {
					plugin.homes.removeHome(player, home);
					Settings.sendMessageHomeDeleted(player, home);
					Messaging.logInfo("Player " + player.getName() + " deleted home location [" + home + "].", plugin);
			} else {
					Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete home location [" + home + "]. Permission denied.", plugin);
		}
	}
	
	public void goPlayerNamedHome(Player player, String owner, String home) {
		if (player.hasPermission("yetihome.othershome.go")) {

			if (plugin.homes.getUserExists(owner)) {
				Location teleport = plugin.homes.getHome(owner, home);


				if (teleport != null) {
					Util.teleportPlayer(player, teleport, plugin);
				}else {
					Settings.sendMessageNoHome(player, owner + ":" + home);
				}
			} else {
					Settings.sendMessageNoPlayer(player, owner);
				}
		
		} else {
		Messaging.logInfo("Player " + player.getName() + " tried to warp to " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}
	
	public void setPlayerNamedHome(Player player, String owner, String home) {
		if (player.hasPermission("yetihome.othershome.set")) {
			plugin.homes.addHome(owner, home, player.getLocation());
			Settings.sendMessageHomeSet(player, owner + ":" + home);
			Messaging.logInfo("Player " + player.getName() + " set player " + owner + "'s home location [" + home + "]", plugin);
		} else {
		Messaging.logInfo("Player " + player.getName() + " tried to set player " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}
	
	public void deletePlayerNamedHome(Player player, String owner, String home) {
		if (player.hasPermission("yetihome.othershome.delete")) {
			if (plugin.homes.getHome(owner, home) != null) {
				plugin.homes.removeHome(owner, home);
				Settings.sendMessageHomeDeleted(player, owner + ":" + home);
				Messaging.logInfo("Player " + player.getName() + " deleted " + owner + "'s home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}
}