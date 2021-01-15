package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.util.ArenaLobbyScoreboard;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class EArenaLobbyScoreboard extends ArenaLobbyScoreboard {
	private EMessagesConfig messages;
	private Map<String, Scoreboard> arenaScoreboards = new HashMap<>();
	private Map<String, Objective> arenaObjectives = new HashMap<>();
	private Map<String, String[]> lines = new HashMap<>();

	public EArenaLobbyScoreboard( Main plugin ) {
		super( plugin.getPluginInstance(), plugin );
		this.messages = (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig();
	}

	private void init( Arena arena ) {
		if( !lines.containsKey( arena.getInternalName() ) )
			lines.put( arena.getInternalName(), new String[ 1 ] );

		String arenaLine = this.messages.scoreboard_arena.replaceAll( "<arena>", arena.getDisplayName() );
		int width = Math.max( Constants.UI_SCOREBOARD_WIDTH, arenaLine.length() );

		Scoreboard arenaScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective arenaObjective = arenaScoreboard.registerNewObjective( arena.getInternalName(), "dummy" );
		arenaObjective.setDisplayName( this.messages.scoreboard_main_title );
		arenaObjective.setDisplaySlot( DisplaySlot.SIDEBAR );

		StringBuilder frameStr = new StringBuilder();
		for( int i = 0; i < width; i++ )
			frameStr.append( messages.scoreboard_separator );

		arenaObjective.getScore( ChatColor.AQUA.toString() + ChatColor.RESET.toString() + frameStr.toString() ).setScore( 5 );
		arenaObjective.getScore( ChatColor.BLACK.toString() + ChatColor.RESET.toString() ).setScore( 4 );
		arenaObjective.getScore( arenaLine ).setScore( 3 );
		arenaObjective.getScore( ChatColor.GRAY.toString() + ChatColor.RESET.toString() ).setScore( 1 );
		arenaObjective.getScore( ChatColor.DARK_RED.toString() + ChatColor.RESET.toString() + frameStr.toString() ).setScore( 0 );

		arenaScoreboards.put( arena.getInternalName(), arenaScoreboard );
		arenaObjectives.put( arena.getInternalName(), arenaObjective );
	}

	private void update( Arena arena ) {
		String[] aLines = lines.get( arena.getInternalName() );
		if( aLines[ 0 ] != null )
			arenaScoreboards.get( arena.getInternalName() ).resetScores( aLines[ 0 ] );
		aLines[ 0 ] = this.messages.scoreboard_players.replaceAll( "<current>", String.valueOf( arena.getAllPlayers().size() ) ).replaceAll( "<max>", String.valueOf( arena.getMaxPlayers() ) );
		arenaObjectives.get( arena.getInternalName() ).getScore( aLines[ 0 ] ).setScore( 2 );
	}

	@Override
	public void updateScoreboard( JavaPlugin plugin, final Arena arena ) {
		if( !arenaScoreboards.containsKey( arena.getInternalName() ) )
			init( arena );

		update( arena );

		Bukkit.getScheduler().runTask( plugin, new Runnable() {
			@Override
			public void run() {
				for( String playerName : arena.getAllPlayers() ) {
					Player player = Bukkit.getPlayer( playerName );
					if( player != null )
						player.setScoreboard( arenaScoreboards.get( arena.getInternalName() ) );
				}
			}
		});
	}
}
