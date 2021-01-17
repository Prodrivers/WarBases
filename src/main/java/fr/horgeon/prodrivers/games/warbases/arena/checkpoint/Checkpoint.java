package fr.horgeon.prodrivers.games.warbases.arena.checkpoint;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.ui.checkpoint.CheckpointUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Checkpoint {
	private Main plugin;
	private EArena a;

	private CheckpointUI ui;

	private Location center;
	private int radius = 3;
	private ArenaTeam team;

	private int progress = 0;
	private int cyclesElapsed = 0;
	private int maxCycles = 12;
	private int playerCycleMultiplier = 1;
	private int maxPlayersCycleMultiplier = 3;

	private int lastContest = 0;
	private int lastActive = 0;

	private int yCheck = 100;
	private int pointsToConquer = 100;

	private ArrayList<ArenaPlayer> activePlayers = new ArrayList<>();

	private boolean inOvertime = false;

	public Checkpoint( Main plugin, EArena a, Location center, int radius, ArenaTeam team, int maxCycles, int playerCycleMultiplier, int maxPlayersCycleMultiplier, int yCheck, int pointsToConquer ) {
		this.a = a;
		this.plugin = plugin;
		this.center = center;
		this.radius = ( radius > 0 ? radius : this.radius );
		this.team = team;

		this.maxCycles = maxCycles;
		this.playerCycleMultiplier = playerCycleMultiplier;
		this.maxPlayersCycleMultiplier = maxPlayersCycleMultiplier;
		this.yCheck = yCheck;
		this.pointsToConquer = pointsToConquer;

		this.ui = new CheckpointUI( plugin, a, center, radius, team );

		reset();

		System.out.println( "[WarBases] Arena " + a.getInternalName() + ": Checkpoint initiated at " + center );
	}

	public Location getCenter() {
		return this.center;
	}

	public ArenaTeam getTeam() {
		return this.team;
	}

	public void preStart() {
		ui.preStart();
	}

	public void start( ArenaPlayer p ) {
		ui.start( p );
	}

	public void stop( ArenaPlayer p ) {
		ui.stop( p );
	}

	public void reset() {
		this.progress = 0;
		this.lastContest = 0;
		inOvertime = false;
		this.activePlayers.clear();
		this.ui.reset();
	}
	
	public int getNumberOfActivePlayers() {
		return this.activePlayers.size();
	}

	public ArrayList<ArenaPlayer> getActivePlayers() {
		return this.activePlayers;
	}

	public boolean isIn( ArenaPlayer p ) {
		if( !p.isDead() ) {
			Location l = p.getLocation();
			float x_diff = l.getBlockX() - center.getBlockX();
			float z_diff = l.getBlockZ() - center.getBlockZ();
			return ( ( ( x_diff * x_diff + z_diff * z_diff ) < ( radius * radius ) ) && Math.abs( l.getBlockY() - center.getBlockY() ) < yCheck );
		}

		return false;
	}

	public boolean conquered() {
		return this.progress >= pointsToConquer;
	}

	void update( HashMap<String, ArenaPlayer> arenaPlayers ) {
		boolean active = false;
		boolean contested = false;
		activePlayers.clear();

		for( Map.Entry<String, ArenaPlayer> entry : arenaPlayers.entrySet() ) {
			ArenaPlayer p = entry.getValue();
			if( isIn( p ) ) {
				p.inCheckpoint( team );

				if( p.getTeam() != this.team ) {
					active = true;

					activePlayers.add( p );
				} else {
					contested = true;
				}
			} else {
				p.outCheckpoint( team );
			}
		}

		if( active ) {
			this.lastActive = 0;
			if( !contested ) {
				if( activePlayers.size() > maxPlayersCycleMultiplier )
					cyclesElapsed += maxPlayersCycleMultiplier * playerCycleMultiplier;
				else
					cyclesElapsed += activePlayers.size() * playerCycleMultiplier;
				this.lastContest++;
			} else {
				this.lastContest = 0;
			}
			Bukkit.getServer().getPluginManager().callEvent( new CheckpointConqueringEvent( a.getInternalName(), team, contested ) );
		} else {
			this.lastContest++;
			this.lastActive++;
		}

		if( cyclesElapsed > maxCycles ) {
			cyclesElapsed = 0;
			if( !inOvertime ) {
				progress++;
				Bukkit.getServer().getPluginManager().callEvent( new CheckpointProgressEvent( a.getInternalName(), team, progress, pointsToConquer - progress ) );
			}
		}
	}

	public int getProgress() {
		return this.progress;
	}

	public int getPointsToConquer() {
		return this.pointsToConquer;
	}

	public int getCyclesElapsed() {
		return this.cyclesElapsed;
	}

	public boolean wasContested() {
		return ( lastContest < plugin.getConfig().getInt( "config.overtime.last_contest_threshold" ) );
	}

	public boolean wasActivated() {
		return ( lastActive < plugin.getConfig().getInt( "config.overtime.last_contest_threshold" ) );
	}

	void enteringOvertime() {
		this.progress--;
		Bukkit.getServer().getPluginManager().callEvent( new CheckpointProgressEvent( a.getInternalName(), team, progress, pointsToConquer - progress ) );
		this.inOvertime = true;
	}

	public boolean isInOvertime() {
		return this.inOvertime;
	}
}
