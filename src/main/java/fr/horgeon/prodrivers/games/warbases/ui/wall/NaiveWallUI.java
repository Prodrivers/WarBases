package fr.horgeon.prodrivers.games.warbases.ui.wall;

import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;

import fr.horgeon.prodrivers.games.warbases.arena.wall.WallBlock;
import gist.kingfaris10.Cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class NaiveWallUI extends WallUI {
	//private HashSet<UUID> fallingBlocks = new HashSet<>();

	public NaiveWallUI( int id, Main plugin, EArena arena, Location low, Location high ) {
		super( id, plugin, arena, low, high );
	}

	protected void save() {
		System.out.println( "[WarBases] Arena " + arena.getInternalName() + ": Wall saving initiated" );

		for( Block worldBlock: worldBlocks ) {
			blocks.put( worldBlock.getLocation(), new WallBlock( worldBlock ) );
		}

		System.out.println( "[WarBases] Arena " + arena.getInternalName() + ": Wall saving done" );
	}

	public void start() {}

	public void fall() {
		//fallingBlocks.clear();

		for( Block worldBlock: worldBlocks ) {
			/*FallingBlock fallingBlock = worldBlock.getWorld().spawnFallingBlock( worldBlock.getLocation(), worldBlock.getType(), worldBlock.getData() );
			fallingBlock.setHurtEntities( false );
			fallingBlocks.add( fallingBlock.getUniqueId() );*/

			worldBlock.setType( Material.AIR );
			BlockState state = worldBlock.getState();
			state.setType( Material.AIR );
			state.update( true );
		}

		/*for( Map.Entry<String, ArenaPlayer> entry : arena.getArenaManager().getPlayers().entrySet() ) {
			entry.getValue().playSound( entry.getValue().getLocation(), "entity.wither.break_block", 2.0F, 0.5F );
		}*/
	}

	public void reset() {
		for( Block worldBlock: worldBlocks ) {
			blocks.get( worldBlock.getLocation() ).clone( worldBlock );
		}
	}

	public void rebuild() {
		reset();
	}

	/*public boolean isOwnFallingBlock( UUID block ) {
		return this.fallingBlocks.contains( block );
	}

	public void removeFallingBlock( UUID block ) {
		this.fallingBlocks.remove( block );
	}*/
}
