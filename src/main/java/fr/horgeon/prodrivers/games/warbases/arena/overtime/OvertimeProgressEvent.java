package fr.horgeon.prodrivers.games.warbases.arena.overtime;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OvertimeProgressEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private ArenaTeam team;
	private float left, max;

	public OvertimeProgressEvent( String arenaName, ArenaTeam team, float left, float max ) {
		this.arenaName = arenaName;
		this.team = team;
		this.left = left;
		this.max = max;
	}

	public float getLeft() {
		return this.left;
	}

	public float getMax() {
		return this.max;
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
