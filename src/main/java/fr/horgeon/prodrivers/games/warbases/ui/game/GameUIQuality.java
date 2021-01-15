package fr.horgeon.prodrivers.games.warbases.ui.game;

public enum GameUIQuality {
	SD,
	HD;

	public static GameUIQuality fromString( String value ) {
		if( value.equalsIgnoreCase( "sd" ) )
			return GameUIQuality.SD;
		else if( value.equalsIgnoreCase( "hd" ) )
			return GameUIQuality.HD;
		return null;
	}

	public String toString() {
		if( this == GameUIQuality.SD )
			return "SD";
		else if( this == GameUIQuality.HD )
			return "HD";
		return "";
	}
}
