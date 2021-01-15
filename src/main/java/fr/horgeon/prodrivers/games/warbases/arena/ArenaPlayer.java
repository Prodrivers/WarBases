package fr.horgeon.prodrivers.games.warbases.arena;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import fr.horgeon.bukkit.packetsutilities.Sounds;
import fr.horgeon.bukkit.packetsutilities.SoundCategory;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.classes.GameClass;
import fr.horgeon.prodrivers.games.warbases.configuration.players.PlayerConfiguration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class ArenaPlayer {
	private Player player;
	private int lives;
	private PlayerConfiguration configuration;
	private GameClass kit;

	private ArenaTeam team;

	private boolean freezed = false;

	private boolean dead = false;

	private HashMap<ArenaTeam, Boolean> isInCheckpoint = new HashMap<>();

	private int kills = 0;

	private boolean comMenu = false;

	private ArenaOutcome outcome;

	private long nextMessageTime = 0;
	private long resetMessageTime = 0;
	private int messageCount = 0;

	private boolean inParty = false;

	public ArenaPlayer( JavaPlugin plugin, Player player, int lives, ArenaTeam team ) {
		this.player = player;
		this.lives = lives;
		this.team = team;

		this.configuration = PlayerConfiguration.get( this );
	}

	void loadKit( PluginInstance pli ) {
		this.kit = GameClass.get( pli, this );
	}

	public void decreaseLive() {
		if( this.lives > 0 )
			this.lives--;
	}

	public int getLives() {
		return this.lives;
	}

	public boolean canRespawn() {
		return ( this.lives > 0 || this.lives == -1 );
	}

	public ArenaTeam getTeam() {
		return this.team;
	}

	public void setTeam( ArenaTeam team ) {
		this.team = team;
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.player.getName();
	}

	public Location getLocation() {
		return this.player.getLocation();
	}

	public Player getKiller() {
		return this.player.getKiller();
	}

	public void sendMessage( String msg ) {
		player.sendMessage( msg );
	}

	public void setScoreboard( Scoreboard sc ) {
		this.player.setScoreboard( sc );
	}

	public PlayerInventory getInventory() {
		return this.player.getInventory();
	}

	public void updateInventory() {
		this.player.updateInventory();
	}

	public UUID getUniqueId() {
		return this.player.getUniqueId();
	}

	public void playSound( String s, SoundCategory category, float sourceVolume, float pitch ) {
		Sounds.playSound( player, player.getLocation(), s, category, sourceVolume, pitch );
	}

	public void playSound( String s, SoundCategory category, float sourceVolume ) {
		Sounds.playSound( player, player.getLocation(), s, category, sourceVolume, 1.0f );
	}

	public void playSound( Location loc, String s, SoundCategory category, float sourceVolume ) {
		Sounds.playSound( player, loc, s, category, sourceVolume, 1.0f );
	}

	public void stopSound( String s ) {
		Sounds.stopSound( player, s );
	}

	public void stopSound( String s, SoundCategory category ) {
		Sounds.stopSound( player, s, category );
	}

	public void setResourcePack( String url ) {
		this.player.setResourcePack( url );
	}

	public PlayerConfiguration getConfiguration() {
		return this.configuration;
	}

	public void freeze() {
		this.freezed = true;
	}

	public void unfreeze() {
		this.freezed = false;
	}

	public boolean freezed() {
		return this.freezed;
	}

	public double getHealth() {
		return this.player.getHealth();
	}

	public void setHealth( double v ) {
		this.player.setHealth( v );
	}

	public void die() {
		this.dead = true;
	}

	public void revive() {
		this.dead = false;
	}

	public boolean isDead() {
		return this.dead;
	}

	public void teleport( Location location ) {
		this.player.teleport( location );
	}

	public GameMode getGameMode() {
		return this.player.getGameMode();
	}

	public void setGameMode( GameMode gamemode ) {
		this.player.setGameMode( gamemode );
	}

	public void addPotionEffect( PotionEffect potionEffect ) {
		this.player.addPotionEffect( potionEffect );
	}

	public void addPotionEffect( PotionEffect potionEffect, boolean force ) {
		this.player.addPotionEffect( potionEffect, force );
	}

	public void removePotionEffect( PotionEffectType potionEffectType ) {
		this.player.removePotionEffect( potionEffectType );
	}

	public void inCheckpoint( ArenaTeam team ) {
		this.isInCheckpoint.put( team, true );
	}

	public void outCheckpoint( ArenaTeam team ) {
		this.isInCheckpoint.put( team, false );
	}

	public boolean isInCheckpoint( ArenaTeam team ) {
		return this.isInCheckpoint.get( team );
	}

	public void setLevel( int level ) {
		this.player.setLevel( level );
	}

	public void setExperience( float exp ) {
		this.player.setExp( exp );
	}

	public void addKill() {
		this.kills++;
	}

	public int getKills() {
		return this.kills;
	}

	public void toggleComMenu() {
		this.comMenu ^= true;
	}

	public boolean isComMenuOpened() {
		return this.comMenu;
	}

	public boolean canSendMessage() {
		long now = System.nanoTime();

		if( resetMessageTime - now < 0 ) {
			messageCount = 0;
		}
		messageCount++;

		if( messageCount > Constants.UI_MESSAGES_MAX_CONSECUTIVE ) {
			resetMessageTime = now + ( Constants.UI_MESSAGES_TIME_BETWEEN_AFTER_MAX * 1000000L );
		} else {
			if( nextMessageTime - now < 0 ) {
				nextMessageTime = now +  ( Constants.UI_MESSAGES_TIME_BETWEEN * 1000000L );
				return true;
			}
		}

		return false;
	}

	void setOutcome( PluginInstance pli, Arena arena, ArenaOutcome outcome ) {
		this.outcome = outcome;
		switch( this.outcome ) {
			case LOSE:
				pli.global_lost.put( getName(), arena );
				break;
			case DRAW:
				final com.comze_instancelabs.minigamesapi.ArenaPlayer internalAp = com.comze_instancelabs.minigamesapi.ArenaPlayer.getPlayerInstance( getName() );
				internalAp.setNoReward( true );
				break;
		}
	}

	public ArenaOutcome outcome() {
		return this.outcome;
	}

	void setInParty( boolean inParty ) {
		this.inParty = inParty;
	}

	public boolean isInParty() {
		return this.inParty;
	}

	public void giveKit() {
		if( kit != null )
			kit.giveKit( this );
	}

	public GameClass getKit() {
		return this.kit;
	}

	Integer getNumberOfConqueredCheckpoints( Main plugin ) {
		try {
			PreparedStatement req = plugin.getMySQL().prepareStatement( "SELECT conquered_checkpoints FROM WarBases WHERE uuid = ?;" );
			req.setString( 1, player.getUniqueId().toString() );
			ResultSet rs = req.executeQuery();

			if( rs.next() ) {
				return rs.getInt( 1 );
			}
		} catch( SQLException ex ) {
			throw new IllegalStateException( "Error while getting number of conquered checkpoitns for player " + player.getName() + ": " + ex.getLocalizedMessage() );
		}

		return null;
	}

	void setNumberOfConqueredCheckpoints( Main plugin, int value ) {
		try {
			PreparedStatement req = plugin.getMySQL().prepareStatement( "UPDATE WarBases SET conquered_checkpoints = ? WHERE uuid = ?;" );
			req.setInt( 1, value );
			req.setString( 2, player.getUniqueId().toString() );
			req.executeUpdate();
		} catch( SQLException ex ) {
			throw new IllegalStateException( "Error while getting number of conquered checkpoitns for player " + player.getName() + ": " + ex.getLocalizedMessage() );
		}
	}
}
