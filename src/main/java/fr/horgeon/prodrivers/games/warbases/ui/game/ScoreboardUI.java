package fr.horgeon.prodrivers.games.warbases.ui.game;

import com.comze_instancelabs.minigamesapi.PluginInstance;
//import fr.horgeon.bukkit.packetsutilities.Scoreboard;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.Checkpoint;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScoreboardUI implements IGameUI {
	private EMessagesConfig messages;
	//private HashMap<String, Scoreboard> arenaScoreboard = new HashMap<>();
	//private HashMap<String, Objective> arenaObjective = new HashMap<>();
	private Scoreboard arenaScoreboard;
	private Objective arenaObjective;
	//private HashMap<ArenaPlayer, Scoreboard> arenaScoreboard = new HashMap<>();
	private String[] lines = new String[ 16 ];
	private int width = Constants.UI_SCOREBOARD_WIDTH;

	/*private int wallTimeLeft = 0;
	private ArrayList<Checkpoint> checkpoints;*/

	public ScoreboardUI( JavaPlugin plugin, PluginInstance pli, EArena arena ) {
		this.messages = (EMessagesConfig) pli.getMessagesConfig();
		reload();
		//lines[ 0 ] = this.messages.scoreboard_arena.replaceAll( "<arena>", arena.getInternalName() );
	}

	public void reload() {}

	public void reset() {}

	public void preUpdateLobby( EArena arena ) {}

	public void updateLobby( EArena arena, ArenaPlayer p ) {}

	public void preStart( final EArena arena, boolean intro ) {
		String arenaLine = this.messages.scoreboard_arena.replaceAll( "<arena>", arena.getDisplayName() );
		width = Math.max( width, arenaLine.length() );

		arenaScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		arenaObjective = arenaScoreboard.registerNewObjective( arena.getInternalName() + "ig", "dummy" );
		arenaObjective.setDisplayName( this.messages.scoreboard_main_title );
		arenaObjective.setDisplaySlot( DisplaySlot.SIDEBAR );

		StringBuilder frameStr = new StringBuilder();
		for( int i = 0; i < width; i++ )
			frameStr.append( messages.scoreboard_separator );
		arenaObjective.getScore( ChatColor.AQUA.toString() + ChatColor.RESET.toString() + frameStr.toString() ).setScore( 7 );
		arenaObjective.getScore( ChatColor.BLACK.toString() + ChatColor.RESET.toString() ).setScore( 6 );
		arenaObjective.getScore( arenaLine ).setScore( 5 );
		arenaObjective.getScore( ChatColor.GREEN.toString() + ChatColor.RESET.toString() ).setScore( 4 );
		arenaObjective.getScore( ChatColor.GRAY.toString() + ChatColor.RESET.toString() ).setScore( 1 );
		arenaObjective.getScore( ChatColor.DARK_RED.toString() + ChatColor.RESET.toString() + frameStr.toString() ).setScore( 0 );
		updateScoreboardLines( arena.getCheckpointManager().all() );
	}

	public void start( final EArena arena, final ArenaPlayer p, boolean intro ) {
		/*p.getInventory().clear();
		p.getInventory().setHeldItemSlot( 1 );
		p.updateInventory();
		super.start( arena, p );
		p.sendMessage( this.messages.welcome );*/
		/*createScoreboard( arena, p );
		completlySendUpdateScoreboard( p );*/
		p.setScoreboard( arenaScoreboard );

	}

	public void postStart( final EArena arena, boolean intro ) {}

	private void updateScoreboardLines( List<Checkpoint> checkpoints ) {
		for( Checkpoint cp: checkpoints )
			updateScoreboardCheckpoint( cp.getTeam(), cp.getProgress() );
	}

	private void updateScoreboardCheckpoint( ArenaTeam team, int progress ) {
		String progressStr = String.valueOf( progress );
		String teamStr = team.toName( this.messages ).toUpperCase();
		String paddingStr = new String( new char[ width + 3 - teamStr.length() - progressStr.length() ] ).replace( "\0", " " );
		if( lines[ team.toInt() ] != null ) {
			arenaScoreboard.resetScores( lines[ team.toInt() ] );
		}
		lines[ team.toInt() ] = this.messages.scoreboard_team.replaceAll( "<teamColor>", team.toChatColor().toString() ).replaceAll( "<team>", teamStr ).replaceAll( "<progress>", progressStr ).replaceAll( "<padding>", paddingStr );
		/*lines[ ( ( team.equals( ArenaTeam.Red ) ? 0 : 2 ) + 2 ) ] = this.messages.scoreboard_team.replaceAll( "<teamColor>", team.toChatColor().toString() ).replaceAll( "<team>", teamStr ).replaceAll( "<progress>", progressStr ).replaceAll( "<padding>", paddingStr );*/
		arenaObjective.getScore( lines[ team.toInt() ] ).setScore( ( team == ArenaTeam.Red ? 3 : 2 ) );
	}

	private void sendUpdateScoreboardCheckpoint( ArenaPlayer p, ArenaTeam team ) {
		/*Scoreboard score = arenaScoreboard.get( p );
		if( score != null ) {
			int index = ( team.equals( ArenaTeam.Red ) ? 0 : 2 ) + 2;
			score.setLine( index, lines[ index ] );
		}*/
	}

	private void createScoreboard( EArena arena, ArenaPlayer p ) {
		/*Scoreboard score = new Scoreboard( p.getPlayer(), arena.getInternalName(), this.messages.scoreboard_main_title );
		score.create();
		arenaScoreboard.put( p, score );*/
	}

	private void removeScoreboard( ArenaPlayer p ) {
		/*Scoreboard score = arenaScoreboard.get( p );
		if( score != null ) {
			score.destroy();
		}*/
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard sc = manager.getNewScoreboard();
		sc.clearSlot( DisplaySlot.SIDEBAR );
		p.setScoreboard( sc );
	}

	private void completlySendUpdateScoreboard( ArenaPlayer p ) {
		/*Scoreboard score = arenaScoreboard.get( p );
		if( score != null ) {
			for( int i = 0; i < 16; i++ ) {
				if( lines[ i ] != null )
					score.setLine( i, lines[ i ] );
			}
		}*/
	}

	public void preUpdate( EArena arena, CheckpointManager checkpointManager, WallManager wallManager, boolean startOfSecond ) {
		//this.wallTimeLeft = wallManager.getTimeLeft();
		//this.checkpoints = checkpointManager.all();
		//updateScoreboardLines( checkpointManager.all() );
	}

	public void update( EArena arena, ArenaPlayer p ) {
		/*if( !arenaScoreboard.containsKey( arena.getInternalName() ) ) {
			arenaScoreboard.put( arena.getInternalName(), Bukkit.getScoreboardManager().getNewScoreboard() );
			arenaObjective.get( arena.getInternalName() ).setDisplayName( this.messages.scoreboard_title.replaceAll( "<arena>", arena.getInternalName() ) );
		}
		if( !arenaObjective.containsKey( arena.getInternalName() ) ) {
			arenaObjective.put( arena.getInternalName(), arenaScoreboard.get( arena.getInternalName() ).registerNewObjective( arena.getInternalName(), "dummy" ) );
		}

		arenaObjective.get( arena.getInternalName() ).setDisplaySlot( DisplaySlot.SIDEBAR );

		for( Checkpoint cp : this.checkpoints ) {
			arenaObjective.get( arena.getInternalName() ).getScore( cp.getTeam().toChatColor() + cp.getTeam().toString().toUpperCase() + ":" ).setScore( cp.getProgress() );
		}

		arenaObjective.get( arena.getInternalName() ).getScore( ChatColor.AQUA + "WALL-TIME-LEFT:" ).setScore( wallTimeLeft );

		arenaObjective.get( arena.getInternalName() ).getScore( ChatColor.GREEN + "LIVES:" ).setScore( p.getLives() );

		p.setScoreboard( arenaScoreboard.get( arena.getInternalName() ) );*/
		//completlySendUpdateScoreboard( p );
		p.setScoreboard( arenaScoreboard );
	}

	/*@Override
	public void stopAll( EArena arena, ArenaPlayer player ) {
		super.stopAll( arena, player );
		if( pli.containsGlobalLost( player.getName() ) )
			player.sendMessage( this.messages.defeat );
		else
			player.sendMessage( this.messages.victory );
	}

	public void postStop() {
		this.isStopped.set( true );
	}*/

	public void clear( ArenaPlayer p ) {
		/*ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard sc = manager.getNewScoreboard();
		sc.clearSlot( DisplaySlot.SIDEBAR );
		p.setScoreboard( sc );*/
		removeScoreboard( p );
	}

	public void preCheckpointProgress( ArenaTeam team, int progress, int progressLeft ) {
		updateScoreboardCheckpoint( team, progress );
	}

	public void checkpointProgress( ArenaPlayer p, ArenaTeam team ) {
		sendUpdateScoreboardCheckpoint( p, team );
	}

	public void arenaStartPending( ArenaPlayer p ) {}
	public void arenaStopPending( ArenaPlayer p ) {}

	public void arenaPlayersApplyingResourcepack( ArenaPlayer p ) {}

	public void onKilledByPlayer( ArenaPlayer p, ArenaPlayer killer, boolean respawn ) {}

	public void joinTeam( ArenaPlayer p, ArenaTeam team ) {}

	public void preStop( final EArena arena, final CheckpointManager checkpointManager, final WallManager wallManager, boolean skip, boolean outro ) {}
	public void stop( final EArena arena, final ArenaPlayer p, boolean skip, boolean outro ) {}
	public void postStop( final EArena arena, boolean skip, boolean outro ) {}

	public void preAnnounceDeath( ArenaPlayer killer, ArenaPlayer killed ) {}
	public void announceDeath( ArenaPlayer killer, ArenaPlayer killed, ArenaPlayer target ) {}

	public void onKill( ArenaPlayer killer, ArenaPlayer killed ) {}

	public void checkpointConquering( ArenaPlayer p, ArenaTeam team, boolean contested ) {}

	public void preWallFall() {}
	public void wallFall( ArenaPlayer p ) {}

	public void preOvertimeStarted( ArenaTeam team ) {}
	public void overtimeStarted( ArenaPlayer p, ArenaTeam team ) {}

	public void preOvertimeProgress( ArenaTeam team, float left, float max ) {}

	public void lowHealth( ArenaPlayer p ) {}

	public void respawnProgress( EArena arena, ArenaPlayer p, int timeLeft ) {}
	public void respawn( ArenaPlayer p ) {}

	public void preCommunicationMessage( ArenaPlayer sender, CommunicationMessage type ) {}
	public void communicationMessage( ArenaPlayer p, ArenaTeam team, Location senderLocation ) {}

	public void preWallTimerProgress( EArena arena, WallManager wallManager, int timeLeft, String formattedTimeLeft ) {}
	public void wallTimerProgress( ArenaPlayer p ) {}

	public void preTimerProgress( EArena arena, int timeLeft, String formattedTimeLeft ) {}
	public void timerProgress( ArenaPlayer p ) {}
}
