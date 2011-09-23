package core;

public class Card implements Comparable {
	
	private int face;
	private Suit suit;
	
	public Card (int f, Suit s){
		if(valid(f, s)){}
		
		this.face = f;
		this.suit = s;
	}
	
	public boolean valid(int f, Suit s){
		if(f >1 && f < 15 && s.SuitToChar() != 'x'){
		return true;	
		}
		return false;
	}
	
	
	public int getFace(){
		return this.face;
	}
	
	public char getSuit(){
		return this.suit.SuitToChar();
	}
	public int getSuitByInt(){
		return this.suit.SuitToInt();
	}
	
	public String toString(){
		return "" + this.suit.SuitToChar() + this.face; 
	}
	
	public void setFace(int f){
		this.face = f;
	}
	
	public void setSuit(Suit s){
		this.suit = s;
	}
	
	/**
	 * 
	 * @param card
	 * @return true if cards are equal
	 */
	public boolean isEqual(Card card){
		return (this.suit == card.suit && this.face == card.face);
	}
	
	
	
	/**
	 * 
	 * @param card
	 * @return a duplicate of input card
	 */
	public Card duplicateCard(Card card){
		return new Card(card.face, card.suit);
	}

	/**
	 * returns a positive integer if this card is larger than specified object
	 */
	public int compareTo(Object o) {
		if(o instanceof Card){
			
			if(this.face > ((Card)o).getFace())return 1;
			if(this.face < ((Card)o).getFace())return -1;
			if(this.face == ((Card)o).getFace())return 0;
		}
			
		return 0;
	}
	
	public static void printCards(Card[] a){
		
		for(Card card : a){
			System.out.print(card.toString() + " ");
		}
		System.out.println();
	}
	
	
}
