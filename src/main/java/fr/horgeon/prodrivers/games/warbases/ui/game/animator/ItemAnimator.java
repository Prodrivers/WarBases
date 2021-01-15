package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemAnimator {
	private JavaPlugin plugin;
	private BukkitTask task = null;

	public HashMap<Integer, ItemStack> items;

	private ItemStack item;

	public ItemAnimator( JavaPlugin plugin, HashMap<Integer, ItemStack> items ) {
		this.plugin = plugin;
		this.items = items;
	}

	public void init( Set<Map.Entry<String, ArenaPlayer>> players ) {
		this.task = null;
		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().freeze();
			entry.getValue().setGameMode( GameMode.ADVENTURE );
			entry.getValue().removePotionEffect( PotionEffectType.SLOW );
			entry.getValue().removePotionEffect( PotionEffectType.BLINDNESS );
		}
	}

	public void update( Set<Map.Entry<String, ArenaPlayer>> players, int frame ) {
		this.item = this.items.get( frame );

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			updatePlayer( entry.getValue() );
		}
	}

	public void updatePlayer( ArenaPlayer p ) {
		p.getInventory().setHeldItemSlot( 0 );
		if( p.getConfiguration().getUI().isEnhanced )
			p.getInventory().getItem( 0 ).setDurability( this.item.getDurability() );
		else
			p.getInventory().removeItem( this.item );
	}

	public void finish( Set<Map.Entry<String, ArenaPlayer>> players ) {
		if( this.task != null )
			this.task.cancel();

		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			entry.getValue().unfreeze();
			entry.getValue().getInventory().removeItem( this.item );
		}
	}

	public void animate( Set<Map.Entry<String, ArenaPlayer>> players ) {
		init( players );

		this.task = Bukkit.getScheduler().runTaskTimer( this.plugin, new ItemAnimatorTask( this, players ), 1L, 1L );
	}
}
