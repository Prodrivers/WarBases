package fr.horgeon.prodrivers.games.warbases.timer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseTimerTickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String arenaName;
	private int timeLeft;
	private String formattedTimeLeft;

	public BaseTimerTickEvent( String arenaName, int timeLeft, String formattedTimeLeft ) {
		this.arenaName = arenaName;
		this.timeLeft = timeLeft;
		this.formattedTimeLeft = formattedTimeLeft;
	}

	public int getTimeLeft() {
		return this.timeLeft;
	}

	public String getFormattedTimeLeft() {
		return this.formattedTimeLeft;
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
