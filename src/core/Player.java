package core;

import java.util.ArrayList;

import third_party.cards_py;

public class Player {
	protected Card[] hand = new Card[2];
	protected String name;
	protected int stack; // money; stack of chips
	protected int currentBet; //amount gone into the pot for this particular hand.
	public static final int STARTSTACK = 10000;
	protected int power[];
	protected TexasHoldEm game;
	
	//Statistical fields, not for any real use.
	protected int wincount = 0; // counts number of wins
	protected int showdownCount = 0; // counts number of showdowns the player has a part in.
	protected int easywins = 0;
	
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

	/**
	 * 
	 * @return intelligently calculated amount of chips to bet
	 */
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

	/**
	 * Resets currentBet amount, as hand is over.
	 */
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
			if(c != null)pHand.add(c);
		this.setPower(cards_py.calcCardsPower(pHand));	
	}

	/**
	 * returns ID for use when looking up strength in db
	 */
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

	/**
	 * 
	 * @return smallest amount necessary to bet in order to stay in the game.
	 */
	public int minimumBet() {
		return game.getHighbet() - currentBet;
	}
	
	/**
	 * 
	 * @param tableSizeLimit denotes how many of the cards on the table we include when calculating handstrength.
	 * If 5, use as many cards as available, but if less, use only that amount, even if table contains river as well.
	 * Usually used with 5 as parameter, except when opponent modeling for phase 3 players.
	 * @return The hand's strength, calculated by comparing hand to all other possible hands.
	 */
	public double handStrength(int tableSizeLimit) {
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
						((double) (win + (draw / 2)) 
								/ 
								(double) (win + draw + loss)),
						k);
	}
	
	public void incrementWinCount(){
		this.wincount++;
	}
	
	public int getWinCount(){
		return this.wincount;
	}
	
	public void incrementshowdownCount(){
		this.showdownCount++;
	}
	
	public int getShowdhownCount(){
		return this.showdownCount;
	}
	
	public void incrementeasyWinsCount(){
		this.easywins++;
	}
	
	public int getEasyWinsCount(){
		return this.easywins;
	}
	
	
	
}


