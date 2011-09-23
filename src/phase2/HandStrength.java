package phase2;

import java.util.ArrayList;

import javax.net.ssl.HandshakeCompletedEvent;

import core.*;

public class HandStrength {

	ArrayList<Card[]> hands;
	Database db;

	public HandStrength() {
		hands = new ArrayList<Card[]>();
		db = new Database();
	}

	public void genHoldCards() {
		Card[] c, c2;
		for (int i = 2; i < 15; i++) {
			for (int j = i + 1; j < 15; j++) {
				if (i == j)
					continue;
				c = new Card[2];
				c[0] = new Card(j, Suit.C);
				c[1] = new Card(i, Suit.D);
				hands.add(c);

				c2 = new Card[2];
				c2[0] = new Card(j, Suit.C);
				c2[1] = new Card(i, Suit.C);
				hands.add(c2);
			}
		}
		/*
		 * for (int i = 2; i < 15; i++) { for (int j = i + 1; j < 15; j++) { if
		 * (i == j) continue; Card[] c = new Card[2]; c[0] = new Card(j,
		 * Suit.C); c[1] = new Card(i, Suit.C); hands.add(c); } }
		 * System.out.println("generated " + hands.size() + " hands");
		 */
		for (int i = 2; i < 15; i++) {
			c = new Card[2];
			c[0] = new Card(i, Suit.C);
			c[1] = new Card(i, Suit.D);
			hands.add(c);
		}
		System.out.println("generated " + hands.size() + " hands");
	}

	public static void main(String args[]) {

		HandStrength hs = new HandStrength();
		hs.genHoldCards();
		/*
		 * for (Card[] testingHand : hs.hands) System.out.println(testingHand[0]
		 * == null ? "null" : testingHand[0] .toString() + " " + testingHand[1]
		 * == null ? "null" : testingHand[1] .toString());
		 * Card.printCards(testingHand);
		 */

		hs.performRolloutSimulation();
		/*
		 * hs.db.connect();hs.db.execute(
		 * "insert into hole_strength values(1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1,1.1)"
		 * ); hs.db.disconnect();
		 */

	}

	private void performRolloutSimulation() {
		CardDeck cd = new CardDeck();
		Card[] community = new Card[5];
		ArrayList<Card[]> players = new ArrayList<Card[]>();
		int w, d, l, pair = 0, counter = 0;
		db.connect();
		double strengths[] = new double[18];
		for (Card[] testingHand : hands) { // simulation for each hole
			int offSet = ((testingHand[0].getSuit() == testingHand[1].getSuit()) ? 0
					: 9); // adding offset to be used later
			// if cards are suited
			// IN DB: suited-----unsuited
			int holeId = testingHand[0].getFace() * 100
					+ testingHand[1].getFace();
			for (int p = 1; p < 10; p++) { // simulations for each player size
				w = d = l = 0;
				for (int k = 0; k < 1000; k++) { // number of simulations per
					// state
					cd.resetDeck();
					cd.removeTwoCardsByNumber(13
							* testingHand[0].getSuitByInt()
							+ testingHand[0].getFace() - 2, 13
							* testingHand[1].getSuitByInt()
							+ testingHand[1].getFace() - 2);
					cd.shuffle();

					// create new table
					for (int i = 0; i < 5; i++)
						community[i] = cd.dealCard();

					// give out new cards to other players
					players.clear();
					for (int i = 0; i < p; i++) {
						Card[] c = { cd.dealCard(), cd.dealCard() };
						players.add(c);
					}
					int wc = 0;
					int lc = 0;
					for (Card cs[] : players) {
						int test = TexasHoldEm.compareHands(Player
								.getHandAsList(testingHand), Player
								.getHandAsList(cs), community, 5);
						if (test == 1) {
							wc++;
						} else if (test == -1) {
							lc++;
						}
					}
					if (wc == p)
						w++;
					else if (lc > 0)
						l++;
					else
						d++;
				}
				// TODO possibly add back draws ?
				strengths[offSet + p - 1] = ((double) w)
						/ ((double) (w + d + l));
//				System.out.println(w + " " + d + " " + l);
			}
			Card.printCards(testingHand);
			System.out.println(++counter + "/169");
			pair++;
			if (pair % 2 == 0
					|| (testingHand[0].getFace() == testingHand[1].getFace())) {
				String values = "";
				for (int i = 0; i < 18; i++)
					values += ", " + strengths[i];
				values = "insert into hole_strength values(" + holeId + values
						+ ");";

				db.execute(values);
				pair = 0;
				strengths = new double[18];
			}

		}
		db.disconnect();
	}
}
