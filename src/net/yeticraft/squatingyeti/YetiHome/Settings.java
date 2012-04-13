package net.yeticraft.squatingyeti.YetiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static YamlConfiguration Config;
	private static YetiHome plugin;
	private static boolean permissiveGroupHandling = true;

	public static void initialize(YetiHome plugin) {
	Settings.plugin = plugin;
	}

    public static void loadSettings(File configFile) {
	// Create configuration file if not exist
    	if (!configFile.exists()) {
    		try {
    			configFile.getParentFile().mkdirs();

    			BufferedReader in = new BufferedReader(new InputStreamReader(plugin.getResource("config.yml")));
    			BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
    			String line;

    			while ((line = in.readLine()) != null) {
    					out.write(line + Util.newLine());
    			}

    			in.close();
    			out.close();
    		} catch (Exception e) {
    				Messaging.logWarning("Could not write the default config file.", plugin);
    				plugin.getServer().getPluginManager().disablePlugin(plugin);
    		}
    	}

     // Reading from YML file
    	Config = new YamlConfiguration();
    	try {
    			Config.load(configFile);
    	} catch (Exception e) {
    			Messaging.logSevere("Could not load the configuration file: " + e.getMessage(), plugin);
    	}

    	permissiveGroupHandling = isPermissiveGroupHandlingEnabled();

    }


    public static int getSettingInt(Player player, String setting, int defaultValue, boolean findMax, boolean negativeMax) {
    	// Get the player group
    	String[] playerGroups = YetiPermissions.getGroups(player.getWorld().getName(), player.getName());

    	List <Integer> settings = new ArrayList<Integer>();

    	if (playerGroups != null)
    	{
    			for (int i=0; i<playerGroups.length; i++)

    				// Player group found
    				if (Config.isSet("YetiHome.groups." + playerGroups[i] + "." + setting))
    					// Settings for player group exists.
    					settings.add(Config.getInt("YetiHome.groups." + playerGroups[i] + "." + setting, defaultValue));
    	}
    	if (settings.size() == 0)
    		// Get from default
    		settings.add(Config.getInt("YetiHome.default." + setting, defaultValue));

    	int settingValue = settings.get(0);
    	for (int i=1; i<settings.size(); i++)
    	{
    		int test=settings.get(i);
    		if ((test == -1) && findMax && negativeMax)
    			return -1;
    		if ((settings.get(i) > settingValue)==(findMax))
    		{
    			settingValue = settings.get(i);
    		}
    	}
    	return settingValue;
    }

    public static boolean getSettingBoolean(Player player, String setting, boolean defaultValue, boolean findTrue) {
    		// Get the player group
    		String[] playerGroups = YetiPermissions.getGroups(player.getWorld().getName(), player.getName());

    		boolean settingValue = !findTrue; // If true trumps false, start with false and look for true. If false trumps true, start with true and look for false.

    		if (playerGroups != null)
    		{
    			for (int i=0; i<playerGroups.length; i++)

    				// Player group found
    				if (Config.isSet("YetiHome.groups." + playerGroups[i] + "." + setting))
    					// Settings for player group exists.
    					if (Config.getBoolean("YetiHome.groups." + playerGroups[i] + "." + setting, defaultValue)!=settingValue)
    						return !settingValue; // If it's different from the starting point, then it trumps the starting point and should be returned
    			return settingValue; // Otherwise, nothing was found so return the starting value;
    		}
    		return defaultValue; // None of the groups even had this setting; return default.
    }

    public static boolean isHomeOnDeathEnabled() {
    	return Config.getBoolean("YetiHome.enableHomeOnDeath", false);
    }

    public static boolean isEconomyEnabled() {
    	return Config.getBoolean("YetiHome.enableEconomy", false);
    }

    public static boolean isPermissiveGroupHandlingEnabled() {
    	return Config.getBoolean("YetiHome.permissiveGroupHandling", true);
    }

    public static int getSetNamedHomeCost(Player player) {
    	return getSettingInt(player, "setNamedHomeCost", 0, !permissiveGroupHandling, false);
    }

    public static int getSetHomeCost(Player player) {
    	return getSettingInt(player, "setHomeCost", 0, !permissiveGroupHandling, false);
    }

    public static int getHomeCost (Player player) {
    	return getSettingInt(player, "homeCost", 0, !permissiveGroupHandling, false);
    }

    public static int getNamedHomeCost(Player player) {
    	return getSettingInt(player, "namedHomeCost", 0, !permissiveGroupHandling, false);
    }

    public static int getOthersHomeCost(Player player) {
    	return getSettingInt(player, "othersHomeCost", 0, !permissiveGroupHandling, false);
    }

    public static int getSettingCooldown(Player player) {
    	return getSettingInt(player, "cooldown", 0, !permissiveGroupHandling, false);
    }

    public static int getSettingMaxHomes(Player player) {
    	return getSettingInt(player, "maxhomes", -1, !permissiveGroupHandling, true);
    }

    public static void sendMessageTooManyParameters(CommandSender sender) {
    	String message = Config.getString("YetiHome.messages.tooManyParameters", null);

    	if (message != null) Messaging.sendSuccess(sender, message);
    }

    public static void sendMessageDefaultHomeSet(CommandSender sender) {
    	String message = Config.getString("YetiHome.messages.defaultHomeSetMessage", null);

    	if (message != null) Messaging.sendSuccess(sender, message);
    }

    public static void sendMessageCannotDeleteDefaultHome(CommandSender sender) {
    	String message = Config.getString("YetiHome.messages.cannotDeleteDefaultHomeMessage", null);

    	if (message != null) Messaging.sendError(sender, message);
    }

    public static void sendMessageHomeSet(CommandSender sender, String home) {
    	String message = Config.getString("YetiHome.messages.homeSetMessage", null);

    	if (message != null) {
    		Messaging.sendSuccess(sender, message.replaceAll("\\{HOME\\}", home));
    	}
    }

    public static void sendMessageHomeDeleted(CommandSender sender, String home) {
    	String message = Config.getString("YetiHome.messages.homeDeletedMessage", null);

    	if (message != null) {
    		Messaging.sendSuccess(sender, message.replaceAll("\\{NAME\\}", home).replaceAll("\\{HOME\\}", home));
    	}
    }

    public static void sendMessageCooldown(CommandSender sender, int timeLeft) {
    	String message = Config.getString("YetiHome.messages.cooldownMessage", null);

    	if (message != null) {
    		Messaging.sendError(sender, message.replaceAll("\\{SECONDS\\}", Integer.toString(timeLeft)));
    	}
    }

    public static void sendMessageMaxHomes(CommandSender sender, int currentHomes, int maxHomes) {
    	String message = Config.getString("YetiHome.messages.tooManyHomesMessage", null);

    	if (message != null) {
    		Messaging.sendError(sender, message.replaceAll("\\{CURRENT\\}", Integer.toString(currentHomes)).replaceAll("\\{MAX\\}", Integer.toString(maxHomes)));
    	}
    }

    public static void sendMessageNoHome(CommandSender sender, String home) {
    	String message = Config.getString("YetiHome.messages.noHomeMessage", null);

    	if (message != null) {
    		Messaging.sendError(sender, message.replaceAll("\\{HOME\\}", home));
    	}
    }

    public static void sendMessageNoDefaultHome(CommandSender sender) {
    	String message = Config.getString("YetiHome.messages.noDefaultHomeMessage", null);

    	if (message != null) Messaging.sendError(sender, message);
    }

    public static void sendMessageNoPlayer(CommandSender sender, String targetPlayer) {
    	String message = Config.getString("YetiHome.messages.noPlayerMessage", null);

    	if (message != null) {
    		Messaging.sendError(sender, message.replaceAll("\\{PLAYER\\}", targetPlayer));
    	}
    }

    public static void sendMessageNotEnoughMoney(Player player, double amount) {
    	String message = Config.getString("YetiHome.messages.econNotEnoughFunds", null);

    	if (message != null) {
    		Messaging.sendError(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
    	}
    }

    public static void sendMessageDeductForHome(Player player, double amount) {
    	String message = Config.getString("YetiHome.messages.econDeductedForHome", null);
    	if (message != null) {
    		Messaging.sendSuccess(player,message.replaceAll("\\{AMOUNT\\}", amount+""));
    	}
    }

    public static void sendMessageDeductForSet(Player player, double amount) {
    	String message = Config.getString("YetiHome.messages.econDeductForSet", null);
    	if (message != null) {
    		Messaging.sendSuccess(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
    	}
    }
    
    public static void sendMessageHomeList(CommandSender sender, String homeList) {
    	String message = Config.getString("YetiHome.messages.homeListMessage", null);

    	if (message != null) {
    		Messaging.sendSuccess(sender, message.replaceAll("\\{LIST\\}", homeList));
    	}
    }
    
    public static void sendMessageOthersHomeList(CommandSender sender, String player, String homeList) {
    	String message = Config.getString("YetiHome.messages.homeListOthersMessage", null);

    	if (message != null) {
    		Messaging.sendSuccess(sender, message.replaceAll("\\{PLAYER\\}", player).replaceAll("\\{LIST\\}", homeList));
    	}
    }
}