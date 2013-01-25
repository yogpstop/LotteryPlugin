package org.yogpstop.lp;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.server.ServerCommandEvent;

public class EventListener implements Listener {
	private static boolean recruitment;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		if (recruitment
				&& !Bukkit.getOfflinePlayer(e.getName()).isWhitelisted()
				&& !Bukkit.getOfflinePlayer(e.getName()).isBanned()) {
			WhitelistHandler.enter(e.getName());
			e.disallow(Result.KICK_WHITELIST, "応募に成功しました。");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled())
			return;
		String[] splitted = e.getMessage().trim().substring(1).split(" ");
		String command = splitted[0];
		String[] args = new String[splitted.length - 1];
		for (int i = 1; i < splitted.length; i++)
			args[i - 1] = splitted[i];
		try {
			if (onCommand(command, args, new PlayerSender(e.getPlayer()))) {
				e.setCancelled(true);
			} else {
				e.getPlayer().sendMessage(usage());
			}
		} catch (Exception ex) {
			if (ex instanceof NotMyCommandException) {
				return;
			}
			e.getPlayer().sendMessage(usage());
			Logger.getGlobal().log(Level.SEVERE, ex.toString());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerCommand(ServerCommandEvent e) {
		String[] splitted = e.getCommand().trim().split(" ");
		String command = splitted[0];
		String[] args = new String[splitted.length - 1];
		for (int i = 1; i < splitted.length; i++)
			args[i - 1] = splitted[i];
		try {
			if (onCommand(command, args, new ConsoleSender(e.getSender()))) {
				e.setCommand("nothingatall");
			} else {
				e.getSender().sendMessage(usage());
			}
		} catch (Exception ex) {
			if (ex instanceof NotMyCommandException) {
				return;
			}
			e.getSender().sendMessage(usage());
			Logger.getGlobal().log(Level.SEVERE, ex.toString());
		}
	}

	private static boolean onCommand(String command, String[] args, Sender s)
			throws Exception {
		if (command.equals("lottery")) {
			if (args.length == 0)
				return false;
			if (args[0].equals("start") && args.length == 2) {
				recruitment = true;
				WhitelistHandler.start(Integer.parseInt(args[1]), s);
				return true;
			} else if ((args[0].equals("set") || args[0].equals("setwm"))
					&& args.length == 2) {
				recruitment = false;
				WhitelistHandler.set(Integer.parseInt(args[1]),
						args[0].contains("wm"), s);
				return true;
			} else if (args[0].equals("add") && args.length >= 3) {
				ArrayList<String> players = new ArrayList<String>(
						args.length - 2);
				for (int i = 2; i < args.length; i++)
					players.add(args[i]);
				WhitelistHandler.add(players, Integer.parseInt(args[1]) - 1, s);
				return true;
			} else if (args[0].equals("addrand") && args.length >= 2) {
				ArrayList<String> players = new ArrayList<String>(
						args.length - 1);
				for (int i = 1; i < args.length; i++)
					players.add(args[i]);
				WhitelistHandler.addrand(players, s);
				return true;
			} else if (args[0].equals("teams") && args.length == 1) {
				WhitelistHandler.teams(s);
				return true;
			} else if (args[0].equals("team") && args.length == 2) {
				WhitelistHandler.team(Integer.parseInt(args[1]) - 1, s);
				return true;
			} else if (args[0].equals("static") && args.length > 1) {
				if (args[1].equals("list") && args.length == 2) {
					WhitelistHandler.wllist(s);
					return true;
				} else if (args[1].equals("add") && args.length >= 3) {
					ArrayList<String> players = new ArrayList<String>(
							args.length - 2);
					for (int i = 2; i < args.length; i++)
						players.add(args[i]);
					WhitelistHandler.wladd(players, s);
					return true;
				} else if (args[1].equals("remove") && args.length >= 3) {
					ArrayList<String> players = new ArrayList<String>(
							args.length - 2);
					for (int i = 2; i < args.length; i++)
						players.add(args[i]);
					WhitelistHandler.wldelete(players, s);
					return true;
				}
			} else if (args[0].equals("reset") && args.length == 1) {
				WhitelistHandler.reset(s);
				return true;
			}
			return false;
		}
		throw new NotMyCommandException();
	}

	private static String[] usage() {
		ArrayList<String> usage = new ArrayList<String>();
		usage.add(ChatColor.RED.toString() + "抽選プラグイン使用方法");
		usage.add("/lottery start <チーム数> - 募集を開始します。");
		usage.add("/lottery set <一チーム毎の人数> - 募集を終了し抽選をします。");
		usage.add("/lottery setwm - 抽選ユーザーに自分を含めて抽選をします。");
		usage.add("/lottery add <チーム番号> <プレイヤー名>... - 特定ユーザーを確実に特定のチームに追加します。");
		usage.add("/lottery addrand <プレイヤー名>... - 特定ユーザーを確実にランダムで選ばれたチームに追加します。");
		usage.add("/lottery team <チーム番号> - 特定チームのメンバー一覧を表示します。");
		usage.add("/lottery teams - 全チームのメンバー一覧を表示します。");
		usage.add("/lottery reset - ホワイトリストをリセットして、該当しないプレイヤーをキックします。");
		usage.add("/lottery static list - 静的ホワイトリストに所属しているユーザー一覧を表示します。");
		usage.add("/lottery static add <プレイヤー名>... - 特定ユーザーを静的ホワイトリストに追加します。");
		usage.add("/lottery static remove <プレイヤー名>... - 特定ユーザーを静的ホワイトリストから削除します。");
		return usage.toArray(new String[usage.size()]);
	}
}
