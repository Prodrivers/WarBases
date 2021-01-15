package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.*;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.ui.wall.FAWEWallUI;
import fr.horgeon.prodrivers.games.warbases.ui.wall.WallUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class EArenaSetup extends ArenaSetup {
	public void setSpawn( JavaPlugin plugin, String arenaName, ArenaTeam arenaTeam, Location location ) {
		Util.saveComponentForArena( plugin, arenaName, "spawns.spawn" + Integer.toString( arenaTeam.toInt() ), location );
	}

	@Override
	@Deprecated
	public void setSpawn( JavaPlugin plugin, String arenaname, Location l ) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int autoSetSpawn( JavaPlugin plugin, String arenaname, Location l ) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void setSpawn( JavaPlugin plugin, String arenaname, Location l, int count ) {
		throw new UnsupportedOperationException();
	}

	/*@Override
	public Arena saveArena( JavaPlugin plugin, String arenaname ) {
		if( !Validator.isArenaValid( plugin, arenaname ) ) {
			Bukkit.getConsoleSender().sendMessage( ChatColor.RED + "Arena " + arenaname + " appears to be invalid." );
			return null;
		}
		PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance( plugin );
		if( pli.getArenaByName( arenaname ) != null ) {
			pli.removeArenaByName( arenaname );
		}
		EArena a = Main.initArena( plugin, arenaname );
		if( a.getArenaType() == ArenaType.REGENERATION ) {
			if( Util.isComponentForArenaValid( plugin, arenaname, "bounds" ) ) {
				Util.saveArenaToFile( plugin, arenaname );
			} else {
				Bukkit.getConsoleSender().sendMessage( ChatColor.RED + "Could not save arena to file because boundaries were not set up." );
			}
		}
		this.setArenaVIP( plugin, arenaname, false );
		pli.addArena( a );
		return a;
	}*/

	/*public static boolean checkpointSetupBlock( JavaPlugin plugin, PluginInstance pli, BlockPlaceEvent event ) {
		if( event.getBlock().getType() == Material.WOOL ) {
			Player p = event.getPlayer();
			if( p.hasPermission( MinigamesAPI.getAPI().getPermissionGamePrefix( "warbases" ) + ".setup" ) ) {
				String arenaname_ = event.getItemInHand().getItemMeta().getDisplayName();

				if( arenaname_ == null )
					return false;

				String args[] = arenaname_.split( ":" );
				if( !args[ 0 ].equalsIgnoreCase( "warbases" ) ) {
					return false;
				}

				if( args[ 1 ].equalsIgnoreCase( "setcheckpoint" ) ) {
					ArenaTeam team = ArenaTeam.fromString( args[ 3 ] );
					if( team != null ) {
						String arenaName = args[ 2 ];

						Location l = event.getBlock().getLocation();

						String cpName = "checkpoints.cp" + team.toInt();
						Util.saveComponentForArena( plugin, arenaName, cpName + ".center", l.clone().subtract( 0.5, 1, 0.5 ) );
						pli.getArenasConfig().getConfig().set( ArenaConfigStrings.ARENAS_PREFIX + arenaName + "." + cpName + ".team", team.toInt() );
						pli.getArenasConfig().getConfig().set( ArenaConfigStrings.ARENAS_PREFIX + arenaName + "." + cpName + ".radius", 3 );
						pli.getArenasConfig().saveConfig();
						p.sendMessage( pli.getMessagesConfig().successfully_set.replaceAll( "<component>", "checkpoint " + team ) );
						return true;
					}
				}
			}
		}

		return false;
	}*/

	public static void checkpointSetup( JavaPlugin plugin, PluginInstance pli, String arenaName, ArenaTeam team, Location location ) {
		Util.saveComponentForArena( plugin, arenaName, "checkpoints.cp" + Integer.toString( team.toInt() ), location );
		String path = "checkpoints.cp" + Integer.toString( team.toInt() );
		pli.getArenasConfig().getConfig().set( path + ".team", team.toInt() );
		pli.getArenasConfig().getConfig().set( path + ".radius", 3 );
	}

	public static void checkpointRadiusSetup( PluginInstance pli, String arenaName, ArenaTeam team, int radius ) {
		String cpName = "checkpoints.cp" + team.toInt();
		pli.getArenasConfig().getConfig().set( ArenaConfigStrings.ARENAS_PREFIX + arenaName + "." + cpName + ".radius", radius );
		pli.getArenasConfig().saveConfig();
	}

	public static boolean wallSetup( JavaPlugin plugin, PluginInstance pli, String arenaName, Location low, Location high ) {
		return wallSetup( WallManager.getWallsCount( pli, arenaName ), plugin, pli, arenaName, low, high );
	}

	public static boolean wallSetup( Integer id, JavaPlugin plugin, PluginInstance pli, String arenaName, Location low, Location high ) {
		// Store the selection in the configuration
		if( low == null )
			throw new NullPointerException( "Low corner is null" );
		if( high == null )
			throw new NullPointerException( "High corner is null" );

		if( id == null )
			return wallSetup( plugin, pli, arenaName, low, high );

		try {
			Util.saveComponentForArena( plugin, arenaName, "walls.wall" + Integer.toString( id ) + ".low", low.clone() );
			Util.saveComponentForArena( plugin, arenaName, "walls.wall" + Integer.toString( id ) + ".high", high.clone() );
		} catch( NullPointerException ex ) {
			System.err.println( "[WarBases] Error while trying to set wall position in the arena configuration: configuration file probably broken/missing." );
			ex.printStackTrace();

			return false;
		}

		// Save the section in a schematic, used by the plugin when resetting
		return FAWEWallUI.saveWallSection( id, plugin, arenaName, low, high );
	}

	public static boolean gameWallSetup( JavaPlugin plugin, PluginInstance pli, String arenaName, Location low, Location high ) {
		return gameWallSetup( WallManager.getWallsCount( pli, arenaName ) - 1, plugin, pli, arenaName, low, high );
	}

	public static boolean gameWallSetup( Integer id, JavaPlugin plugin, PluginInstance pli, String arenaName, Location low, Location high ) {
		// Store the selection in the configuration
		if( low == null )
			throw new NullPointerException( "Low corner is null" );
		if( high == null )
			throw new NullPointerException( "High corner is null" );

		if( id == null )
			return gameWallSetup( plugin, pli, arenaName, low, high );

		// Save the section in a schematic, used by the plugin when resetting
		return FAWEWallUI.saveGameWallSection( id, plugin, arenaName, low, high );
	}

	public static boolean setVariable( PluginInstance pli, String arenaName, ArenaVariable variable, String value ) {
		ConfigurationSection configurationSection = pli.getArenasConfig().getConfig().getConfigurationSection( ArenaConfigStrings.ARENAS_PREFIX + arenaName );
		if( configurationSection != null ) {
			ArenaVariable.save( configurationSection, variable, value );
			pli.getArenasConfig().saveConfig();
			return true;
		}
		return false;
	}
}
