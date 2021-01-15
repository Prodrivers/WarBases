package fr.horgeon.prodrivers.games.warbases.configuration;

import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class EDefaultConfig extends DefaultConfig {
	public EDefaultConfig( JavaPlugin plugin, boolean custom ) {
		super( plugin, custom );

		if( !custom ) {
			plugin.getConfig().addDefault( "config.selection_items.teams_selection_item", Material.WOOL.toString() );

			plugin.getConfig().addDefault( "config.gui.enhanced_ui.base_item", Material.DIAMOND_HOE.toString() );

			plugin.getConfig().addDefault( "config.overtime.max", 5.0 );
			plugin.getConfig().addDefault( "config.overtime.max_reduce_step", 0.05 );
			plugin.getConfig().addDefault( "config.overtime.last_contest_threshold", 60 );

			plugin.getConfig().addDefault( "config.respawn.time", 5 );

			// Override default configuration
			plugin.getConfig().addDefault( "config.effects.dead_in_fake_bed", false ); // Strong incompatibility with Minecraft 1.9
			plugin.getConfig().addDefault( "config.broadcast_win", false );
			plugin.getConfig().addDefault( "config.countdowns.ingame_countdown_enabled", false );
			plugin.getConfig().addDefault( "config.use_custom_scoreboard", false );
			plugin.getConfig().addDefault( "config.use_spectator_scoreboard", true );
			plugin.getConfig().addDefault( "config.chat_enabled", true );
			plugin.getConfig().addDefault( "config.chat_per_arena_only", true );

			ArenaVariable.addDefaults( plugin.getConfig() );

			/*plugin.getConfig().addDefault( "config.lobbies.main.location.world", "WORLD" );
			plugin.getConfig().addDefault( "config.lobbies.main.location.x", 0 );
			plugin.getConfig().addDefault( "config.lobbies.main.location.y", 255 );
			plugin.getConfig().addDefault( "config.lobbies.main.location.z", 0 );
			plugin.getConfig().addDefault( "config.lobbies.main.location.yaw", 0 );
			plugin.getConfig().addDefault( "config.lobbies.main.location.pitch", 0 );
			plugin.getConfig().addDefault( "config.lobbies.game.location.world", "WORLD" );
			plugin.getConfig().addDefault( "config.lobbies.game.location.x", 0 );
			plugin.getConfig().addDefault( "config.lobbies.game.location.y", 255 );
			plugin.getConfig().addDefault( "config.lobbies.game.location.z", 0 );
			plugin.getConfig().addDefault( "config.lobbies.game.location.yaw", 0 );
			plugin.getConfig().addDefault( "config.lobbies.game.location.pitch", 0 );*/

			plugin.getConfig().options().copyDefaults( true );

			// Override default configuration
			plugin.getConfig().set( "config.effects.dead_in_fake_bed", false ); // Strong incompatibility with Minecraft 1.9
			plugin.getConfig().set( "config.broadcast_win", false );
			plugin.getConfig().set( "config.countdowns.ingame_countdown_enabled", false );
			plugin.getConfig().set( "config.use_custom_scoreboard", false );
			plugin.getConfig().set( "config.use_spectator_scoreboard", true );
			plugin.getConfig().set( "config.chat_enabled", true );
			plugin.getConfig().set( "config.chat_per_arena_only", true );

			plugin.saveConfig();
		}
	}
}
