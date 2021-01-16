package fr.horgeon.prodrivers.games.warbases.ui;

import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.configuration.players.PlayerConfiguration;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesGameSection;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesLobbySection;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUIKey;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUIQuality;
import fr.prodrivers.bukkit.commons.sections.IProdriversSection;
import fr.prodrivers.bukkit.commons.sections.SectionManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ResourcePackManager implements Listener {
	private static final String configPath = "config.gui.enhanced_ui.resourcepacks";

	private Main plugin;
	private HashMap<GameUIKey, Map.Entry<String, String>> resourcePacks = new HashMap<>();

	private boolean enhancedUIAvailable = false;

	public ResourcePackManager( Main plugin ) {
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		this.plugin = plugin;
		load();
	}

	public void reload() {
		load();
	}

	private void addUI( ConfigurationSection uiSect, GameUI ui, GameUIQuality quality ) {
		if( uiSect.isSet( "hash" ) && uiSect.isSet( "url" ) ) {
			this.resourcePacks.put(
					new GameUIKey( ui, quality ),
					new AbstractMap.SimpleEntry<>( uiSect.getString( "hash" ), uiSect.getString( "url" ) )
			);
			System.out.println( "[WarBases] Successfully loaded resource pack for UI " + ui.toString() + ( quality != null ? ", quality " + quality.toString() : ", no quality" ) );
		} else {
			System.err.println( "[WarBases] Ill-formatted entry for UI " + ui.toString() + ", quality " + quality.toString() + ": missing " + ( !uiSect.isSet( "hash" ) ? "hash," : "" ) + ( !uiSect.isSet( "url" ) ? "url" : "" ) );
		}
	}

	private void load() {
		this.resourcePacks.clear();

		ConfigurationSection config = this.plugin.getConfig().getConfigurationSection( configPath );

		for( String uiStr : config.getKeys( false ) ) {
			ConfigurationSection uiSect = config.getConfigurationSection( uiStr );
			GameUI ui = GameUI.fromString( uiStr );

			if( ui != null ) {
				if( ui.hasQuality ) {
					for( String qualityStr : uiSect.getKeys( false ) ) {
						GameUIQuality quality = GameUIQuality.fromString( qualityStr );

						if( quality != null ) {
							addUI( uiSect.getConfigurationSection( qualityStr ), ui, quality );
						} else {
							System.err.println( "[WarBases] Invalid entry in resources packs configuration: UI " + uiStr + " with quality " + qualityStr + " is invalid." );
						}
					}
				} else {
					addUI( uiSect, ui, null );
				}
			} else if( uiStr.equalsIgnoreCase( "empty" ) ) {
				addUI( uiSect, GameUI.Empty, null );
			} else {
				System.err.println( "[WarBases] Invalid entry in resources packs configuration: UI " + uiStr + " is invalid." );
			}
		}

		enhancedUIAvailable = checkLoad();

		if( !enhancedUIAvailable )
			System.err.println( "[WarBases] WarBases Enhanced UI unavailable due to incorrect resourcepack configuration. Make sure every enhanced UI and every quality have a correct configuration, and that the empty resource pack entry is available." );
	}

	private boolean checkLoad() {
		for( GameUI ui : GameUI.values() ) {
			if( ui.isEnhanced ) {
				if( ui.hasQuality ) {
					for( GameUIQuality quality : GameUIQuality.values() ) {
						if( !resourcePacks.containsKey( new GameUIKey( ui, quality ) ) ) {
							return false;
						}
					}
				} else {
					if( !resourcePacks.containsKey( new GameUIKey( ui, null ) ) ) {
						return false;
					}
				}
			}
		}

		return resourcePacks.containsKey( new GameUIKey( GameUI.Empty, null ) );
	}

	public boolean isEnhancedUIAvailable() {
		return enhancedUIAvailable;
	}

	public void revertToScoreboard( Player p, boolean silent ) {
		PlayerConfiguration.set( this.plugin, p, GameUI.Light );
		if( !silent )
			p.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).revert_to_scoreboard );
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChangeStateResourcePack( PlayerResourcePackStatusEvent e ) {
		if( e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || e.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD )
			revertToScoreboard( e.getPlayer(), false );
	}

	public void sendResourcePack( Player p ) {
		//if( plugin.getLobby().isInLobby( p ) ) {
		IProdriversSection current = SectionManager.getCurrentSection( p );
		if( current == null || current.getName().equals( WarbasesLobbySection.name ) || current.getName().equals( WarbasesGameSection.name ) ) {
			System.out.println( "[WarBases] Sending resource pack to player" );

			PlayerConfiguration configuration = PlayerConfiguration.get( this.plugin, p );

			System.out.println( "-> UI " + configuration.getUI() + " with quality " + configuration.getQuality() );

			if( configuration.getUI().isEnhanced ) {
				Map.Entry<String, String> rp = this.resourcePacks.get( new GameUIKey( configuration.getUI(), ( configuration.getUI().hasQuality ? configuration.getQuality() : null ) ) );
				if( rp != null ) {
					if( rp.getValue() != null ) {
						System.out.println( "-> RP value: " + rp.getValue() );
						p.setResourcePack( rp.getValue() );
					} else {
						System.out.println( "-> Null RP value" );
						revertToScoreboard( p.getPlayer(), true );
					}
				} else {
					p.sendMessage( ( (EMessagesConfig) plugin.getPluginInstance().getMessagesConfig() ).ui_enhanced_not_available );
					PlayerConfiguration.set( this.plugin, p, GameUI.Light );
				}
			}
		}
	}

	public void revertResourcePack( Player p ) {
		if( PlayerConfiguration.get( this.plugin, p ).getUI() != GameUI.Light ) {
			p.setResourcePack( this.resourcePacks.get( new GameUIKey( GameUI.Empty, null ) ).getValue() );
		}
	}

	public void join( Player player ) {
		sendResourcePack( player );
	}

	public void leave( Player player ) {
		revertResourcePack( player );
	}
}
