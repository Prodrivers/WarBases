package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.ArenaState;
import fr.horgeon.prodrivers.games.warbases.timer.BaseTimer;
import fr.horgeon.prodrivers.games.warbases.ui.game.UIManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RespawnTimer extends BaseTimer {
	private EArena arena;
	private UIManager uiManager;
	private ArenaPlayer player;

	RespawnTimer( JavaPlugin plugin, EArena arena, UIManager uiManager, ArenaPlayer player ) {
		super( plugin );
		this.arena = arena;
		this.uiManager = uiManager;
		this.player = player;
	}

	@Override
	protected void update() {
		this.timeLeft--;
		if( this.timeLeft <= 0 ) {
			stop();
			if( arena.getArenaState() == ArenaState.INGAME )
				arena.finallyRespawn( player );
		} else {
			uiManager.respawnProgress( player, timeLeft );
		}
	}
}
