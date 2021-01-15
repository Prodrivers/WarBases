package fr.horgeon.prodrivers.games.warbases.configuration;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ArenaVariable {
	public final String configurationPath;
	public final Class<?> type;
	public final Object defaultValue;

	private ArenaVariable( String configurationPath, Class<?> type, Object defaultValue ) {
		this.configurationPath = configurationPath;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	private static Map<String, ArenaVariable> variables = new HashMap<>();

	static {
		variables.put( "checkpoint_register_y_axis", new ArenaVariable( "checkpoint.register_y_axis", Integer.class, 100 ) );
		variables.put( "checkpoint_points_to_conquer", new ArenaVariable( "checkpoint.points_to_conquer", Integer.class, 100 ) );
		variables.put( "checkpoint_max_number_of_cycles", new ArenaVariable( "checkpoint.cycles.max_number_of_cycles", Integer.class, 12 ) );
		variables.put( "checkpoint_cycle_multiplier_per_player", new ArenaVariable( "checkpoint.cycles.cycle_multiplier_per_player", Integer.class, 1 ) );
		variables.put( "checkpoint_max_number_of_players_for_multipliers", new ArenaVariable( "checkpoint.cycles.max_number_of_players_for_multipliers", Integer.class, 3 ) );
		variables.put( "wall_countdown", new ArenaVariable( "wall.countdown", Integer.class, 3 * 60 ) );
		variables.put( "lives", new ArenaVariable( "lives", Integer.class, 0 ) );
		variables.put( "time", new ArenaVariable( "time", Integer.class, 300 )  );
		variables.put( "intro", new ArenaVariable( "ui.intro", Boolean.class, false ) );
		variables.put( "outro", new ArenaVariable( "ui.outro", Boolean.class, true ) );
	}

	public static boolean contains( String name ) {
		return variables.containsKey( name );
	}

	public static ArenaVariable get( String name ) {
		return variables.get( name );
	}

	static void addDefaults( ConfigurationSection configuration ) {
		for( ArenaVariable variable : variables.values() ) {
			configuration.addDefault( Constants.CONFIGURATION_DEFAULT_PREFIX + "." + variable.configurationPath, variable.defaultValue );
		}
	}

	public static void save( ConfigurationSection configuration, ArenaVariable variable, String value_ ) throws IllegalArgumentException {
		Object value = null;
		if( variable.type.equals( Integer.class ) ) {
			try {
				value = Integer.valueOf( value_ );
			} catch( NumberFormatException ex ) {
				throw new IllegalArgumentException( "Provided value is not an Integer as expected for this variable." );
			}
		} else if( variable.type.equals( Boolean.class ) ) {
			value = Boolean.valueOf( value_ );
		}
		if( value != null )
			configuration.set( variable.configurationPath, value );
	}

	public Object getValue( Main plugin, Arena arena ) {
		ConfigurationSection config = plugin.getPluginInstance().getArenasConfig().getConfig();
		String path = ArenaConfigStrings.ARENAS_PREFIX + arena.getInternalName() + "." + this.configurationPath;
		if( config.isSet( path ) ) {
			return config.get( path );
		}

		path = Constants.CONFIGURATION_DEFAULT_PREFIX + "." + this.configurationPath;

		if( plugin.getConfig().isSet( path ) ) {
			return plugin.getConfig().get( path );
		}

		return this.defaultValue;
	}
}
