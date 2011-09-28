package phase3;

import java.sql.ResultSet;
import java.sql.SQLException;

import core.*;

public class PhaseThreePlayer extends Player {

	private P3ContextAnalyzer context;
	private double aggressiveness;
	//for statistical analysis
	public boolean usedContext = false;
	public int wonUsingContext = 0;

	public PhaseThreePlayer(String name, TexasHoldEm game,
			P3ContextAnalyzer context, double aggressiveness) {
		super(name, game);
		this.context = context;
		this.aggressiveness = aggressiveness;
	}

	/**
	 * Uses information on opponents, hand strength and pot odds to decide which
	 * action to take.
	 */
	protected int placeBet() {
		int ID = getHoleID();
		int suited = (isSuited()) ? 0 : 9;
		double handStrength = 0.0, deeperHandStrength = 0.0;
		int minimum = game.getHighbet() - currentBet;
		double highestOpp = context.highestAnticipated(game.getGameState().getStateNum());
		

		double playerincrease = 1.0;
		if (game.getActivePlayersSize() > 5)
			playerincrease = Math.pow(1.04, game.getActivePlayersSize() - 3);

		switch (game.getGameState()) {
		case Pre_flop:

			Database db = new Database();
			db.connect();

			ResultSet rs = db.query("select hole_strength."
					+ (suited + game.getNumPlayers() - 1)
					+ " from hole_strength where ref = " + ID + ";");
			try {
				rs.next();
				handStrength = rs.getDouble(1) * aggressiveness
						* playerincrease;
				deeperHandStrength = handStrength - potOdds();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			db.disconnect();

			if (highestOpp > 0.0) {
				System.out.println(printUseOfContextAnalysis(handStrength, highestOpp));
				double strengthDiff = handStrength - highestOpp;
				if (strengthDiff > .05) {
					return 100 + minimum;
				} else if (strengthDiff > -.05) {
					return 50 + minimum;
				} else if (strengthDiff > -.15) {
					return minimum;
				} else
					return 0;

			}

			if (deeperHandStrength > .14) {
				return 100 + minimum;
			} else if (deeperHandStrength > .0) {
				return 50 + minimum;
			} else if (deeperHandStrength > -.05) {
				return minimum;
			} else
				return 0;
		case Flop:
		case Turn:
		case River:

			handStrength = handStrength(5) * playerincrease;
			
			
			if (highestOpp != 0) {
				double strengthDiff = handStrength - highestOpp;
				System.out.println(printUseOfContextAnalysis(handStrength,
						highestOpp));
				if (strengthDiff > .1) {
					return 200 + minimum;
				} else if (strengthDiff > .0) {
					return 50 + minimum;
				} else if (strengthDiff > -.05) {
					return minimum;
				} else
					return 0;

			}

			double hs = handStrength - potOdds();
			if (hs > .6) {
				return 220 + minimumBet();
			} else if (hs > .40) {
				return 150 + minimumBet();
			} else if (hs > .25) {
				return 50 + minimumBet();
			} else if (hs > .1) {
				return minimumBet();
			} else
				return 0;
		default:
			return 0;
		}

	}

	public String printUseOfContextAnalysis(double handStrength, double opponent) {
		return new String("" + this.name + " used context analysis, hs: "
				+ handStrength + "\t opponent: " + opponent + " diff: "
				+ (handStrength - opponent));
	}
}
