package fr.horgeon.prodrivers.games.warbases.arena.overtime;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OvertimeStartedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private ArenaTeam team;

	public OvertimeStartedEvent( String arenaName, ArenaTeam team ) {
		this.arenaName = arenaName;
		this.team = team;
	}

	public ArenaTeam getTeam() {
		return this.team;
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
