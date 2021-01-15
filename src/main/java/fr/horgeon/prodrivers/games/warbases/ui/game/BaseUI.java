package fr.horgeon.prodrivers.games.warbases.ui.game;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.connorlinfoot.bountifulapi.BountifulAPI;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.ui.events.UIFullyStartedEvent;
import fr.horgeon.prodrivers.games.warbases.ui.events.UIFullyStoppedEvent;
import fr.horgeon.prodrivers.games.warbases.ui.game.animator.Animator;
import fr.horgeon.prodrivers.games.warbases.ui.game.animator.AnimatorFinishCallback;
import fr.horgeon.prodrivers.games.warbases.ui.game.animator.ArenaStartAnimator;
import fr.horgeon.prodrivers.games.warbases.ui.game.animator.ArenaStopAnimator;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.lang.reflect.Field;
import java.util.HashMap;

public class BaseUI implements IGameUI {
	protected JavaPlugin plugin;
	protected PluginInstance pli;
	protected EMessagesConfig messages;

	protected BossBar timeBar;
	protected HashMap<ArenaTeam, BossBar> overtimeBars = new HashMap<>();

	protected boolean playTitle = false;
	protected String title;

	private String message;

	public BaseUI( JavaPlugin plugin, PluginInstance pli ) {
		this.plugin = plugin;
		this.pli = pli;
		this.messages = (EMessagesConfig) pli.getMessagesConfig();
	}

	public void arenaStartPending( ArenaPlayer p ) {
		p.sendMessage( messages.arena_starting );
	}

	public void arenaStopPending( ArenaPlayer p ) {
		p.sendMessage( messages.arena_stopping );
	}

	public void arenaPlayersApplyingResourcepack( ArenaPlayer p ) {
		p.sendMessage( messages.players_applying_resourcepack );
	}

	public void onKilledByPlayer( ArenaPlayer p, ArenaPlayer killer, boolean respawn ) {
		BountifulAPI.sendTitle( p.getPlayer(), 5, 30, 10, messages.you_have_been_killed_by.replaceAll( "<killer>", killer.getName() ), "" );

		if( p.canRespawn() ) {
			RespawnUI task = new RespawnUI( 5 * 20, p );
			task.setTask( Bukkit.getScheduler().runTaskTimer( this.plugin, task, 0L, 1L ) );
		}
	}

	public void onKill( ArenaPlayer killer, ArenaPlayer killed ) {
		BountifulAPI.sendActionBar( killer.getPlayer(), messages.player_killed.replaceAll( "<killed>", killed.getName() ), 25 );
	}

	public void joinTeam( ArenaPlayer p, ArenaTeam team ) {
		p.sendMessage( messages.team_joined.replaceAll( "<teamColor>", team.toChatColor().toString() ).replaceAll( "<team>", team.toName( (EMessagesConfig) pli.getMessagesConfig() ) ) );
	}

	public void reload() {}

	public void reset() {}

	/*public void preStart( EArena arena ) {
		this.timeLeft = arena.getTimeMax();
		computeCurrentTimeBar( arena );
	}

	public void start( EArena arena, ArenaPlayer p ) {
		this.timeBar.addPlayer( p.getPlayer() );
	}

	public void postStart() {}*/

	public void preStart( final EArena arena, boolean intro ) {
		reset();

		if( intro ) {
			Animator animator = new ArenaStartAnimator( this.plugin, new AnimatorFinishCallback() {
				public void afterAnimationFinish( Animator animator ) {
					Bukkit.getServer().getPluginManager().callEvent( new UIFullyStartedEvent( arena.getInternalName(), getClass().getCanonicalName() ) );
				}
			}, arena.getBoundaries() ); //, this.enhancedUI.warbasesOpenItems
			animator.animate( arena.getArenaManager().getPlayers().entrySet(), 1.0f / ( Constants.UI_START_DURATION * 20 ) );
		}
		//computeCurrentTimeBar( arena );
	}

	public void start( final EArena arena, final ArenaPlayer p, boolean intro ) {
		//this.timeBar.addPlayer( p.getPlayer() );
		if( intro )
			BountifulAPI.sendTitle( p.getPlayer(), 20, 60,10, this.messages.welcome, "" );
		p.getInventory().clear();
		p.getInventory().setHeldItemSlot( 0 );
		p.updateInventory();
	}

	public void postStart( final EArena arena, boolean intro ) {
		if( !intro ) {
			Bukkit.getServer().getPluginManager().callEvent( new UIFullyStartedEvent( arena.getInternalName(), getClass().getCanonicalName() ) );
		}
	}

	public void preUpdateLobby( EArena arena ) {}
	public void updateLobby( EArena arena, ArenaPlayer p ) {}

	public void preUpdate( EArena arena, CheckpointManager checkpointManager, WallManager wallManager, boolean startOfSecond ) {}

	public void update( EArena arena, ArenaPlayer p ) {
		experienceBar( p );
	}

	private void experienceBar( ArenaPlayer p ) {
		int lives = p.getLives();
		if( lives > -1 )
			p.setLevel( lives );
		else
			p.setLevel( 100 );
	}

	/*public void preStop( EArena arena, CheckpointManager checkpointManager, WallManager wallManager ) {}

	public void stopAll( EArena arena, ArenaPlayer p ) {
		BossBarAPI.removeAllBars( p.getPlayer() );
	}

	public void postStop() {}*/

	public void preStop( final EArena arena, final CheckpointManager checkpointManager, final WallManager wallManager, boolean skip, boolean outro ) {
		/*Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
			@Override
			public void run() {
				ItemAnimator animator = new StopItemAnimator( plugin, plugin.getPluginInstance(), enhancedUI.victoryItems, enhancedUI.defeatItems );
				animator.animate( arena.getArenaManager().getPlayers().entrySet() );
			}
		}, 50L );*/

		if( !skip && outro ) {
			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					Animator animator = new ArenaStopAnimator( plugin, new AnimatorFinishCallback() {
						public void afterAnimationFinish( Animator animator ) {
							Bukkit.getServer().getPluginManager().callEvent( new UIFullyStoppedEvent( arena.getInternalName(), getClass().getCanonicalName() ) );
						}
					}, arena.getBoundaries());
					animator.animate( arena.getArenaManager().getPlayers().entrySet(), 1.0f / ( Constants.UI_STOP_DURATION * 20 - 80 ) );
				}
			}, 80L ); //133L
		}
	}

	public void stop( final EArena arena, final ArenaPlayer p, boolean skip, boolean outro ) {
		BossBarAPI.removeAllBars( p.getPlayer() );
		p.getInventory().clear();
		p.getInventory().setHeldItemSlot( 0 );
		p.updateInventory();

		if( !skip ) {
			p.addPotionEffect( new PotionEffect( PotionEffectType.SLOW, 100, 2, true, false ), true );

			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					switch( p.outcome() ) {
						case WIN:
							BountifulAPI.sendTitle( p.getPlayer(), 2, 40, 30, messages.victory, "" );
							break;
						case DRAW:
							BountifulAPI.sendTitle( p.getPlayer(), 2, 40, 30, messages.draw, "" );
							break;
						case LOSE:
							BountifulAPI.sendTitle( p.getPlayer(), 2, 40, 30, messages.defeat, "" );
							break;
					}
				}
			}, 50L );
		}
	}

	public void postStop( final EArena arena, boolean skip, boolean outro ) {
		if( skip ) {
			Bukkit.getServer().getPluginManager().callEvent( new UIFullyStoppedEvent( arena.getInternalName(), getClass().getCanonicalName() ) );
		}
	}

	public void clear( ArenaPlayer p ) {
		BossBarAPI.removeAllBars( p.getPlayer() );
	}

	public void preAnnounceDeath( ArenaPlayer killer, ArenaPlayer killed ) {
		if( killer != null )
			this.message = this.messages.broadcast_killed.replaceAll( "<killer>", killer.getName() ).replaceAll( "<killed>", killed.getName() ).replaceAll( "<killedTeamColor>", killed.getTeam().toChatColor().toString() ).replaceAll( "<killerTeamColor>", killer.getTeam().toChatColor().toString() );
		else
			this.message = this.messages.broadcast_suicide.replaceAll( "<killed>", killed.getName() ).replaceAll( "<killedTeamColor>", killed.getTeam().toChatColor().toString() );
	}

	public void announceDeath( ArenaPlayer killer, ArenaPlayer killed, ArenaPlayer target ) {
		target.sendMessage( this.message );
	}

	public void checkpointConquering( ArenaPlayer p, ArenaTeam team, boolean contested ) {
		if( p.getTeam() == team ) {
			if( !contested ) {
				BountifulAPI.sendActionBar( p.getPlayer(), this.messages.checkpoint_being_captured, 4 );
			} else {
				BountifulAPI.sendActionBar( p.getPlayer(), this.messages.checkpoint_contesting, 4 );
			}
		} else {
			if( p.isInCheckpoint( team ) ) {
				if( contested ) {
					BountifulAPI.sendActionBar( p.getPlayer(), this.messages.checkpoint_being_contested, 4 );
				} else {
					BountifulAPI.sendActionBar( p.getPlayer(), this.messages.checkpoint_capturing, 4 );
				}
			}
		}
	}

	public void preWallFall() {}

	public void wallFall( ArenaPlayer p ) {
		BountifulAPI.sendTitle( p.getPlayer(), 2, 5, 25, "", messages.attack_commencing );
	}

	public void preOvertimeStarted( ArenaTeam team ) {
		this.overtimeBars.put(
			team,
			BossBarAPI.addBar( new TextComponent( this.messages.overtime ), BossBarAPI.Color.YELLOW, BossBarAPI.Style.PROGRESS, 1 )
		);
	}

	public void overtimeStarted( ArenaPlayer p, ArenaTeam team ) {
		BossBarAPI.removeAllBars( p.getPlayer() );
		this.overtimeBars.get( team ).addPlayer( p.getPlayer() );
	}

	public void preOvertimeProgress( ArenaTeam team, float left, float max ) {
		this.overtimeBars.get( team ).setProgress( left / max );
	}

	public void preCommunicationMessage( ArenaPlayer sender, CommunicationMessage type ) {
		try {
			Field f = this.messages.getClass().getDeclaredField( "in_game_messages_" + type.toString().toLowerCase() );
			this.message = ( (String) f.get( this.messages ) ).replaceAll( "<sender>", sender.getName() );
		} catch( Exception e ) {
			System.err.println( "Exception while trying to play communication message." );
			System.err.println( e.getLocalizedMessage() );
		}
	}

	public void communicationMessage( ArenaPlayer p, ArenaTeam team, Location senderLocation ) {
		if( p.getTeam() == team )
			p.sendMessage( this.message );
	}

	public void preTimerProgress( EArena arena, int timeLeft, String formattedTimeLeft ) {
		this.timeBar = BossBarAPI.addBar( new TextComponent( formattedTimeLeft ), BossBarAPI.Color.WHITE, BossBarAPI.Style.PROGRESS, ( (float) timeLeft ) / arena.getTimeMax() );
	}

	public void timerProgress( ArenaPlayer p ) {
		if( this.timeBar != null ) {
			BossBarAPI.removeAllBars( p.getPlayer() );
			this.timeBar.addPlayer( p.getPlayer() );
		}
	}

	public void preWallTimerProgress( EArena arena, WallManager wallManager, int timeLeft, String formattedTimeLeft ) {
		this.playTitle = false;
		this.timeBar = BossBarAPI.addBar( new TextComponent( formattedTimeLeft ), BossBarAPI.Color.WHITE, BossBarAPI.Style.PROGRESS, ( (float) timeLeft ) / wallManager.getTimeMax() );

		if( timeLeft <= 5 ) {
			this.playTitle = true;
			this.title = this.messages.time_left_title_prefix + timeLeft;
		}
	}

	public void wallTimerProgress( ArenaPlayer p ) {
		if( this.timeBar != null ) {
			BossBarAPI.removeAllBars( p.getPlayer() );
			this.timeBar.addPlayer( p.getPlayer() );
		}

		if( this.playTitle )
			BountifulAPI.sendTitle( p.getPlayer(), 5, 15,0, this.title, "" );
	}

	public void respawn( ArenaPlayer p ) {
		p.setExperience( 0 );
	}

	public void respawnProgress( EArena arena, ArenaPlayer p, int timeLeft ) {
		p.setExperience( (float) timeLeft / arena.getRespawnTimeMax() );
	}

	public void preCheckpointProgress( ArenaTeam team, int progress, int progressLeft ) {}
	public void checkpointProgress( ArenaPlayer p, ArenaTeam team ) {}

	public void lowHealth( ArenaPlayer p ) {}
}
