package phase1;

import core.Player;
import core.TexasHoldEm;
import core.TexasHoldEm.GameState;

public class PhaseOnePlayer extends Player {
	int strategy = 0;

	/**
	 * 
	 * @param number
	 * @param game
	 * @param strategy
	 *            1 for more optimistic, aggressive playing style. 0 for more
	 *            conservative style.
	 */
	public PhaseOnePlayer(int number, TexasHoldEm game, int strategy) {
		super("Computer " + number, game);
		if (strategy < 0 || strategy > 1)
			throw new IllegalArgumentException("Strategy" + strategy
					+ "does not exist");
		this.strategy = strategy;
	}

	protected int placeBet() {
		// System.out.println("phase1_bets");
		switch (game.getGameState()) {
		case Pre_flop:
			switch (strategy) {
			case 0:
				if (Math.max(hand[0].getFace(), hand[1].getFace()) > 11)
					return 150;
				else if (hand[0].getFace() + hand[1].getFace() > 18)
					return 250;
				else if (hand[0].getFace() == hand[1].getFace())
					return 190;
				else if (hand[0].getSuit() == hand[1].getSuit())
					return 120;
				return 0;
			case 1:
				if (Math.max(hand[0].getFace(), hand[1].getFace()) > 9)
					return 150;
				else if (hand[0].getFace() + hand[1].getFace() > 16)
					return 250;
				else if (hand[0].getFace() == hand[1].getFace())
					return 200;
				else if (hand[0].getSuit() == hand[1].getSuit())
					return 160;
				return 0;
			default:
				return 0;
			}
		case Flop:
		case Turn:
		case River:
			calculatePower();
			switch(power[0]){
			case 1:
				return (strategy==0 ? 100: 120);
			case 2:
				return (strategy==0 ? 140: 170);
			case 3:
				return (strategy==0 ? 170: 210);
			case 4:
				return (strategy==0 ? 200: 260);
			case 5:
				return (strategy==0 ? 250: 300);
			default:
				return (strategy==0 ? 290: 450);
			
			}
		default:
			return 0;
		}
	}
}
