package org.yogpstop.lp;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleSender implements Sender {
	private CommandSender cs;

	public ConsoleSender(CommandSender c) {
		cs = c;
	}

	@Override
	public String getPlayerName() {
		return Bukkit.getOperators().iterator().next().getName();
	}

	@Override
	public void sendMessage(String message) {
		cs.sendMessage(message);
	}
}
