package fr.horgeon.prodrivers.games.warbases.arena.wall;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class WallBlock {
	Material type;
	MaterialData data;

	public WallBlock( Block block ) {
		type  = block.getState().getType();
		data = block.getState().getData();
	}
	
	public void clone( Block block ) {
		block.setType( type );
		BlockState state = block.getState();
		state.setType( type );
		state.setData( data );
		state.update( true );
	}
}
