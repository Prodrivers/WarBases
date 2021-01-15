package fr.horgeon.prodrivers.games.warbases.ui.game;

import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.connorlinfoot.bountifulapi.BountifulAPI;
import fr.horgeon.bukkit.packetsutilities.SoundCategory;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallManager;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.ui.game.enhanced.ItemPreparator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnhancedUI implements IGameUI {
	private JavaPlugin plugin;
	private EMessagesConfig messages;
	private Material baseItem = Material.DIAMOND_HOE;

	public HashMap<Integer, ItemStack> redHUDItems = new HashMap<>();
	public HashMap<Integer, ItemStack> blueHUDItems = new HashMap<>();
	public HashMap<Integer, ItemStack> wallHUDItems = new HashMap<>();
	public HashMap<Integer, ItemStack> warbasesOpenItems = new HashMap<>();
	public HashMap<Integer, ItemStack> victoryItems = new HashMap<>();
	public HashMap<Integer, ItemStack> defeatItems = new HashMap<>();
	private ItemStack redHUDItem;
	private ItemStack blueHUDItem;
	private ItemStack wallHUDItem;
	private ItemStack warbasesOpenItem;
	private ItemStack victoryItem;
	private ItemStack defeatItem;

	private boolean playSound = false;
	private String sound;
	private Location soundLocation;
	private boolean playMusic = false;
	private String music;
	private boolean prepareToAttackSound = false;
	private float tickSoundPitch;

	private Random random = new Random();

	public EnhancedUI( Main plugin, PluginInstance pli ) {
		this.plugin = plugin;
		this.messages = (EMessagesConfig) pli.getMessagesConfig();
		reload();
	}

	public void reset() {
		this.playSound = false;
		this.prepareToAttackSound = false;
	}

	public void reload() {
		this.playSound = false;
		this.prepareToAttackSound = false;
		baseItem = Material.getMaterial( plugin.getConfig().getString( "config.gui.enhanced_ui.base_item" ) );
		if( baseItem == null || baseItem == Material.AIR )
			baseItem = Material.DIAMOND_HOE;
		generateItems();
	}

	private void generateItems() {
		new ItemPreparator( this, baseItem );
	}

	public void onKilledByPlayer( ArenaPlayer p, ArenaPlayer killer, boolean respawn ) {
		p.playSound( "music.death", SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
	}

	public void onKill( ArenaPlayer killer, ArenaPlayer killed ) {
		final ArenaPlayer killer_ = killer;
		killer.playSound( "effect.kill", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
		if( random.nextInt( Constants.UI_EFFECT_TAUNT_PROBABILITY ) == 0 )
			killer.playSound( "hero.taunt", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
		if( killer.getKills() % Constants.UI_EFFECT_KILLSTREAK_STEP == 0 ) {
			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					killer_.playSound( "effect.killstreak", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
					BountifulAPI.sendTitle( killer_.getPlayer(), 2, 5, 25, "", messages.kill_streak.replaceAll( "<number>", Integer.toString( killer_.getKills() ) ) );
				}
			}, 20L );
		}
	}

	public void preUpdateLobby( EArena arena ) {
		/*this.playSound = false;

		int countdown = arena.getCurrentLobbyCountdownTime();
		if( countdown == 60 || countdown == 30 || countdown == 10 ) {
			this.playSound = true;
			this.sound = "announcer.timeleft.remaining." + countdown;
		}*/
	}

	public void updateLobby( EArena arena, ArenaPlayer p ) {
		/*if( playSound )
			p.playSound( p.getLocation(), sound, Constants.UI_ENHANCED_VOLUME_UI, 1.0F );*/
	}

	/*@Override
	public void preStart( EArena arena ) {
		reset();
		super.preStart( arena );
		this.warbasesOpenItem = new ItemStack( this.baseItem, 1, (short) 217 );
		prepareItem( this.warbasesOpenItem );
	}

	public void start( EArena arena, ArenaPlayer p ) {
		p.getInventory().clear();
		p.getInventory().setItem( 4, this.warbasesOpenItem );
		p.getInventory().setHeldItemSlot( 1 );
		p.updateInventory();
		super.start( arena, p );
	}

	public void postStart() {
		System.out.println( "EnhancedUI scheduled to start" );
		( new Timer() ).schedule( new TimerTask() {
			public void run() {
				isStarted.set( true );
				System.out.println( "EnhancedUI start completed" );
			}
		}, 4 * 1000 );
		super.postStart();
	}*/

	public void preStart( final EArena arena, boolean intro ) {
		this.redHUDItem = this.redHUDItems.get( 0 );
		this.wallHUDItem = this.wallHUDItems.get( this.wallHUDItems.size() - 2 );
		this.blueHUDItem = this.blueHUDItems.get( 0 );
	}

	public void start( final EArena arena, final ArenaPlayer p, boolean intro ) {
		if( intro ) {
			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					p.playSound( "headsoldier.intro", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
				}
			}, 5L );
		}
	}

	public void postStart( final EArena arena, boolean intro ) {}

	public void update( EArena arena, ArenaPlayer p ) {
		giveHUDItems( p );
	}

	/*@Override
	public void preStop( EArena arena, CheckpointManager checkpointManager, WallManager wallManager ) {
		this.victoryItem = new ItemStack( this.baseItem, 1, (short) 218 );
		this.defeatItem = new ItemStack( this.baseItem, 1, (short) 219 );
		prepareItem( this.victoryItem );
		prepareItem( this.defeatItem );
		super.preStop( arena, checkpointManager, wallManager );
	}

	public void stopAll( EArena arena, ArenaPlayer p ) {
		super.stopAll( arena, p );
		p.getInventory().clear();
		if( this.pli.containsGlobalLost( p.getName() ) ) {
			p.getInventory().setItem( 4, this.defeatItem );
		} else {
			p.getInventory().setItem( 4, this.victoryItem );
		}
		p.getInventory().setHeldItemSlot( 4 );
		p.updateInventory();
	}

	public void postStop() {
		System.out.println( "EnhancedUI scheduled to stopAll" );
		( new Timer() ).schedule( new TimerTask() {
			public void run() {
				isStopped.set( true );
				System.out.println( "EnhancedUI stopAll completed" );
			}
		}, 4 * 1000 );
		super.postStop();
	}*/

	public void stop( final EArena arena, final ArenaPlayer p, boolean skip, boolean outro ) {
		p.stopSound( "music.start", SoundCategory.RECORDS );
		p.stopSound( "music.overtime", SoundCategory.RECORDS );
		p.stopSound( "music.death", SoundCategory.RECORDS );
		p.stopSound( "music.timeslow", SoundCategory.RECORDS );

		if( !skip ) {
			p.playSound( "effect.end", SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );

			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					switch( p.outcome() ) {
						case WIN:
							p.playSound( "music.victory", SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
							break;
						case DRAW:
						case LOSE:
							p.playSound( "music.defeat", SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
							break;
					}
				}
			}, 20L );

			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					switch( p.outcome() ) {
						case WIN:
							p.playSound( "headsoldier.victory", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
							break;
						case DRAW:
							p.playSound( "headsoldier.draw", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
							break;
						case LOSE:
							p.playSound( "headsoldier.defeat", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
							break;
					}
				}
			}, 50L );

			if( outro ) {
				Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
					@Override
					public void run() {
						switch( p.outcome() ) {
							case WIN:
								p.playSound( "headsoldier.victory_text", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
								break;
							case DRAW:
								p.playSound( "headsoldier.draw_text", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
								break;
							case LOSE:
								p.playSound( "headsoldier.defeat_text", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
								break;
						}
					}
				}, 75L );
			}
		}
	}

	public void postStop( final EArena arena, boolean skip, boolean outro ) {}

	public void clear( ArenaPlayer p ) {
		for( int i = 2; i < 7; i++ )
			p.getInventory().clear( i );

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard sc = manager.getNewScoreboard();
		sc.clearSlot( DisplaySlot.SIDEBAR );
		p.setScoreboard( sc );
	}

	public void preAnnounceDeath( ArenaPlayer killer, ArenaPlayer killed ) {}

	public void preCheckpointProgress( ArenaTeam team, int progress, int progressLeft ) {
		if( team == ArenaTeam.Red )
			this.redHUDItem = this.redHUDItems.get( progress );
		else if( team == ArenaTeam.Blue )
			this.blueHUDItem = this.blueHUDItems.get( progress );

		if( progressLeft <= 20 ) {
			this.tickSoundPitch = 1.0f + ( 20 - progressLeft ) * 0.05f;
		} else {
			this.tickSoundPitch = 0;
		}
	}

	public void checkpointProgress( ArenaPlayer p, ArenaTeam team ) {
		if( p.getTeam() != team ) {
			if( this.tickSoundPitch == 0 )
				p.playSound( "effect.capture.ticklow", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
			else
				p.playSound( "effect.capture.tick", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI, tickSoundPitch );
		}
	}

	public void checkpointConquering( ArenaPlayer p, ArenaTeam team, boolean contested ) {
		if( p.getTeam() == team ) {
			if( !contested ) {
				p.playSound( "effect.capture.captured", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
			} else {
				p.playSound( "effect.capture.contest", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
			}
		} else {
			if( p.isInCheckpoint( team ) ) {
				if( contested ) {
					p.playSound(  "effect.capture.contest", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
				}
			}
		}
	}

	public void preWallFall() {
		Bukkit.getScheduler().runTaskLater( MinigamesAPI.getAPI(), new Runnable() {
			public void run() {
				wallHUDItem = wallHUDItems.get( Constants.UI_ENHANCED_ITEM_WALL_FALLED );
			}
		}, 100L );
		this.wallHUDItem = this.wallHUDItems.get( Constants.UI_ENHANCED_ITEM_WALL_FALLING );
	}

	public void wallFall( ArenaPlayer p ) {
		p.playSound( "announcer.attack.start", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
	}

	public void preOvertimeStarted( ArenaTeam team ) {
		this.wallHUDItem = this.wallHUDItems.get( Constants.UI_ENHANCED_ITEM_OVERTIME );
	}

	public void overtimeStarted( ArenaPlayer p, ArenaTeam team ) {
		p.playSound( "announcer.overtime", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
		p.playSound( "music.overtime", SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
	}

	public void lowHealth( ArenaPlayer p ) {
		p.playSound( "hero.healthslow", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
	}

	public void respawn( ArenaPlayer p ) {
		final ArenaPlayer fp = p;
		if( random.nextInt( Constants.UI_EFFECT_RESPAWN_PROBABILITY ) == 0 ) {
			Bukkit.getScheduler().runTaskLater( this.plugin, new Runnable() {
				@Override
				public void run() {
					fp.playSound( "hero.respawn", SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
				}
			}, 10L );
		}
	}

	public void preCommunicationMessage( ArenaPlayer sender, CommunicationMessage type ) {
		this.sound = "hero." + type.toString().toLowerCase();
	}

	public void communicationMessage( ArenaPlayer p, ArenaTeam team, Location senderLocation ) {
		if( p.getTeam() == team )
			p.playSound( senderLocation, sound, SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_PLAYER2PLAYER );
	}

	private ArenaPlayer pickRandomPlayer( EArena arena ) {
		Object[] values = arena.getArenaManager().getPlayers().values().toArray();
		return (ArenaPlayer) values[ random.nextInt( values.length ) ];
	}

	public void preTimerProgress( EArena arena, int timeLeft, String formattedTimeLeft ) {
		this.playSound = false;
		this.playMusic = false;

		if( timeLeft == 60 || timeLeft == 30 ) {
			this.playSound = true;
			this.sound = "announcer.timeleft.remaining." + timeLeft;
		}
		if( timeLeft == 28 ) {
			this.playMusic = true;
			this.music = "music.timeslow";
		}
		if( timeLeft == 26 ) {
			this.playSound = true;
			this.sound = "hero.timeslow";
			this.soundLocation = pickRandomPlayer( arena ).getLocation();
		}
	}

	public void timerProgress( ArenaPlayer p ) {
		if( playSound )
			p.playSound( soundLocation, sound, SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_PLAYER2PLAYER );
		if( playMusic )
			p.playSound( music, SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
	}

	public void preWallTimerProgress( EArena arena, WallManager wallManager, int timeLeft, String formattedTimeLeft ) {
		this.playSound = false;
		this.playMusic = false;

		this.wallHUDItem = this.wallHUDItems.get( ( timeLeft  * 10 ) / wallManager.getTimeMax() );
		if( timeLeft <= 5 ) {
			this.playSound = true;
			this.sound = "announcer.timeleft." + timeLeft;
			/*} else if( timeLeft == 10 ) {
				this.playSound = true;
				this.sound = "announcer.timeleft.remaining.attack.10";
			} else if( timeLeft == 20 ) {
				this.playSound = true;
				this.sound = "announcer.timeleft.remaining.attack.20";*/
		} else if( timeLeft == 30 ) {
			this.playSound = true;
			this.sound = "announcer.timeleft.remaining.attack.30";
			/*} else if( timeLeft == 37 ) {
				this.playSound = true;
				this.sound = "music.start";*/
		} else if( timeLeft == 14 ) {
			this.playMusic = true;
			this.music = "music.start";
		} else if( !prepareToAttackSound ) {
			this.prepareToAttackSound = true;
			this.playSound = true;
			this.sound = "announcer.attack.prepare";
		}
	}

	public void wallTimerProgress( ArenaPlayer p ) {
		if( playSound )
			p.playSound( sound, SoundCategory.VOICE, Constants.UI_ENHANCED_VOLUME_UI );
		if( playMusic )
			p.playSound( music, SoundCategory.RECORDS, Constants.UI_ENHANCED_VOLUME_MUSIC );
	}

	private void giveHUDItems( ArenaPlayer p ) {
		if( p.isComMenuOpened() ) {
			for( int i = 0; i < 5; i++ )
				p.getInventory().setItem( 2 + i, this.wallHUDItems.get( Constants.UI_ENHANCED_ITEM_MESSAGES_BASE + i ) );
		} else {
			p.getInventory().setItem( 3, this.redHUDItem );
			p.getInventory().setItem( 4, this.wallHUDItem );
			p.getInventory().setItem( 5, this.blueHUDItem );
		}
	}

	public void arenaStartPending( ArenaPlayer p ) {}
	public void arenaStopPending( ArenaPlayer p ) {}

	public void arenaPlayersApplyingResourcepack( ArenaPlayer p ) {}

	public void joinTeam( ArenaPlayer p, ArenaTeam team ) {}

	public void preUpdate( EArena arena, CheckpointManager checkpointManager, WallManager wallManager, boolean startOfSecond ) {}

	public void preStop( final EArena arena, final CheckpointManager checkpointManager, final WallManager wallManager, boolean skip, boolean outro ) {}

	public void announceDeath( ArenaPlayer killer, ArenaPlayer killed, ArenaPlayer target ) {}

	public void preOvertimeProgress( ArenaTeam team, float left, float max ) {}

	public void respawnProgress( EArena arena, ArenaPlayer p, int timeLeft ) {}
}
