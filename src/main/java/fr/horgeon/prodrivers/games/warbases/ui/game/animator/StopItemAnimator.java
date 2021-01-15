package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StopItemAnimator extends ItemAnimator{
	private JavaPlugin plugin;
	private PluginInstance pli;
	private BukkitTask task = null;

	public HashMap<Integer, ItemStack> victory, defeat;
	private ItemStack victoryItem, defeatItem;

	public StopItemAnimator( JavaPlugin plugin, PluginInstance pli, HashMap<Integer, ItemStack> victory, HashMap<Integer, ItemStack> defeat ) {
		super( plugin, victory );
		this.plugin = plugin;
		this.pli = pli;
		this.victory = victory;
		this.defeat = defeat;
	}

	@Override
	public void update( Set<Map.Entry<String, ArenaPlayer>> players, int frame ) {
		this.victoryItem = this.items.get( frame );
		this.defeatItem = this.items.get( frame );

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			updatePlayer( entry.getValue() );
		}
	}

	@Override
	public void updatePlayer( ArenaPlayer p ) {
		p.getInventory().setHeldItemSlot( 0 );
		switch( p.outcome() ) {
			case WIN:
				if( p.getConfiguration().getUI().isEnhanced )
					p.getInventory().setItem( 0, this.victoryItem );
				else
					p.getInventory().removeItem( this.victoryItem );
				break;

			case LOSE:
				if( p.getConfiguration().getUI().isEnhanced )
					p.getInventory().setItem( 0, this.defeatItem );
				else
					p.getInventory().removeItem( this.defeatItem );
			break;
		}
	}
}
