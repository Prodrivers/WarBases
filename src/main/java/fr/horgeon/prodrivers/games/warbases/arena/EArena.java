package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.*;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.overtime.OvertimeManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.ArenaVariable;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesGameSection;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesLobbySection;
import fr.horgeon.prodrivers.games.warbases.ui.game.UIManager;
import fr.prodrivers.bukkit.commons.exceptions.IllegalSectionLeavingException;
import fr.prodrivers.bukkit.commons.parties.Party;
import fr.prodrivers.bukkit.commons.parties.PartyManager;
import fr.prodrivers.bukkit.commons.sections.SectionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EArena extends Arena {
	private Main plugin = null;

	ArenaManager arenaManager;
	CheckpointManager checkpointManager;
	WallManager wallManager;
	UIManager uiManager;
	private ArenaTimer timer;
	OvertimeManager overtimeManager;

	private int maxTime = 0;
	private float maxOvertime = 5.0f;
	private float maxOvertimeReduceStep = 0.05f;

	private BukkitTask updater, lobbyUpdater;
	private int lives = 3;
	private int respawnTime = 5;

	private boolean started = false;

	private int max_players;

	private HashMap<UUID, ArenaTeam> partyTeams = new HashMap<>();

	public EArena( Main plugin, String arena_id ) {
		super( plugin, arena_id, ArenaType.DEFAULT );
		this.plugin = plugin;

		this.checkpointManager = new CheckpointManager( plugin, this );
		this.arenaManager = new ArenaManager();
		this.wallManager = new WallManager( plugin, this );
		this.uiManager = new UIManager( plugin, this, checkpointManager, wallManager );
		this.timer = new ArenaTimer( plugin, this );
		this.overtimeManager = new OvertimeManager( this, checkpointManager );

		//loadConfig();
	}

	@Override
	public void init( Location signloc, ArrayList<Location> spawns, Location mainlobby, Location waitinglobby, int max_players, int min_players, boolean viparena ) {
		this.max_players = max_players;
		super.init( signloc, spawns, mainlobby, waitinglobby, max_players, min_players, viparena );
		loadConfig();
		reloadManagers();
		wallManager.reset();
	}

	@Override
	public void joinPlayerLobby( final UUID playerUuid ) {
		Party party;
		boolean inParty = false;
		ArenaTeam team = null;

		if( getArenaState() != ArenaState.JOIN && this.getArenaState() != ArenaState.STARTING )
			return;

		final Player player = Bukkit.getPlayer( playerUuid );

		if( !getPluginInstance().arenaSetup.getArenaEnabled( this.plugin, this.getInternalName() ) ) {
			Util.sendMessage( this.plugin, player, getPluginInstance().getMessagesConfig().arena_disabled );
			return;
		} else if( getPluginInstance().containsGlobalPlayer( player.getName() ) ) {
			Util.sendMessage( this.plugin, player, getPluginInstance().getMessagesConfig().already_in_arena );
			return;
		} else if( getArcadeInstance() == null && isVIPArena() && !player.hasPermission( MinigamesAPI.getAPI().getPermissionGamePrefix( this.plugin.getName() ) + ".arenas." + this.getInternalName() + ".vip" ) ) {
			Util.sendMessage( this.plugin, player, getPluginInstance().getMessagesConfig().no_perm_to_join_arena.replaceAll( "<arena>", this.getInternalName() ) );
			return;
		}

		//if( !plugin.getLobby().isInLobby( player ) ) {
		if( !( SectionManager.getCurrentSection( player ) instanceof WarbasesLobbySection ) ) {
			player.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).arena_join_not_in_lobby );
			return;
		}

		party = PartyManager.getParty( player.getUniqueId() );

		if( party != null ) {
			if( party.isPartyOwner( player.getUniqueId() ) ) {
				if( this.getAllPlayers().size() + party.size() > this.max_players / ArenaTeam.values().length ) {
					player.sendMessage( MinigamesAPI.getAPI().partymessages.party_too_big_to_join );
					return;
				}

				for( UUID playerUniqueId : party.getPlayers() ) {
					Player targetPlayer = Bukkit.getPlayer( playerUniqueId );
					//if( !plugin.getLobby().isInLobby( targetPlayer ) ) {
					if( targetPlayer != null && !( SectionManager.getCurrentSection( targetPlayer ) instanceof WarbasesLobbySection ) ) {
						targetPlayer.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).arena_join_not_in_lobby );
						player.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).arena_join_not_in_lobby_party );
						return;
					}
				}

				int min = this.max_players, now;

				team = ArenaTeam.values()[ 0 ];
				for( ArenaTeam cycledTeam : ArenaTeam.values() ) {
					now = arenaManager.teamNumberOfPlayers( cycledTeam );
					if( now < min ) {
						min = now;
						team = cycledTeam;
					}
				}

				for( UUID playerUniqueId : party.getPlayers() )
					partyTeams.put( playerUniqueId, team );

				for( UUID playerUniqueId : party.getPlayers() ) {
					Player partyPlayer = Bukkit.getPlayer( playerUniqueId );
					if( partyPlayer != null && partyPlayer.isOnline() ) {
						this.joinPlayerLobby( partyPlayer.getUniqueId() );
					}
				}

				inParty = true;
			} else if( !partyTeams.containsKey( player.getUniqueId() ) ) {
				player.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).party_cannot_join_by_yourself );
				return;
			}
		}

		try {
			SectionManager.enter( player, WarbasesGameSection.name, getInternalName(), true );
		} catch( IllegalSectionLeavingException ex ) {
			player.sendMessage( ( (EMessagesConfig) getPluginInstance().getMessagesConfig() ).join_forbidden );
			return;
		}

		super.joinPlayerLobby( playerUuid );

		if( partyTeams.containsKey( player.getUniqueId() ) ) {
			team = partyTeams.get( player.getUniqueId() );
			inParty = true;
		}

		final ArenaPlayer ap = new ArenaPlayer( plugin, player, lives, team );

		ap.setInParty( inParty );

		Bukkit.getScheduler().runTaskLater( MinigamesAPI.getAPI(), new Runnable() {
			public void run() {
				if( getArenaState() != ArenaState.INGAME ) {
					uiManager.correctLobbyItems( player );
					if( !ap.isInParty() )
						plugin.getTeamsSelectorUI().giveItem( player );
				}
			}
		}, 17L );

		arenaManager.add( ap );

		uiManager.join( ap );
	}

	@Override
	public void leavePlayer( final String playerName, boolean fullLeave ) {
		ArenaPlayer ap = arenaManager.getPlayer( playerName );
		super.leavePlayer( playerName, fullLeave );
		this.uiManager.leave( ap );
		this.checkpointManager.stop( ap );
		this.arenaManager.remove( playerName );
		SectionManager.enter( ap.getPlayer(), WarbasesLobbySection.name );
	}

	@Override
	public void leavePlayer( String playername, boolean fullLeave, boolean endofGame ) {
		if( !endofGame && this.getAllPlayers().size() < 2 && this.getArenaState() != ArenaState.JOIN ) {
			for( ArenaPlayer player : arenaManager.getPlayers().values() ) {
				player.setLevel( 0 );
				player.setExperience( 0 );
			}
		}

		super.leavePlayer( playername, fullLeave, endofGame );
	}

	@Override
	public void startLobby( boolean countdown ) {
		super.startLobby( countdown );
		this.lobbyUpdater = Bukkit.getScheduler().runTaskTimer( MinigamesAPI.getAPI(), new Runnable() {
			@Override
			public void run() {
				uiManager.updateLobby();
			}
		}, 5L, 20L );
	}

	@Override
	public void start( boolean tp ) {
		if( this.lobbyUpdater != null ) {
			this.lobbyUpdater.cancel();
			this.lobbyUpdater = null;
		}

		setAlwaysPvP( true );

		new TeamBalancer( this );

		partyTeams.clear();

		/*boolean ready = this.wallManager.ready();

		for( Map.Entry<String, ArenaPlayer> entry : arenaManager.getPlayers().entrySet() ) {
			ArenaPlayer p = entry.getValue();
			if( !ready )
				this.uiManager.get( p ).arenaStartPending( p );
		}

		try {
			while( !this.wallManager.ready() ) {
				Thread.sleep( 1000 );
			}
		} catch( InterruptedException e ) {
			System.err.println( "[WarBases] WARNING! Exception while waiting for wall saving! WALL RESETTING MIGHT NOT WORK!" );
			System.err.println( e.getLocalizedMessage() );
		}*/

		this.uiManager.start();
	}

	public void continueStart() {
		for( Map.Entry<String, ArenaPlayer> entry : arenaManager.getPlayers().entrySet() ) {
			ArenaPlayer p = entry.getValue();
			if( p.getTeam() == null ) {
				p.setOutcome( plugin.getPluginInstance(), this, ArenaOutcome.LOSE );
				p.die();
				spectate( p.getName() );
			}
		}

		super.start( false );

		super.timer.onArenaStop();

		timer.init( getTimeMax() );
		overtimeManager.init( maxOvertime, maxOvertimeReduceStep );
	}

	@Override
	public void started() {
		if( !this.started ) {
			final EArena a = this;
			Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
				public void run() {
					EMessagesConfig messages = (EMessagesConfig) getPluginInstance().getMessagesConfig();
					String tip;
					checkpointManager.preStart();

					for( Map.Entry<String, ArenaPlayer> entry : arenaManager.getPlayers().entrySet() ) {
						ArenaPlayer player = entry.getValue();

						player.loadKit( getPluginInstance() );

						Util.teleportPlayerFixed( player.getPlayer(), arenaManager.getSpawn( a, player ) );
						tip = messages.getRandomTip();
						if( tip != null )
							player.sendMessage( tip );
						//}
						//}
						//}, 2L );

						//Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
						//public void run() {
						//checkpointManager.preStart();

						//for( Map.Entry<String, ArenaPlayer> entry : arenaManager.getPlayers().entrySet() ) {
						//ArenaPlayer player = entry.getValue();

						player.giveKit();
						player.getInventory().setHeldItemSlot( 0 );
						checkpointManager.start( player );
					}
				}
				//}, 20L );
			}, 2L );

			this.updater = Bukkit.getScheduler().runTaskTimer( plugin, new ArenaUpdater( this ), 5L, 5L );
			this.wallManager.start();

			this.started = true;
		}
	}

	private void loadConfig() {
		this.lives = (int) ArenaVariable.get( "lives" ).getValue( this.plugin, this );
		this.maxTime = (int) ArenaVariable.get( "time" ).getValue( this.plugin, this );
		this.maxOvertime = (float) plugin.getConfig().getDouble( "config.overtime.max" );
		this.maxOvertimeReduceStep = (float) plugin.getConfig().getDouble( "config.overtime.max_reduce_step" );
		this.respawnTime = plugin.getConfig().getInt( "config.respawn.time" );
	}

	public void reloadManagers() {
		this.wallManager.stop();
		this.timer.stop();
		this.overtimeManager.stopAll();
		this.checkpointManager.reload();
		this.wallManager.reload();
		this.uiManager.reload();

		loadConfig();
	}

	public void stop( boolean checkpointFinished, boolean skipOvertime, boolean skipUI ) {
		if( this.started ) {
			System.out.println( "[WarBases] Arena is stopping!" );
			this.timer.stop();

			if( !skipOvertime ) {
				if( overtimeManager.shouldIntervene( checkpointFinished ) )
					return;
			}

			new DecideWiningTeam( this.plugin, this.plugin.getPluginInstance(), this, this.arenaManager, this.checkpointManager, checkpointFinished && checkpointManager.isFinished() );

			System.out.println( "[WarBases] Arena is stopping for real! No overtime here, no FFA." );

			this.started = false;

			if( this.updater != null ) {
				this.updater.cancel();
				this.updater = null;
			}

			this.wallManager.stop();
			this.uiManager.stop( skipUI );
		}
	}

	@Override
	public synchronized void stopArena() {
		this.stop( false, true, true );
	}

	@Override
	public void stop() {
		stop( false, true, true );
	}

	public void continueStop() {
		this.uiManager.reset();

		this.wallManager.reset();

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			this.checkpointManager.stop( entry.getValue() );
		}

		super.stop();

		this.checkpointManager.reset();

		this.arenaManager.reset();
	}

	public ArenaManager getArenaManager() {
		return this.arenaManager;
	}

	public UIManager getUIManager() {
		return this.uiManager;
	}

	public WallManager getWallManager() {
		return this.wallManager;
	}

	public CheckpointManager getCheckpointManager() {
		return this.checkpointManager;
	}

	public int getTimeMax() {
		return this.maxTime;
	}

	public int getRespawnTimeMax() {
		return this.respawnTime;
	}

	public void respawn( Player killed ) {
		respawn( this.arenaManager.getPlayer( killed ) );
	}

	public void respawn( final ArenaPlayer killed ) {
		respawn( null, killed );
	}

	public void respawn( final ArenaPlayer killer, final ArenaPlayer killed ) {
		//this.arenaManager.playerRespawning( p );
		killed.die();

		if( !getArenaManager().teamStillHasPlayers( killed.getTeam() ) ) {
			Bukkit.getScheduler().runTaskLater( plugin, new Runnable() {
				public void run() {
					stop( false, true, false );
				}
			}, 5L );
		}

		if( killed.canRespawn() ) {
			if( getArenaState() == ArenaState.INGAME ) {
				killed.decreaseLive();
				effectiveRespawn( killer, killed, true );
				spectate( killed );
				RespawnTimer timer = new RespawnTimer( plugin, this, uiManager, killed );
				timer.init( respawnTime );
				timer.start();
				//arena.arenaManager.playerHasRespawned( p );
			} else {
				//arena.arenaManager.playerHasRespawned( p );
				leavePlayer( killed.getName(), false );
			}
		} else {
			if( getArenaState() == ArenaState.INGAME ) {
				effectiveRespawn( killer, killed, false );
				spectate( killed );
				//arena.arenaManager.playerHasRespawned( p );
			} else {
				//arena.arenaManager.playerHasRespawned( p );
				leavePlayer( killed.getName(), false );
			}
		}
	}

	public void respawnCore( ArenaPlayer killed ) {
		killed.setHealth( 20D );
		Util.teleportPlayerFixed( killed.getPlayer(), this.arenaManager.getSpawn( this, killed ) );
	}

	void finallyRespawn( ArenaPlayer player ) {
		player.revive();
		unspectate( player );
		respawnCore( player );
		player.giveKit();
		uiManager.respawn( player );
	}

	public void effectiveRespawn( ArenaPlayer killer, ArenaPlayer killed, boolean respawn ) {
		respawnCore( killed );
		onEliminated( killed.getName() );
		this.uiManager.broadcastDeath( killer, killed, respawn );
	}

	/*@Override
	public void spectateGame( String playername ) {
		final Player p = Bukkit.getPlayer( playername );
		this.getPluginInstance().getSpectatorManager().setSpectate( p, true );

		if( p != null ) {
			Util.clearInv( p );
			p.setAllowFlight( true );
			p.setFlying( true );
			this.plugin.getPluginInstance().getSpectatorManager().hideSpectator( p, this.getAllPlayers() );
			this.plugin.getPluginInstance().scoreboardManager.updateScoreboard( this.plugin, this );

			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					Util.teleportPlayerFixed( p, p.getLocation().clone().add( 0.0D, 10.0D, 0.0D ) );
				}
			}, 2L );

			this.spectateRaw( p );

			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					Util.clearInv( p );
					Util.giveSpectatorItems( plugin, p );
				}
			}, 3L );
		}
	}*/

	public void spectate( ArenaPlayer p ) {
		spectate( p.getName() );
	}

	@Override
	public void spectate( String playername ) {
		if( Validator.isPlayerValid( this.plugin, playername, this ) ) {
			Player p = Bukkit.getPlayer( playername );
			if( p == null ) {
				return;
			}

			/*this.getPluginInstance().global_lost.put( playername, this );
			if( !this.plugin.getConfig().getBoolean( "config.spectator.spectator_after_fall_or_death" ) ) {
				this.leavePlayer( playername, false, false );
				this.getPluginInstance().scoreboardManager.updateScoreboard( this.plugin, this );
				return;
			}*/

			this.spectateGame( playername );
		}
	}

	public void unspectate( ArenaPlayer p ) {
		unspectate( p.getPlayer() );
	}

	public void unspectate( Player p ) {
		this.getPluginInstance().getSpectatorManager().setSpectate( p, false );

		if( p != null ) {
			Util.clearInv( p );
			p.setAllowFlight( false );
			p.setFlying( false );
			p.setGameMode( GameMode.SURVIVAL );
			this.plugin.getPluginInstance().getSpectatorManager().showSpectator( p );
			this.plugin.getPluginInstance().scoreboardManager.updateScoreboard( this.plugin, this );
		}
	}

	public final ArenaTimer getTimer() {
		return this.timer;
	}

	public final OvertimeManager getOvertimeManager() {
		return this.overtimeManager;
	}

	public void wallFall() {
		this.timer.start();
	}

	void handleAchievements( final ArenaPlayer p ) {
		/*int conquered = 0;
		String conqueredPath = "warbases." + p.getUniqueId() + ".conquered";
		if( this.plugin.getConfig().isSet( conqueredPath ) ) {
			conquered = this.plugin.getConfig().getInt( conqueredPath );
		}
		this.plugin.getConfig().set( conqueredPath, conquered + 1 );
		this.plugin.saveConfig();
		if( conquered + 1 > 99 ) {
			this.plugin.getPluginInstance().getArenaAchievements().setAchievementDone( p.getName(), "capture_hundred_checkpoints_all_time", true );
		}*/
		Bukkit.getScheduler().runTaskAsynchronously( this.plugin, new Runnable() {
			@Override
			public void run() {
				int conquered = p.getNumberOfConqueredCheckpoints( plugin );
				conquered++;
				p.setNumberOfConqueredCheckpoints( plugin, conquered );
				if( conquered > 99 ) {
					Bukkit.getScheduler().runTask( plugin, new Runnable() {
						@Override
						public void run() {
							plugin.getPluginInstance().getArenaAchievements().setAchievementDone( p.getName(), "capture_hundred_checkpoints_all_time", false );
						}
					} );
				}
			}
		} );
	}
}