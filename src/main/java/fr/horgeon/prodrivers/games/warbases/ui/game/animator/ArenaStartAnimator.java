package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import com.comze_instancelabs.minigamesapi.util.Cuboid;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ArenaStartAnimator extends AbsoluteAnimator {
	enum Yaw {
		North( 180 ),
		East( 270 ),
		South( 0 ),
		West( 90 );

		private float value;

		Yaw( float value ) {
			this.value = value;
		}

		public float getValue() {
			return value;
		}
	}

	private float yawOffset = 20.0f;
	private float pitchOffset = 45.0f;

	/*public HashMap<Integer, ItemStack> items;
	private int frame;
	private boolean frameFinished;

	private ItemStack item;*/

	public ArenaStartAnimator( JavaPlugin plugin, AnimatorFinishCallback callback, Cuboid boundaries ) { //, HashMap<Integer, ItemStack> items
		super( plugin, callback );
		positions( boundaries );
		//this.items = items;
	}

	public void positions( Cuboid boundaries ) {
		Random rand = new Random();

		double startX, endX, handleX, startZ, endZ, handleZ;
		float startYaw, endYaw;

		Location higherPoint = boundaries.getHighLoc();
		Location lowerPoint = boundaries.getLowLoc();

		double diffY = ( higherPoint.getY() - lowerPoint.getY() ) / 4,
				startY = higherPoint.getY() + diffY,
				endY = higherPoint.getY(),
				handleY = startY + diffY / 2;

		double diffX = higherPoint.getX() - lowerPoint.getX();
		int signX = ( diffX > 0 ? 1 : ( diffX < 0 ? -1 : 0 ) );
		/*diffX /= 10;
		double startX = higherPoint.getX() - signX * diffX,
				endX = lowerPoint.getX() + signX * diffX,
				handleX = endX;*/

		double diffZ = higherPoint.getZ() - lowerPoint.getZ();
		int signZ = ( diffZ > 0 ? 1 : ( diffZ < 0 ? -1 : 0 ) );
		diffZ /= 10;
		/*double startZ = higherPoint.getZ() - signZ * diffZ,
				endZ = lowerPoint.getZ() + signZ * diffZ,
				handleZ = startZ;*/

		/*if( diffX >= 0 && diffZ < 0 ) {
			startYaw = Yaw.North.getValue();
			endYaw = Yaw.West.getValue();
		} else if( diffX < 0 && diffZ < 0 ) {
			startYaw = Yaw.West.getValue();
			endYaw = Yaw.South.getValue();
		} else if( diffX < 0 && diffZ >= 0 ) {
			startYaw = Yaw.South.getValue();
			endYaw = Yaw.East.getValue();
		} else if( diffX >= 0 && diffZ >= 0 ) {
			startYaw = Yaw.East.getValue();
			endYaw = Yaw.North.getValue();
		}*/

		if( diffX > diffZ ) {
			startZ = higherPoint.getZ() - signZ * diffZ;
			endZ = lowerPoint.getZ() + signZ * diffZ;
			handleZ = ( higherPoint.getZ() + lowerPoint.getZ() ) / 2;

			startX = ( higherPoint.getX() + lowerPoint.getX() ) / 2;
			endX = startX;

			boolean bool = rand.nextBoolean();
			handleX = startX + ( bool ? 1 : -1 ) * signX * diffX;
			startYaw = ( bool ? Yaw.West.getValue() + signZ * yawOffset : Yaw.East.getValue() - signZ * yawOffset );
			endYaw = ( bool ? Yaw.West.getValue() - signZ * yawOffset : Yaw.East.getValue() + signZ * yawOffset );
		} else {
			startX = higherPoint.getX() - signX * diffX;
			endX = lowerPoint.getX() + signX * diffX;
			handleX = ( higherPoint.getX() + lowerPoint.getX() ) / 2;

			startZ = ( higherPoint.getZ() + lowerPoint.getZ() ) / 2;
			endZ = startZ;

			boolean bool = rand.nextBoolean();
			handleZ = startZ + ( bool ? 1 : -1 ) * signZ * diffZ;
			startYaw = ( bool ? Yaw.North.getValue() - signX * yawOffset : Yaw.South.getValue() + signX * yawOffset );
			endYaw = ( bool ? Yaw.North.getValue() + signX * yawOffset : Yaw.South.getValue() - signX * yawOffset );
		}

		super.positions(
				new Location( higherPoint.getWorld(), startX, startY, startZ, startYaw, pitchOffset ),
				new Location( higherPoint.getWorld(), endX, endY, endZ, endYaw, pitchOffset ),
				new Location( higherPoint.getWorld(), handleX, handleY, handleZ )
		);
	}

	/*@Override
	public void init( Set<Map.Entry<String, ArenaPlayer>> players ) {
		this.frame = 0;
		this.frameFinished = false;

		super.init( players );

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().setGameMode( GameMode.ADVENTURE );
		}
	}

	@Override
	public void update( Set<Map.Entry<String, ArenaPlayer>> players, float t ) {
		this.item = this.items.get( this.frame );
		super.update( players, t );
		this.frame++;
		if( this.frame >= this.items.size() )
			this.frameFinished = true;
	}

	@Override
	public void updatePlayer( ArenaPlayer p, Location loc ) {
		super.updatePlayer( p, loc );
		p.getInventory().setHeldItemSlot( 0 );
		if( p.getSelectedUI().isEnhanced )
			p.getInventory().setItem( 0, this.item );
		else
			p.getInventory().removeItem( this.item );
	}*/
}
