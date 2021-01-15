package fr.horgeon.prodrivers.games.warbases.arena;

import fr.horgeon.prodrivers.games.warbases.timer.BaseTimer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaTimer extends BaseTimer {
	private EArena arena;

	ArenaTimer( JavaPlugin plugin, EArena arena ) {
		super( plugin );
		this.arena = arena;
	}

	@Override
	protected void update() {
		this.timeLeft--;
		if( this.timeLeft <= 0 ) {
			this.arena.stop( false, false, false );
		} else {
			Bukkit.getServer().getPluginManager().callEvent( new ArenaTimerTickEvent( arena.getInternalName(), this.timeLeft, formattedTimeLeft() ) );
		}
	}
}
