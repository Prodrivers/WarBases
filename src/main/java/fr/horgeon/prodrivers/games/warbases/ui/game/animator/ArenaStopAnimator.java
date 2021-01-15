package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import com.comze_instancelabs.minigamesapi.util.Cuboid;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ArenaStopAnimator extends RelativeAnimator {
	public ArenaStopAnimator( JavaPlugin plugin, AnimatorFinishCallback callback, Cuboid boundaries ) {
		super( plugin, callback );
		positions( boundaries );
	}

	public void positions( Cuboid boundaries ) {
		double startX, endX, handleX, startZ, endZ, handleZ;

		Location higherPoint = boundaries.getHighLoc();
		Location lowerPoint = boundaries.getLowLoc();

		double diffY = higherPoint.getY() - lowerPoint.getY(),
			startY = 0,
			endY = diffY * 2,
			handleY = diffY;

		startX = 0;
		endX = 0;
		handleX = 0;

		startZ = 0;
		endZ = 0;
		handleZ = 0;


		this.startPoint = new Location( higherPoint.getWorld(), startX, startY, startZ, 0, 0 );
		this.endPoint = new Location( higherPoint.getWorld(), endX, endY, endZ, 0, 90.0f );
		this.handlePoint = new Location( higherPoint.getWorld(), handleX, handleY, handleZ );
	}

	@Override
	public void finish( Set<Map.Entry<String, ArenaPlayer>> players ) {
		super.finish( players );

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().addPotionEffect( new PotionEffect( PotionEffectType.BLINDNESS, 60, 2, true, false ), true );
		}
	}
}
