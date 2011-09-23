package phase2;

import java.sql.ResultSet;
import java.sql.SQLException;

import core.*;

public class PhaseTwoPlayer extends Player {

	private double aggressiveness;

	public PhaseTwoPlayer(String name, TexasHoldEm game, double aggressiveness) {
		super(name, game);
		this.aggressiveness = aggressiveness;
		// TODO Auto-generated constructor stub
	}

	protected int placeBet() {
		int ID = getHoleID();
		int suited = (isSuited()) ? 0 : 9;
		double handStrength = 0.0, deeperHandStrength = 0.0; 
		int minimum = game.getHighbet() - currentBet;
		
		switch (game.getGameState()) {
		case Pre_flop:

			Database db = new Database();
			db.connect();

			ResultSet rs = db.query("select hole_strength."
					+ (suited + game.getNumPlayers() - 1)
					+ " from hole_strength where ref = " + ID + ";");
			try {
				rs.next();
				handStrength = rs.getDouble(1);// * game.getNumPlayers();
				deeperHandStrength = handStrength*aggressiveness - potOdds();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.disconnect();
			System.out.println("" + getName() + ": hs " + handStrength + ", dhs " + deeperHandStrength);
			if (deeperHandStrength > .14) {
				return 100 + minimum;
			} else if (deeperHandStrength > .0) {
				return 50 + minimum;
			} else if (deeperHandStrength > -.14) {
				return minimum;
			} else
				return 0;
		case Flop:
		case Turn:
		case River:
			// printCards();
			// System.out.println("HS "+handStrength());//+" "+TexasHoldEm.result(getPower()));

			double hs = handStrength(5)*aggressiveness - potOdds();
//			System.out.println("" + getName() + ": hs " + handStrength + ", dhs " + deeperHandStrength + ", new hs " + hs);
			if (hs > .1) {
				return 200 + minimumBet();
			} else if (hs > .0) {
				return 50 + minimumBet();
			} else if (hs > -.1) {
				return minimumBet();
			} else
				return 0;
		default:
			return 0;
		}
		
	}



}
