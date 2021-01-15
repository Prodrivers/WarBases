package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class ArenaManager {
	private HashMap<String, ArenaPlayer> arenaPlayers = new HashMap<>();
	private HashMap<ArenaTeam, List<ArenaPlayer>> arenaColorPlayers = new HashMap<>();

	public ArenaManager() {
		buildColorTeams();
	}

	public void add( ArenaPlayer p ) {
		arenaPlayers.put( p.getName(), p );

		if( p.getTeam() != null ) {
			arenaColorPlayers.get( p.getTeam() ).add( p );
		}
	}

	public void remove( String playerName ) {
		ArenaPlayer ap = arenaPlayers.get( playerName );
		if( ap != null && ap.getTeam() != null ) {
			arenaColorPlayers.get( ap.getTeam() ).remove( ap );
		}

		arenaPlayers.remove( playerName );
	}

	public ArenaPlayer getPlayer( Player p ) {
		if( p != null )
			return arenaPlayers.get( p.getName() );
		else
			return null;
	}

	public ArenaPlayer getPlayer( Entity p ) {
		if( p != null && p instanceof Player )
			return arenaPlayers.get( p.getName() );
		else
			return null;
	}

	public ArenaPlayer getPlayer( ProjectileSource p ) {
		if( p != null && p instanceof Player )
			return arenaPlayers.get( ( (Player) p ).getName() );
		else
			return null;
	}

	void buildColorTeams() {
		arenaColorPlayers.clear();

		for( ArenaTeam team : ArenaTeam.values() ) {
			arenaColorPlayers.put( team, new ArrayList<ArenaPlayer>() );
		}

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaPlayers.entrySet() ) {
			if( entry.getValue().getTeam() != null ) {
				arenaColorPlayers.get( entry.getValue().getTeam() ).add( entry.getValue() );
			}
		}
	}

	public ArenaPlayer getPlayer( String playerName ) {
		return arenaPlayers.get( playerName );
	}

	public Location getSpawn( Arena a, ArenaPlayer p ) {
		return a.getSpawns().get( p.getTeam().toInt() );
	}

	public HashMap<String, ArenaPlayer> getPlayers() {
		return this.arenaPlayers;
	}

	public void reset() {
		this.arenaPlayers.clear();
	}

	public int teamNumberOfPlayers( ArenaTeam team ) {
		/*int count = 0;

		for( Map.Entry<String, ArenaPlayer> entry : this.arenaPlayers.entrySet() ) {
			if( entry.getValue().getTeam() == team )
				count++;
		}

		return count;*/

		if( team != null )
			return this.arenaColorPlayers.get( team ).size();
		return 0;
	}

	public boolean teamStillHasPlayers( ArenaTeam team ) {
		/*for( Map.Entry<String, ArenaPlayer> entry : this.arenaPlayers.entrySet() ) {
			if( entry.getValue().getTeam() == team && !entry.getValue().isDead() )
				return true;
		}

		return false;*/

		if( team != null ) {
			for( ArenaPlayer player : this.arenaColorPlayers.get( team ) ) {
				if( !player.isDead() )
					return true;
			}
		}

		return false;
	}

	public List<ArenaPlayer> getPlayersInTeam( ArenaTeam team ) {
		if( team != null )
			return this.arenaColorPlayers.get( team );
		return null;
	}
}
