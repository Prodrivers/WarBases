package fr.horgeon.prodrivers.games.warbases.ui;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.IconMenu;
import com.comze_instancelabs.minigamesapi.util.Validator;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamsSelectorUI {
	private Main plugin = null;
	private PluginInstance pli = null;
	private HashMap<String, IconMenu> lastIconMenu = new HashMap<>();
	private ArrayList<String> redLore = new ArrayList<>();
	private ArrayList<String> blueLore = new ArrayList<>();

	private Material teamSelectorItem;

	public TeamsSelectorUI( Main plugin, PluginInstance pli ) {
		this.plugin = plugin;
		this.pli = pli;

		load();

		redLore.add( ( (EMessagesConfig) pli.getMessagesConfig() ).team_join_lore.replaceAll( "<teamColor>", ArenaTeam.Red.toChatColor().toString() ).replaceAll( "<team>", ArenaTeam.Red.toName( (EMessagesConfig) pli.getMessagesConfig() ) ) );
		blueLore.add( ( (EMessagesConfig) pli.getMessagesConfig() ).team_join_lore.replaceAll( "<teamColor>", ArenaTeam.Blue.toChatColor().toString() ).replaceAll( "<team>", ArenaTeam.Blue.toName( (EMessagesConfig) pli.getMessagesConfig() ) ) );
	}

	public void load() {
		this.teamSelectorItem = Material.getMaterial( plugin.getConfig().getString( "config.selection_items.teams_selection_item" ) );
	}

	public void giveItem( Player player ) {
		giveItem( player, null );
	}

	public void giveItem( Player player, ArenaTeam team ) {
		ItemStack teamsItem = new ItemStack( this.teamSelectorItem, 1, ( team != null ? team.toColorByte() : (byte) 0 ) );
		if( teamsItem.getType() != Material.AIR ) {
			ItemMeta teamsItemMeta = teamsItem.getItemMeta();
			teamsItemMeta.setDisplayName( ( (EMessagesConfig) pli.getMessagesConfig() ).teams_item );
			teamsItem.setItemMeta( teamsItemMeta );
		}
		player.getInventory().setItem( 4, teamsItem );
		player.updateInventory();
	}

	public void modifyItem( Player player, ArenaTeam team ) {
		HashMap<Integer, ? extends ItemStack> wools = player.getInventory().all( Material.WOOL );
		for( Map.Entry<Integer, ? extends ItemStack> entry : wools.entrySet() ) {
			if( entry.getValue().getItemMeta().getDisplayName().equals( ( (EMessagesConfig) this.pli.getMessagesConfig() ).teams_item ) ) {
				MaterialData data = entry.getValue().getData();
				if( data instanceof Wool ) {
					Wool woolData = (Wool) data;
					woolData.setColor( team.toDyeColor() );
					entry.getValue().setData( data );
				}
			}
		}
	}

	public void openGUI( final String playerName ) {
		final TeamsSelectorUI ts = this;
		IconMenu iconMenu;

		if( !Validator.isPlayerOnline( playerName ) ) {
			return;
		}

		final Player player = Bukkit.getPlayerExact( playerName );

		if( this.lastIconMenu.containsKey( playerName ) ) {
			iconMenu = this.lastIconMenu.get( playerName );
		} else {
			iconMenu = new IconMenu( ( (EMessagesConfig) this.pli.getMessagesConfig() ).teams_item, 9, new IconMenu.OptionClickEventHandler() {
				@Override
				public void onOptionClick( IconMenu.OptionClickEvent event ) {
					if( event.getPlayer().getName().equalsIgnoreCase( playerName ) ) {
						if( ts.pli.containsGlobalPlayer( playerName ) ) {
							Arena arena = ts.pli.getArenaByGlobalPlayer( playerName );
							if( arena != null && arena instanceof EArena ) {
								final String d = event.getName();
								final ArenaPlayer p = ( (EArena) arena ).getArenaManager().getPlayer( event.getPlayer() );
								ts.setTeam( (EArena) arena, d, p );
							}
						}
					}
					event.setWillClose( true );
				}
			}, this.plugin );

			iconMenu.setOption( 0, getTeamIcon( ArenaTeam.Red ), ArenaTeam.Red.toDisplayName( (EMessagesConfig) pli.getMessagesConfig() ), redLore.get( 0 ) );
			iconMenu.setOption( 1, getTeamIcon( ArenaTeam.Blue ), ArenaTeam.Blue.toDisplayName( (EMessagesConfig) pli.getMessagesConfig() ), blueLore.get( 0 ) );
		}

		iconMenu.open( player );
		this.lastIconMenu.put( playerName, iconMenu );
	}

	public ItemStack getTeamIcon( ArenaTeam team ) {
		ItemStack is = new ItemStack( Material.WOOL, 1, team.toColorByte() );
		/*Wool data = ( ( Wool ) is.getData() );
		data.setColor( team.toDyeColor() );
		is.setData( data );*/
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName( team.toDisplayName( (EMessagesConfig) pli.getMessagesConfig() ) );
		if( team == ArenaTeam.Red )
			meta.setLore( redLore );
		else if( team == ArenaTeam.Blue )
			meta.setLore( blueLore );
		is.setItemMeta( meta );
		return is;
	}

	public void setTeam( EArena arena, String eventItemName, ArenaPlayer player ) {
		setTeam( arena, ArenaTeam.fromDisplayName( (EMessagesConfig) pli.getMessagesConfig(), eventItemName ), player );
	}

	public void setTeam( EArena arena, ArenaTeam team, ArenaPlayer player ) {
		if( team != null ) {
			player.setTeam( team );
			giveItem( player.getPlayer(), team );
			arena.getUIManager().get( player ).joinTeam( player, team );
		}
	}

	public boolean isTeamSelectorItem( ItemStack item ) {
		return item.getType().equals( this.teamSelectorItem );
	}
}
