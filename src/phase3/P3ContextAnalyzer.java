package phase3;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import core.Database;
import core.Player;
import core.TexasHoldEm;

public class P3ContextAnalyzer {

	private static final double potOddsBins[] = { -0.1, 0.1, 0.2, 0.3 };
	private static final int raiseBins[] = {50, 100, 200, 300};
	// player # , gamestate, number of raises, potOddsBin, action , 0 = strength
	// <-> 1 = numberofobs
	private static double contextOdds[][][][][][] = new double[10][4][4][4][4][2]; // global
																					// model
	private static int bets; // number of raises
	private TexasHoldEm game;
	private ArrayList<int[]> contextQueue; // model for current hand

	public P3ContextAnalyzer(TexasHoldEm game) {
		this.game = game;
		contextQueue = new ArrayList<int[]>();
	}

	public void newBettingRound(ArrayList<Player> players) {
		bets = 0;
	}

	public void raise() {
		bets++;
	}

	/**
	 * Adds An event into the contextqueue of this hand, which might go into the
	 * global eventlist if player continues into showdown.
	 * 
	 * @param p
	 *            player doing action
	 * @param a
	 *            action done
	 */
	public void event(Player p, TexasHoldEm.Action a) {
		int event[] = { game.getIndexOfPlayer(p),
				game.getGameState().getStateNum(), raiseBin(p.getCurrentBet() - game.getHighbet()),
				potOddsBin(p.potOdds()), a.getActionNum() };
		contextQueue.add(event);
	}

	/**
	 * This method notifies that the hand has ended, and that the
	 * contextAnalyzer can now add hand strengths to all context-action pairs by
	 * players who showed their hands, and add these pairs to the global model.
	 * 
	 * @param players
	 *            list of players who were in the showdown.
	 */
	public void notifyEndOfHand(ArrayList<Player> players) {
		boolean inShowdown = false;
		for (int[] e : contextQueue) {
			Player eventPlayer = null;
			for (Player p : players) {
				if (game.getIndexOfPlayer(p) == e[0]) {
					inShowdown = true;
					eventPlayer = p;
					break;
				}
			}
			if (!inShowdown)
				continue;
			inShowdown = false;

			double handStrength = 0.0;
			switch (e[1]) {
			case 0: // pre-flop
				int suited = (eventPlayer.isSuited()) ? 0 : 9;
				int ID = eventPlayer.getHoleID();
				Database db = new Database();
				db.connect();

				ResultSet rs = db.query("select hole_strength."
						+ (suited + game.getNumPlayers() - 1)
						+ " from hole_strength where ref = " + ID + ";");
				try {
					rs.next();
					handStrength = rs.getDouble(1);
				} catch (SQLException sql) {
					sql.printStackTrace();
				}
				db.disconnect();
				break;
			case 1: // flop
			case 2: // turn
			case 3: // river
				handStrength = eventPlayer.handStrength(e[1] + 2);
				break;
			default:
				throw new NullPointerException();
			}
			contextOdds[e[0]][e[1]][e[2]][e[3]][e[4]][0] += handStrength;
			contextOdds[e[0]][e[1]][e[2]][e[3]][e[4]][1]++;
		}
		contextQueue = new ArrayList<int[]>();
	}

	/*
	 * public double getPlayerModel(Player p){ return
	 * contextOdds[game.getActivePlayersSize
	 * ()][game.getGameState().getStateNum()][bets][potOddsBin(p.potOdds())][][]
	 * }
	 */

	/**
	 * Using the opponent model, a phase 3 player calculates the highest
	 * probable hand strength of all the other active players.
	 */
	public double highestAnticipated() {
		double highest = 0.0, tmp, divisor;
		int cx[];
		if (contextQueue.size() == 0)
			return 0.0;
		for (int i = contextQueue.size() - 1; i > contextQueue.size()
				- game.getActivePlayersSize() + 1
				&& i >= 0; i--) {
			// get current contextplayer array from queue of last activities
			// System.out.println("i: " +i);
			cx = contextQueue.get(i);
			divisor = contextOdds[cx[0]][cx[1]][cx[2]][cx[3]][cx[4]][1];
			if (divisor < 2)
				continue;
			tmp = contextOdds[cx[0]][cx[1]][cx[2]][cx[3]][cx[4]][0] / divisor;
			// accumulated strength divided by number of occurrences
			if (tmp > highest)
				highest = tmp;
		}
		return highest;
	}

	/**
	 * 
	 * @param potOdds
	 *            the calculated pot odds
	 * @return cannot have continuous pot odds, so pot odds are placed into one
	 *         of four buckets, or bins. returns bin Id.
	 */
	private int potOddsBin(double potOdds) {
		for (int i = 3; i >= 0; i--)
			if (potOdds > potOddsBins[i])
				return i;
		throw new NullPointerException();
	}
	
	private int raiseBin(int raise) {
		for (int i = 3; i >= 0; i--)
			if (raise > raiseBins[i])
				return i+1;
		return 0;
	}
}
