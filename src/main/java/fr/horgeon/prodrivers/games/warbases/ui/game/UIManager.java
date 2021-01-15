package fr.horgeon.prodrivers.games.warbases.ui.game;

import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import fr.horgeon.bukkit.packetsutilities.NetworkManager;
import fr.horgeon.bukkit.packetsutilities.ProtocolVersion;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.*;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.ui.game.animator.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UIManager {
	private Main plugin;
	private EArena arena;
	private CheckpointManager checkpointManager;
	private WallManager wallManager;
	private ArenaManager arenaManager;

	private BaseUI baseUI;
	private EnhancedUI enhancedUI;
	private ScoreboardUI scoreboardUI;

	private boolean intro = false, outro = true;

	private EMessagesConfig messages;

	public UIManager( Main plugin, EArena arena, CheckpointManager checkpointManager, WallManager wallManager ) {
		this.plugin = plugin;

		this.arena = arena;
		this.arenaManager = arena.getArenaManager();
		this.checkpointManager = checkpointManager;
		this.wallManager = wallManager;

		this.messages = (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig();

		this.baseUI = new BaseUI( plugin, plugin.getPluginInstance() );
		this.scoreboardUI = new ScoreboardUI( plugin, plugin.getPluginInstance(), this.arena );
		this.enhancedUI = new EnhancedUI( plugin, plugin.getPluginInstance() );

		loadConfig();
	}

	public void reload() {
		baseUI.reload();
		scoreboardUI.reload();
		enhancedUI.reload();
		loadConfig();
	}

	private void loadConfig() {
		intro = (boolean) ArenaVariable.get( "intro" ).getValue( this.plugin, this.arena );
		outro = (boolean) ArenaVariable.get( "outro" ).getValue( this.plugin, this.arena );
	}

	public void join( ArenaPlayer p ) {}

	public void leave( ArenaPlayer p ) {
		clear( p );
	}

	public void start() {
		baseUI.preStart( this.arena, intro );
		scoreboardUI.preStart( this.arena, intro );
		enhancedUI.preStart( this.arena, intro );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.start( this.arena, entry.getValue(), intro );
			get( entry.getValue() ).start( this.arena, entry.getValue(), intro );
		}

		scoreboardUI.postStart( this.arena, intro );
		enhancedUI.postStart( this.arena, intro );
		baseUI.postStart( this.arena, intro );
	}

	public void continueStart() {
		this.arena.continueStart();
	}

	public void continueStop() {
		this.arena.continueStop();
	}

	public void update( boolean startOfSecond ) {
		baseUI.preUpdate( this.arena, this.checkpointManager, this.wallManager, startOfSecond );
		scoreboardUI.preUpdate( this.arena, this.checkpointManager, this.wallManager, startOfSecond );
		enhancedUI.preUpdate( this.arena, this.checkpointManager, this.wallManager, startOfSecond );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.update( this.arena, entry.getValue() );
			get( entry.getValue() ).update( this.arena, entry.getValue() );
		}
	}

	public void updateLobby() {
		baseUI.preUpdateLobby( this.arena );
		scoreboardUI.preUpdateLobby( this.arena );
		enhancedUI.preUpdateLobby( this.arena );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.updateLobby( this.arena, entry.getValue() );
			get( entry.getValue() ).updateLobby( this.arena, entry.getValue() );
		}
	}

	public IGameUI get( ArenaPlayer p ) {
		if( p.getConfiguration().getUI().isEnhanced && plugin.getResourcePackManager().isEnhancedUIAvailable() )
			return this.enhancedUI;
		return this.scoreboardUI;
	}

	public void reset() {
		this.baseUI.reset();
		scoreboardUI.reset();
		this.enhancedUI.reset();
	}

	public void clear( ArenaPlayer p ) {
		baseUI.clear( p );
		get( p ).clear( p );
	}

	public void correctLobbyItems( Player p ) {
		if( p == null ) return;

		ItemStack item = p.getInventory().getItem( 2 );
		p.getInventory().clear( 2 );
		p.getInventory().setItem( 7, item );
	}

	public void broadcastDeath( ArenaPlayer killer, ArenaPlayer killed, boolean respawn ) {
		baseUI.preAnnounceDeath( killer, killed );
		scoreboardUI.preAnnounceDeath( killer, killed );
		enhancedUI.preAnnounceDeath( killer, killed );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.announceDeath( killer, killed, entry.getValue() );
			get( entry.getValue() ).announceDeath( killer, killed, entry.getValue() );
		}

		if( killer != null ) {
			baseUI.onKilledByPlayer( killed, killer, respawn );
			get( killed ).onKilledByPlayer( killed, killer, respawn );
			baseUI.onKill( killer, killed );
			get( killer ).onKill( killer, killed );
		}
	}

	public void stop( boolean skipUI ) {
		baseUI.preStop( this.arena, this.checkpointManager, this.wallManager, skipUI, outro );
		scoreboardUI.preStop( this.arena, this.checkpointManager, this.wallManager, skipUI, outro );
		enhancedUI.preStop( this.arena, this.checkpointManager, this.wallManager, skipUI, outro );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.stop( this.arena, entry.getValue(), skipUI, outro );
			get( entry.getValue() ).stop( this.arena, entry.getValue(), skipUI, outro );
		}

		scoreboardUI.postStop( this.arena, skipUI, outro );
		enhancedUI.postStop( this.arena, skipUI, outro );
		baseUI.postStop( this.arena, skipUI, outro );
	}

	public void checkpointProgress( ArenaTeam team, int progress, int progressLeft ) {
		baseUI.preCheckpointProgress( team, progress, progressLeft );
		scoreboardUI.preCheckpointProgress( team, progress, progressLeft );
		enhancedUI.preCheckpointProgress( team, progress, progressLeft );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.checkpointProgress( entry.getValue(), team );
			get( entry.getValue() ).checkpointProgress( entry.getValue(), team );
		}
	}

	public void checkpointConquering( ArenaTeam team, boolean contested ) {
		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.checkpointConquering( entry.getValue(), team, contested );
			get( entry.getValue() ).checkpointConquering( entry.getValue(), team, contested );
		}
	}

	public void wallFall() {
		baseUI.preWallFall();
		scoreboardUI.preWallFall();
		enhancedUI.preWallFall();

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.wallFall( entry.getValue() );
			get( entry.getValue() ).wallFall( entry.getValue() );
		}
	}

	public void overtimeStarted( ArenaTeam team ) {
		baseUI.preOvertimeStarted( team );
		scoreboardUI.preOvertimeStarted( team );
		enhancedUI.preOvertimeStarted( team );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.overtimeStarted( entry.getValue(), team );
			get( entry.getValue() ).overtimeStarted( entry.getValue(), team );
		}
	}

	public void overtimeProgress( ArenaTeam team, float left, float max ) {
		baseUI.preOvertimeProgress( team, left, max );
		scoreboardUI.preOvertimeProgress( team, left, max );
		enhancedUI.preOvertimeProgress( team, left, max );
	}

	public static boolean validateUIVersion( Player player, GameUI selectedUI ) {
		if( selectedUI.isEnhanced ) {
			if( NetworkManager.getVersion( player ) < ProtocolVersion.v1_9_3 ) {
				return false;
			}
		}

		return true;
	}

	public void lowHealth( ArenaPlayer player ) {
		baseUI.lowHealth( player );
		get( player ).lowHealth( player );
	}

	public void respawn( ArenaPlayer player ) {
		baseUI.respawn( player );
		get( player ).respawn( player );
	}

	public void communicationMessage( ArenaPlayer sender, CommunicationMessage type ) {
		if( sender.canSendMessage() ) {
			baseUI.preCommunicationMessage( sender, type );
			scoreboardUI.preCommunicationMessage( sender, type );
			enhancedUI.preCommunicationMessage( sender, type );

			for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
				baseUI.communicationMessage( entry.getValue(), sender.getTeam(), sender.getLocation() );
				get( entry.getValue() ).communicationMessage( entry.getValue(), sender.getTeam(), sender.getLocation() );
			}
		} else {
			sender.sendMessage( messages.in_game_messages_please_wait );
		}
	}

	public void timerProgress( int timeLeft, String formattedTimeLeft ) {
		baseUI.preTimerProgress( this.arena, timeLeft, formattedTimeLeft );
		scoreboardUI.preTimerProgress( this.arena, timeLeft, formattedTimeLeft );
		enhancedUI.preTimerProgress( this.arena, timeLeft, formattedTimeLeft );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.timerProgress( entry.getValue() );
			get( entry.getValue() ).timerProgress( entry.getValue() );
		}
	}

	public void wallTimerProgress( int timeLeft, String formattedTimeLeft ) {
		baseUI.preWallTimerProgress( this.arena, this.arena.getWallManager(), timeLeft, formattedTimeLeft );
		scoreboardUI.preWallTimerProgress( this.arena, this.arena.getWallManager(), timeLeft, formattedTimeLeft );
		enhancedUI.preWallTimerProgress( this.arena, this.arena.getWallManager(), timeLeft, formattedTimeLeft );

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			baseUI.wallTimerProgress( entry.getValue() );
			get( entry.getValue() ).wallTimerProgress( entry.getValue() );
		}
	}

	public void respawnProgress( ArenaPlayer p, int timeLeft ) {
		baseUI.respawnProgress( arena, p, timeLeft );
		get( p ).respawnProgress( arena, p, timeLeft );
	}
}
