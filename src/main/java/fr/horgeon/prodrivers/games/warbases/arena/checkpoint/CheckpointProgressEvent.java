package fr.horgeon.prodrivers.games.warbases.arena.checkpoint;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CheckpointProgressEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private ArenaTeam team;
	private int progress;
	private int progressLeft;

	public CheckpointProgressEvent( String arenaName, ArenaTeam team, int progress, int progressLeft ) {
		this.arenaName = arenaName;
		this.team = team;
		this.progress = progress;
		this.progressLeft = progressLeft;
	}

	public ArenaTeam getTeam() {
		return this.team;
	}

	public int getProgress() {
		return this.progress;
	}

	public int getProgressLeft() {
		return this.progressLeft;
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