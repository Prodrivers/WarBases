package fr.horgeon.prodrivers.games.warbases.arena.wall;

import fr.horgeon.prodrivers.games.warbases.timer.BaseTimer;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WallTimer extends BaseTimer {
	private EArena arena;
	private WallManager manager;

	WallTimer( JavaPlugin plugin, EArena arena, WallManager manager ) {
		super( plugin );
		this.arena = arena;
		this.manager = manager;
	}

	@Override
	protected void update() {
		this.timeLeft--;
		if( this.timeLeft <= 0 ) {
			this.manager.fall();
			stop();
		} else {
			Bukkit.getServer().getPluginManager().callEvent( new WallTimerTickEvent( arena.getInternalName(), this.timeLeft, formattedTimeLeft() ) );
		}
	}
}
