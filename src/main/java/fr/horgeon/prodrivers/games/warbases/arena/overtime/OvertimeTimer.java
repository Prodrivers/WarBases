package fr.horgeon.prodrivers.games.warbases.arena.overtime;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointConqueredEvent;
import org.bukkit.Bukkit;

public class OvertimeTimer {
	private OvertimeManager manager;
	private ArenaTeam team;
	private float maxTime = 5f;
	private float step = 0.05f;
	private float timer = 0;
	private boolean started = false;
	private boolean hasStarted = false;
	private boolean startedAsCheckpoint = false;
	private boolean isContested = false;

	OvertimeTimer( OvertimeManager manager, ArenaTeam team ) {
		this.manager = manager;
		this.team = team;
	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean isHasStarted() {
		return this.hasStarted;
	}

	public float getTime() {
		return this.timer;
	}

	public float getMaxTime() {
		return this.maxTime;
	}

	void init( float maxTime, float step ) {
		this.maxTime = maxTime;
		this.step = step / 4;
		this.hasStarted = false;
		this.isContested = false;
	}

	void start( boolean startedAsCheckpoint ) {
		this.started = true;
		this.hasStarted = true;
		this.timer = this.maxTime;
		this.startedAsCheckpoint = startedAsCheckpoint;
		Bukkit.getServer().getPluginManager().callEvent( new OvertimeStartedEvent( manager.arena.getInternalName(), team ) );
		Bukkit.getServer().getPluginManager().callEvent( new OvertimeProgressEvent( manager.arena.getInternalName(), team, this.timer, this.maxTime ) );

		System.out.println( "[WarBases] Overtime started!" );
	}

	void update() {
		if( started ) {
			if( !this.isContested ) {
				this.timer -= 0.25f;
				Bukkit.getServer().getPluginManager().callEvent( new OvertimeProgressEvent( manager.arena.getInternalName(), team, this.timer, this.maxTime ) );

				System.out.println( "[WarBases] Overtime update has been made: " + timer + "/" + maxTime );

				if( this.timer <= 0 ) {
					this.manager.stopAll();
					if( startedAsCheckpoint ) {
						System.out.println( "[WarBases] Overtime done! Checkpoint conquered." );
						Bukkit.getServer().getPluginManager().callEvent( new CheckpointConqueredEvent( manager.arena.getInternalName(), team, false, true ) );
					} else {
						System.out.println( "[WarBases] Overtime done!" );
						this.manager.arena.stop( false, true, false );
					}
				}
			} else {
				this.isContested = false;
			}
		}
	}

	void stop() {
		this.started = false;
		this.isContested = false;
		this.timer = 0;
	}

	void contested() {
		this.maxTime -= step;
		if( this.maxTime <= 0.5f )
			this.maxTime = 0.5f;
		this.timer = this.maxTime;

		this.isContested = true;

		System.out.println( "[WarBases] Overtime progress has been made: " + timer + "/" + maxTime );

		Bukkit.getServer().getPluginManager().callEvent( new OvertimeProgressEvent( manager.arena.getInternalName(), team, this.timer, this.maxTime ) );
	}
}
