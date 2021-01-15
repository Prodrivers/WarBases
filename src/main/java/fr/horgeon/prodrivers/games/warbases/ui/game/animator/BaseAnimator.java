package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseAnimator implements Animator {
	private JavaPlugin plugin;
	protected Location startPoint, endPoint, handlePoint;
	private BukkitTask task = null;
	protected HashMap<ArenaPlayer, Location> originalLocations = new HashMap<>();
	protected AnimatorFinishCallback callback;

	public BaseAnimator( JavaPlugin plugin, AnimatorFinishCallback callback ) {
		this.plugin = plugin;
		this.callback = callback;
	}

	public void positions( Location startPoint, Location endPoint, Location handlePoint ) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.handlePoint = handlePoint;
	}

	public Location computeBezierPosition( float t ) {
		float oppositet = ( 1 - t );
		float coeff1 = oppositet * oppositet;
		float coeff2 = 2 * oppositet * t;
		float coeff3 = t * t;

		return new Location(
				this.startPoint.getWorld(),
				coeff1 * this.startPoint.getX() + coeff2 * this.handlePoint.getX() + coeff3 * this.endPoint.getX(),
				coeff1 * this.startPoint.getY() + coeff2 * this.handlePoint.getY() + coeff3 * this.endPoint.getY(),
				coeff1 * this.startPoint.getZ() + coeff2 * this.handlePoint.getZ() + coeff3 * this.endPoint.getZ(),
				this.startPoint.getYaw() * oppositet + this.endPoint.getYaw() * t,
				this.startPoint.getPitch() * oppositet + this.endPoint.getPitch() * t
		);
	}

	public void init( Set<Map.Entry<String, ArenaPlayer>> players ) {
		this.task = null;
		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().freeze();
			entry.getValue().setGameMode( GameMode.SPECTATOR );
			entry.getValue().removePotionEffect( PotionEffectType.SLOW );
			entry.getValue().removePotionEffect( PotionEffectType.BLINDNESS );
			this.originalLocations.put( entry.getValue(), entry.getValue().getLocation().clone() );
		}
	}

	public void finish( Set<Map.Entry<String, ArenaPlayer>> players ) {
		if( this.task != null )
			this.task.cancel();

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().setGameMode( GameMode.ADVENTURE );
			entry.getValue().unfreeze();
			entry.getValue().teleport( this.originalLocations.get( entry.getValue() ) );
		}

		callback.afterAnimationFinish( this );
	}

	public void animate( Set<Map.Entry<String, ArenaPlayer>> players, float speed ) {
		init( players );

		this.task = Bukkit.getScheduler().runTaskTimer( this.plugin, new AnimatorTask( this, players, speed ), 1L, 1L );
	}
}
