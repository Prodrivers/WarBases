package fr.horgeon.prodrivers.games.warbases.classes;

import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.AClass;
import com.comze_instancelabs.minigamesapi.util.Util;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class GameClass {
	private static Map<String, GameClass> classes = new HashMap<>();

	public final Map<Enchantment, Integer> helmetEnchantments;
	public final ItemStack chestplate;
	public final ItemStack leggings;
	public final ItemStack boots;
	private final List<ItemStack> items;

	GameClass( PluginInstance pli, String className ) {
		String helmetEnchantsStr = pli.getClassesConfig().getConfig().getString( "config.kits." + className + ".helmetEnchants" );
		String chestplateStr = pli.getClassesConfig().getConfig().getString( "config.kits." + className + ".chestplate" );
		String leggingsStr = pli.getClassesConfig().getConfig().getString( "config.kits." + className + ".leggings" );
		String bootsStr = pli.getClassesConfig().getConfig().getString( "config.kits." + className + ".boots" );
		String itemsStr = pli.getClassesConfig().getConfig().getString( "config.kits." + className + ".items" );

		List<ItemStack> chestplateIS = Util.parseItems( chestplateStr );
		List<ItemStack> leggingsIS = Util.parseItems( leggingsStr );
		List<ItemStack> bootsIS = Util.parseItems( bootsStr );
		chestplate = ( chestplateIS.size() > 0 ? chestplateIS.get( 0 ) : null );
		leggings = ( leggingsIS.size() > 0 ? leggingsIS.get( 0 ) : null );
		boots = ( bootsIS.size() > 0 ? bootsIS.get( 0 ) : null );
		items = Util.parseItems( itemsStr );

		this.helmetEnchantments = parseEnchantments( helmetEnchantsStr );
	}

	private static Map<Enchantment, Integer> parseEnchantments( String enchantmentsConcatenedStr ) {
		String[] enchantmentsStr = enchantmentsConcatenedStr.split( "#" );
		Map<Enchantment, Integer> enchantments = new LinkedHashMap<>();

		for( String enchantmentStr : enchantmentsStr ) {
			String[] enchantmentPropertiesStr = enchantmentStr.split( ":" );

			if( enchantmentPropertiesStr.length > 1 ) {
				Enchantment enchantment = Enchantment.getByName( enchantmentPropertiesStr[ 0 ] );

				if( enchantment == null )
					continue;

				int level;

				try {
					level = Integer.parseInt( enchantmentPropertiesStr[ 1 ] );
				} catch( NumberFormatException ex ) {
					level = 1;
				}

				enchantments.put( enchantment, level );
			}
		}

		return enchantments;
	}

	private ItemStack getColoredLeatherArmor( ArenaPlayer player, Material armor ) {
		ItemStack leatherPiece = new ItemStack( armor, 1 );
		LeatherArmorMeta leatherPieceMeta = (LeatherArmorMeta) leatherPiece.getItemMeta();
		leatherPieceMeta.setColor( player.getTeam().toColor() );
		leatherPiece.setItemMeta( leatherPieceMeta );
		return leatherPiece;
	}

	private void addDefaultHelmet( ArenaPlayer player ) {
		ItemStack helmet = getColoredLeatherArmor( player, Material.LEATHER_HELMET );
		helmet.addUnsafeEnchantments( this.helmetEnchantments );
		player.getInventory().setHelmet( helmet );
	}

	private void addDefaultChestplate( ArenaPlayer player ) {
		player.getInventory().setChestplate( getColoredLeatherArmor( player, Material.LEATHER_CHESTPLATE ) );
	}

	private void addDefaultLeggings( ArenaPlayer player ) {
		player.getInventory().setLeggings( getColoredLeatherArmor( player, Material.LEATHER_LEGGINGS ) );
	}

	private void addDefaultBoots( ArenaPlayer player ) {
		player.getInventory().setBoots( getColoredLeatherArmor( player, Material.LEATHER_BOOTS ) );
	}

	public void giveKit( ArenaPlayer player ) {
		player.getInventory().clear();
		player.updateInventory();

		addDefaultHelmet( player );

		if( this.chestplate != null ) {
			player.getInventory().setChestplate( this.chestplate );
		} else {
			addDefaultChestplate( player );
		}

		if( this.leggings != null ) {
			player.getInventory().setLeggings( this.leggings );
		} else {
			addDefaultLeggings( player );
		}

		if( this.boots != null ) {
			player.getInventory().setBoots( this.boots );
		} else {
			addDefaultBoots( player );
		}

		int i = 0;
		for(ItemStack item : items) {
			player.getInventory().setItem( i, item );
			i++;
			if(i == 2) {
				i = 7; // Skip through UI
			}
		}

		player.updateInventory();
	}

	public static void load( PluginInstance pli ) {
		classes.clear();

		for( AClass aClass : pli.getAClasses().values() ) {
			classes.put( aClass.getInternalName(), new GameClass( pli, aClass.getInternalName() ) );
		}
	}

	public static GameClass get( String className ) {
		return classes.get( className );
	}

	public static GameClass get( PluginInstance pli, ArenaPlayer player ) {
		pli.getClassesHandler().getClass( player.getName() );
		AClass currentClass = pli.getPClasses().get( player.getName() );
		return get( currentClass.getInternalName() );
	}
}
