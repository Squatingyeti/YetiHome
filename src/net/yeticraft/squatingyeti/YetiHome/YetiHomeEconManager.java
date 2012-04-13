package net.yeticraft.squatingyeti.YetiHome;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class YetiHomeEconManager {

	public static EconomyHandler handler;
	public static Economy economy = null;
	public static YetiHome plugin;

	public enum EconomyHandler {
		VAULT, NONE
	}

	protected static void initialize(YetiHome plugin) {
		YetiHomeEconManager.plugin = plugin;
		
		if (Settings.isEconomyEnabled()) {
			Plugin pVault = plugin.getServer().getPluginManager().getPlugin("Vault");
			if (pVault != null){
				RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
				economy = economyProvider.getProvider();
				if (economy != null) {
					handler = EconomyHandler.VAULT;
					Messaging.logInfo("Economy system provided by: Vault v" + pVault.getDescription().getVersion() + " and " + economy.getName() + " v" + plugin.getServer().getPluginManager().getPlugin(economy.getName()).getDescription().getVersion() +"!", plugin);
				} else {
					handler = EconomyHandler.NONE;
					Messaging.logWarning("An economy plugin wasn't detected!", plugin);
				}
			} else {
				handler = EconomyHandler.NONE;
				Messaging.logWarning("An economy plugin wasn't detected!", plugin);
			}
		} else {
			handler = EconomyHandler.NONE;
		}
	}

	public static boolean hasEnough(String player, double amount) {
		if (handler == EconomyHandler.VAULT) {
			if (economy.isEnabled()) {
				return economy.has(player, amount);
			} else {
				return true;
			}
		} else
			return true;
	}

	public static boolean chargePlayer(String player, double amount) {
		if (handler == EconomyHandler.VAULT) {
			if (hasEnough(player, amount)) {
				if (economy.isEnabled()) {
					economy.withdrawPlayer(player, amount);
				}
				return true;
			} else
				return false;
		} else
			return true;
	}

	public static String formatCurrency(double amount) {
		if (handler == EconomyHandler.VAULT) {
			if ( economy.isEnabled() ) {
				return economy.format(amount);
			} else {
				return amount+"";
			}
		} else
			return amount+"";
	}
}