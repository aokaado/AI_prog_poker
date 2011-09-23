package phase1;

import java.util.Scanner;

import core.Player;
import core.TexasHoldEm;

public class HumanPlayer extends Player {

	Scanner sc;

	public HumanPlayer(Scanner sc, TexasHoldEm game) {
		super("Console", game);
		this.sc = sc;
	}

	protected int placeBet() {
		System.out.println("Your cards:");
		printCards();
		System.out.println("Highest bet is currently " + game.getHighbet()
				+ ".\nyour bet is at " + currentBet
				+ ".\nto stay in you have to bet atleast "
				+ (game.getHighbet() - currentBet) + ".\nYour bet: ");
		return sc.nextInt();
	}

}
