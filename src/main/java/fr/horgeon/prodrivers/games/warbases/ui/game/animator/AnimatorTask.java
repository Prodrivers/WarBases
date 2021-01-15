package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;

import java.util.Set;
import java.util.Map;

public class AnimatorTask implements Runnable {
	private Animator animator;
	private Set<Map.Entry<String, ArenaPlayer>> players;
	private float t = 0, speed;

	public AnimatorTask( Animator animator, Set<Map.Entry<String, ArenaPlayer>> players ) {
		this( animator, players, 1.0f );
	}

	public AnimatorTask( Animator animator, Set<Map.Entry<String, ArenaPlayer>> players, float speed ) {
		this.animator = animator;
		this.speed = speed;
		this.players = players;
	}

	@Override
	public void run() {
		this.t += speed;

		if( t > 1 )
			animator.finish( this.players );

		this.animator.update( this.players, t );
	}
}
