package fr.horgeon.prodrivers.games.warbases.arena.overtime;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.Checkpoint;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;

import java.util.HashMap;
import java.util.Map;

public class OvertimeManager {
	EArena arena;
	private CheckpointManager checkpointManager;
	private HashMap<ArenaTeam, OvertimeTimer> timers = new HashMap<>();
	private boolean started = false;

	public OvertimeManager( EArena arena, CheckpointManager checkpointManager ) {
		this.arena = arena;
		this.checkpointManager = checkpointManager;

		for( ArenaTeam team : ArenaTeam.values() )
			this.timers.put( team, new OvertimeTimer( this, team ) );
	}

	public void init( float maxTime, float step ) {
		for( Map.Entry<ArenaTeam, OvertimeTimer> timer : this.timers.entrySet() )
			timer.getValue().init( maxTime, step );
	}

	public void start( ArenaTeam team, boolean fromCheckpoint ) {
		this.timers.get( team ).start( fromCheckpoint );
		this.started = true;
	}

	public void update() {
		if( started ) {
			for( Map.Entry<ArenaTeam, OvertimeTimer> timer : this.timers.entrySet() )
				timer.getValue().update();
		}
	}

	public void stopAll() {
		this.started = false;
		for( Map.Entry<ArenaTeam, OvertimeTimer> timer : this.timers.entrySet() )
			timer.getValue().stop();
	}

	public boolean isStarted() {
		return this.started;
	}

	void contested( ArenaTeam team ) {
		System.out.println( "[WarBases] Contesing overtime!" );
		OvertimeTimer timer = this.timers.get( team );
		if( timer.isStarted() )
			timer.contested();
	}

	public boolean shouldIntervene( boolean checkpointFinished ) {
		boolean intervene = false;

		for( Checkpoint checkpoint : checkpointManager.all() ) {
			if( ( !checkpointFinished && checkpoint.wasActivated() ) || checkpoint.wasContested() ) {
				OvertimeTimer ot = timers.get( checkpoint.getTeam() );
				if( !ot.isHasStarted() ) {
					System.out.println( "[WarBases] Overtime should happen! Invervening." );
					ot.start( false );
					this.started = true;
					intervene = true;
				} else {
					System.out.println( "[WarBases] Overtime should have happened, but overtime was already started for this one!" );
				}
			}
		}

		return intervene;
	}
}
