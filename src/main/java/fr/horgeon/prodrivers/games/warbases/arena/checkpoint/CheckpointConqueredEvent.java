package fr.horgeon.prodrivers.games.warbases.arena.checkpoint;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CheckpointConqueredEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private ArenaTeam team;
	private boolean wasContested;
	private boolean wasInOvertime;
	private boolean cancelled;

	public CheckpointConqueredEvent( String arenaName, Checkpoint checkpoint ) {
		this( arenaName, checkpoint, false );
	}

	public CheckpointConqueredEvent( String arenaName, Checkpoint checkpoint, boolean wasInOvertime ) {
		this.arenaName = arenaName;
		this.team = checkpoint.getTeam();
		this.wasContested = checkpoint.wasContested();
		this.wasInOvertime = wasInOvertime;
		System.out.println( "[WarBases] Checkpoint conquered fired! " + arenaName + "/" + team + "/" + wasContested + "/" + wasInOvertime );
	}

	public CheckpointConqueredEvent( String arenaName, ArenaTeam team, boolean wasContested, boolean completly ) {
		this.arenaName = arenaName;
		this.team = team;
		this.wasContested = wasContested;
		this.wasInOvertime = completly;
		System.out.println( "[WarBases] Checkpoint conquered fired! " + arenaName + "/" + team + "/" + wasContested + "/" + completly );
	}

	public ArenaTeam getTeam() {
		return this.team;
	}

	public String getArenaName() {
		return this.arenaName;
	}

	public boolean isWasInOvertime() {
		return this.wasInOvertime;
	}

	public boolean getWasContested() {
		return this.wasContested;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bln) {
		this.cancelled = bln;
	}
}