package fr.horgeon.prodrivers.games.warbases.ui.game;

public enum GameUI {
	OnTopOfHotbar( true, true ),
	InHotbar( true, true ),
	Light( false, false ),
	Empty( false, false );

	public final boolean isEnhanced;
	public final boolean hasQuality;

	private GameUI( boolean isEnhanced, boolean hasQuality ) {
		this.isEnhanced = isEnhanced;
		this.hasQuality = hasQuality;
	}

	public static GameUI fromString( String value ) {
		if( value.equalsIgnoreCase( "onTopOfHotbar" ) )
			return GameUI.OnTopOfHotbar;
		else if( value.equalsIgnoreCase( "inHotbar" ) )
			return GameUI.InHotbar;
		else if( value.equalsIgnoreCase( "light" ) )
			return GameUI.Light;
		return null;
	}

	public String toString() {
		if( this == GameUI.OnTopOfHotbar )
			return "OnTopOfHotbar";
		else if( this == GameUI.InHotbar )
			return "InHotbar";
		else if( this == GameUI.Light )
			return "Light";
		return "";
	}
}
