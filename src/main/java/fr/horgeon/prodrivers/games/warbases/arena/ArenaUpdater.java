package fr.horgeon.prodrivers.games.warbases.arena;

public class ArenaUpdater implements Runnable {
	private EArena arena;
	private short timer = 0;

	public ArenaUpdater( EArena arena ) {
		this.arena = arena;
	}

	@Override
	public void run() {
		this.timer++;
		if( timer >= 4 )
			timer = 0;


		arena.checkpointManager.update();
		arena.overtimeManager.update();
		arena.uiManager.update( timer == 0 );
	}
}
