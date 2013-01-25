package org.yogpstop.lp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LotteryPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		getServer().getPluginManager()
				.registerEvents(new EventListener(), this);
		WhitelistHandler.init();
	}

	@Override
	public void onDisable() {
		WhitelistHandler.reset(new ConsoleSender(Bukkit.getConsoleSender()));
	}
}
