package fr.horgeon.prodrivers.games.warbases;

import com.comze_instancelabs.minigamesapi.*;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.sql.MySQL;
import com.comze_instancelabs.minigamesapi.util.AClass;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;
import fr.horgeon.prodrivers.games.warbases.arena.*;
import fr.horgeon.prodrivers.games.warbases.arena.overtime.OvertimeListener;
import fr.horgeon.prodrivers.games.warbases.classes.GameClass;
import fr.horgeon.prodrivers.games.warbases.commands.ECommandHandler;
import fr.horgeon.prodrivers.games.warbases.configuration.EArenasConfig;
import fr.horgeon.prodrivers.games.warbases.configuration.EClassesConfig;
import fr.horgeon.prodrivers.games.warbases.configuration.EDefaultConfig;
import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesGameSection;
import fr.horgeon.prodrivers.games.warbases.sections.WarbasesLobbySection;
import fr.horgeon.prodrivers.games.warbases.ui.ResourcePackManager;
import fr.horgeon.prodrivers.games.warbases.ui.TeamsSelectorUI;
import fr.horgeon.prodrivers.games.warbases.ui.listeners.GameUIListener;
import fr.prodrivers.bukkit.commons.sections.SectionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Main extends JavaPlugin implements Listener {
	private static MinigamesAPI api = null;
	private PluginInstance pli = null;
	private ECommandHandler cmdhandler = new ECommandHandler();

	private TeamsSelectorUI teamsSelectorUI = null;
	private ResourcePackManager resourcePackManager = null;
	//private Lobby lobby = null;

	private WarbasesLobbySection lobbySection;
	private WarbasesGameSection gameSection;

	private Connection mysql;

	public void onEnable() {
		api = MinigamesAPI.setupAPI( this, "warbases", EArena.class, new EArenasConfig( this ), new EMessagesConfig( this ), new EClassesConfig( this ), new StatsConfig( this, false ), new EDefaultConfig( this, false ), true );
		pli = api.getPluginInstance( this );

		initDatabase();

		pli.addArenas( loadArenas( this, pli.getArenasConfig() ) );
		Bukkit.getPluginManager().registerEvents( this, this );
		pli.scoreboardManager = new EArenaScoreboard( this );
		pli.scoreboardLobbyManager = new EArenaLobbyScoreboard( this );
		pli.arenaSetup = new EArenaSetup();

		new GameUIListener( this );
		new OvertimeListener( this );

		EArenaListener listener = new EArenaListener( this, pli, "warbases" );
		pli.setArenaListener( listener );
		MinigamesAPI.registerArenaListenerLater( this, listener );
		pli.setAchievementGuiEnabled( true );

		this.teamsSelectorUI = new TeamsSelectorUI( this, pli );
		this.resourcePackManager = new ResourcePackManager( this );
		//this.lobby = new Lobby( this );
		this.lobbySection = new WarbasesLobbySection( this );
		this.gameSection = new WarbasesGameSection( this );

		SectionManager.register( this.lobbySection );
		SectionManager.register( this.gameSection );

		Bukkit.getScheduler().runTaskLater( this, new Runnable() {
			@Override
			public void run() {
				GameClass.load( pli );
			}
		}, 30L );

		this.getConfig().options().copyDefaults( true );
		this.saveConfig();

		boolean continue_ = false;
		for( Method method : pli.getArenaAchievements().getClass().getMethods() ) {
			if( method.getName().equalsIgnoreCase( "addDefaultAchievement" ) ) {
				continue_ = true;
			}
		}
		if( continue_ ) {
			pli.getArenaAchievements().addDefaultAchievement( "capture_hundred_checkpoints_all_time", "Capture 100 checkpoints all-time!", 1000 );
			pli.getAchievementsConfig().getConfig().options().copyDefaults( true );
			pli.getAchievementsConfig().saveConfig();
		}
	}

	private void initDatabase() throws IllegalStateException {
		try {
			Field f = pli.getSQLInstance().getClass().getDeclaredField( "MySQL" );
			f.setAccessible( true );
			MySQL mysqldb = (MySQL) f.get( pli.getSQLInstance() );
			assert mysqldb != null;
			this.mysql = mysqldb.open();
			assert this.mysql != null;
		} catch( NoSuchFieldException | IllegalAccessException | AssertionError ex ) {
			throw new IllegalStateException( "Database initialization failed. Either MySQL is disabled or connection was not successfully established." );
		}
	}

	private static ArrayList<Arena> loadArenas( JavaPlugin plugin, ArenasConfig cf ) {
		ArrayList<Arena> ret = new ArrayList<>();
		FileConfiguration config = cf.getConfig();
		if( !config.isSet( "arenas" ) ) {
			return ret;
		}
		for( String arena : config.getConfigurationSection( ArenaConfigStrings.ARENAS_PREFIX ).getKeys( false ) ) {
			if( Validator.isArenaValid( plugin, arena, cf.getConfig() ) ) {
				ret.add( initArena( plugin, arena ) );
			}
		}
		return ret;
	}

	public static EArena initArena( JavaPlugin javaPlugin, String arena ) {
		Main plugin = (Main) javaPlugin;
		EArena a = new EArena( plugin, arena );
		ArenaSetup s = plugin.getPluginInstance().arenaSetup;
		a.init( Util.getSignLocationFromArena( plugin, arena ), Util.getAllSpawns( plugin, arena ), Util.getMainLobby( plugin ), Util.getComponentForArena( plugin, arena, "lobby" ), s.getPlayerCount( plugin, arena, true ), s.getPlayerCount( plugin, arena, false ), s.getArenaVIP( plugin, arena ) );
		return a;
	}

	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		String permissionPrefix = api.getPermissionGamePrefix( "WarBases" );
		if( cmdhandler.handleSupplementaryArgs( this, pli, permissionPrefix, sender, cmd, label, args ) )
			return true;
		return cmdhandler.handleArgs( this, permissionPrefix, "/" + cmd.getName(), sender, args );
	}

	public static MinigamesAPI getAPI() {
		return api;
	}

	public PluginInstance getPluginInstance() {
		return this.pli;
	}

	public TeamsSelectorUI getTeamsSelectorUI() {
		return this.teamsSelectorUI;
	}

	public ResourcePackManager getResourcePackManager() {
		return this.resourcePackManager;
	}

	/*@Deprecated
	public Lobby getLobby() {
		return this.lobby;
	}*/

	public WarbasesLobbySection getLobbySection() {
		return this.lobbySection;
	}

	public WarbasesGameSection getGameSection() {
		return this.gameSection;
	}

	public Connection getMySQL() {
		return mysql;
	}

	/*public boolean leave( Player player ) {
		if( this.pli.containsGlobalPlayer( player.getName() ) ) {
			String playername = player.getName();

			Arena a = pli.getArenaByGlobalPlayer( player.getName() );
			if( a.getArcadeInstance() != null ) {
				a.getArcadeInstance().leaveArcade( playername, true );
			}

			a.leavePlayer( playername, false, false );
			return true;
		} else {
			lobby.leave( player );
			return true;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit( PlayerQuitEvent event ) {
		lobby.leave( event.getPlayer() );
	}

	@EventHandler
	public void onHub( HubTeleportEvent event ) {
		leave( event.getPlayer() );
	}*/

	@Override
	public void onDisable() {
		super.onDisable();
		for( Arena a_ : this.pli.getArenas() ) {
			( (EArena) a_ ).getWallManager().rebuild();
		}
		this.pli.reloadAllArenas();
		try {
			this.mysql.close();
		} catch( SQLException ex ) {}
	}
}
