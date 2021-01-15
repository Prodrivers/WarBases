package fr.horgeon.prodrivers.games.warbases.arena.wall;

import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.ui.wall.FAWEWallUI;
import fr.horgeon.prodrivers.games.warbases.ui.wall.WallUI;
import org.bukkit.Location;

public class Wall {

	private Location lowCorner;
	private Location highCorner;

	private WallUI ui;

	private boolean fallen = false;

	public Wall( int id, Main plugin, EArena arena, Location low, Location high ) {
		this.lowCorner = low;
		this.highCorner = high;

		this.ui = new FAWEWallUI( id, plugin, arena, low, high );

		System.out.println( "[WarBases] Arena " + arena.getInternalName() + ": Wall initiated at " + lowCorner + "/" + highCorner );
	}

	public void fall() {
		if( !this.fallen ) {
			this.ui.fall();
			this.fallen = true;
		}
	}

	public boolean hasFallen() {
		return this.fallen;
	}

	public void reset() {
		this.fallen = false;
		this.ui.reset();
	}

	public void stop() {
		this.ui.stop();
	}

	public boolean ready() {
		return this.ui.saved();
	}

	WallUI getUI() {
		return this.ui;
	}
}
