package fr.horgeon.prodrivers.games.warbases.ui.game;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import org.bukkit.Location;

public interface IGameUI {
	void arenaStartPending( ArenaPlayer p );
	void arenaStopPending( ArenaPlayer p );

	void arenaPlayersApplyingResourcepack( ArenaPlayer p );

	void onKilledByPlayer( ArenaPlayer p, ArenaPlayer killer, boolean respawn );

	void joinTeam( ArenaPlayer p, ArenaTeam team );

	void reload();

	void reset();

	void preUpdateLobby( EArena arena );
	void updateLobby( EArena arena, ArenaPlayer p );

	void preStart( final EArena arena, boolean intro );
	void start( final EArena arena, final ArenaPlayer p, boolean intro );
	void postStart( final EArena arena, boolean intro );

	void preUpdate( EArena arena, CheckpointManager checkpointManager, WallManager wallManager, boolean startOfSecond );
	void update( EArena arena, ArenaPlayer p );

	void preStop( final EArena arena, final CheckpointManager checkpointManager, final WallManager wallManager, boolean skip, boolean outro );
	void stop( final EArena arena, final ArenaPlayer p, boolean skip, boolean outro );
	void postStop( final EArena arena, boolean skip, boolean outro );

	void clear( ArenaPlayer p );

	void preAnnounceDeath( ArenaPlayer killer, ArenaPlayer killed );
	void announceDeath( ArenaPlayer killer, ArenaPlayer killed, ArenaPlayer target );

	void onKill( ArenaPlayer killer, ArenaPlayer killed );

	void preCheckpointProgress( ArenaTeam team, int progress, int progressLeft );
	void checkpointProgress( ArenaPlayer p, ArenaTeam team );

	void checkpointConquering( ArenaPlayer p, ArenaTeam team, boolean contested );

	void preWallFall();
	void wallFall( ArenaPlayer p );

	void preOvertimeStarted( ArenaTeam team );
	void overtimeStarted( ArenaPlayer p, ArenaTeam team );

	void preOvertimeProgress( ArenaTeam team, float left, float max );

	void lowHealth( ArenaPlayer p );

	void respawnProgress( EArena arena, ArenaPlayer p, int timeLeft );
	void respawn( ArenaPlayer p );

	void preCommunicationMessage( ArenaPlayer sender, CommunicationMessage type );
	void communicationMessage( ArenaPlayer p, ArenaTeam team, Location senderLocation );

	void preWallTimerProgress( EArena arena, WallManager wallManager, int timeLeft, String formattedTimeLeft );
	void wallTimerProgress( ArenaPlayer p );

	void preTimerProgress( EArena arena, int timeLeft, String formattedTimeLeft );
	void timerProgress( ArenaPlayer p );
}
