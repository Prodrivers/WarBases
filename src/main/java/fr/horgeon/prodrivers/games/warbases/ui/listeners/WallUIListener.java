package fr.horgeon.prodrivers.games.warbases.ui.listeners;

import fr.horgeon.prodrivers.games.warbases.ui.wall.WallUI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class WallUIListener implements Listener {
	private WallUI ui = null;

	public WallUIListener( JavaPlugin plugin, WallUI ui ) {
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		this.ui = ui;
	}

	/*@EventHandler( priority = EventPriority.LOW )
	public void onEntityChangeBlock( EntityChangeBlockEvent event ) {
		if( event.getEntity() instanceof FallingBlock && ui.isOwnFallingBlock( event.getEntity().getUniqueId() ) ) {
			ui.removeFallingBlock( event.getEntity().getUniqueId() );
			//event.getBlock().setType( Material.AIR );
			event.setCancelled( true );
		}
	}*/
}
