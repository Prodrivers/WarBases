package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;

import java.util.Map;
import java.util.Set;

public class ItemAnimatorTask implements Runnable {
	private ItemAnimator animator;
	private Set<Map.Entry<String, ArenaPlayer>> players;
	private int frame = 0;

	public ItemAnimatorTask( ItemAnimator animator, Set<Map.Entry<String, ArenaPlayer>> players ) {
		this.animator = animator;
		this.players = players;
	}

	@Override
	public void run() {
		this.frame++;

		if( this.frame >= animator.items.size() )
			animator.finish( this.players );

		this.animator.update( this.players, frame );
	}
}