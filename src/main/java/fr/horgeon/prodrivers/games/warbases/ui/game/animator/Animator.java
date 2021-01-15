package fr.horgeon.prodrivers.games.warbases.ui.game.animator;

import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;

public interface Animator {
	void positions( Location startPoint, Location endPoint, Location handlePoint );

	void init( Set<Map.Entry<String, ArenaPlayer>> players );

	void updatePlayer( ArenaPlayer p, Location loc );

	void update( Set<Map.Entry<String, ArenaPlayer>> players, float t );

	void finish( Set<Map.Entry<String, ArenaPlayer>> players );

	void animate( Set<Map.Entry<String, ArenaPlayer>> players, float speed );
}
