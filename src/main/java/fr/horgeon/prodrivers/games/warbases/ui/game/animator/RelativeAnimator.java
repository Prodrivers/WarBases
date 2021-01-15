package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RelativeAnimator extends BaseAnimator {
	public RelativeAnimator( JavaPlugin plugin, AnimatorFinishCallback callback ) {
		super( plugin, callback );
	}

	@Override
	public void positions( Location startPoint, Location endPoint, Location handlePoint ) {
		super.positions(
			new Location( startPoint.getWorld(), 0, 0, 0 ),
			endPoint.subtract( startPoint ).clone(),
			handlePoint.subtract( startPoint ).clone()
		);
	}

	public void updatePlayer( ArenaPlayer p, Location loc ) {
		Location temp;
		temp = this.originalLocations.get( p ).clone().add( loc );
		temp.setPitch( loc.getPitch() );
		p.teleport( temp );
	}

	public void update( Set<Map.Entry<String, ArenaPlayer>> players, float t ) {
		Location position = computeBezierPosition( t );

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			updatePlayer( entry.getValue(), position );
		}
	}
}