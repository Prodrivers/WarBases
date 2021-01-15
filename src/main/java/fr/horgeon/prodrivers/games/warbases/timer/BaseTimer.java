package fr.horgeon.prodrivers.games.warbases.timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class BaseTimer {
	protected JavaPlugin plugin;
	protected int timeLeft = 0;
	protected BukkitTask timer;

	public BaseTimer( JavaPlugin plugin ) {
		this.plugin = plugin;
	}

	public void init( int timeMax ) {
		this.timeLeft = timeMax;
	}

	public void start() {
		this.timer = Bukkit.getScheduler().runTaskTimer( this.plugin, new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, 0L, 20L );
	}

	protected void update() {
		this.timeLeft--;
		if( this.timeLeft <= 0 ) {
			System.out.println( "[WarBases] Arena stopping because time is running out." );
			stop();
		}
	}

	public int timeLeft() {
		return this.timeLeft;
	}

	public String formattedTimeLeft() {
		int seconds = this.timeLeft % 60;
		int minutes = ( this.timeLeft / 60 ) % 60;
		return String.format( "%02d:%02d", minutes, seconds );
	}

	public void stop() {
		System.out.println( "[WarBases] Arena timer is stopping." );
		if( this.timer != null )
			this.timer.cancel();
		this.timeLeft = 0;
	}

	public boolean started() {
		return ( this.timer != null );
	}
}
