package fr.horgeon.prodrivers.games.warbases.configuration.players;

import com.mongodb.client.model.Updates;
import fr.horgeon.prodrivers.games.warbases.Constants;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUI;
import fr.horgeon.prodrivers.games.warbases.ui.game.GameUIQuality;
import fr.prodrivers.bukkit.commons.storage.StorageProvider;
import org.bson.Document;
import org.bukkit.entity.Player;

public class PlayerConfiguration {
	private GameUI ui;
	private GameUIQuality quality;

	private PlayerConfiguration( Player player ) {
		load( player );
	}

	private void load( Player player ) {
		this.ui = PlayerConfiguration.getUI( player );
		this.quality = PlayerConfiguration.getQuality( player );
	}

	public GameUI getUI() {
		return this.ui;
	}

	public GameUIQuality getQuality() {
		return this.quality;
	}

	public static PlayerConfiguration get( ArenaPlayer player ) {
		return get( player.getPlayer() );
	}

	public static PlayerConfiguration get( Player player ) {
		return new PlayerConfiguration( player );
	}

	private static GameUI getUI( Player player ) {
		GameUI sel;

		Document playerData = StorageProvider.getPlayer( player.getUniqueId() );
		if( playerData != null ) {
			Object warbasesData = playerData.get( Constants.STORAGE_PLAYER_WARBASES );
			if( warbasesData != null && warbasesData instanceof Document ) {
				String storedUi = ( (Document) warbasesData ).getString( Constants.STORAGE_PLAYER_WARBASES_UI );
				if( storedUi != null ) {
					sel = GameUI.fromString( storedUi );
					if( sel != null )
						return sel;
				}
			}
		}

		return GameUI.Light;
	}

	private static GameUIQuality getQuality( Player player ) {
		GameUIQuality sel;

		Document playerData = StorageProvider.getPlayer( player.getUniqueId() );
		if( playerData != null ) {
			Object warbasesData = playerData.get( Constants.STORAGE_PLAYER_WARBASES );
			if( warbasesData != null && warbasesData instanceof Document ) {
				String storedUiQuality = ( (Document) warbasesData ).getString( Constants.STORAGE_PLAYER_WARBASES_UI_QUALITY );
				if( storedUiQuality != null ) {
					sel = GameUIQuality.fromString( storedUiQuality );
					if( sel != null )
						return sel;
				}
			}
		}

		return GameUIQuality.SD;
	}

	public static void set( Player player, GameUI ui ) {
		StorageProvider.updatePlayer( player.getUniqueId(), Updates.set( Constants.STORAGE_PLAYER_WARBASES + "." + Constants.STORAGE_PLAYER_WARBASES_UI, ui.toString() ) );
	}

	public static void set( Player player, GameUIQuality quality ) {
		StorageProvider.updatePlayer( player.getUniqueId(), Updates.set( Constants.STORAGE_PLAYER_WARBASES + "." + Constants.STORAGE_PLAYER_WARBASES_UI_QUALITY, quality.toString() ) );
	}
}
