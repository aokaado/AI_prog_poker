package core;

import java.util.ArrayList;
import java.util.Collections;

public class CardDeck {

	private ArrayList<Card> cards;

	public CardDeck() {
		resetDeck();
		shuffle();
	}

	/**
	 * builds a new deck
	 */
	public void resetDeck() {
		cards = new ArrayList<Card>();
		for (Suit f : Suit.values())
			for (int i = 2; i < 15; i++)
				cards.add(new Card(i, f));

	}

	public void shuffle() {
		for (int i = 0; i < 52 * 3; i++) {
			int swap = (int) (Math.random() * cards.size());
			Card swapped = cards.get(swap);
			cards.remove(swap);
			cards.add(swapped);
		}
		// TODO increase amount of shuffling ?

	}

	public Card dealCard() {
		Card c = cards.get(cards.size() - 1);
		cards.remove(c);
		return c;
	}

	public void removeSpecificCard(Card c) {
		cards.remove(c);
	}

	public void removeTwoCardsByNumber(int i, int j) {
		if (i > j) {
			cards.remove(i);
			cards.remove(j);
		}
		cards.remove(j);
		cards.remove(i);
	}
	/*
	 * public void removeCards(ArrayList<Card> cs) { Collections.sort(cs);
	 * Collections.reverse(cs); for (Card c: cs) {
	 * cards.remove(c.getSuitByInt()*13+c.getFace()); } }
	 */

}
