package fr.horgeon.prodrivers.games.warbases.ui.game;

public enum CommunicationMessage {
	Hello( 600 ),
	Thanks( 601 ),
	Acknowledge( 602 ),
	GroupUp( 603 ),
	Attack( 604 ),
	Defend( 605 );

	private short damageValue;

	CommunicationMessage( int damageValue ) {
		this.damageValue = (short) damageValue;
	}

	public final short getItemDamageValue() {
		return this.damageValue;
	}
}
