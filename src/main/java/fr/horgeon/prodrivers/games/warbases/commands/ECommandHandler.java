package fr.horgeon.prodrivers.games.warbases.commands;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.commands.CommandHandler;
import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.util.Validator;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.EArenaSetup;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.classes.GameClass;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.ui.PreferencesSelector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.lang.StringBuilder;

public class ECommandHandler extends CommandHandler {
	public ECommandHandler() {
		super();
	}

	@Override
	protected void initCmdDesc() {
		super.initCmdDesc();

		this.cmddesc.remove( "setspawn <arena>" );
		this.cmddesc.put( "setspawn <arena> <team>", "Sets the spawn point." );
		this.cmddesc.put( "setcheckpoint <arena> <team>", "Sets the checkpoint center for a team." );
		this.cmddesc.put( "setcheckpointradius <arena> <team> <radius>", "Sets the checkpoint radius for a team." );
		this.cmddesc.put( "setwall <arena> <low/high/save/game> [id]", "Sets a new wall's position, with an optional ID." );
		this.cmddesc.put( "setvariable <arena> <variable> <value>", "Sets an arena property." );
		this.cmddesc.put( "", null );
		this.cmddesc.put( "prefs", "Allows you to set your preferences." );
		this.cmddesc.put( "team", "Allows you to select your team during the Join phase of an arena." );
		this.cmddesc.put( "credits", "See WarBases credits" );
	}

	private EMessagesConfig getMessages( PluginInstance pli ) {
		return (EMessagesConfig) pli.getMessagesConfig();
	}

	@Override
	public boolean sendHelp( String cmd, CommandSender sender ) {
		if( sender.hasPermission( Main.getAPI().getPermissionGamePrefix( "WarBases" ) + ".setup" ) ) {
			return super.sendHelp( cmd, sender );
		}

		String prefix = ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "|" + ChatColor.BLUE + " WarBases " + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "|" + ChatColor.RESET + " ";

		sender.sendMessage( prefix + ChatColor.GOLD + "/hub wb:" + ChatColor.RESET + " Aller au hub du WarBases" );
		sender.sendMessage( prefix + ChatColor.GOLD + "/wb prefs:" + ChatColor.RESET + " Mettre à jour les préférences" );
		sender.sendMessage( prefix + ChatColor.GOLD + "/wb credits:" + ChatColor.RESET + " Voir les crédits" );
		return true;
	}

	private void credits( PluginInstance pli, String permissionPrefix, CommandSender sender, String[] args ) {
		String prefix = ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "|" + ChatColor.BLUE + " WarBases " + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "|" + ChatColor.RESET + " ";

		sender.sendMessage( prefix + ChatColor.BOLD + "WarBases : un jeu Prodrivers" );
		sender.sendMessage( prefix );
		sender.sendMessage( prefix + ChatColor.GRAY + "Concept : " + ChatColor.RESET + "Colasix, Destro_Jones" );
		sender.sendMessage( prefix + ChatColor.GRAY + "Développement : " + ChatColor.RESET + "Horgeon" );
		sender.sendMessage( prefix + ChatColor.GRAY + "Visuels : " + ChatColor.RESET + "Horgeon" );
		sender.sendMessage( prefix + ChatColor.GRAY + "Voix : " + ChatColor.RESET + "Éric Goultard, Patrick Borg" );
		sender.sendMessage( prefix );
		sender.sendMessage( prefix + "Les éléments utilisés sont la propriété respective de leurs auteurs." );
	}

	@Override
	public boolean setSpawn( PluginInstance pli, CommandSender sender, String[] args, String permissionPrefix, String cmd, String action, JavaPlugin plugin, Player p ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return true;
		}
		if( args.length > 2 ) {
			ArenaTeam team = ArenaTeam.fromString( args[ 2 ] );
			if( team != null ) {
				( (EArenaSetup) pli.arenaSetup ).setSpawn( plugin, args[ 1 ], team, p.getLocation() );
				sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "spawn " + team ) );
				return true;
			}

			sender.sendMessage( getMessages( pli ).invalid_team );

			sender.sendMessage( getMessages( pli ).arena_invalid.replaceAll( "<arena>", args[ 1 ] ) );
		} else {
			sender.sendMessage( ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <team>" );
		}
		return true;
	}

	@Override
	public boolean setDescription( final PluginInstance pli, final CommandSender sender, final String[] args, final String permissionPrefix, final String cmd, final String action, final JavaPlugin plugin, final Player p ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( pli.getMessagesConfig().no_perm );
			return true;
		}
		if( args.length > 2 ) {
			if( Validator.isArenaValid( plugin, args[ 1 ] ) ) {
				StringBuilder desc = new StringBuilder( args[ 2 ] );
				for( int i = 3; i < args.length; i++ ) {
					desc.append( " " );
					desc.append( args[ i ] );
				}
				pli.getArenasConfig().getConfig().set( ArenaConfigStrings.ARENAS_PREFIX + args[ 1 ] + ArenaConfigStrings.DESCRIPTION_SUFFIX, desc.toString() );
				pli.getArenasConfig().saveConfig();
				sender.sendMessage( pli.getMessagesConfig().successfully_set.replaceAll( "<component>", "description" ) );
			}
		} else {
			sender.sendMessage( ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd + " " + action + " <arena> <description>" );
		}
		return true;
	}

	@Override
	public boolean saveArena( PluginInstance pli, CommandSender sender, String[] args, String permissionPrefix, String cmd, String action, JavaPlugin plugin, Player p ) {
		if( args.length > 1 ) {
			ConfigurationSection config = pli.getArenasConfig().getConfig().getConfigurationSection( ArenaConfigStrings.ARENAS_PREFIX + args[ 1 ] );
			if( !config.isSet( "walls.wall0" ) ) {
				return false;
			}

			for( ArenaTeam team : ArenaTeam.values() ) {
				if( !config.isSet( "spawns.spawn" + team.toInt() ) || !config.isSet( "checkpoints.cp" + team.toInt() ) )
					return false;
			}
		}

		return super.saveArena( pli, sender, args, permissionPrefix, cmd, action, plugin, p );
	}

	/*private void giveCheckpointMaker( PluginInstance pli, String permissionPrefix, Player p, String[] args ) {
		if( !p.hasPermission( permissionPrefix + ".setup" ) ) {
			p.sendMessage( getMessages( pli ).no_perm );
			return;
		}
		if( args.length > 1 ) {
			Arena arena = pli.getArenaByName( args[ 1 ] );
			if( arena != null && arena instanceof EArena ) {
				if( args.length > 2 ) {
					ArenaTeam team = ArenaTeam.fromString( args[ 2 ] );
					if( team != null ) {
						giveCheckpointMaker( pli, p, args[ 1 ], team );
						return;
					}
				}

				p.sendMessage( getMessages( pli ).invalid_team );
				return;
			}

			p.sendMessage( getMessages( pli ).arena_invalid.replaceAll( "<arena>", args[ 1 ] ) );
			return;
		}

		p.sendMessage( getMessages( pli ).no_arena_name );
	}

	private void giveCheckpointMaker( PluginInstance pli, Player p, String arenaName, ArenaTeam team ) {
		p.sendMessage( getMessages( pli ).place_wool_for_checkpoints );
		ItemStack item = new ItemStack( Material.WOOL, 1, team.toColorByte() );
		ItemMeta im = item.getItemMeta();
		im.setDisplayName( "warbases:setcheckpoint:" + arenaName + ":" + team );
		item.setItemMeta( im );
		p.getInventory().addItem( item );
		p.updateInventory();
	}*/

	private void setCheckpoint( JavaPlugin plugin, PluginInstance pli, String permissionPrefix, Player sender, String[] args ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return;
		}
		if( args.length > 1 ) {
			if( args.length > 2 ) {
				ArenaTeam team = ArenaTeam.fromString( args[ 2 ] );
				if( team != null ) {
					EArenaSetup.checkpointSetup( plugin, pli, args[ 1 ], team, sender.getLocation() );
					sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "checkpoint center" ) );
					return;
				}
			}

			sender.sendMessage( getMessages( pli ).invalid_team );
			return;
		}

		sender.sendMessage( getMessages( pli ).no_arena_name );
	}

	private void setCheckpointRadius( PluginInstance pli, String permissionPrefix, CommandSender sender, String[] args ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return;
		}
		if( args.length > 1 ) {
			if( args.length > 2 ) {
				ArenaTeam team = ArenaTeam.fromString( args[ 2 ] );
				if( team != null ) {
					if( args.length > 3 ) {
						int radius = Integer.valueOf( args[ 3 ] );
						if( radius > 0 ) {
							EArenaSetup.checkpointRadiusSetup( pli, args[ 1 ], team, radius );
							sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "checkpoint radius" ) );
							return;
						}
					}

					sender.sendMessage( getMessages( pli ).invalid_radius );
					return;
				}
			}

			sender.sendMessage( getMessages( pli ).invalid_team );
			return;
		}

		sender.sendMessage( getMessages( pli ).no_arena_name );
	}

	private void wallSetup( JavaPlugin plugin, PluginInstance pli, String permissionPrefix, Player sender, String[] args ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return;
		}

		if( args.length > 1 ) {
			if( args.length > 2 ) {
				String pos = args[ 2 ];
				if( pos.equalsIgnoreCase( "low" ) || pos.equalsIgnoreCase( "high" ) || pos.equalsIgnoreCase( "save" ) || pos.equalsIgnoreCase( "game" ) ) {
					if( args.length > 3 ) {
						try {
							Integer id = Integer.valueOf( args[ 3 ] );
							wallSetup( plugin, pli, sender, args[ 1 ], pos, id );
						} catch( NumberFormatException ex ) {
							sender.sendMessage( getMessages( pli ).invalid_value );
							return;
						}
					} else {
						wallSetup( plugin, pli, sender, args[ 1 ], pos );
						return;
					}
				}
			}

			sender.sendMessage( getMessages( pli ).invalid_position );
			return;
		}

		sender.sendMessage( getMessages( pli ).no_arena_name );
	}

	private HashMap<String, Location> lowCorners = new HashMap<>();
	private HashMap<String, Location> highCorners = new HashMap<>();

	private void wallSetup( JavaPlugin plugin, PluginInstance pli, Player sender, String arenaName, String position ) {
		wallSetup( plugin, pli, sender, arenaName, position, null );
	}

	private void wallSetup( JavaPlugin plugin, PluginInstance pli, Player sender, String arenaName, String position, Integer id ) {
		if( position.equalsIgnoreCase( "low" ) ) {
			lowCorners.put( sender.getName(), sender.getLocation().getBlock().getLocation() );

			sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "temporary wall low corner" ) );
		} else if( position.equalsIgnoreCase( "high" ) ) {
			highCorners.put( sender.getName(), sender.getLocation().getBlock().getLocation() );

			sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "temporary wall high corner" ) );
		} else if( position.equalsIgnoreCase( "save" ) ) {
			if( lowCorners.containsKey( sender.getName() ) && highCorners.containsKey( sender.getName() ) ) {
				if( EArenaSetup.wallSetup( id, plugin, pli, arenaName, lowCorners.get( sender.getName() ), highCorners.get( sender.getName() ) ) )
					sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "wall ID " + Integer.toString( WallManager.getWallsCount( pli, arenaName ) - 1 ) ) );
				else
					sender.sendMessage( getMessages( pli ).error_occured_command );
			} else {
				sender.sendMessage( getMessages( pli ).no_corner_set );
			}
		} else if( position.equalsIgnoreCase( "game" ) ) {
			if( lowCorners.containsKey( sender.getName() ) && highCorners.containsKey( sender.getName() ) ) {
				if( EArenaSetup.gameWallSetup( id, plugin, pli, arenaName, lowCorners.get( sender.getName() ), highCorners.get( sender.getName() ) ) )
					sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", "wall ID " + Integer.toString( WallManager.getWallsCount( pli, arenaName ) - 1 ) ) );
				else
					sender.sendMessage( getMessages( pli ).error_occured_command );
			} else {
				sender.sendMessage( getMessages( pli ).no_corner_set );
			}
		}
	}

	private void reloadManagers( PluginInstance pli, String permissionPrefix, CommandSender sender, String[] args ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return;
		}

		if( args.length > 1 ) {
			Arena arena = pli.getArenaByName( args[ 1 ] );
			if( arena != null && arena instanceof EArena ) {
				( (EArena) arena ).reloadManagers();
				sender.sendMessage( getMessages( pli ).successfully_reloaded );
				return;
			}
			sender.sendMessage( getMessages( pli ).arena_invalid.replaceAll( "<arena>", args[ 1 ] ) );
			return;
		}
		sender.sendMessage( getMessages( pli ).no_arena_name );
	}

	private void setTeam( Main plugin, PluginInstance pli, String permissionPrefix, Player player, String[] args ) {
		if( pli.containsGlobalPlayer( player.getName() ) ) {
			Arena arena = pli.getArenaByGlobalPlayer( player.getName() );
			if( arena != null && arena instanceof EArena ) {
				if( arena.getArenaState() == ArenaState.JOIN ) {
					if( args.length > 1 ) {
						ArenaTeam team = ArenaTeam.fromString( args[ 1 ] );
						if( team != null ) {
							ArenaPlayer ap = ( (EArena) arena ).getArenaManager().getPlayer( player );
							plugin.getTeamsSelectorUI().setTeam( (EArena) arena, team, ap );
							return;
						}
					}
					player.sendMessage( getMessages( pli ).invalid_team );
					return;
				}
				player.sendMessage( getMessages( pli ).arena_already_started );
				return;
			}
		}
		player.sendMessage( getMessages( pli ).not_in_arena );
	}

	private void setVariables( PluginInstance pli, String permissionPrefix, CommandSender sender, String[] args ) {
		if( !sender.hasPermission( permissionPrefix + ".setup" ) ) {
			sender.sendMessage( getMessages( pli ).no_perm );
			return;
		}

		if( args.length > 1 ) {
			if( args.length > 2 ) {
				ArenaVariable variable = ArenaVariable.get( args[ 2 ] );
				if( variable != null ) {
					if( args.length > 3 ) {
						try {
							if( EArenaSetup.setVariable( pli, args[ 1 ], variable, args[ 3 ] ) ) {
								sender.sendMessage( getMessages( pli ).successfully_set.replaceAll( "<component>", args[ 2 ] ) );
							} else {
								sender.sendMessage( getMessages( pli ).error_occured_command );
							}
							return;
						} catch( IllegalArgumentException ex ) {}
					}

					sender.sendMessage( getMessages( pli ).invalid_value );
					return;
				}
			}

			sender.sendMessage( getMessages( pli ).invalid_variable );
			return;
		}

		sender.sendMessage( getMessages( pli ).no_arena_name );
	}

	private void reload( Main plugin, PluginInstance pli, CommandSender sender ) {
		GameClass.load( pli );
		plugin.getResourcePackManager().reload();
		//plugin.getLobby().reload();
		plugin.getLobbySection().reload();
	}

	public boolean handleSupplementaryArgs( Main plugin, PluginInstance pli, String permissionPrefix, CommandSender sender, Command cmd, String label, String[] args ) {
		if( args.length > 0 ) {
			if( args[ 0 ].equalsIgnoreCase( "team" ) ) {
				if( sender instanceof Player )
					setTeam( plugin, pli, permissionPrefix, (Player) sender, args );
				else
					sender.sendMessage( getMessages( pli ).not_a_player );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "setcheckpoint" ) ) {
				if( sender instanceof Player )
					//giveCheckpointMaker( pli, permissionPrefix, (Player) sender, args );
					setCheckpoint( plugin, pli, permissionPrefix, (Player) sender, args );
				else
					sender.sendMessage( getMessages( pli ).not_a_player );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "setcheckpointradius" ) ) {
				setCheckpointRadius( pli, permissionPrefix, sender, args );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "setwall" ) ) {
				if( sender instanceof Player )
					wallSetup( plugin, pli, permissionPrefix, (Player) sender, args );
				else
					sender.sendMessage( getMessages( pli ).not_a_player );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "prefs" ) ) {
				if( sender instanceof Player )
					PreferencesSelector.handle( plugin, pli, permissionPrefix, (Player) sender, args );
				else
					sender.sendMessage( getMessages( pli ).not_a_player );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "setvariable" ) ) {
				setVariables( pli, permissionPrefix, sender, args );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "credits" ) ) {
				credits( pli, permissionPrefix, sender, args );
				return true;
			} else if( args[ 0 ].equalsIgnoreCase( "reload" ) ) {
				reloadManagers( pli, permissionPrefix, sender, args );
				return false;
			/*} else if( args[ 0 ].equalsIgnoreCase( "lobby" ) ) {
				plugin.getLobby().handleCommand( sender );
				return true;*/
			} else if( args[ 0 ].equalsIgnoreCase( "reload" ) ) {
				reload( plugin, pli, sender );
				return false;
			}
		}
		return false;
	}
}
