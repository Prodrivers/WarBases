package fr.horgeon.prodrivers.games.warbases.arena;

import fr.horgeon.prodrivers.games.warbases.configuration.EMessagesConfig;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public enum ArenaTeam {
	Blue,
	Red;

	public static ArenaTeam fromString( String value ) {
		if( value.equalsIgnoreCase( "red" ) )
			return ArenaTeam.Red;
		else if( value.equalsIgnoreCase( "blue" ) )
			return ArenaTeam.Blue;
		return null;
	}

	public static ArenaTeam fromDisplayName( EMessagesConfig msgs, String value ) {
		for( ArenaTeam team : ArenaTeam.values() )
			if( team.toDisplayName( msgs ).equals( value ) )
				return team;
		return null;
	}

	public static ArenaTeam fromInt( int value ) {
		if( value == 0 )
			return ArenaTeam.Blue;
		else if( value == 1 )
			return ArenaTeam.Red;
		return null;
	}

	public int toInt() {
		if( this == Blue )
			return 0;
		else if( this == Red )
			return 1;
		else
			return -1;
	}

	public Color toColor() {
		if( this == Blue )
			return Color.BLUE;
		else
			return Color.RED;
	}

	public byte toColorByte() {
		if( this == Red )
			return (byte) 14;
		else if( this == Blue )
			return (byte) 11;
		return (byte) 0;
	}

	public ChatColor toChatColor() {
		if( this == Blue )
			return ChatColor.BLUE;
		else if( this == Red )
			return ChatColor.RED;
		return ChatColor.WHITE;
	}

	public DyeColor toDyeColor() {
		if( this == Blue )
			return DyeColor.BLUE;
		else if( this == Red )
			return DyeColor.RED;
		return DyeColor.WHITE;
	}

	public String toName( EMessagesConfig msgs ) {
		return msgs.team_names.get( this );
	}

	public String toDisplayName( EMessagesConfig msgs ) {
		return toChatColor() + toName( msgs );
	}
}
