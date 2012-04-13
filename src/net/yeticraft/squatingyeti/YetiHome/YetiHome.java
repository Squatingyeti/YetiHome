package net.yeticraft.squatingyeti.YetiHome;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldedit.Vector;

public class YetiHome extends JavaPlugin {
	public HomeManager homes;
	public CoolDownManager cooldowns;
	public CommandExecutor commandExecutor;
	
	private YetiHomePlayerListener playerListener = new YetiHomePlayerListener(this);

	@Override
	public void onDisable() {
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " unloaded.", this);
	}
	
	@Override
	public void onEnable() {
		String pluginDataPath = this.getDataFolder().getAbsolutePath() + File.separator;
		
		File dataPath = new File(pluginDataPath);
		if (!dataPath.exists()) {
				dataPath.mkdirs();
		}
		
		this.homes = new HomeManager(new File(pluginDataPath + "homes.txt"), this);
		this.cooldowns = new CoolDownManager(new File(pluginDataPath + "cooldowns.txt"),this);

		this.commandExecutor = new CommandExecutor(this);
		
		if (!YetiPermissions.initialize(this)) return;
		disableEssentials();
		Settings.initialize(this);
		Settings.loadSettings(new File(pluginDataPath + "config.yml"));
		YetiHomeEconManager.initialize(this);
		
		this.homes.loadHomes();
		this.cooldowns.loadCooldowns();
		
		registerEvents();
		
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " loaded.",this);
	}
	
	private void disableEssentials() {
		Plugin essentialsHome = getServer().getPluginManager().getPlugin("EssentialsHome");
		
		if (essentialsHome != null) {
			if (!essentialsHome.isEnabled()) {
				getServer().getPluginManager().enablePlugin(essentialsHome);
			}
			getServer().getPluginManager().disablePlugin(essentialsHome);
		}
	}
	
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command must be run in game");
		} else {
			onCommandFromPlayer((Player) sender, cmd, commandLabel, args);
		}
		return true;
	}
	
	private void onCommandFromPlayer(Player player, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("home")) {
			
			if (args.length == 0) {
				this.commandExecutor.goDefaultHome(player);
			
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);
				
				if (homeArgs.length >1 ) {
						this.commandExecutor.goPlayerNamedHome(player, homeArgs[0], homeArgs[1]);
				} else { 
						this.commandExecutor.goNamedHome(player, homeArgs[0]);
				}
				
			} else {
					Settings.sendMessageTooManyParameters(player);
			}
		} else if (cmd.getName().equalsIgnoreCase("sethome")) {
			if (!compliantHome(player.getLocation(), player.getName())) {
				player.sendMessage(ChatColor.RED + "This area does not smell friendly");
			}
			if (args.length == 0) {
				this.commandExecutor.setDefaultHome(player);
			
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);
				
				if (homeArgs.length > 1) {
						this.commandExecutor.setPlayerNamedHome(player, homeArgs[0], homeArgs[1]);
				} else {
						this.commandExecutor.setNamedHome(player, homeArgs[0]);
				}
				
			} else {
					Settings.sendMessageTooManyParameters(player);
			}
		} else if (cmd.getName().compareToIgnoreCase("deletehome") == 0) {
				
				if (args.length == 0) {
					this.commandExecutor.deleteDefaultHome(player);
				} else if (args.length == 1) {
					String homeArgs[] = Util.splitHome(args[0]);
					
					if (homeArgs.length >1) {
						this.commandExecutor.deletePlayerNamedHome(player, homeArgs[0], homeArgs[1]);
					} else {
						this.commandExecutor.deleteNamedHome(player, homeArgs[0]);
					}
				} else {
						Settings.sendMessageTooManyParameters(player);
				}
			} else if (cmd.getName().compareToIgnoreCase("listhomes") == 0){

				if (args.length == 0) {
					this.commandExecutor.listHomes(player);
				} else if (args.length == 1) {
					this.commandExecutor.listPlayerHomes(player, args[0]);
				} else {
					Settings.sendMessageTooManyParameters(player);
				}
			}
		}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	public boolean compliantHome(Location loc, String player) {
		
		WorldGuardPlugin worldGuard = getWorldGuard();
		
		Vector pt = toVector(loc);
		
		RegionManager regionManager = worldGuard.getRegionManager(loc.getWorld());
		List<String> regionSet = regionManager.getApplicableRegionsIDs(pt);
		
		PermissionManager pexPlayer = PermissionsEx.getPermissionManager();
		PermissionUser pPlayer = pexPlayer.getUser(player);
		
		boolean inRegionGroup = false;
		
		for(String currentRegion : regionSet){
	
			if (pPlayer.inGroup(currentRegion.toLowerCase())) inRegionGroup = true;
			
		}
		
	if (inRegionGroup) return true;
	return false;
	
	}
}