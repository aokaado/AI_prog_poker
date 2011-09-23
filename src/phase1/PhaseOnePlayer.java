package phase1;

import core.Player;
import core.TexasHoldEm;

public class PhaseOnePlayer extends Player {
	
	public PhaseOnePlayer(int number, TexasHoldEm game){
		super("Computer "+ number, game);
	}

	
	protected int placeBet() {
//		System.out.println("phase1_bets");
		if (Math.max(hand[0].getFace(),hand[1].getFace()) > 11) return 150;
		else if (hand[0].getFace()+hand[1].getFace() > 18) return 250;
		else if (hand[0].getFace() == hand[1].getFace()) return 190;
		else if (hand[0].getSuit() == hand[1].getSuit()) return 120;
		return 0;
	}
}
