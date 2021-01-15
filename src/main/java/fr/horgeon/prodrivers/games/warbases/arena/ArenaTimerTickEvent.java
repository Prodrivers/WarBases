package fr.horgeon.prodrivers.games.warbases.arena;

import fr.horgeon.prodrivers.games.warbases.timer.BaseTimerTickEvent;

public class ArenaTimerTickEvent extends BaseTimerTickEvent {
	public ArenaTimerTickEvent( String arenaName, int timeLeft, String formattedTimeLeft ) {
		super( arenaName, timeLeft, formattedTimeLeft );
	}
}