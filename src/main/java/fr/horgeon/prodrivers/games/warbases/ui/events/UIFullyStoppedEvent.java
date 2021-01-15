package fr.horgeon.prodrivers.games.warbases.ui.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class UIFullyStoppedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private String ui;

	public UIFullyStoppedEvent( String arenaName, String ui ) {
		this.arenaName = arenaName;
		this.ui = ui;
	}

	public String getUI() {
		return this.ui;
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