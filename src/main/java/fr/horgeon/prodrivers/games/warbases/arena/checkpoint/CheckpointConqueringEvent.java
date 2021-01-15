package fr.horgeon.prodrivers.games.warbases.arena.checkpoint;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CheckpointConqueringEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private ArenaTeam team;
	private boolean contested;

	public CheckpointConqueringEvent( String arenaName, ArenaTeam team, boolean contested ) {
		this.arenaName = arenaName;
		this.team = team;
		this.contested = contested;
	}

	public ArenaTeam getTeam() {
		return this.team;
	}

	public boolean getContested() {
		return this.contested;
	}

	public String getArenaName() {
		return this.arenaName;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}