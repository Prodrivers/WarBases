package fr.horgeon.prodrivers.games.warbases.ui.game.enhanced;

import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.ui.game.CommunicationMessage;
import fr.horgeon.prodrivers.games.warbases.ui.game.EnhancedUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemPreparator {
	private EnhancedUI ui;
	private Material baseItem;

	public ItemPreparator( EnhancedUI ui, Material baseItem ) {
		this.ui = ui;
		this.baseItem = baseItem;
		prepare();
	}

	private void prepare() {
		ui.redHUDItems.clear();
		ui.blueHUDItems.clear();
		ui.wallHUDItems.clear();
		prepareRed();
		prepareBlue();
		prepareWall();
		/*prepareOpenAnimation();
		prepareVictoryAnimation();
		prepareDefeatAnimation();*/
		prepareCommunicationMessages();
	}

	private void prepareOpenAnimation() {
		ItemStack item;
		for( int i = 0; i <= 92; i++ ) {
			item = new ItemStack( this.baseItem, 1, (short) ( 300 + i ) );
			ui.warbasesOpenItems.put( i, item );
			prepareItem( item );
		}
	}

	private void prepareVictoryAnimation() {
		ItemStack item;
		for( int i = 0; i <= 81; i++ ) {
			item = new ItemStack( this.baseItem, 1, (short) ( 400 + i ) );
			ui.victoryItems.put( i, item );
			prepareItem( item );
		}
	}

	private void prepareDefeatAnimation() {
		ItemStack item;
		for( int i = 0; i <= 81; i++ ) {
			item = new ItemStack( this.baseItem, 1, (short) ( 500 + i ) );
			ui.defeatItems.put( i, item );
			prepareItem( item );
		}
	}

	private void prepareWall() {
		ItemStack item;
		for( int i = 0; i <= 12; i++ ) {
			item = new ItemStack( baseItem, 1, (short) ( 204 + i ) );
			ui.wallHUDItems.put( i, item );
			prepareItem( item );
		}

		/*ui.wallHUDItems.put( ui.WALL_FALLING, new ItemStack( ui.baseItem, 1, (short) 214 ) );
		ui.wallHUDItems.put( ui.WALL_FALLED, new ItemStack( ui.baseItem, 1, (short) 215 ) );*/
	}

	private void prepareRed() {
		ItemStack item;
		for( int i = 0; i <= 100; i++ ) {
			item = new ItemStack( this.baseItem, 1, (short) ( 2 + i ) );
			ui.redHUDItems.put( i, item );
			prepareItem( item );
		}
	}

	private void prepareBlue() {
		ItemStack item;
		for( int i = 0; i <= 100; i++ ) {
			item = new ItemStack( baseItem, 1, (short) ( 103 + i ) );
			ui.blueHUDItems.put( i, item );
			prepareItem( item );
		}
	}

	private void prepareCommunicationMessages() {
		ItemStack item;
		for( int i = 0; i < CommunicationMessage.values().length; i++ ) {
			item = new ItemStack( baseItem, 1, CommunicationMessage.values()[ i ].getItemDamageValue() );
			ui.wallHUDItems.put( Constants.UI_ENHANCED_ITEM_MESSAGES_BASE + i, item );
			prepareItem( item );
		}
	}

	private void prepareItem( ItemStack item ) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( "" );
		meta.spigot().setUnbreakable( true );
		meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
		meta.addItemFlags( ItemFlag.HIDE_UNBREAKABLE );
		item.setItemMeta( meta );
	}
}
