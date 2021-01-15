package fr.horgeon.prodrivers.games.warbases.arena;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TeamBalancer {
	private ArenaManager manager;
	private Random rand = new Random();

	private Set<Map.Entry<String, ArenaPlayer>> players;

	private ArrayList<ArenaPlayer> bluePlayers = new ArrayList<>();
	private ArrayList<ArenaPlayer> redPlayers = new ArrayList<>();
	private ArrayList<ArenaPlayer> bluePlayersNoParty = new ArrayList<>();
	private ArrayList<ArenaPlayer> redPlayersNoParty = new ArrayList<>();
	private ArrayList<ArenaPlayer> noTeamPlayers = new ArrayList<>();

	private int maxPlayers;

	private int maxBluePlayers;
	private int maxRedPlayers;

	private boolean blueFavorised;
	private boolean redFavorised;

	public TeamBalancer( EArena arena ) {
		this.manager = arena.getArenaManager();
		this.players = arena.getArenaManager().getPlayers().entrySet();
		this.maxPlayers = arena.getMaxPlayers();

		init();
	}

	private void init() {
		System.out.println( "[WarBases] Team balancer initiated:" );

		stage1();
		stage2();
		stage3();

		manager.buildColorTeams();

		System.out.println( "[WarBases] Team balancing done." );
	}

	private void stage1() {
		// First we need to compute essential values and put each players in their corresponding team
		computeAbsoluteMax();
		initialAssignation();

		// We then assign each player that isn't in a team a team
		assignateNoTeamPlayers();
	}

	private void stage2() {
		// Now it's time for balancing

		// The second stage consist of a party-aware rebalancing:
		// Exceeding players will get moved to the opposite team if necessary,
		// but parties will not get broken apart

		// We recompute maximum players per team based on the current number of players, so each team has (hopefully) the same number of players
		computeRelativeMax();

		// Then we try to balance each team if they have exceeding players, restarting if their is still more players than a team should have
		rebalanceAll( true );
	}

	private void stage3() {
		// The third stage is the "last-ditch effort" stage:
		// If teams are still not balanced, we will proceed with a non-party-aware rebalancing.
		// It does the same thing that the second stage does, this time not considering parties and breaking them if necessary

		// We recompute maximum players per team based on the current number of players, so each team has (hopefully) the same number of players
		computeRelativeMax();

		// Then we try to balance each team if they have exceeding players, restarting if their is still more players than a team should have
		rebalanceAll( false );
	}

	private void computeAbsoluteMax() {
		// We compute the maximum number of players per team
		maxBluePlayers = maxPlayers / 2;
		maxRedPlayers = maxPlayers / 2;

		blueFavorised = false;
		redFavorised = false;

		if( maxPlayers % 2 != 0 ) {
			// If we have an odd number of maximum players, since our computation gave us only the integer part of the division, we need to increment a team's maximum players by one in order to cover the whole maximum number of players
			// We choose a random team and increment by one its maximum number of players
			if( rand.nextInt( 2 ) > 0 ) {
				maxRedPlayers++;
				redFavorised = true;
			} else {
				maxBluePlayers++;
				blueFavorised = true;
			}
		}

		System.out.println( "-> Max players is: " + maxPlayers );
		System.out.println( "-> Max Red players is: " + maxRedPlayers );
		System.out.println( "-> Max Blue players is: " + maxBluePlayers );
	}

	private void initialAssignation() {
		// We go over every registered arena players, and put them in the according collections
		for( Map.Entry<String, ArenaPlayer> entry : players ) {
			if( entry.getValue().getTeam() != null ) {
				if( entry.getValue().getTeam() == ArenaTeam.Blue ) {
					bluePlayers.add( entry.getValue() );
					if( !entry.getValue().isInParty() )
						bluePlayersNoParty.add( entry.getValue() );
					System.out.println( "-> [" + entry.getValue().getName() + "] is blue (" + entry.getValue().getTeam() + ")" );
					continue;
				} else if( entry.getValue().getTeam() == ArenaTeam.Red ) {
					redPlayers.add( entry.getValue() );
					if( !entry.getValue().isInParty() )
						redPlayersNoParty.add( entry.getValue() );
					System.out.println( "-> [" + entry.getValue().getName() + "] is red (" + entry.getValue().getTeam() + ")" );
					continue;
				}
			}

			noTeamPlayers.add( entry.getValue() );
			System.out.println( "-> [" + entry.getValue().getName() + "] has no team (" + entry.getValue().getTeam() + ")" );
		}
	}

	private void assignateNoTeamPlayers() {
		// First of all, we assign each player not currently in a team to alternatively red and blue team, if possible
		// The goal is to have every players in a team, in a (kind-of) balanced way

		// Boolean that decides if we should put the player in blue team
		// We take a random value so that the blue team is not always favored
		// Either way, we alternate between blue and red until no players is left in the "no-team" list
		boolean toBlueTeam = ( new Random() ).nextBoolean();

		// We go over every players currently not in a team
		for( ArenaPlayer p : noTeamPlayers ) {
			if( toBlueTeam ) {
				// If it's blue team's turn to receive a player
				if( bluePlayers.size() < maxBluePlayers ) {
					// If blue team can accept a player, place it in blue team
					bluePlayers.add( p );
					if( !p.isInParty() )
						bluePlayersNoParty.add( p );
					p.setTeam( ArenaTeam.Blue );
					System.out.println( "-> [" + p.getName() + "] had no team, now blue (originally blue)" );
				} else {
					// If blue team is already full, place it in red team
					redPlayers.add( p );
					if( !p.isInParty() )
						redPlayersNoParty.add( p );
					p.setTeam( ArenaTeam.Red );
					System.out.println( "-> [" + p.getName() + "] had no team, now red (originally blue)" );
				}
			} else {
				// If it's red team's turn to receive a player
				if( redPlayers.size() < maxRedPlayers ) {
					// If red team can accept a player, place it in red team
					redPlayers.add( p );
					if( !p.isInParty() )
						redPlayersNoParty.add( p );
					p.setTeam( ArenaTeam.Red );
					System.out.println( "-> [" + p.getName() + "] had no team, now red (originally red)" );
				} else {
					// If red team is already full, place it in blue team
					bluePlayers.add( p );
					if( !p.isInParty() )
						bluePlayersNoParty.add( p );
					p.setTeam( ArenaTeam.Blue );
					System.out.println( "-> [" + p.getName() + "] had no team, now blue (originally red)" );
				}
			}

			toBlueTeam ^= true;
		}
	}

	private void computeRelativeMax() {
		// We recompute maximum players per team based on the current number of players, so each team has (hopefully) the same number of players
		int numberOfPlayers = redPlayers.size() + bluePlayers.size();
		maxBluePlayers = numberOfPlayers / 2;
		maxRedPlayers = numberOfPlayers / 2;

		if( numberOfPlayers % 2 != 0 ) {
			// We take into account the possibility of an odd number of players, based on the previous choice
			if( redFavorised ) {
				maxRedPlayers++;
			} else {
				maxBluePlayers++;
			}
		}
	}

	private void rebalanceAll( boolean partyAware ) {
		// We try to balance each team if they have exceeding players, restarting if their is still more players than a team should have

		int i = 0;
		do {
			rebalance( partyAware );
			i++;

			if( i > 2 ) {
				// After 3 trials, if teams are still not balanced, we give up and proceed with the rest
				break;
			}
		} while( ( redPlayers.size() > maxRedPlayers ) || ( bluePlayers.size() > maxBluePlayers ) );
	}

	private void rebalance( boolean partyAware ) {
		// Number of authorized iterations and current ierations
		int iterations = bluePlayers.size();
		int iteration = 0;

		// While blue players has too many players, and has player in the non-party-one if party-aware
		while( bluePlayers.size() > maxBluePlayers && ( !partyAware || bluePlayersNoParty.size() > 0 ) ) {
			// Get a random blue player, from the not-in-party one if we're party-aware
			ArenaPlayer p;
			if( partyAware )
				p = bluePlayersNoParty.get( rand.nextInt( bluePlayersNoParty.size() ) );
			else
				p = bluePlayers.get( rand.nextInt( bluePlayers.size() ) );

			// If we're not party-aware OR we're party-aware and this player is not in a party
			if( !partyAware || !p.isInParty() ) {
				// We put him into red team
				bluePlayers.remove( p );
				bluePlayersNoParty.remove( p );
				redPlayers.add( p );
				if( !p.isInParty() )
					redPlayersNoParty.add( p );
				p.setTeam( ArenaTeam.Red );
				System.out.println( "-> [" + p.getName() + "] transfered blue -> red (" + ( partyAware ? "party-aware" : "non-party-aware" ) + ", " + ( p.isInParty() ? "in party" : "not in party" ) + ")" );
			}

			iteration++;
			if( iteration > iterations )
				break;
		}

		// Number of authorized iterations and current ierations
		iterations = redPlayers.size();
		iteration = 0;

		// While red players has too many players, and has player in the non-party-one if party-aware
		while( redPlayers.size() > maxRedPlayers && ( !partyAware || redPlayersNoParty.size() > 0 ) ) {
			// Get a random red player, from the not-in-party one if we're party-aware
			ArenaPlayer p;
			if( partyAware )
				p = redPlayersNoParty.get( rand.nextInt( redPlayers.size() ) );
			else
				p = redPlayers.get( rand.nextInt( redPlayers.size() ) );

			// If we're not party-aware OR we're party-aware and this player is not in a party
			if( !partyAware || !p.isInParty() ) {
				// We put him into red team
				redPlayers.remove( p );
				redPlayersNoParty.remove( p );
				bluePlayers.add( p );
				if( !p.isInParty() )
					bluePlayersNoParty.add( p );
				p.setTeam( ArenaTeam.Blue );
				System.out.println( "-> [" + p.getName() + "] transfered red -> blue (" + ( partyAware ? "party-aware" : "non-party-aware" ) + ", " + ( p.isInParty() ? "in party" : "not in party" ) + ")" );
			}

			iteration++;
			if( iteration > iterations )
				break;
		}
	}
}
