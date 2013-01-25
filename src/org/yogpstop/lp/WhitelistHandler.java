package org.yogpstop.lp;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class WhitelistHandler {
	private static ArrayList<ArrayList<String>> player;
	private static ArrayList<String> wl;
	private static ArrayList<String> splayer;

	protected static void enter(String s) {
		splayer.add(s);
	}

	protected static void init() {
		splayer = new ArrayList<String>();
		wl = new ArrayList<String>();
		player = new ArrayList<ArrayList<String>>();
		for (OfflinePlayer p : Bukkit.getWhitelistedPlayers())
			wl.add(p.getName());
	}

	protected static void reset(Sender snd) {
		for (ArrayList<String> a : player) {
			for (String s : a) {
				Bukkit.getOfflinePlayer(s).setWhitelisted(false);
			}
		}
		for (String s : wl) {
			Bukkit.getOfflinePlayer(s).setWhitelisted(true);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.isWhitelisted()) {
				p.kickPlayer("ホワイトリストをリセットしました");
			}
		}
		snd.sendMessage("ホワイトリストをリセットしました");
	}

	protected static void wllist(Sender snd) {
		snd.sendMessage("下記のプレイヤーが静的ホワイトリストに所属しています");
		StringBuffer sb = new StringBuffer();
		for (String s : wl) {
			sb.append(s);
			sb.append(' ');
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void wladd(ArrayList<String> player, Sender snd) {
		snd.sendMessage("下記のプレイヤーを静的ホワイトリストに追加しました");
		StringBuffer sb = new StringBuffer();
		for (String s : player) {
			Bukkit.getOfflinePlayer(s).setWhitelisted(true);
			wl.add(s);
			sb.append(s);
			sb.append(' ');
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void wldelete(ArrayList<String> player, Sender snd) {
		snd.sendMessage("下記のプレイヤーを静的ホワイトリストから削除しました");
		StringBuffer sb = new StringBuffer();
		for (String s : player) {
			Bukkit.getOfflinePlayer(s).setWhitelisted(false);
			wl.remove(s);
			sb.append(s);
			sb.append(' ');
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void start(int i, Sender snd) {
		reset(snd);
		player = new ArrayList<ArrayList<String>>(i);
		for (int j = 0; j < i; j++) {
			player.add(new ArrayList<String>());
		}
		snd.sendMessage("合計".concat(Integer.toString(i)).concat(
				"チームで応募受付を開始しました"));
	}

	protected static void set(int size, boolean wm, Sender s) {
		Random r = new Random();
		if (wm) {
			splayer.add(s.getPlayerName());
		}
		for (int z = 0; z < size; z++) {
			for (ArrayList<String> as : player) {
				if (splayer.size() < 1)
					continue;
				try {
					as.get(z);
				} catch (IndexOutOfBoundsException e) {
					int index = r.nextInt(splayer.size());
					String name = splayer.get(index);
					as.add(name);
					Bukkit.getOfflinePlayer(name).setWhitelisted(true);
					splayer.remove(index);
				}
			}
		}
		splayer = new ArrayList<String>();
		s.sendMessage("応募の締め切りと1チーム".concat(Integer.toString(size)).concat(
				"人での抽選が完了しました"));
		teams(s);
	}

	protected static void add(ArrayList<String> playername, int i, Sender snd) {
		snd.sendMessage("下記のプレイヤーを第".concat(Integer.toString(i + 1)).concat(
				"チームに追加しました"));
		StringBuffer sb = new StringBuffer();
		for (String s : playername) {
			player.get(i).add(s);
			Bukkit.getOfflinePlayer(s).setWhitelisted(true);
			sb.append(s);
			sb.append(' ');
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void addrand(ArrayList<String> playername, Sender snd) {
		snd.sendMessage("下記のプレイヤーをランダムでどこかのチームに追加しました");
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int z = 0; true; z++) {
			for (ArrayList<String> as : player) {
				if (playername.size() < 1)
					break;
				try {
					as.get(z);
				} catch (IndexOutOfBoundsException e) {
					int i = r.nextInt(playername.size());
					String s = playername.get(i);
					playername.remove(i);
					as.add(s);
					Bukkit.getOfflinePlayer(s).setWhitelisted(true);
					sb.append(s);
					sb.append(' ');
				}
			}
			if (playername.size() < 1)
				break;
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void team(int i, Sender snd) {
		snd.sendMessage("第".concat(Integer.toString(i + 1)).concat(
				"チームのプレイヤー一覧:"));
		StringBuffer sb = new StringBuffer();
		for (String s : player.get(i)) {
			sb.append(s);
			sb.append(' ');
		}
		snd.sendMessage(sb.toString().trim().replaceAll(" ", ","));
	}

	protected static void teams(Sender s) {
		for (int i = 0; i < player.size(); i++) {
			team(i, s);
		}
	}
}
