package fr.horgeon.prodrivers.games.warbases.configuration.players;

import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUIQuality;
import org.bukkit.entity.Player;

public class PlayerConfiguration {
	private GameUI ui;
	private GameUIQuality quality;

	private PlayerConfiguration( Main plugin, Player player ) {
		load( plugin, player );
	}

	private void load( Main plugin, Player player ) {
		this.ui = PlayerConfiguration.getUI( plugin, player );
		this.quality = PlayerConfiguration.getQuality( plugin, player );
	}

	public GameUI getUI() {
		return this.ui;
	}

	public GameUIQuality getQuality() {
		return this.quality;
	}

	public static PlayerConfiguration get( Main plugin, ArenaPlayer player ) {
		return get( plugin, player.getPlayer() );
	}

	public static PlayerConfiguration get( Main plugin, Player player ) {
		return new PlayerConfiguration( plugin, player );
	}

	private static GameUI getUI( Main plugin, Player player ) {
		try {
			return GameUI.valueOf(plugin.getConfig().getString( "players." + player.getUniqueId() + ".ui.type" ));
		} catch(IllegalArgumentException|NullPointerException e) {
			return GameUI.Light;
		}
	}

	private static GameUIQuality getQuality( Main plugin, Player player ) {
		try {
			return GameUIQuality.valueOf(plugin.getConfig().getString( "players." + player.getUniqueId() + ".ui.quality" ));
		} catch(IllegalArgumentException|NullPointerException e) {
			return GameUIQuality.SD;
		}
	}

	public static void set( Main plugin, Player player, GameUI ui ) {
		plugin.getConfig().set( "players." + player.getUniqueId() + ".ui.type", ui.toString() );
	}

	public static void set( Main plugin, Player player, GameUIQuality quality ) {
		plugin.getConfig().set( "players." + player.getUniqueId() + ".ui.quality", quality.toString() );
	}
}
