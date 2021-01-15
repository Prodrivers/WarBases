package fr.horgeon.prodrivers.games.warbases.ui.wall;

import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallBlock;
import gist.kingfaris10.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class WallUI {
	protected final int id;

	protected Main plugin;
	protected EArena arena;

	protected Cuboid worldBlocks;

	protected HashMap<Location, WallBlock> blocks = new HashMap<>();

	protected BukkitTask loader;

	protected AtomicBoolean saved = new AtomicBoolean( false );

	protected WallUI( int id, Main plugin, EArena arena, Location low, Location high ) {
		this.id = id;
		this.plugin = plugin;
		this.arena = arena;

		this.worldBlocks = new Cuboid( low, high );

		try {
			loader = Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
				public void run() {
					save();
					saved.set( true );
					reset();
				}
			}, 5L );
		} catch( IllegalPluginAccessException ex ) {}
	}

	public boolean saved() {
		return this.saved.get();
	}

	public void stop() {
		if( this.loader != null )
			this.loader.cancel();
	}

	abstract protected void save();

	abstract public void fall();
	abstract public void reset();
	abstract public void rebuild();
}
