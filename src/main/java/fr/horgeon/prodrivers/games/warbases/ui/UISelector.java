package fr.horgeon.prodrivers.games.warbases.ui;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.mongodb.client.model.Updates;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.configuration.players.PlayerConfiguration;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUIQuality;
import fr.horgeon.prodrivers.games.warbases.ui.game.UIManager;
import fr.prodrivers.bukkit.commons.storage.StorageProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class UISelector {
	private static BaseComponent[] lightweightEnabled = null, enhancedEnabledInHotbarSD, enhancedEnabledInHotbarHD; //, enhancedEnabledOnTopOfHotbarSD, enhancedEnabledOnTopOfHotbarHD;
	private static TextComponent preferenceSet;
	private static BaseComponent[] tooOldForEnhanced;

	private static void build( PluginInstance pli ) {
		TextComponent frame, blank, intro, lightweight_disabled, enhanced_disabled, lightweight_enabled, enhanced_enabled, enhanced_flavor, inhotbar_disabled, ontopofhotbar_disabled, inhotbar_enabled, ontopofhotbar_enabled, persitentcommenu_disabled, persitentcommenu_enabled, enhanced_quality, quality_sd_disabled, quality_sd_enabled, quality_hd_disabled, quality_hd_enabled;
		BaseComponent[] click_to_change;
		EMessagesConfig messages = (EMessagesConfig) pli.getMessagesConfig();
		int width = Constants.UI_SELECTOR_WIDTH;

		blank = new TextComponent( "" );

		StringBuilder frameStr = new StringBuilder();
		for( int i = 0; i < width; i++ )
			frameStr.append( messages.preferences_selector_frame_character );
		frame = new TextComponent( frameStr.toString() );
		frame.setColor( ChatColor.GRAY );

		click_to_change = TextComponent.fromLegacyText( messages.preferences_selector_click_to_change );

		intro = new TextComponent( " " + messages.ui_selector_intro );
		intro.setColor( ChatColor.WHITE );
		intro.setBold( true );

		lightweight_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_light );
		lightweight_disabled.setColor( ChatColor.RED );
		lightweight_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		lightweight_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui enableLightweight" ) );

		lightweight_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_light );
		lightweight_enabled.setColor( ChatColor.GREEN );

		enhanced_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_enhanced );
		enhanced_disabled.setColor( ChatColor.RED );
		enhanced_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		enhanced_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui enableEnhanced" ) );

		enhanced_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_enhanced );
		enhanced_enabled.setColor( ChatColor.GREEN );

		enhanced_flavor = new TextComponent( " " + messages.ui_selector_enhanced_flavor_intro );
		enhanced_flavor.setColor( ChatColor.WHITE );
		enhanced_flavor.setBold( true );

		inhotbar_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_enhanced_flavor_inhotbar );
		inhotbar_disabled.setColor( ChatColor.RED );
		inhotbar_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		inhotbar_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui enableInHotbar" ) );

		inhotbar_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_enhanced_flavor_inhotbar );
		inhotbar_enabled.setColor( ChatColor.GREEN );

		ontopofhotbar_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_enhanced_flavor_ontopofhotbar );
		ontopofhotbar_disabled.setColor( ChatColor.RED );
		ontopofhotbar_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		ontopofhotbar_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui enableOnTopOfHotbar" ) );

		ontopofhotbar_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_enhanced_flavor_ontopofhotbar );
		ontopofhotbar_enabled.setColor( ChatColor.GREEN );

		persitentcommenu_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_persistentcommenu );
		persitentcommenu_disabled.setColor( ChatColor.RED );
		persitentcommenu_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		persitentcommenu_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui enablePersComMenu" ) );

		persitentcommenu_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_persistentcommenu );
		persitentcommenu_enabled.setColor( ChatColor.GREEN );
		persitentcommenu_enabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		persitentcommenu_enabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui disablePersComMenu" ) );

		enhanced_quality = new TextComponent( " " + messages.ui_selector_enhanced_quality_intro );
		enhanced_quality.setColor( ChatColor.WHITE );
		enhanced_quality.setBold( true );

		quality_sd_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_enhanced_quality_sd );
		quality_sd_disabled.setColor( ChatColor.RED );
		quality_sd_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		quality_sd_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui qualitySd" ) );

		quality_sd_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_enhanced_quality_sd );
		quality_sd_enabled.setColor( ChatColor.GREEN );

		quality_hd_disabled = new TextComponent( "  " + messages.preferences_selector_bullet_unchecked + " " + messages.ui_selector_enhanced_quality_hd );
		quality_hd_disabled.setColor( ChatColor.RED );
		quality_hd_disabled.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, click_to_change ) );
		quality_hd_disabled.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/warbases prefs ui qualityHd" ) );

		quality_hd_enabled = new TextComponent( "  " + messages.preferences_selector_bullet_checked + " " + messages.ui_selector_enhanced_quality_hd );
		quality_hd_enabled.setColor( ChatColor.GREEN );

		preferenceSet = new TextComponent( messages.ui_selector_preference_set );
		preferenceSet.setBold( true );
		preferenceSet.setColor( ChatColor.GREEN );

		tooOldForEnhanced = TextComponent.fromLegacyText( messages.version_too_old_for_enhanced_ui );

		lightweightEnabled = new BaseComponent[]{ frame, blank, intro, lightweight_enabled, enhanced_disabled, blank, frame };
		//enhancedEnabledInHotbarSD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_flavor, inhotbar_enabled, ontopofhotbar_disabled, blank, enhanced_quality, quality_sd_enabled, quality_hd_disabled, blank, frame };
		enhancedEnabledInHotbarSD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_quality, quality_sd_enabled, quality_hd_disabled, blank, frame };
		//enhancedEnabledOnTopOfHotbarSD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_flavor, inhotbar_disabled, ontopofhotbar_enabled, blank, enhanced_quality, quality_sd_enabled, quality_hd_disabled, blank, frame };
		//enhancedEnabledInHotbarHD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_flavor, inhotbar_enabled, ontopofhotbar_disabled, blank, enhanced_quality, quality_sd_disabled, quality_hd_enabled, blank, frame };
		enhancedEnabledInHotbarHD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_quality, quality_sd_disabled, quality_hd_enabled, blank, frame };
		//enhancedEnabledOnTopOfHotbarHD = new BaseComponent[]{ frame, blank, intro, lightweight_disabled, enhanced_enabled, blank, enhanced_flavor, inhotbar_disabled, ontopofhotbar_enabled, blank, enhanced_quality, quality_sd_disabled, quality_hd_enabled, blank, frame };
	}

	private static void send( JavaPlugin plugin, PluginInstance pli, Player player ) {
		if( lightweightEnabled == null )
			build( pli );

		PlayerConfiguration configuration = PlayerConfiguration.get( player );

		if( configuration.getUI() == GameUI.Light ) {
			for( BaseComponent comp : lightweightEnabled )
				player.getPlayer().spigot().sendMessage( comp );
		} else if( configuration.getUI() == GameUI.InHotbar ) {
			switch( configuration.getQuality() ) {
				case HD:
					for( BaseComponent comp : enhancedEnabledInHotbarHD )
						player.getPlayer().spigot().sendMessage( comp );
					break;

				case SD:
				default:
					for( BaseComponent comp : enhancedEnabledInHotbarSD )
						player.getPlayer().spigot().sendMessage( comp );
					break;
			}
		}/* else if( configuration.getUI() == GameUI.OnTopOfHotbar ) {
			switch( configuration.getQuality() ) {
				case HD:
					for( BaseComponent comp : enhancedEnabledOnTopOfHotbarHD )
						player.getPlayer().spigot().sendMessage( comp );
					break;

				case SD:
				default:
					for( BaseComponent comp : enhancedEnabledOnTopOfHotbarSD )
						player.getPlayer().spigot().sendMessage( comp );
					break;
			}
		}*/
	}

	private static void enableUI( Main plugin, Player player, GameUI ui ) {
		if( !UIManager.validateUIVersion( player, ui ) ) {
			player.spigot().sendMessage( tooOldForEnhanced );
			return;
		}

		PlayerConfiguration.set( player, ui );

		player.spigot().sendMessage( preferenceSet );

		if( ui.isEnhanced )
			plugin.getResourcePackManager().sendResourcePack( player );
		else
			plugin.getResourcePackManager().revertResourcePack( player );
	}

	private static void enableUI( Main plugin, Player player ) {
		enableUI( plugin, player, PlayerConfiguration.get( player ).getUI() );
	}

	private static void enableLightweight( Main plugin, Player player ) {
		PlayerConfiguration.set( player, GameUIQuality.SD );
		enableUI( plugin, player, GameUI.Light );
	}

	private static void enableEnhanced( Main plugin, Player player ) {
		enableUI( plugin, player, GameUI.InHotbar );
	}

	private static void enableInHotbar( Main plugin, Player player ) {
		enableUI( plugin, player, GameUI.InHotbar );
	}

	/*private static void enableOnTopOfHotbar( Main plugin, Player player ) {
		enableUI( plugin, player, GameUI.OnTopOfHotbar );
	}*/

	private static void enableQuality( Main plugin, Player player, GameUIQuality quality ) {
		PlayerConfiguration.set( player, quality );
		enableUI( plugin, player );
	}

	private static void enableQualitySd( Main plugin, Player player ) {
		enableQuality( plugin, player, GameUIQuality.SD );
	}

	private static void enableQualityHd( Main plugin, Player player ) {
		enableQuality( plugin, player, GameUIQuality.HD );
	}

	/*private static void enablePersComMenu( JavaPlugin plugin, Player player ) {
		String uiPath = "warbases." + player.getUniqueId() + ".ui";
		if( plugin.getConfig().isSet( uiPath ) ) {
			GameUI ui = GameUI.fromString( plugin.getConfig().getString( uiPath ) );
			if( ui == GameUI.InHotbar || ui == GameUI.OnTopOfHotbar ) {
				plugin.getConfig().set( "warbases." + player.getUniqueId() + ".persistentcommenu", true );
				plugin.saveConfig();
				player.spigot().sendMessage( preferenceSet );
			}
		}
	}

	private static void disablePersComMenu( JavaPlugin plugin, Player player ) {
		plugin.getConfig().set( "warbases." + player.getUniqueId() + ".persistentcommenu", false );
		plugin.saveConfig();
		player.spigot().sendMessage( preferenceSet );
	}*/

	public static void handle( Main plugin, PluginInstance pli, String permissionPrefix, Player player, String[] args ) {
		if( args.length > 2 ) {
			switch( args[ 2 ] ) {
				case "enableLightweight":
					enableLightweight( plugin, player );
					break;
				case "enableEnhanced":
					enableEnhanced( plugin, player );
					break;
				case "enableInHotbar":
					enableInHotbar( plugin, player );
					break;
				/*case "enableOnTopOfHotbar":
					enableOnTopOfHotbar( plugin, player );
					break;*/
				case "qualitySd":
					enableQualitySd( plugin, player );
					break;
				case "qualityHd":
					enableQualityHd( plugin, player );
					break;
			}
		}

		send( plugin, pli, player );
	}

	/*public static GameUI get( Plugin plugin, Player player ) {
		GameUI sel;

		String path = "warbases." + player.getUniqueId() + ".ui";

		if( plugin.getConfig().isSet( path ) ) {
			sel = GameUI.fromString( plugin.getConfig().getString( path ) );
			if( sel == null )
				sel = GameUI.Light;
		} else {
			sel = GameUI.Light;
		}

		return sel;
	}

	public static void set( Plugin plugin, Player player, GameUI ui ) {
		String path = "warbases." + player.getUniqueId() + ".ui";
		plugin.getConfig().set( path, ui.toString() );
		plugin.saveConfig();
	}

	public static GameUIQuality getQuality( Plugin plugin, Player player ) {
		GameUIQuality sel;

		String path = "warbases." + player.getUniqueId() + ".uiquality";

		if( plugin.getConfig().isSet( path ) ) {
			sel = GameUIQuality.fromString( plugin.getConfig().getString( path ) );
			if( sel == null )
				sel = GameUIQuality.SD;
		} else {
			sel = GameUIQuality.SD;
		}

		return sel;
	}

	public static void set( Plugin plugin, Player player, GameUIQuality quality ) {
		String path = "warbases." + player.getUniqueId() + ".uiquality";
		plugin.getConfig().set( path, quality.toString() );
		plugin.saveConfig();
	}*/
}
