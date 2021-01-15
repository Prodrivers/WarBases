package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.arena.checkpoint.CheckpointManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class DecideWiningTeam {
	private JavaPlugin plugin;
	private PluginInstance pli;
	private EArena arena;
	private ArenaManager arenaManager;
	private CheckpointManager checkpointManager;

	DecideWiningTeam( JavaPlugin plugin, PluginInstance pli, EArena arena, ArenaManager arenaManager, CheckpointManager checkpointManager, boolean checkpointFinished ) {
		this.plugin = plugin;
		this.pli = pli;
		this.arena = arena;
		this.arenaManager = arenaManager;
		this.checkpointManager = checkpointManager;

		if( checkpointFinished )
			checkpoint();
		else
			normal();
	}

	private void normal() {
		int notAllDeadTeams = 0;

		// For each team
		for( ArenaTeam team : ArenaTeam.values() ) {
			// If the team does not have any alive players
			if( !this.arenaManager.teamStillHasPlayers( team ) ) {
				// For each player of this team
				for( ArenaPlayer ap : this.arenaManager.getPlayersInTeam( team ) ) {
					if( ap.getTeam() == team ) {
						// Mark him as loosing
						ap.setOutcome( pli, arena, ArenaOutcome.LOSE );
					}
				}
			} else {
				// If not, increase the counter of not-all dead teams
				notAllDeadTeams++;

				// And set its players as winning
				for( ArenaPlayer ap : this.arenaManager.getPlayersInTeam( team ) ) {
					if( ap.getTeam() == team ) {
						// Mark him as winning
						ap.setOutcome( pli, arena, ArenaOutcome.WIN );
					}
				}
			}
		}

		// If all the teams still have players
		if( notAllDeadTeams == ArenaTeam.values().length ) {
			/*// We mark the team whose checkpoint has been conquered the most as the losing one, and every other one as the wining one

			// For each checkpoints, store the one who has been conquered the most
			Checkpoint losing = checkpointManager.all().get( 0 );
			for( Checkpoint chkpt : checkpointManager.all() ) {
				if( chkpt.getProgress() > losing.getProgress() )
					losing = chkpt;
			}

			// For each player whose team is the considered checkpoint's one
			for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
				if( entry.getValue().getTeam() ==  losing.getTeam() ) {
					// Mark him as loosing
					this.pli.global_lost.put( entry.getValue().getName(), this.arena );
					entry.getValue().lose();
				}
			}*/

			// It's a draw!
			for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
				entry.getValue().setOutcome( pli, arena, ArenaOutcome.DRAW );
			}
		}
	}

	private void checkpoint() {
		// If we were in Overtime
		if( checkpointManager.finished().isInOvertime() ) {
			checkpointOvertime();
		} else {
			checkpointNormal();
		}
	}

	private void checkpointNormal() {
		for( ArenaPlayer p : this.checkpointManager.finished().getActivePlayers() ) {
			arena.handleAchievements( p );
		}

		// We mark the team whose checkpoint has been conquered as the losing one, and every other one as the wining one

		// For each player in the game
		for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
			// If the player is in the losing team
			if( entry.getValue().getTeam() ==  this.checkpointManager.finished().getTeam() ) {
				// Mark him as loosing
				entry.getValue().setOutcome( pli, arena, ArenaOutcome.LOSE );
			} else {
				// Mark him as wining
				entry.getValue().setOutcome( pli, arena, ArenaOutcome.WIN );
			}
		}
	}

	private void checkpointOvertime() {
		// We get the team left on the point
		ArenaTeam leftTeam = null;
		boolean both = false;

		//
		for( ArenaPlayer p : this.checkpointManager.finished().getActivePlayers() ) {
			if( leftTeam == null && !both ) {
				leftTeam = p.getTeam();
			} else if( leftTeam != p.getTeam() ) {
				both = true;
				leftTeam = null;
			}
		}

		// If both teams were on the point, or the left team is the one that own the point (they were able to eject the opponent)
		if( both || leftTeam == this.checkpointManager.finished().getTeam() ) {
			// It's a draw!
			for( Map.Entry<String, ArenaPlayer> entry : this.arenaManager.getPlayers().entrySet() ) {
				entry.getValue().setOutcome( pli, arena, ArenaOutcome.DRAW );
			}
			return;
		}

		// If the team left is the opposite team
		if( leftTeam != this.checkpointManager.finished().getTeam() ) {
			// They successfully conquered ! Proceed as usual
			checkpointNormal();
		}
	}
}
