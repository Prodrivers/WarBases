package fr.horgeon.prodrivers.games.warbases.ui.listeners;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTimerTickEvent;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointConqueringEvent;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointProgressEvent;
import fr.horgeon.prodrivers.games.warbases.arena.overtime.OvertimeProgressEvent;
import fr.horgeon.prodrivers.games.warbases.arena.overtime.OvertimeStartedEvent;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallFallEvent;
import fr.horgeon.prodrivers.games.warbases.arena.wall.WallTimerTickEvent;
import fr.horgeon.prodrivers.games.warbases.ui.events.UIFullyStartedEvent;
import fr.horgeon.prodrivers.games.warbases.ui.events.UIFullyStoppedEvent;
import fr.horgeon.prodrivers.games.warbases.ui.game.CommunicationMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.InventoryHolder;

public class GameUIListener implements Listener {
	private PluginInstance pli;

	public GameUIListener( Main plugin ) {
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		this.pli = plugin.getPluginInstance();
	}

	@EventHandler( priority = EventPriority.NORMAL )
	public void onPlayerItemHeld( PlayerItemHeldEvent e ) {
		final Arena a_ = pli.getArenaByGlobalPlayer( e.getPlayer().getName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME && e.getNewSlot() > 1 && e.getNewSlot() < 7 ) {
			ArenaPlayer ap = ( (EArena) a_ ).getArenaManager().getPlayer( e.getPlayer() );
			if( ap.isComMenuOpened() ) {
				ap.toggleComMenu();
				ap.getInventory().setHeldItemSlot( e.getPreviousSlot() );
				( (EArena) a_ ).getUIManager().communicationMessage( ap, CommunicationMessage.values()[ e.getNewSlot() - 2 ] );
				ap.getInventory().remove( ap.getInventory().getItem( 2 ) );
				ap.getInventory().remove( ap.getInventory().getItem( 6 ) );
			} else {
				if( e.getNewSlot() - e.getPreviousSlot() > 0 ) {
					e.getPlayer().getInventory().setHeldItemSlot( 7 );
				} else {
					e.getPlayer().getInventory().setHeldItemSlot( 1 );
				}
			}
		}
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onInventoryMoveItemEvent( InventoryMoveItemEvent e ) {
		final InventoryHolder holder = e.getSource().getHolder();

		if( holder instanceof Player ) {
			final Arena a_ = pli.getArenaByGlobalPlayer( ( (Player) holder).getName() );

			if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
				e.setCancelled( true );
			}
		}
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onInventoryClickEvent( InventoryClickEvent e ) {
		final Arena a_ = pli.getArenaByGlobalPlayer( e.getWhoClicked().getName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
			e.setCancelled( true );
		}
	}

	@EventHandler
	public void onUIFullyStarted( UIFullyStartedEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena )
			( (EArena) a_ ).getUIManager().continueStart();
	}

	@EventHandler
	public void onUIFullyStopped( UIFullyStoppedEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena )
			( (EArena) a_ ).getUIManager().continueStop();
	}

	@EventHandler
	public void onCheckpointProgress( CheckpointProgressEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().checkpointProgress( event.getTeam(), event.getProgress(), event.getProgressLeft() );
	}

	@EventHandler
	public void onCheckpointBeingConquered( CheckpointConqueringEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().checkpointConquering( event.getTeam(), event.getContested() );
	}

	@EventHandler
	public void onWallFall( WallFallEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().wallFall();
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onOvertimeStarted( OvertimeStartedEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().overtimeStarted( event.getTeam() );
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onOvertimeProgress( OvertimeProgressEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().overtimeProgress( event.getTeam(), event.getLeft(), event.getMax() );
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onArenaTimerTick( ArenaTimerTickEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().timerProgress( event.getTimeLeft(), event.getFormattedTimeLeft() );
	}

	@EventHandler( priority = EventPriority.MONITOR )
	public void onWallTimerTick( WallTimerTickEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME )
			( (EArena) a_ ).getUIManager().wallTimerProgress( event.getTimeLeft(), event.getFormattedTimeLeft() );
	}
}
