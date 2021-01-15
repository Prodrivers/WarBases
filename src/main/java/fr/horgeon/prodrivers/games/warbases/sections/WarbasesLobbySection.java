package fr.horgeon.prodrivers.games.warbases.sections;

import com.comze_instancelabs.minigamesapi.util.Util;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.prodrivers.bukkit.commons.sections.IProdriversSection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarbasesLobbySection implements IProdriversSection {
	public static String name = "wb";
	private Main plugin;
	private Location loc;

	public WarbasesLobbySection( Main plugin ) {
		this.plugin = plugin;
		reload();
	}

	public String getName() {
		return name;
	}

	public String getPreferredNextSection() {
		return null;
	}

	public boolean forceNextSection() {
		return false;
	}

	public boolean isHub() {
		return true;
	}

	public boolean shouldMoveParty() {
		return true;
	}

	public boolean join( Player player, String subSection, String leavedSection ) {
		if( loc == null )
			return false;

		player.teleport( loc );
		if( !leavedSection.equals( WarbasesGameSection.name ) )
			plugin.getResourcePackManager().join( player );

		return true;
	}

	public void postJoin( Player player, String subSection, String leavedSection ) {}

	public boolean leave( Player player, String enteredSection ) {
		if( !plugin.getPluginInstance().containsGlobalPlayer( player.getName() ) && !enteredSection.equals( WarbasesGameSection.name ) )
			plugin.getResourcePackManager().leave( player );
		return true;
	}

	public void postLeave( Player player, String enteredSection ) {}

	public void reload() {
		loc = Util.getMainLobby( plugin );
	}
}
