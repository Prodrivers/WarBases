package fr.horgeon.prodrivers.games.warbases.arena.wall;

import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.Util;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.configuration.EArenasConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class WallManager {
	private Main plugin;
	private EArena arena;
	private WallTimer timer;

	private int timeMax = 180;

	private ArrayList<Wall> walls = new ArrayList<>();

	public WallManager( Main plugin, EArena arena ) {
		this.plugin = plugin;
		this.arena = arena;
		this.timer = new WallTimer( plugin, arena, this );
	}

	public void load() {
		this.walls.clear();

		FileConfiguration config = plugin.getPluginInstance().getArenasConfig().getConfig();
		String configPath = ArenaConfigStrings.ARENAS_PREFIX + arena.getInternalName() + ".walls";
		if( config.isSet( configPath + "." ) ) {
			for( String wall : config.getConfigurationSection( configPath ).getKeys( false ) ) {
				Location lowCorner = Util.getComponentForArena( plugin, arena.getInternalName(), "walls." + wall + ".low" );
				Location highCorner = Util.getComponentForArena( plugin, arena.getInternalName(), "walls." + wall + ".high" );
				if( lowCorner == null || highCorner == null )
					return;
				this.walls.add( new Wall( Integer.valueOf( wall.replace( "wall", "" ) ), plugin, arena, lowCorner, highCorner ) );
			}
		} else {
			return;
		}

		this.timeMax = (int) ArenaVariable.get( "wall_countdown" ).getValue( this.plugin, this.arena );
		this.timer.init( timeMax );
	}

	public void reload() {
		load();
		reset();
	}

	public void rebuild() {
		for( Wall wall : walls ) {
			wall.getUI().rebuild();
		}
	}

	public void start() {
		this.timer.start();
	}

	void fall() {
		for( Wall wall : walls ) {
			wall.fall();
		}

		Bukkit.getServer().getPluginManager().callEvent( new WallFallEvent( arena.getInternalName() ) );
	}

	public void reset() {
		this.timer.init( timeMax );

		for( Wall wall : walls ) {
			wall.reset();
		}
	}

	public void stop() {
		this.timer.stop();

		for( Wall wall : walls ) {
			wall.stop();
		}
	}

	public static int getWallsCount( PluginInstance pli, String arenaName ) {
		int ret = 0;
		FileConfiguration config = pli.getArenasConfig().getConfig();
		if( config.isSet( ArenaConfigStrings.ARENAS_PREFIX + arenaName + ".walls." ) ) {
			for( String wall : config.getConfigurationSection( ArenaConfigStrings.ARENAS_PREFIX + arenaName + ".walls." ).getKeys( false ) ) {
				ret++;
			}
		}
		return ret;
	}

	public boolean ready() {
		boolean ready = true;
		for( Wall wall : walls ) {
			ready &= wall.ready();
		}
		return ready;
	}

	public int getTimeLeft() {
		return this.timer.timeLeft();
	}

	public int getTimeMax() {
		return this.timeMax;
	}
}
