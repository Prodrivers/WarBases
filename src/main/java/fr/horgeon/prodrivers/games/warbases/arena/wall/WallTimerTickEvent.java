package fr.horgeon.prodrivers.games.warbases.arena.wall;

import fr.horgeon.prodrivers.games.warbases.timer.BaseTimerTickEvent;

public class WallTimerTickEvent extends BaseTimerTickEvent {
	public WallTimerTickEvent( String arenaName, int timeLeft, String formattedTimeLeft ) {
		super( arenaName, timeLeft, formattedTimeLeft );
	}
}
