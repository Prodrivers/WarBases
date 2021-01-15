package fr.horgeon.prodrivers.games.warbases.ui.game;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.scheduler.BukkitTask;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses;

public class RespawnUI implements Runnable {
	private BukkitTask task;
	private int timer;
	private ArenaPlayer player;
	private float step;
	private Corpses.CorpseData corpse;

	public RespawnUI( int max, ArenaPlayer player ) {
		this.timer = max;
		this.player = player;
		this.step = 1.0f / max;

		player.setExperience( 1 );

		corpse = CorpseAPI.spawnCorpse( player.getPlayer(), player.getLocation(), player.getInventory().getContents().clone(), player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots() );
	}

	@Override
	public void run() {
		this.timer--;

		if( this.timer < 0 ) {
			stop();
			return;
		}

		generate();
	}

	public void stop() {
		if( this.task != null )
			this.task.cancel();
		if( this.corpse != null )
			CorpseAPI.removeCorpse( this.corpse );
	}

	public void generate() {
		this.player.setExperience( timer * step );
	}

	public void setTask( BukkitTask task ) {
		this.task  = task;
	}
}
