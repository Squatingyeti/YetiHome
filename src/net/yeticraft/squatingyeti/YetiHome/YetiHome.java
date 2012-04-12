package net.yeticraft.squatingyeti.YetiHome;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
			}
		}
	}