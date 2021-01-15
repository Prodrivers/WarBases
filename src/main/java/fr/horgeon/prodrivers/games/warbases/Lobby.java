package fr.horgeon.prodrivers.games.warbases;

import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

@Deprecated
public class Lobby {
	private Main plugin;
	private Location lobbyLoc, mainLoc;
	private HashSet<UUID> inLobby = new HashSet<>();

	public Lobby( Main plugin ) {
		this.plugin = plugin;
		load();
	}

	public void reload() {
		load();
	}

	private void load() {
		mainLoc = loadLocation( "config.lobbies.main.location" );
		lobbyLoc = loadLocation( "config.lobbies.game.location" );

		System.out.println( "[WarBases] Main lobby set to " + mainLoc );
		System.out.println( "[WarBases] Game lobby set to " + lobbyLoc );
	}

	private Location loadLocation( String base ) {
		return new Location(
				Bukkit.getWorld( plugin.getConfig().getString( base + ".world" ) ),
				plugin.getConfig().getDouble( base + ".x" ),
				plugin.getConfig().getDouble( base + ".y" ),
				plugin.getConfig().getDouble( base + ".z" ),
				(float) plugin.getConfig().getDouble( base + ".yaw" ),
				(float) plugin.getConfig().getDouble( base + ".pitch" )
		);
	}

	public void join( Player player ) {
		if( lobbyLoc != null && !inLobby.contains( player.getUniqueId() ) ) {
			teleport( player );
			inLobby.add( player.getUniqueId() );
			plugin.getResourcePackManager().join( player );
		}
	}

	public void teleport( Player player ) {
		player.teleport( lobbyLoc );
	}

	public void leave( Player player ) {
		if( mainLoc != null && inLobby.contains( player.getUniqueId() ) ) {
			player.teleport( mainLoc );
			inLobby.remove( player.getUniqueId() );
			plugin.getResourcePackManager().leave( player );
		}
	}

	public boolean isInLobby( Player player ) {
		return inLobby.contains( player.getUniqueId() );
	}

	public void handleCommand( CommandSender sender ) {
		if( sender instanceof Player ) {
			if( isInLobby( (Player) sender ) )
				leave( (Player) sender );
			else
				join( (Player) sender );
		} else {
			sender.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).not_a_player );
		}
	}
}
