package fr.horgeon.prodrivers.games.warbases.arena.wall;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class WallFallEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;

	public WallFallEvent( String arenaName ) {
		this.arenaName = arenaName;
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