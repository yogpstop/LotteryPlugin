package org.yogpstop.lp;

import org.bukkit.entity.Player;

public class PlayerSender implements Sender {
	Player p;

	public PlayerSender(Player ply) {
		p = ply;
	}

	@Override
	public String getPlayerName() {
		return p.getName();
	}

	@Override
	public void sendMessage(String message) {
		p.sendMessage(message);
	}

}
