package fr.horgeon.prodrivers.games.warbases.configuration;

import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import fr.horgeon.prodrivers.games.warbases.arena.ArenaTeam;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EMessagesConfig extends MessagesConfig {
	private static Random random;

	static {
		random = new Random();
	}

	public String you_have_been_killed_by = "&cYou have been killed by &4<killer>&c.";
	public String arena_starting = "&bPlease wait while the arena is starting.";
	public String arena_stopping = "&bWaiting for players to respawn.";
	public String no_arena_name = "&cNo arena name provided.";
	public String invalid_position = "&cInvalid position.";
	public String invalid_team = "&cInvalid team.";
	public String no_corner_set = "&cNo low and high corner set";
	public String place_wool_for_checkpoints = "&bPlace down the Wool at every checkpoint you want.";
	public String invalid_radius = "&cInvalid radius.";
	public String team_joined = "&bYou joined team <teamColor><team>&b!";
	public String arena_already_started = "&cThe arena already started!";
	public String teams_item = "&4Teams";
	public String revert_to_scoreboard = "&cYou were reverted to the Scoreboard interface because you either refused or your client hasn't successfully download the resource pack.";
	public String invalid_ui = "&cInvalid UI.";
	public String ui_set = "&aUI set to &3<ui>&a.";
	public String persistentcommenu_set = "&aPersistent Communication Menu set to &3<value>&a.";
	public String players_applying_resourcepack = "&bWaiting for players applying resource packs.";
	public String invalid_variable = "&cInvalid variable.";
	public String invalid_value = "&cInvalid value.";
	public String broadcast_killed = "&4&l✖&r <killerTeamColor>&l<killer>&r ➤ <killedTeamColor>&l<killed>";
	public String broadcast_suicide = "&4&l✖&r <killedTeamColor>&l<killed>";
	public String player_killed = "&f&l<killed>&r &4KILLED&r&f.";
	public String welcome = "&1&lWarBases";
	public String victory = "&6&lVICTORY!";
	public String defeat = "&4&lDEFEAT!";
	public String draw = "&6&lDRAW!";
	public String checkpoint_being_captured = "&lPOINT BEING CAPTURED!";
	public String checkpoint_capturing = "&lCAPTURING POINT...";
	public String checkpoint_being_contested = "&lPOINT CONTESTED!";
	public String checkpoint_contesting = "&lCONTESTING POINT!";
	public String time_left_title_prefix = "&l";
	public String attack_commencing = "&lATTACK COMMENCING!";
	public String kill_streak = "&b&l<number> PLAYERS KILLED IN A ROW!";
	public String overtime = "&lOVERTIME";
	public String version_too_old_for_enhanced_ui = "&cYour Minecraft version is too old for &3Enhanced UI&c. Please use Minecraft version 1.9.3 or upward.";
	public String in_game_messages_hello = "&b&l<sender>&r&b: Hello!";
	public String in_game_messages_thanks = "&b&l<sender>&r&b: Thanks!";
	public String in_game_messages_acknowledge = "&b&l<sender>&r&b: Acknowledge.";
	public String in_game_messages_groupup = "&b&l<sender>&r&b: Join me!";
	public String in_game_messages_attack = "&b&l<sender>&r&b: Attack!";
	public String in_game_messages_defend = "&b&l<sender>&r&b: Defend!";
	public String in_game_messages_please_wait = "&4&lPlease wait before sending a new message.";
	public String preferences_selector_frame_character = "-";
	public String preferences_selector_click_to_change = "Click to change!";
	public String preferences_selector_intro = "Select a category:";
	public String preferences_selector_entry_ui = "User interface";
	public String preferences_selector_is_in_arena = "You can not change your UI settings in-game.";
	public String preferences_selector_bullet_checked = "☑";
	public String preferences_selector_bullet_unchecked = "☐";
	public String ui_selector_intro = "Please select your prefered UI:";
	public String ui_selector_light = "Lightweight UI";
	public String ui_selector_enhanced = "Enhanced UI";
	public String ui_selector_enhanced_flavor_intro = "Please select the UI position:";
	public String ui_selector_enhanced_flavor_inhotbar = "Inside the hotbar";
	public String ui_selector_enhanced_flavor_ontopofhotbar = "On top of the hotbar";
	public String ui_selector_enhanced_quality_intro = "Please select the quality:";
	public String ui_selector_enhanced_quality_sd = "SD";
	public String ui_selector_enhanced_quality_hd = "HD";
	public String ui_selector_persistentcommenu = "Activate persistent communication menu";
	public String ui_selector_preference_set = "UI preferences set.";
	public String ui_enhanced_not_available = "The Enhanced UI is not available. Please inform the server staff.";
	public String scoreboard_main_title = "&1&lWarBases";
	public String scoreboard_separator = "-";
	public String scoreboard_arena = "&6Arena &r<arena>";
	public String scoreboard_team = "<teamColor><team><padding>&r<progress>%";
	public String scoreboard_players = "<current>/<max> players";
	public String scoreboard_points = "<points> points";
	public String arena_join_not_in_lobby = "You need to join the WarBases lobby before joining a game!";
	public String not_a_player = "You are not a player!";
	public String arena_join_not_in_lobby_party = "All players in the party needs to join the WarBases lobby before joining a game!";
	public String error_occured_command = "&aAn error occured when executing this command. PLease advise the server staff.";
	public String join_forbidden = "You currently can not join a game of WarBases.";
	public String chat_format_team = "&a[<points>] %s > %s";
	public String chat_format_global = "&6[<points>] %s > %s";
	public String chat_global_start_character = "!";
	public String party_cannot_join_by_yourself = "Only the party leader can enter a game!";
	public Map<ArenaTeam, String> team_names = new HashMap<>();
	public String team_join_lore = "&rJoin the <teamColor>&l<team>&r team!";;
	public List<String> tips = new ArrayList<>();

	public EMessagesConfig( JavaPlugin plugin ) {
		super( plugin );

		this.you_got_a_kill = "";
		this.player_was_killed_by = "";
		this.getConfig().addDefault( "messages.you_have_been_killed_by", this.you_have_been_killed_by );
		this.getConfig().addDefault( "messages.arena_starting", this.arena_starting );
		this.getConfig().addDefault( "messages.arena_stopping", this.arena_stopping );
		this.getConfig().addDefault( "messages.no_arena_name", this.no_arena_name );
		this.getConfig().addDefault( "messages.invalid_position", this.invalid_position );
		this.getConfig().addDefault( "messages.invalid_team", this.invalid_team );
		this.getConfig().addDefault( "messages.no_corner_set", this.no_corner_set );
		this.getConfig().addDefault( "messages.place_wool_for_checkpoints", this.place_wool_for_checkpoints );
		this.getConfig().addDefault( "messages.invalid_radius", this.invalid_radius );
		this.getConfig().addDefault( "messages.invalid_team", this.invalid_team );
		this.getConfig().addDefault( "messages.team_joined", this.team_joined );
		this.getConfig().addDefault( "messages.arena_already_started", this.arena_already_started );
		this.getConfig().addDefault( "messages.teams_item", this.teams_item );
		this.getConfig().addDefault( "messages.revert_to_scoreboard", this.revert_to_scoreboard );
		this.getConfig().addDefault( "messages.invalid_ui", this.invalid_ui );
		this.getConfig().addDefault( "messages.ui_set", this.ui_set );
		this.getConfig().addDefault( "messages.persistentcommenu_set", this.persistentcommenu_set );
		this.getConfig().addDefault( "messages.players_applying_resourcepack", this.players_applying_resourcepack );
		this.getConfig().addDefault( "messages.invalid_variable", this.invalid_variable );
		this.getConfig().addDefault( "messages.invalid_value", this.invalid_value );
		this.getConfig().addDefault( "messages.broadcast_killed", this.broadcast_killed );
		this.getConfig().addDefault( "messages.broadcast_suicide", this.broadcast_suicide );
		this.getConfig().addDefault( "messages.player_killed", this.player_killed );
		this.getConfig().addDefault( "messages.welcome", this.welcome );
		this.getConfig().addDefault( "messages.victory", this.victory );
		this.getConfig().addDefault( "messages.defeat", this.defeat );
		this.getConfig().addDefault( "messages.draw", this.draw );
		this.getConfig().addDefault( "messages.checkpoint_being_captured", this.checkpoint_being_captured );
		this.getConfig().addDefault( "messages.checkpoint_capturing", this.checkpoint_capturing );
		this.getConfig().addDefault( "messages.checkpoint_being_contested", this.checkpoint_being_contested );
		this.getConfig().addDefault( "messages.checkpoint_contesting", this.checkpoint_contesting );
		this.getConfig().addDefault( "messages.time_left_title_prefix", this.time_left_title_prefix );
		this.getConfig().addDefault( "messages.attack_commencing", this.attack_commencing );
		this.getConfig().addDefault( "messages.kill_streak", this.kill_streak );
		this.getConfig().addDefault( "messages.overtime", this.overtime );
		this.getConfig().addDefault( "messages.version_too_old_for_enhanced_ui", this.version_too_old_for_enhanced_ui );
		this.getConfig().addDefault( "messages.in_game_messages.hello", this.in_game_messages_hello );
		this.getConfig().addDefault( "messages.in_game_messages.thanks", this.in_game_messages_thanks );
		this.getConfig().addDefault( "messages.in_game_messages.acknowledge", this.in_game_messages_acknowledge );
		this.getConfig().addDefault( "messages.in_game_messages.groupup", this.in_game_messages_groupup );
		this.getConfig().addDefault( "messages.in_game_messages.attack", this.in_game_messages_attack );
		this.getConfig().addDefault( "messages.in_game_messages.defend", this.in_game_messages_defend );
		this.getConfig().addDefault( "messages.preferences_selector.frame.character", this.preferences_selector_frame_character );
		this.getConfig().addDefault( "messages.preferences_selector.click_to_change", this.preferences_selector_click_to_change );
		this.getConfig().addDefault( "messages.preferences_selector.intro", this.preferences_selector_intro );
		this.getConfig().addDefault( "messages.preferences_selector.entry.ui", this.preferences_selector_entry_ui );
		this.getConfig().addDefault( "messages.preferences_selector.is_in_arena", this.preferences_selector_is_in_arena );
		this.getConfig().addDefault( "messages.preferences_selector.bullet.checked", this.preferences_selector_bullet_checked );
		this.getConfig().addDefault( "messages.preferences_selector.bullet.unchecked", this.preferences_selector_bullet_unchecked );
		this.getConfig().addDefault( "messages.ui_selector.intro", this.ui_selector_intro );
		this.getConfig().addDefault( "messages.ui_selector.light", this.ui_selector_light );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.option", this.ui_selector_enhanced );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.flavor.intro", this.ui_selector_enhanced_flavor_intro );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.flavor.inhotbar", this.ui_selector_enhanced_flavor_inhotbar );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.flavor.ontopofhotbar", this.ui_selector_enhanced_flavor_ontopofhotbar );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.quality.intro", this.ui_selector_enhanced_quality_intro );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.quality.sd", this.ui_selector_enhanced_quality_sd );
		this.getConfig().addDefault( "messages.ui_selector.enhanced.quality.hd", this.ui_selector_enhanced_quality_hd );
		this.getConfig().addDefault( "messages.ui_selector.persistentcommenu", this.ui_selector_persistentcommenu );
		this.getConfig().addDefault( "messages.ui_selector.preference_set", this.ui_selector_preference_set );
		this.getConfig().addDefault( "messages.scoreboard.main_title", this.scoreboard_main_title );
		this.getConfig().addDefault( "messages.scoreboard.arena", this.scoreboard_arena );
		this.getConfig().addDefault( "messages.scoreboard.separator", this.scoreboard_separator );
		this.getConfig().addDefault( "messages.scoreboard.team", this.scoreboard_team );
		this.getConfig().addDefault( "messages.scoreboard.players", this.scoreboard_players );
		this.getConfig().addDefault( "messages.scoreboard.points", this.scoreboard_points );
		this.getConfig().addDefault( "messages.in_game_messages_please_wait", this.in_game_messages_please_wait );
		this.getConfig().addDefault( "messages.arena_join_not_in_lobby", this.arena_join_not_in_lobby );
		this.getConfig().addDefault( "messages.not_a_player", this.not_a_player );
		this.getConfig().addDefault( "messages.arena_join_not_in_lobby_party", this.arena_join_not_in_lobby_party );
		this.getConfig().addDefault( "messages.error_occured_command", this.error_occured_command );
		this.getConfig().addDefault( "messages.ui_enhanced_not_available", this.ui_enhanced_not_available );
		this.getConfig().addDefault( "messages.join_forbidden", this.join_forbidden );
		this.getConfig().addDefault( "messages.chat_format_team", this.chat_format_team );
		this.getConfig().addDefault( "messages.chat_format_global", this.chat_format_global );
		this.getConfig().addDefault( "messages.chat_global_start_character", this.chat_global_start_character );
		this.getConfig().addDefault( "messages.party_cannot_join_by_yourself", this.party_cannot_join_by_yourself );
		this.getConfig().addDefault( "messages.team.join_lore", this.team_join_lore );

		for( ArenaTeam team : ArenaTeam.values() )
			this.getConfig().addDefault( "messages.team.names." + team.toString(), team.toString() );

		this.getConfig().addDefault( "messages.tips", this.tips );

		this.getConfig().options().copyDefaults( true );
		this.saveConfig();

		this.tips.clear();

		this.you_have_been_killed_by = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.you_have_been_killed_by" ) );
		this.arena_starting = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.arena_starting" ) );
		this.arena_stopping = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.arena_stopping" ) );
		this.no_arena_name = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.no_arena_name" ) );
		this.invalid_position = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_position" ) );
		this.invalid_team = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_team" ) );
		this.no_corner_set = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.no_corner_set" ) );
		this.place_wool_for_checkpoints = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.place_wool_for_checkpoints" ) );
		this.place_wool_for_checkpoints = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.place_wool_for_checkpoints" ) );
		this.invalid_radius = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_radius" ) );
		this.team_joined = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.team_joined" ) );
		this.arena_already_started = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.arena_already_started" ) );
		this.teams_item = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.teams_item" ) );
		this.revert_to_scoreboard = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.revert_to_scoreboard" ) );
		this.invalid_ui = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_ui" ) );
		this.ui_set = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_set" ) );
		this.persistentcommenu_set = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.persistentcommenu_set" ) );
		this.players_applying_resourcepack = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.players_applying_resourcepack" ) );
		this.invalid_variable = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_variable" ) );
		this.invalid_value = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.invalid_value" ) );
		this.broadcast_killed = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.broadcast_killed" ) );
		this.broadcast_suicide = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.broadcast_suicide" ) );
		this.player_killed = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.player_killed" ) );
		this.welcome = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.welcome" ) );
		this.victory = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.victory" ) );
		this.defeat = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.defeat" ) );
		this.draw = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.draw" ) );
		this.checkpoint_being_captured = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.checkpoint_being_captured" ) );
		this.checkpoint_capturing = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.checkpoint_capturing" ) );
		this.checkpoint_being_contested = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.checkpoint_being_contested" ) );
		this.checkpoint_contesting = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.checkpoint_contesting" ) );
		this.time_left_title_prefix = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.time_left_title_prefix" ) );
		this.attack_commencing = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.attack_commencing" ) );
		this.kill_streak = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.kill_streak" ) );
		this.overtime = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.overtime" ) );
		this.version_too_old_for_enhanced_ui = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.version_too_old_for_enhanced_ui" ) );
		this.in_game_messages_hello = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.hello" ) );
		this.in_game_messages_thanks = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.thanks" ) );
		this.in_game_messages_acknowledge = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.acknowledge" ) );
		this.in_game_messages_groupup = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.groupup" ) );
		this.in_game_messages_attack = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.attack" ) );
		this.in_game_messages_defend = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages.defend" ) );
		this.preferences_selector_frame_character = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.frame.character" ) );
		this.preferences_selector_click_to_change = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.click_to_change" ) );
		this.preferences_selector_intro = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.intro" ) );
		this.preferences_selector_entry_ui = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.entry.ui" ) );
		this.preferences_selector_is_in_arena = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.is_in_arena" ) );
		this.preferences_selector_bullet_checked = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.bullet.checked" ) );
		this.preferences_selector_bullet_unchecked = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.preferences_selector.bullet.unchecked" ) );
		this.ui_selector_intro = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.intro" ) );
		this.ui_selector_light = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.light" ) );
		this.ui_selector_enhanced = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.option" ) );
		this.ui_selector_enhanced_flavor_intro = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.flavor.intro" ) );
		this.ui_selector_enhanced_flavor_inhotbar = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.flavor.inhotbar" ) );
		this.ui_selector_enhanced_flavor_ontopofhotbar = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.flavor.ontopofhotbar" ) );
		this.ui_selector_enhanced_quality_intro = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.quality.intro" ) );
		this.ui_selector_enhanced_quality_sd = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.quality.sd" ) );
		this.ui_selector_enhanced_quality_hd = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.enhanced.quality.hd" ) );
		this.ui_selector_persistentcommenu = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.persistentcommenu" ) );
		this.ui_selector_preference_set = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_selector.preference_set" ) );
		this.scoreboard_main_title = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.main_title" ) );
		this.scoreboard_arena = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.arena" ) );
		this.scoreboard_separator = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.separator" ) );
		this.scoreboard_team = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.team" ) );
		this.scoreboard_players = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.players" ) );
		this.scoreboard_points = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.scoreboard.points" ) );
		this.in_game_messages_please_wait = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.in_game_messages_please_wait" ) );
		this.arena_join_not_in_lobby = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.arena_join_not_in_lobby" ) );
		this.not_a_player = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.not_a_player" ) );
		this.arena_join_not_in_lobby_party = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.arena_join_not_in_lobby_party" ) );
		this.error_occured_command = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.error_occured_command" ) );
		this.ui_enhanced_not_available = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.ui_enhanced_not_available" ) );
		this.join_forbidden = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.join_forbidden" ) );
		this.chat_format_team = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.chat_format_team" ) );
		this.chat_format_global = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.chat_format_global" ) );
		this.chat_global_start_character = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.chat_global_start_character" ) );
		this.party_cannot_join_by_yourself = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.party_cannot_join_by_yourself" ) );
		this.team_join_lore = ChatColor.translateAlternateColorCodes( '&', this.getConfig().getString( "messages.team.join_lore" ) );

		for( ArenaTeam team : ArenaTeam.values() )
			team_names.put( team, this.getConfig().getString( "messages.team.names." + team.toString() ) );

		for( String tip : this.getConfig().getStringList( "messages.tips" ) ) {
			this.tips.add( ChatColor.translateAlternateColorCodes( '&', tip ) );
		}
	}

	public String getRandomTip() {
		if( tips.size() > 0 ) {
			int i = random.nextInt( tips.size() );
			return tips.get( i );
		}

		return null;
	}
}
