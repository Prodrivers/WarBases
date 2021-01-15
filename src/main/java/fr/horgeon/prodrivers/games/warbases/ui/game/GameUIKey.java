package fr.horgeon.prodrivers.games.warbases.ui.game;

public class GameUIKey {
	public final GameUI ui;
	public final GameUIQuality quality;

	public GameUIKey( GameUI ui,GameUIQuality quality  ) {
		this.ui = ui;
		this.quality = quality;
	}

	@Override
	public boolean equals( Object obj ) {
		if( !( obj instanceof GameUIKey ) )
			return false;
		GameUIKey ref = (GameUIKey) obj;
		return this.ui == ref.ui && this.quality == ref.quality;
	}

	@Override
	public int hashCode() {
		if( quality != null )
			return ui.hashCode() ^ quality.hashCode();
		else
			return ui.hashCode();
	}

}
