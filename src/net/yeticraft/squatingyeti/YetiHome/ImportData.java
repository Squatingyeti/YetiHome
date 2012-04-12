package net.yeticraft.squatingyeti.YetiHome;

import java.io.BufferedWriter;
import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ImportData {
	// @SuppressWarnings("unchecked") // Likely my javafu is weak here.
	public static void importHomesFromEssentials(BufferedWriter out, YetiHome plugin) {
		File essentialsDir = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");

		if (essentialsDir.exists()) {
			Messaging.logInfo("Importing home locations from Essentials...", plugin);
			File[] userFiles = essentialsDir.listFiles();

			for (File userFile : userFiles) {
				try {
						Messaging.logInfo("Importing from " + userFile.getName(), plugin);
						String user = userFile.getName().replaceAll("\\.yml", "");
						YamlConfiguration userConfig = new YamlConfiguration();
						userConfig.load(userFile);

						// Load new Essentials home format.
						ConfigurationSection homeWorlds = userConfig.getConfigurationSection("homes.home");
						//Map<String, ConfigurationNode> homeWorlds = userConfig.getNodes("home.worlds");

						if (homeWorlds != null) {
									try {
										double X = homeWorlds.getDouble("x", 0);
										double Y = homeWorlds.getDouble("y", 0);
										double Z = homeWorlds.getDouble("z", 0);
										double pitch = homeWorlds.getDouble("pitch", 0);
										double yaw = homeWorlds.getDouble("yaw", 0);
										String world = homeWorlds.getString("world");
										
										out.write(user + ";" + X + ";" + Y + ";" + Z + ";" + pitch + ";" + yaw + ";" + world + ";" + Util.newLine());
									} catch (Exception e) {
											// This entry failed. Ignore and continue.
											Messaging.logInfo("Failed to import home location!", plugin);
											e.printStackTrace();
									}
								}
				} catch (Exception e) {
						Messaging.logWarning("Error importing from " + userFile.getName() + ": " + e.getMessage(), plugin);
				}
			}
		}
	}
}