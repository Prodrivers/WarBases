package fr.horgeon.prodrivers.games.warbases.ui.wall;

import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import fr.horgeon.prodrivers.games.warbases.Main;
import fr.horgeon.prodrivers.games.warbases.arena.EArena;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class FAWEWallUI extends WallUI {
	private CuboidRegion wallRegion = null;
	private Schematic wallGameSchem = null, wallSchem = null;
	private Vector wallOrigin = null;

	public FAWEWallUI( int id, Main plugin, EArena arena, Location low, Location high ) {
		super( id, plugin, arena, low, high );
	}

	protected void save() {
		System.out.println( "[WarBases] Arena " + arena.getInternalName() + ": Wall saving initiated" );

		Vector lowCorner = new Vector( worldBlocks.getLowerX(), worldBlocks.getLowerY(), worldBlocks.getLowerZ() );
		Vector highCorner = new Vector( worldBlocks.getUpperX(), worldBlocks.getUpperY(), worldBlocks.getUpperZ() );
		wallRegion = new CuboidRegion( BukkitUtil.getLocalWorld( worldBlocks.getWorld() ), lowCorner, highCorner );

		if( staticWallSectionLoading() && staticGameWallSectionLoading() ) //dynamicGameSectionLoading()
			System.out.println( "[WarBases] Arena " + arena.getInternalName() + ": Wall saving done" );
	}

	/*private boolean dynamicGameSectionLoading() {
		try {
			ByteArrayOutputStream wallOutStream = new ByteArrayOutputStream();
			wallGameSchem = new Schematic( wallRegion );
			wallGameSchem.save( wallOutStream, ClipboardFormat.SCHEMATIC );

			byte[] wallBytes = wallOutStream.toByteArray();

			ByteArrayInputStream wallInStream = new ByteArrayInputStream( wallBytes );

			Clipboard wallClipboard = ClipboardFormat.SCHEMATIC.getReader( wallInStream ).read( BukkitUtil.getLocalWorld( worldBlocks.getWorld() ).getWorldData() );
			wallOrigin = wallClipboard.getOrigin();
			wallGameSchem = new Schematic( wallClipboard );

			return true;
		} catch( IOException ioe ) {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", wall save encountered an in-memory error buffer exception: " + ioe.getLocalizedMessage() + ". WALL SAVING NOT DONE." );
			ioe.printStackTrace();

			wallRegion = null;
			wallOrigin = null;
			wallGameSchem = null;
		}

		return false;
	}*/

	private boolean staticWallSectionLoading() {
		try {
			wallSchem = wallSectionLoader( "wall" );
			wallOrigin = wallSchem.getClipboard().getOrigin();

			return true;
		} catch( IOException | NullPointerException ex ) {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", wall load encountered an file loading exception: " + ex.getLocalizedMessage() + ". WALL LOADING NOT DONE." );
			ex.printStackTrace();

			wallSchem = null;
			wallOrigin = null;
		}

		return false;
	}

	private boolean staticGameWallSectionLoading() {
		try {
			wallGameSchem = wallSectionLoader( "wallgame" );

			return true;
		} catch( IOException ioe ) {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", game wall load encountered an file loading exception: " + ioe.getLocalizedMessage() + ". WALL LOADING NOT DONE." );
			ioe.printStackTrace();

			wallGameSchem = null;
		}

		return false;
	}

	private Schematic wallSectionLoader( String name ) throws IOException {
		FileInputStream wallInStream = new FileInputStream( new File( plugin.getDataFolder() + "/" + name + "_" + arena.getInternalName() + "_" + String.valueOf( id ) + ".schematic" ) );

		Clipboard wallClipboard = ClipboardFormat.SCHEMATIC.getReader( wallInStream ).read( BukkitUtil.getLocalWorld( worldBlocks.getWorld() ).getWorldData() );
		return new Schematic( wallClipboard );
	}

	public static boolean saveGameWallSection( int id, JavaPlugin plugin, String arenaName, Location low, Location high ) {
		return wallSectionSaver( "wallgame", id, plugin, arenaName, low, high );
	}

	public static boolean saveWallSection( int id, JavaPlugin plugin, String arenaName, Location low, Location high ) {
		return wallSectionSaver( "wall", id, plugin, arenaName, low, high );
	}

	public static boolean wallSectionSaver( String name, int id, JavaPlugin plugin, String arenaName, Location low, Location high ) {
		try {
			FileOutputStream wallOutStream = new FileOutputStream( new File( plugin.getDataFolder() + "/" + name + "_" + arenaName + "_" + String.valueOf( id ) + ".schematic" ) );

			Vector lowVec = new Vector( low.getX(), low.getY(), low.getZ() );
			Vector highVec = new Vector( high.getX(), high.getY(), high.getZ() );

			CuboidRegion wallRegion = new CuboidRegion( BukkitUtil.getLocalWorld( low.getWorld() ), lowVec, highVec );

			Schematic wallSchem = new Schematic( wallRegion );
			wallSchem.save( wallOutStream, ClipboardFormat.SCHEMATIC );

			return true;
		} catch( IOException ioe ) {
			System.err.println( "[WarBases] In arena " + arenaName + ", wall save encountered an file loading exception: " + ioe.getLocalizedMessage() + ". WALL SAVING NOT DONE." );
			ioe.printStackTrace();
		}

		return false;
	}

	public void fall() {
		if( wallGameSchem != null && wallOrigin != null ) {
			EditSession session = new EditSessionBuilder( worldBlocks.getWorld().getName() ).build();
			wallGameSchem.paste( session, wallOrigin, true );
			session.flushQueue();
		} else {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", attempted to make fall a non-saved wall." );
		}
	}

	/*public void reset() {
		if( wallRegion != null ) {
			EditSession session = new EditSessionBuilder( worldBlocks.getWorld().getName() ).build();
			try {
				session.setBlocks( wallRegion, new BaseBlock( BlockID.CLOTH, 0 ) );
			} catch( MaxChangedBlocksException ex ) {
				System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", wall reset exceeded the maximum number of changed blocks: " + ex.getLocalizedMessage() + ". WALL RESET NOT DONE CORRECTLY." );
				ex.printStackTrace();
			}
		} else {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", attempted to reset a non-saved wall." );
		}
	}*/

	public void reset() {
		if( wallSchem != null && wallOrigin != null && saved.get() ) {
			EditSession session = new EditSessionBuilder( worldBlocks.getWorld().getName() ).build();
			wallSchem.paste( session, wallOrigin, true );
			session.flushQueue();
		} else {
			System.err.println( "[WarBases] In arena " + arena.getInternalName() + ", attempted to reset a non-saved wall." );
		}
	}

	public void rebuild() {
		fall();
	}
}