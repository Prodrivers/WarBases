package fr.horgeon.prodrivers.games.warbases.ui;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PreferencesSelector {
	private static BaseComponent[] dialog;

	private static void build( PluginInstance pli ) {
		TextComponent frame, blank, intro, entry_ui;
		BaseComponent[] click_to_change;

		EMessagesConfig messages = (EMessagesConfig) pli.getMessagesConfig();
		int width = Constants.UI_SELECTOR_WIDTH;

		blank = new TextComponent( "" );

		StringBuilder frameStr = new StringBuilder();
		for( int i = 0; i < width; i++ )
			frameStr.append( messages.preferences_selector_frame_character );
		frame = new TextComponent( frameStr.toString() );
		frame.setColor( ChatColor.GRAY );

		intro = new TextComponent( " " + messages.preferences_selector_intro );
		intro.setColor( ChatColor.WHITE );
		intro.setBold( true );

		click_to_change = TextComponent.fromLegacyText( messages.preferences_selector_click_to_change );

		entry_ui = new TextComponent( " " + messages.preferences_selector_entry_ui );
		entry_ui.setColor( ChatColor.GREEN );
		entry_ui.setBold( true );
		entry_ui.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		entry_ui.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui" ) );

		dialog = new BaseComponent[]{ frame, blank, intro, blank, entry_ui, blank, frame };
	}

	private static void send( JavaPlugin plugin, PluginInstance pli, Player player ) {
		if( dialog == null )
			build( pli );

		final Arena a_ = pli.getArenaByGlobalPlayer( player.getName() );
		if( a_ != null ) {
			player.sendMessage( ( (EMessagesConfig) pli.getMessagesConfig() ).preferences_selector_is_in_arena );
			return;
		}

		for( BaseComponent comp : dialog )
			player.spigot().sendMessage( comp );
	}

	public static void handle( Main plugin, PluginInstance pli, String permissionPrefix, Player player, String[] args ) {
		if( args.length > 1 ) {
			switch( args[ 1 ] ) {
				case "ui":
					UISelector.handle( plugin, pli, permissionPrefix, player, args );
					return;
			}
		}

		send( plugin, pli, player );
	}
}
