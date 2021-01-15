package fr.horgeon.prodrivers.games.warbases.arena.checkpoint;

import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.util.Util;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.configuration.EArenasConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CheckpointManager {
	private Main plugin;
	private EArena arena;

	private ArrayList<Checkpoint> checkpoints = new ArrayList<>();
	private HashMap<ArenaTeam, Checkpoint> checkpointsMap = new HashMap<>();

	private Checkpoint finished = null;

	public CheckpointManager( Main plugin, EArena arena ) {
		this.plugin = plugin;
		this.arena = arena;

		//load();
	}

	public void load() {
		EArenasConfig arenaConfig = (EArenasConfig) this.plugin.getPluginInstance().getArenasConfig();

		FileConfiguration config = arenaConfig.getConfig();
		String configPath = ArenaConfigStrings.ARENAS_PREFIX + this.arena.getInternalName() + ".checkpoints";

		if( config.isSet( configPath + "." ) ) {

			int maxCycles = (int) ArenaVariable.get( "checkpoint_max_number_of_cycles" ).getValue( this.plugin, this.arena );
			int playerCycleMultiplier = (int) ArenaVariable.get( "checkpoint_cycle_multiplier_per_player" ).getValue( this.plugin, this.arena );
			int maxPlayersCycleMultiplier = (int) ArenaVariable.get( "checkpoint_max_number_of_players_for_multipliers" ).getValue( this.plugin, this.arena );
			int yCheck = (int) ArenaVariable.get( "checkpoint_register_y_axis" ).getValue( this.plugin, this.arena );
			int pointsToConquer = (int) ArenaVariable.get( "checkpoint_points_to_conquer" ).getValue( this.plugin, this.arena );

			for( String cp : config.getConfigurationSection( configPath ).getKeys( false ) ) {
				if( config.isSet( configPath + "." + cp + ".center" ) && config.isSet( configPath + "." + cp + ".team" ) && config.isSet( configPath + "." + cp + ".radius" ) ) {
					ArenaTeam team = ArenaTeam.fromInt( config.getInt( configPath + "." + cp + ".team" ) );
					int radius = config.getInt( configPath + "." + cp + ".radius" );

					if( team != null ) {
						Checkpoint cp_ = new Checkpoint(
							this.plugin,
							this.arena,
							Util.getComponentForArena( this.plugin, this.arena.getInternalName(), "checkpoints." + cp + ".center" ),
							radius,
							team,
							maxCycles,
							playerCycleMultiplier,
							maxPlayersCycleMultiplier,
							yCheck,
							pointsToConquer
						);
						this.checkpoints.add( cp_ );
						this.checkpointsMap.put( cp_.getTeam(), cp_ );
					}
				}
			}
		}
	}

	public void reload() {
		load();
	}

	public void update() {
		if( finished == null ) {
			for( Checkpoint cp : this.checkpoints ) {
				cp.update( this.arena.getArenaManager().getPlayers() );

				if( cp.conquered() ) {
					this.finished = cp;
					Bukkit.getServer().getPluginManager().callEvent( new CheckpointConqueredEvent( arena.getInternalName(), cp ) );
				}
			}
		}
	}

	public boolean isFinished() {
		return this.finished != null;
	}

	public Checkpoint finished() {
		return this.finished;
	}

	public void reset() {
		this.finished = null;

		for( Checkpoint cp : this.checkpoints ) {
			cp.reset();
		}
	}

	public void preStart() {
		for( Checkpoint cp : this.checkpoints ) {
			cp.preStart();
		}
	}

	public void start( ArenaPlayer p ) {
		for( Checkpoint cp : this.checkpoints ) {
			cp.start( p );
		}
	}

	public void stop( ArenaPlayer p ) {
		for( Checkpoint cp : this.checkpoints ) {
			cp.stop( p );
		}
	}

	public ArrayList<Checkpoint> all() {
		return this.checkpoints;
	}

	public Checkpoint get( ArenaTeam team ) {
		return this.checkpointsMap.get( team );
	}

	public void enteringOvertime( ArenaTeam team ) {
		finished = null;
		get( team ).enteringOvertime();
	}

	public void endingOvertime( ArenaTeam team ) {
		this.finished = get( team );
		Bukkit.getServer().getPluginManager().callEvent( new CheckpointConqueredEvent( arena.getInternalName(), team, false, true ) );
	}
}
