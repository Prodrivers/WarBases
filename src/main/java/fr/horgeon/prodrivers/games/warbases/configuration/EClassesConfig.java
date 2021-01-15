package fr.horgeon.prodrivers.games.warbases.configuration;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.config.ClassesConfig;

public class EClassesConfig extends ClassesConfig {

	public EClassesConfig( JavaPlugin plugin ) {
		super( plugin, true );
		this.getConfig().options().header( "Used for saving classes. Default class:" );
		this.getConfig().addDefault( "config.kits.default.name", "Default" );
		this.getConfig().addDefault( "config.kits.default.items", "267#KNOCKBACK:1*1" );
		this.getConfig().addDefault( "config.kits.default.lore", "The Default class." );
		this.getConfig().addDefault( "config.kits.default.requires_money", false );
		this.getConfig().addDefault( "config.kits.default.requires_permission", false );
		this.getConfig().addDefault( "config.kits.default.money_amount", 100 );
		this.getConfig().addDefault( "config.kits.default.permission_node", MinigamesAPI.getAPI().getPermissionKitPrefix() + ".default" );

		this.getConfig().options().copyDefaults( true );
		this.saveConfig();
	}

}
