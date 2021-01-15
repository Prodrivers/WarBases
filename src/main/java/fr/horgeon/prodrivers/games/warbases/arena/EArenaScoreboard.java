package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.util.ArenaScoreboard;
import fr.horgeon.prodrivers.games.warbases.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EArenaScoreboard extends ArenaScoreboard {
	private Main plugin = null;

	public EArenaScoreboard( Main plugin ) {
		this.plugin = plugin;
	}

	@Override
	public void updateScoreboard( JavaPlugin plugin, Arena arena) {}

	@Override
	public void removeScoreboard( String arenaName, Player p ) {
		Arena arena = plugin.getPluginInstance().getArenaByGlobalPlayer( p.getName() );
		if( arena != null && arena instanceof EArena ) {
			ArenaPlayer ap = ( (EArena) arena ).getArenaManager().getPlayer( p );
			if( ap != null )
				( (EArena) arena ).getUIManager().get( ap ).clear( ap );
		}
	}
}
