package core;

import java.util.ArrayList;

import third_party.cards_py;

public class Player {
	protected Card[] hand = new Card[2];
	protected String name;
	protected int stack; // money; stack of chips
	protected int currentBet;
	public static final int STARTSTACK = 10000;
	protected int power[]; // TODO b�r endres senere, gj�r det enkelt f�rst
	protected TexasHoldEm game;

	public Player(String name, TexasHoldEm game) {
		this.name = name;
		this.stack = STARTSTACK;
		this.game = game;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param money
	 *            sets players stack equal to money
	 */
	public void setStack(int money) {
		this.stack = money;
	}

	/**
	 * 
	 * @param money
	 *            added after winning a round
	 */
	public void recieveMoneyFromWin(int money) {
		this.stack += money;
	}

	/**
	 * 
	 * @param bet
	 *            remove bet amount from player stack
	 */
	public void loseMoneyfromBet(int bet) {
		this.stack -= bet;
	}

	public int bet() {
		return placeBet();
	}

	public int getCurrentBet() {
		return currentBet;
	}

	public void setCurrentBet(int currentbet) {
		this.currentBet = currentbet;
	}

	public void forceBet(int chips) {
		currentBet = chips;
		loseMoneyfromBet(chips);
	}

	protected int placeBet() {
		// Placeholder for overridden method
		throw new NullPointerException("dummymethod accidentally being used");
	}

	public void setPower(int power[]) {
		this.power = power;
	}

	public int[] getPower() {
		return this.power;
	}

	protected void endBettingRound() {
		currentBet = 0;
	}

	public int getStack() {
		return this.stack;
	}

	public Card[] getHand() {
		return hand;
	}

	public ArrayList<Card> getHandAsList() {
		ArrayList<Card> handList = new ArrayList<Card>();
		handList.add(hand[0]);
		handList.add(hand[1]);
		return handList;
	}

	public static ArrayList<Card> getHandAsList(Card cards[]) {
		ArrayList<Card> handList = new ArrayList<Card>();
		handList.add(cards[0]);
		handList.add(cards[1]);
		return handList;
	}

	public void assignCard(Card c) {
		if (this.hand[0] == null) {
			this.hand[0] = c;
			return;
		}
		this.hand[1] = c;
	}

	public void clearCards() {
		hand = new Card[2];
	}

	public void printCards() {
		System.out.println(this.hand[0].toString() + " and "
				+ this.hand[1].toString());
	}

	public void calculatePower() {

		ArrayList<Card> pHand = getHandAsList();
		for (Card c : game.getTable())
			pHand.add(c);

		this.setPower(cards_py.calcCardsPower(pHand));
	}

	public int getHoleID() {
		if (hand[0].getFace() < hand[1].getFace()) {
			Card tmp = hand[0];
			hand[0] = hand[1];
			hand[1] = tmp;
		}
		return (hand[0].getFace() * 100 + hand[1].getFace());

	}

	public boolean isSuited() {
		if (hand[0].getSuit() == hand[1].getSuit())
			return true;
		return false;
	}

	public boolean handContainsCard(Card c) {
		for (Card card : hand) {
			if (card.isEqual(c))
				return true;
		}
		return false;
	}

	public double potOdds() {
		return (double) (minimumBet())
				/ (double) (minimumBet() + game.getPot());
	}

	public int minimumBet() {
		return game.getHighbet() - currentBet;
	}
	
	public double handStrength(int tableSizeLimit) {
		// CardDeck cd = new CardDeck();
		// cd.resetDeck();
		int win = 0, draw = 0, loss = 0, k = game.getActivePlayersSize();
		Card[] c = new Card[2];
		for (Suit s : Suit.values()) { // C-D-H-S
			for (int i = 2; i < 15; i++) {
				c[0] = new Card(i, s);
				for (Suit suit : Suit.values()) {
					for (int j = 2; j < 15; j++) {
						c[1] = new Card(j, suit);
						if (c[1].isEqual(c[0]))
							continue;
						if (handContainsCard(c[1]) || handContainsCard(c[0])
								|| game.containsCard(c[0])
								|| game.containsCard(c[1]))
							continue;

						int test = TexasHoldEm.compareHands(Player
								.getHandAsList(hand), Player.getHandAsList(c),
								game.getTable(), tableSizeLimit);
						if (test == 1) {
							win++;
						} else if (test == -1) {
							loss++;
						} else
							draw++;

					}
				}
			}
		}
		return Math
				.pow(
						((double) (win + (draw / 2)) / (double) (win + draw + loss)),
						k);
	}
}
