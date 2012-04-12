package net.yeticraft.squatingyeti.YetiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ImportData {
	// @SuppressWarnings("unchecked") // Likely my javafu is weak here.
	public static void importHomesFromEssentials(BufferedWriter out, YetiHome plugin) {
		File essentialsDir = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");

		if (essentialsDir.exists()) {
			Messaging.logInfo("Importing home locations from Essentials...", plugin);
			File[] userFiles = essentialsDir.listFiles();
			List<?> homeLocation; // <?> should be <Object>. But I couldn't compile that way. So, my javafu is weak.

			for (File userFile : userFiles) {
				try {
						Messaging.logInfo("Importing from " + userFile.getName(), plugin);
						String user = userFile.getName().replaceAll("\\.yml", "");
						YamlConfiguration userConfig = new YamlConfiguration();
						userConfig.load(userFile);

						// Load old Essentials home format.
						homeLocation = userConfig.getList("home");

						if (homeLocation != null && !homeLocation.isEmpty()) {
							try {
								double X = (Double) homeLocation.get(0);
								double Y = (Double) homeLocation.get(1);
								double Z = (Double) homeLocation.get(2);
								double pitch = (Double) homeLocation.get(4);
								double yaw = (Double) homeLocation.get(3);
								String world = (String) homeLocation.get(5);

								out.write(user + ";" + X + ";" + Y + ";" + Z + ";" + pitch + ";" + yaw + ";" + world + ";" + Util.newLine());
							} catch (Exception e) {
									// This entry failed. Ignore and continue.
									Messaging.logInfo("Failed to import home location!", plugin);
									e.printStackTrace();
							}
						}

						// Load new Essentials home format.
						ConfigurationSection homeWorlds = userConfig.getConfigurationSection("home.worlds");
						//Map<String, ConfigurationNode> homeWorlds = userConfig.getNodes("home.worlds");

						if (homeWorlds != null) {
							for (String homeWorld : homeWorlds.getKeys(false)) {
								ConfigurationSection homeData = userConfig.getConfigurationSection("home.worlds." + homeWorld);

								if (homeData != null) {
									try {
										double X = homeData.getDouble("x", 0);
										double Y = homeData.getDouble("y", 0);
										double Z = homeData.getDouble("z", 0);
										double pitch = homeData.getDouble("pitch", 0);
										double yaw = homeData.getDouble("yaw", 0);
										String world = homeData.getString("world");

										out.write(user + ";" + X + ";" + Y + ";" + Z + ";" + pitch + ";" + yaw + ";" + world + ";" + Util.newLine());
									} catch (Exception e) {
											// This entry failed. Ignore and continue.
											Messaging.logInfo("Failed to import home location!", plugin);
											e.printStackTrace();
									}
								}
							}
						}
				} catch (Exception e) {
						Messaging.logWarning("Error importing from " + userFile.getName() + ": " + e.getMessage(), plugin);
				}
			}
		}
	}

	public static void importHomesFromMultipleHomes(BufferedWriter out, YetiHome plugin) {
		File essentialsDir = new File("plugins" + File.separator + "MultipleHomes" + File.separator + "Homes");

		if (essentialsDir.exists()) {
			Messaging.logInfo("Importing home locations from MultipleHomes...", plugin);
			File[] userFiles = essentialsDir.listFiles();

			for (File userFile : userFiles) {
					String name = userFile.getName().replaceAll("home\\_", "").replaceAll("\\.txt", "");

					try {
						FileReader fstream = new FileReader(userFile);
						BufferedReader reader = new BufferedReader(fstream);

						String line = reader.readLine().trim();

						while (line != null) {
							if (line.startsWith("~")) {
								String[] split = line.split(":");

								if (split.length == 2) {
									String user = split[0].substring(1);
									String[] values = split[1].split("_");
									double X = 0, Y = 0, Z = 0;
									float pitch = 0, yaw = 0;
									String world = "";
									int homeNumber;

									try {
										X = Double.parseDouble(values[0]);
										Y = Double.parseDouble(values[1]);
										Z = Double.parseDouble(values[2]);
										pitch = Float.parseFloat(values[4]);
										yaw = Float.parseFloat(values[3]);
										world = values[5];
										homeNumber = Integer.parseInt(name);

										if (homeNumber == 0) {
											out.write(user + ";" + X + ";" + Y + ";" + Z + ";" + pitch + ";" + yaw + ";" + world + ";" + Util.newLine());
										} else {
											out.write(user + ";" + X + ";" + Y + ";" + Z + ";" + pitch + ";" + yaw + ";" + world + ";" + homeNumber + Util.newLine());
										}
									} catch (Exception e) {
										// This entry failed. Ignore and continue.
										if (line!=null) {
											Messaging.logWarning("Failed to load home location! Line: " + line, plugin);
										}
									}
								}
							}

							line = reader.readLine();
						}

						reader.close();
					} catch (Exception e) {
						Messaging.logSevere("Failed to import homes from MultipleHomes!", plugin);
					}
			}
		}
	}
}