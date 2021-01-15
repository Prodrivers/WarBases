package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaListener;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointConqueredEvent;
import fr.horgeon.prodrivers.games.warbases.arena.overtime.OvertimeStartedEvent;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallFallEvent;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Collection;

public class EArenaListener extends ArenaListener {
	private Main plugin;
	private PluginInstance pli = null;

	private HashSet<Integer> alreadyHandledEvents = new HashSet<>();

	public EArenaListener( JavaPlugin plugin, PluginInstance pinstance, String minigame ) {
		super( plugin, pinstance, minigame );
		this.plugin = (Main) plugin;
		this.pli = pinstance;
	}

	@Override
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat( final AsyncPlayerChatEvent event ) {
		final Player p = event.getPlayer();
		if( p != null && pli.containsGlobalPlayer( p.getName() ) ) {
			if( !this.pli.chat_enabled ) {
				event.setCancelled( true );
				return;
			}

			/*Bukkit.getScheduler().runTask( this.plugin, new Runnable() {
				@Override
				public void run() {*/
					Arena a = pli.getArenaByGlobalPlayer( p.getName() );
					if( a != null && a instanceof EArena ) {
						final ArenaPlayer ap = ( (EArena) a ).getArenaManager().getPlayer( p );
						if( ap != null ) {
							if( ap.canSendMessage() ) {
								String format;
								Collection<ArenaPlayer> players;

								if( a.getArenaState() == ArenaState.INGAME && !event.getMessage().startsWith( ( (EMessagesConfig) pli.getMessagesConfig() ).chat_global_start_character ) ) {
									format = ( (EMessagesConfig) pli.getMessagesConfig() ).chat_format_team.replace( "<points>", String.valueOf( pli.getStatsInstance().getPoints( event.getPlayer().getName() ) ) );
									players = ( (EArena) a ).getArenaManager().getPlayersInTeam( ap.getTeam() );
									event.setMessage( event.getMessage().replace( ( (EMessagesConfig) pli.getMessagesConfig() ).chat_global_start_character, "" ) );
								} else {
									format = ( (EMessagesConfig) pli.getMessagesConfig() ).chat_format_global.replace( "<points>", String.valueOf( pli.getStatsInstance().getPoints( event.getPlayer().getName() ) ) );
									players = ( (EArena) a ).getArenaManager().getPlayers().values();
								}

								//event.setFormat( format );
								event.setFormat( "%2$s" );
								event.setMessage( String.format( format, event.getPlayer().getName(), event.getMessage() ) );

								if( players != null ) {
									event.getRecipients().clear();
									for( ArenaPlayer receiver : players )
										event.getRecipients().add( receiver.getPlayer() );
								}
							} else {
								ap.sendMessage( ( (EMessagesConfig) pli.getMessagesConfig() ).in_game_messages_please_wait );
							}
						}
					}
				/*}
			});*/
		}
	}

	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove( PlayerMoveEvent event ) {
		Player p = event.getPlayer();
		if( p != null && pli.containsGlobalPlayer( p.getName() ) ) {
			Arena a = pli.getArenaByGlobalPlayer( p.getName() );
			if( a != null && a instanceof EArena ) {
				final ArenaPlayer ap = ( (EArena) a ).getArenaManager().getPlayer( p );
				if( ap.freezed() ) {
					event.setCancelled( true );
					return;
				}

				if( a.getArenaState() == ArenaState.INGAME ) {
					if( p.getLocation().getY() < 0 ) {
						// player fell
						event.setCancelled( true );
						( (EArena) a ).respawn( p );
					}
				}/* else {
					a.leavePlayer( p.getName(), false );
				}*/
			}
		}
	}

	/*@Override
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath( PlayerDeathEvent event ) {
		Entity p = event.getEntity();
		if( p != null && pli.containsGlobalPlayer( p.getName() ) ) {
			final Arena a_ = pli.getArenaByGlobalPlayer( p.getName() );
			if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
				EArena a = (EArena) a_;
				event.getDrops().clear();
				event.setDeathMessage( null );
				ArenaPlayer ap = a.getArenaManager().getPlayer( p );
				ArenaPlayer killer = a.getArenaManager().getPlayer( ap.getKiller() );

				if( killer != null ) {
					a.getUIManager().broadcastDeath( killer, ap );
				}

				a.respawn( ap );

				a.onEliminated( p.getName() );
			}
		}
	}*/

	private void playerDamagedByPlayer( EntityDamageEvent event, EArena arena, ArenaPlayer attacker, ArenaPlayer attacked ) {
		if( attacker.getTeam() == attacked.getTeam() ) {
			// same team
			event.setCancelled( true );
		} else {
			if( event.getDamage() > Constants.UI_LOW_HEALTH_THRESHOLD ) {
				arena.uiManager.lowHealth( attacked );
			}

			if( attacked.getHealth() - event.getDamage() <= 0D ) {
				// different team, player is dead
				event.setCancelled( true );
				attacker.addKill();
				arena.respawn( attacker, attacked );
			}
		}
	}

	@Override
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage( final EntityDamageEvent event ) {
		if( event instanceof EntityDamageByEntityEvent ) {
			if( !this.alreadyHandledEvents.contains( event.hashCode() ) )
				onEntityDamageByEntity( (EntityDamageByEntityEvent) event );
			else
				this.alreadyHandledEvents.remove( event.hashCode() );
		}

		Entity e = event.getEntity();
		if( e instanceof Player ) {
			Player p = (Player) e;

			if( pli.containsGlobalPlayer( p.getName() ) ) {
				final Arena a = pli.getArenaByGlobalPlayer( p.getName() );

				if( a != null && a instanceof EArena ) {
					super.onEntityDamage( event );

					if( a.getArenaState() == ArenaState.INGAME ) {
						if( event.getDamage() > Constants.UI_LOW_HEALTH_THRESHOLD ) {
							( (EArena) a ).uiManager.lowHealth( ( (EArena) a ).arenaManager.getPlayer( p ) );
						}
						if( p.getHealth() - event.getDamage() <= 0D ) {
							event.setCancelled( true );
							( (EArena) a ).respawn( p );
						}
					} else {
						event.setCancelled( true );
						//a.leavePlayer( p.getName(), false );
					}
				}
			}
		}
	}

	@Override
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity( EntityDamageByEntityEvent event ) {
		if( !this.alreadyHandledEvents.contains( event.hashCode() ) ) {
			this.alreadyHandledEvents.add( event.hashCode() );

			Entity e = event.getEntity();
			if( e instanceof Player ) {
				Player p = (Player) e;

				if( pli.containsGlobalPlayer( p.getName() ) ) {
					final Arena a_ = pli.getArenaByGlobalPlayer( p.getName() );

					if( a_ != null && a_ instanceof EArena ) {
						super.onEntityDamageByEntity( event );

						if( a_.getArenaState() == ArenaState.INGAME ) {
							EArena a = (EArena) a_;
							ArenaPlayer ap = a.getArenaManager().getPlayer( p );

							if( event.getDamager() instanceof Player ) {
								playerDamagedByPlayer( event, a, a.getArenaManager().getPlayer( event.getDamager() ), ap );
							} else if( event.getDamager() instanceof Arrow ) {
								Arrow ar = (Arrow) event.getDamager();

								if( ar.getShooter() instanceof Player ) {
									playerDamagedByPlayer( event, a, a.getArenaManager().getPlayer( ar.getShooter() ), ap );
								}
							} else {
								if( event.getDamage() > Constants.UI_LOW_HEALTH_THRESHOLD ) {
									a.uiManager.lowHealth( ap );
								}

								if( p.getHealth() - event.getDamage() <= 0D ) {
									event.setCancelled( true );
									a.respawn( p );
								}
							}
						} else {
							event.setCancelled( true );
							//a_.leavePlayer( p.getName(), false );
						}
					}
				}
			}
		} else {
			this.alreadyHandledEvents.remove( event.hashCode() );
		}
	}

	@EventHandler
	public void onPlayerDropItem( PlayerDropItemEvent event ) {
		Arena a = this.pli.getArenaByGlobalPlayer( event.getPlayer().getName() );

		if( a != null ) {
			event.setCancelled( true );
			if( a instanceof EArena && a.getArenaState() == ArenaState.INGAME )
				( (EArena) a ).arenaManager.getPlayer( event.getPlayer() ).toggleComMenu();
		}
	}

	@Override
	@EventHandler
	public void onPlayerPickupItem( PlayerPickupItemEvent event ) {
		Player p = event.getPlayer();
		if( pli.containsGlobalPlayer( p.getName() ) ) {
			event.setCancelled( true );
		}
	}

	@EventHandler
	public void onBreak( BlockBreakEvent event ) {
		final Player p = event.getPlayer();
		if( pli.containsGlobalPlayer( p.getName() ) ) {
			event.setCancelled( true );
		}
	}

	@Override
	@EventHandler
	public void onBlockPlace( BlockPlaceEvent event ) {
		/*if( EArenaSetup.checkpointSetupBlock( plugin, pli, event ) ) {
			event.setCancelled( true );
		}*/
		final Player p = event.getPlayer();
		if( pli.containsGlobalPlayer( p.getName() ) ) {
			event.setCancelled( true );
		}
	}

	/*@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn( PlayerRespawnEvent event ) {
		final Player p = event.getPlayer();
		if( this.pli.containsGlobalPlayer( p.getName() ) ) {
			final Arena a = this.pli.getArenaByGlobalPlayer( p.getName() );
			if( a != null && a instanceof EArena ) {
				( (EArena) a ).getArenaManager().playerHasRespawned( p );
			}
		}
	}*/

	@Override
	@EventHandler
	public void onSignUse( PlayerInteractEvent event ) {
		super.onSignUse( event );

		if( event.hasItem() ) {
			final Player p = event.getPlayer();
			if( !pli.containsGlobalPlayer( p.getName() ) ) {
				return;
			}
			Arena a = pli.getArenaByGlobalPlayer( p.getName() );
			if( a.isArcadeMain() ) {
				return;
			}
			if( a.getArenaState() != ArenaState.INGAME && ( (Main) pli.getPlugin() ).getTeamsSelectorUI().isTeamSelectorItem( event.getItem() ) ) {
				event.setCancelled( true );
				( (Main) pli.getPlugin() ).getTeamsSelectorUI().openGUI( p.getName() );
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWallFall( WallFallEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
			( (EArena) a_ ).wallFall();
		}
	}

	@EventHandler
	public void onCheckpointConquered( CheckpointConqueredEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME && !event.isCancelled() ) {
			System.out.println( "[WarBases] Arena stop because of checkpoint conquest!" );
			( (EArena) a_ ).stop( true, false, false );
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onOvertimeStarted( OvertimeStartedEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
			( (EArena) a_ ).getTimer().stop();
		}
	}

	@Override
	public void onPlayerDrop( PlayerDropItemEvent event ) {
	}
}
