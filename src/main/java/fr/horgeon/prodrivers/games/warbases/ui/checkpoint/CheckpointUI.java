package fr.horgeon.prodrivers.games.warbases.ui.checkpoint;

import fr.horgeon.bukkit.packetsutilities.FakeTeam;
import fr.horgeon.bukkit.packetsutilities.entities.FakeEntity;
import fr.horgeon.bukkit.packetsutilities.entities.FakeSlime;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaPlayer;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckpointUI {
	private Main plugin;
	private EArena arena;
	private Location center;
	private ArenaTeam team;
	private FakeSlime entity;
	private HashMap<String, FakeTeam> colorTeams = new HashMap<>();
	private HashMap<String, FakeTeam> collisionTeams = new HashMap<>();
	private List<String> colorTeamPlayers = new ArrayList<>();

	public CheckpointUI( Main plugin, EArena arena, Location center, int radius, ArenaTeam team ) {
		float size = 2.f * radius / 0.36f;
		int sizeRound = Math.round( size );
		System.out.println( "[WarBases] Computed size of Slime is: " + size + "/" + Math.round( size ) );
		float yOffset = size / 2.f - size / 25.f - 1.25f;
		System.out.println( "[WarBases] Computed Y offset of Slime is: " + yOffset );
		this.plugin = plugin;
		this.arena = arena;
		this.center = center;
		this.team = team;
		entity = new FakeSlime( center.clone().subtract( 0, yOffset, 0 ), FakeEntity.generateUniqueId( "chckptslime" + team.name(), FakeEntity.getNextEntityId() ) );
		entity.setVisible( false );
		entity.setGlowing( true );
		entity.setSilent( true );
		entity.setSize( sizeRound );
	}

	public void preStart() {
		colorTeamPlayers.add( entity.getUniqueId().toString() );
		for( ArenaPlayer player : arena.getArenaManager().getPlayersInTeam( team ) ) {
			colorTeamPlayers.add( player.getName() );
		}
	}

	private static String currentTime() {
		SimpleDateFormat sdfDate = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );//dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format( now );
		return strDate;
	}

	public void start( ArenaPlayer player ) {
		if( player.getConfiguration().getUI().isEnhanced ) {
			entity.addPlayer( player.getPlayer() );
			entity.create( player.getPlayer() );

			String name = "chckptslmcl" + team.name();
			try {
				FakeTeam collisionTeam = new FakeTeam( name );
				collisionTeam.setPlayers( Arrays.asList( new String[]{ player.getName(), entity.getUniqueId().toString() } ) );
				collisionTeam.setCollisionRule( FakeTeam.CollisionRule.NEVER );
				collisionTeam.createTeam( player.getPlayer() );
				collisionTeams.put( player.getName(), collisionTeam );
			} catch( ArrayIndexOutOfBoundsException ex ) {
				System.err.println( "[WarBases] ArrayIndexOutOfBoundsException while creating collision team for " + player.getName() );
				System.err.println( "[WarBases] -> Collision team name (" + name.length() + "): " + name );
			}
		}

		String name = "chckptslmco" + team.name();
		try {
			System.out.println( "Color team name (" + name.length() + "): " + name );
			FakeTeam colorTeam = new FakeTeam( name );
			colorTeam.setPrefix( team.toChatColor().toString() );
			//colorTeam.setPlayers( Arrays.asList( new String[]{ entity.getUniqueId().toString() } ) );
			colorTeam.setPlayers( colorTeamPlayers );
			colorTeam.createTeam( player.getPlayer() );
			colorTeams.put( player.getName(), colorTeam );
		} catch( ArrayIndexOutOfBoundsException ex ) {
			System.err.println( "[WarBases] ArrayIndexOutOfBoundsException while creating color team for " + player.getName() );
			System.err.println( "[WarBases] -> Color team name (" + name.length() + "): " + name );
		}
	}

	public void stop( ArenaPlayer player ) {
		entity.destroy( player.getPlayer() );
		entity.removePlayer( player.getPlayer() );

		try {
			colorTeams.get( player.getName() ).removeTeam( player.getPlayer() );
			collisionTeams.get( player.getName() ).removeTeam( player.getPlayer() );
		} catch( NullPointerException e ) {}
	}

	public void reset() {
		colorTeams.clear();
		collisionTeams.clear();
		colorTeamPlayers.clear();
	}
}
