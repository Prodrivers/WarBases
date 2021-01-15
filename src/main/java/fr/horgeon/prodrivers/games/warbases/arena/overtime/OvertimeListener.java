package fr.horgeon.prodrivers.games.warbases.arena.overtime;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointConqueredEvent;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointConqueringEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class OvertimeListener implements Listener {
	private PluginInstance pli;

	public OvertimeListener( Main plugin ) {
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		pli = plugin.getPluginInstance();
	}

	@EventHandler
	public void onCheckpointBeingConquered( CheckpointConqueringEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME && event.getContested() ) {
			System.out.println( "[WarBases] Conquering and contested event for team " + event.getTeam() + " in arena " + event.getArenaName() + " in overtime listener" );
			( (EArena) a_ ).getOvertimeManager().contested( event.getTeam() );
		}
	}

	/*@EventHandler( priority = EventPriority.MONITOR )
	public void onCheckpointConquered( CheckpointConqueredEvent event ) {
		final Arena a_ = pli.getArenaByName( event.getArenaName() );

		if( !event.isCancelled() && !event.isWasInOvertime() && event.getWasContested() && a_ != null && a_ instanceof EArena && a_.getArenaState() == ArenaState.INGAME ) {
			System.out.println( "[WarBases] Checkpoint conquest event intercepted! Overtime starting..." );
			event.setCancelled( true );
			( (EArena) a_ ).getCheckpointManager().enteringOvertime( event.getTeam() );
			( (EArena) a_ ).getOvertimeManager().start( event.getTeam(), true );
		}
	}*/
}
