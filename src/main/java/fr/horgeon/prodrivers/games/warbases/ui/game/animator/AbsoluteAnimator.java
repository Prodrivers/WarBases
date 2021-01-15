package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import org.bukkit.Location;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.Map;

public class AbsoluteAnimator extends BaseAnimator {
	public AbsoluteAnimator( JavaPlugin plugin, AnimatorFinishCallback callback ) {
		super( plugin, callback );
	}

	public void updatePlayer( ArenaPlayer p, Location loc ) {
		p.teleport( loc );
	}

	public void update( Set<Map.Entry<String, ArenaPlayer>> players, float t ) {
		Location position = computeBezierPosition( t );
		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			updatePlayer( entry.getValue(), position );
		}
	}
}