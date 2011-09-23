package third_party;

import core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class cards_py {

	/*
	 * enum Suit { C, D, H, S;
	 * 
	 * public char SuitToChar() { switch (this) { case C: return 'C'; case D:
	 * return 'D'; case H: return 'H'; case S: return 'S';
	 * 
	 * } return 'x'; // should come this far anyway }
	 * 
	 * }
	 */

	String _card_value_names_[] = { "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "jack", "queen", "king", "ace" };
	HashMap<Suit, String> _card_suits_ = new HashMap<Suit, String>();

	public cards_py() {
		_card_suits_.put(Suit.C, "Clubs");
		_card_suits_.put(Suit.D, "Diamonds");
		_card_suits_.put(Suit.H, "Hearts");
		_card_suits_.put(Suit.S, "Spades");
	}

	private static void kd_sort(ArrayList<Card> cards, String dir) {
		// printArrayList(cards);
		// System.out.println("sorting");
		Collections.sort(cards);
		// printArrayList(cards);
		if (dir.equals("decrease")) {
			// printArrayList(cards);
			// System.out.println("reversing");
			Collections.reverse(cards);
			// printArrayList(cards);
		}
		// System.out.println("END KD SORT");

	}

	private static void kd_sortP(ArrayList<ArrayList<Card>> cards, String dir) {
		partSort(cards);
		if (dir.equals("decrease")) {
			Collections.reverse(cards);
		}

	}

	private static void partSort(ArrayList<ArrayList<Card>> cards) {
		// printArrayArrayList(cards);
		for (int i = 0; i < cards.size(); i++) {
			for (int j = 1; j < cards.size(); j++) {
				if (cards.get(j - 1).size() > cards.get(j).size()) {
					ArrayList<Card> temp = cards.get(j - 1);
					cards.set(j - 1, cards.get(j));
					cards.set(j, temp);
				}
			}
		}
		// printArrayArrayList(cards);
	}

	private static ArrayList<ArrayList<Card>> partition(ArrayList<Card> cards) {
		kd_sort(cards, "increase"); // prop_func -> getface
		// printArrayList(cards);
		ArrayList<ArrayList<Card>> partition = new ArrayList<ArrayList<Card>>();
		ArrayList<Card> subset = new ArrayList<Card>();
		int last_key = -1;
		// int counter = 0;
		for (Card c : cards) {
			int new_key = c.getFace(); // apply(prop_func on [c])
			if (subset.size() == 0 || !(last_key == new_key)) {
				if (subset.size() > 0) {
					partition.add(subset);
				}
				subset = new ArrayList<Card>();
				subset.add(c);
				last_key = new_key;

			} else {
				subset.add(c);
			}
		}
		if (subset.size() > 0) {
			partition.add(subset);
		}
		// printArrayArrayList(partition);
		return partition;
	}

	private static ArrayList<ArrayList<Card>> sorted_partition(
			ArrayList<Card> cards) {
		ArrayList<ArrayList<Card>> p = partition(cards);
		// printArrayArrayList(p);
		// | eq_func
		// -> x == y
		kd_sortP(p, "decrease"); // prop_func - > length(x)
		return p;
	}

	private static ArrayList<ArrayList<Card>> gen_value_groups(
			ArrayList<Card> cards) {
		ArrayList<Card> cards2 = (ArrayList<Card>) cards.clone();
		return sorted_partition(cards2);
	}

	private static ArrayList<ArrayList<Card>> gen_suit_groups(ArrayList<Card> cards) {

		ArrayList<Card> cards2 = (ArrayList<Card>) cards.clone();
		return sorted_partitionS(cards2);

	}

	private static ArrayList<Card> find_flush(ArrayList<Card> cards) {
		ArrayList<ArrayList<Card>> sgroups = gen_suit_groups(cards);
		if (sgroups.get(0).size() >= 5) {// TODO target_len = 5 ?
			return sgroups.get(0);
		}
		return null;
	}

	private static ArrayList<Card> gen_ordered_cards(ArrayList<Card> cards,
			String dir) {
		ArrayList<Card> cards2 = (ArrayList<Card>) cards.clone();
		// System.out.println("B4 KDSORT");
		// printArrayList(cards2);
		kd_sort(cards2, dir);
		// System.out.println("after KDSORT");
		// printArrayList(cards2);
		return cards2;
	}

	private static ArrayList<Card> scan(ArrayList<Card> cards,
			ArrayList<Card> straight, Card ace) {
		if (straight.size() == 5) {// target_let
			return straight;
		} else if (ace != null && 2 == straight.get(0).getFace()
				&& straight.size() == 4) { // taget_len -1
			straight.add(0, ace);
			return straight;
		} else if (!(cards != null && cards.size() != 0)) {
			return null;
		}

		ArrayList<Card> c = new ArrayList<Card>();
		c.add(cards.get(0));
		cards.remove(0);
		if (c.get(0).getFace() == straight.get(0).getFace() - 1) {
			c.addAll(straight);
			return scan(cards, c, ace);
		} else if (c.get(0).getFace() == straight.get(0).getFace()) {
			return scan(cards, straight, ace);
		} else {
			return scan(cards, c, ace);
		}
	}

	private static ArrayList<Card> find_straight(ArrayList<Card> cards) {
		Card ace = null;
		for (Card c : cards)
			if (c.getFace() == 14)
				ace = c;
		ArrayList<Card> scards = gen_ordered_cards(cards, "decrease");

		ArrayList<Card> top_card = new ArrayList<Card>();
		top_card.add(scards.get(0));
		scards.remove(0);
		return scan(scards, top_card, ace);
	}

	public static ArrayList<Card> max_group_vals(
			ArrayList<ArrayList<Card>> groups, int count) {
		ArrayList<Card> vals = new ArrayList<Card>();
		for (ArrayList<Card> g : groups) {
			vals.add(g.get(0));
		}
		kd_sort(vals, "decrease");
		return subList_a(vals, 0, count-1);
	}

	public static int[] calc_straight_flush_power(ArrayList<Card> cards) {
		int l[] = { 9, cards.get(cards.size() - 1).getFace() };
		return l;
	}

	public static int[] calc_4_kind_power(ArrayList<ArrayList<Card>> cards) {
		ArrayList<Card> c = max_group_vals(subList(cards, 1, cards.size() - 1),
				1);
		int l[] = new int[3];
		l[0] = 8;
		l[1] = cards.get(0).get(0).getFace();
		l[2] = c.get(0).getFace();
		return l;
	}

	public static int[] calc_full_house_power(ArrayList<ArrayList<Card>> cards) {
		int l[] = new int[3];
		l[0] = 7;
		l[1] = cards.get(0).get(0).getFace();
		l[2] = cards.get(1).get(0).getFace();
		return l;
	}

	public static int[] calc_simple_flush_power(ArrayList<Card> flush) {
		ArrayList<Card> new_flush = (ArrayList<Card>) flush.clone();
		kd_sort(new_flush, "decrease");
		
		int l[] = new int[6];
		l[0] = 6;
		int i = 1;
		for (Card c : new_flush){
			l[i++] = c.getFace();
			if(i == 6) break;
		}
		return l;
	}

	public static int[] calc_straight_power(ArrayList<Card> cards) {
		int l[] = { 5, cards.get(cards.size() - 1).getFace() };
		return l;
	}

	public static int[] calc_3_kind_power(ArrayList<ArrayList<Card>> cards) {
		ArrayList<Card> c = max_group_vals(subList(cards, 1, cards.size() - 1),
				2);
		int l[] = new int[4];
		l[0] = 4;
		l[1] = cards.get(0).get(0).getFace();
		int i = 2;
		for (Card the_card : c) {
			l[i++] = the_card.getFace();
			if (i == 4)
				break;
		}
		return l;
	}

	public static int[] calc_2_pair_power(ArrayList<ArrayList<Card>> cards) {
		ArrayList<Card> c = max_group_vals(subList(cards, 2, cards.size() - 1),
				1);
		int l[] = new int[4];
		l[0] = 3;
		l[1] = cards.get(0).get(0).getFace();
		l[2] = cards.get(1).get(0).getFace();
		l[3] = c.get(0).getFace();
		return l;
	}

	public static int[] calc_pair_power(ArrayList<ArrayList<Card>> cards) {
		// printArrayArrayList(cards);
		ArrayList<ArrayList<Card>> sl = subList(cards, 1, cards.size() - 1);
		// printArrayArrayList(sl);
		ArrayList<Card> c = max_group_vals(sl, 3);
		// printArrayList(c);
		int l[] = new int[5];
		l[0] = 2;
		l[1] = cards.get(0).get(0).getFace();
		int i = 2;
		for (Card the_card : c) {
			l[i++] = the_card.getFace();
			if (i == 5)
				break;
		}
		return l;
	}

	public static int[] calc_high_card_power(ArrayList<Card> cards) {
		int l[] = new int[6];
		l[0] = 1;
		int i = 1;
		// System.out.println("before gen");
		// printArrayList(cards);
		ArrayList<Card> cards2 = gen_ordered_cards(cards, "decrease");
		// System.out.println("after gen:");
		// printArrayList(cards2);
		for (Card c : cards2) {
			l[i++] = c.getFace();
			if (i == 6)
				break;
		}
		return l;
	}

	public static ArrayList<ArrayList<Card>> subList(
			ArrayList<ArrayList<Card>> c, int a, int b) {
		ArrayList<ArrayList<Card>> c2 = new ArrayList<ArrayList<Card>>();
		for (int i = a; i <= b; i++)
			c2.add(c.get(i));
		return c2;
	}

	public static ArrayList<Card> subList_a(ArrayList<Card> c, int a, int b) {
		ArrayList<Card> c2 = new ArrayList<Card>();
		for (int i = a; i <= b; i++)
			c2.add(c.get(i));
		return c2;
	}

	public static int[] calcCardsPower(ArrayList<Card> cards) {

		ArrayList<ArrayList<Card>> vgroups = gen_value_groups(cards);
		
		ArrayList<Card> flush = find_flush(cards); // target len = 5 ?
		ArrayList<Card> str_in_flush = null;
		
		if (flush != null) {
			str_in_flush = find_straight(flush); // target_len = 5
		}
//		printArrayArrayList(vgroups);
//		System.out.println(vgroups.get(0).size());

		// printArrayArrayList(vgroups);
		if (flush != null && str_in_flush != null) {
//			System.out.println("str flush");
			return calc_straight_flush_power(str_in_flush);
		} else if (vgroups.get(0).size() == 4) {
//			System.out.println("4k");
			return calc_4_kind_power(vgroups);
		} else if (vgroups.get(0).size() == 3 && vgroups.size() > 1
				&& vgroups.get(1).size() >= 2) {
//			System.out.println("full house");
			return calc_full_house_power(vgroups);
		} else if (flush != null) {
//			System.out.println("flush");
			return calc_simple_flush_power(flush);
		} else {
			ArrayList<Card> straight = find_straight(cards);
			if (straight != null) {
//				System.out.println("straight");
				return calc_straight_power(straight);
			} else if (vgroups.get(0).size() == 3) {
//				System.out.println("3k");
				return calc_3_kind_power(vgroups);
			} else if (vgroups.get(0).size() == 2) {
//				System.out.print("pair->");
				if (vgroups.size() > 1 && vgroups.get(1).size() == 2) {
//					System.out.println("two");
					return calc_2_pair_power(vgroups);
				} else {
//					System.out.println("one");
					return calc_pair_power(vgroups);
				}

			} else {
//				System.out.println("high card");
				return calc_high_card_power(cards);
			}
		}
		//
		//
		//
	}

	private static void kd_sortS(ArrayList<Card> cards, String dir) {
		sortS(cards);
		if (dir.equals("decrease")) {
			Collections.reverse(cards);
		}

	}

	private static void sortS(ArrayList<Card> cards) {
		for (int i = 0; i < cards.size(); i++) {
			for (int j = 1; j < cards.size(); j++) {
				if (cards.get(j - 1).getSuit() > cards.get(j).getSuit()) {
					Card temp = cards.get(j - 1);
					cards.set(j - 1, cards.get(j));
					cards.set(j, temp);
				}
			}
		}
	}

	private static void kd_sortPS(ArrayList<ArrayList<Card>> cards, String dir) {
		partSortS(cards);
		if (dir.equals("decrease")) {
			Collections.reverse(cards);
		}

	}

	private static void partSortS(ArrayList<ArrayList<Card>> cards) {
		for (int i = 0; i < cards.size(); i++) {
			for (int j = 1; j < cards.size(); j++) {
				if (cards.get(j - 1).size() > cards.get(j).size()) {
					ArrayList<Card> temp = cards.get(j - 1);
					cards.set(j - 1, cards.get(j));
					cards.set(j, temp);
				}
			}
		}
	}

	private static ArrayList<ArrayList<Card>> partitionS(ArrayList<Card> cards) {
		//System.out.println("inside partitionS, 426");
		//printArrayList(cards);
		kd_sortS(cards, "increase"); // prop_func -> getface
		//System.out.println("inside partitionS, after kd_sortS");
		//printArrayList(cards);
		ArrayList<ArrayList<Card>> partition = new ArrayList<ArrayList<Card>>();
		ArrayList<Card> subset = new ArrayList<Card>();
		int last_key = -1;
		for (Card c : cards) {
			int new_key = c.getSuit(); // apply(prop_func on [c]) ENDRET TIL GETSUIT! <- (prev GETFACE)
			if (subset.size() == 0 || !(last_key == new_key)) {
				if (subset.size() > 0) {
					partition.add(subset);
				}
				subset = new ArrayList<Card>();
				subset.add(c);
				last_key = new_key;

			} else {
				subset.add(c);
			}
		}
		if (subset.size() > 0) {
			partition.add(subset);
		}
		return partition;
	}

	private static ArrayList<ArrayList<Card>> sorted_partitionS(ArrayList<Card> cards) {
		ArrayList<ArrayList<Card>> p = partitionS(cards); // prop_func ->
		// getface
		// | eq_func
		// -> x == y
		kd_sortPS(p, "decrease"); // prop_func - > length(x)
		return p;
	}

	public static void printArrayList(ArrayList<Card> cards) {
		for (Card c : cards) {
			System.out.print(c.getSuit() + " " + c.getFace() + "\t");
		}
		System.out.print("\n------AL------\n");
	}

	public static void printArrayArrayList(ArrayList<ArrayList<Card>> cards) {
		for (ArrayList<Card> card : cards) {
			printArrayList(card);

		}
		System.out.print("------AAL------\n");

	}

	public static void main(String args[]) {
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(new Card(9, Suit.D));
		cards.add(new Card(6, Suit.S));
		cards.add(new Card(6, Suit.H));
		cards.add(new Card(3, Suit.S));
		cards.add(new Card(11, Suit.D));
		cards.add(new Card(13, Suit.D));
		cards.add(new Card(3, Suit.C));
		
		int values[] = calcCardsPower(cards);
		for (int i = 0; i < values.length; i++) {
			System.out.println(i + ":" + values[i]);
		}
	}
}
