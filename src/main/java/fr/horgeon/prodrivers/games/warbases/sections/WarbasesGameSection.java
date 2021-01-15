package fr.horgeon.prodrivers.games.warbases.sections;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.prodrivers.bukkit.commons.sections.IProdriversSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarbasesGameSection implements IProdriversSection {
	public static String name = "warbasesgame";
	private PluginInstance pli;

	public WarbasesGameSection( Main plugin ) {
		this.pli = plugin.getPluginInstance();
	}

	public String getName() {
		return name;
	}

	public String getPreferredNextSection() {
		return WarbasesLobbySection.name;
	}

	public boolean forceNextSection() {
		return true;
	}

	public boolean isHub() {
		return false;
	}

	public boolean shouldMoveParty() {
		return true;
	}

	public boolean join( Player player, String subSection, String leavedSection ) {
		return true;
	}

	public void postJoin( Player player, String subSection, String leavedSection ) {}

	public boolean leave( Player player, String enteredSection ) {
		String playername = player.getName();

		Arena a = pli.getArenaByGlobalPlayer( player.getName() );
		if( a != null && a instanceof EArena ) {
			if( a.getArcadeInstance() != null ) {
				a.getArcadeInstance().leaveArcade( playername, true );
			}

			a.leavePlayer( playername, false, false );
		}

		return true;
	}

	public void postLeave( Player player, String enteredSection ) {}
}
