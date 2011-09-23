package core;

public enum Suit {

	C, D, H, S;
	
	public char SuitToChar(){
		switch (this) {
		case C:
			return 'C';
		case D:
			return 'D';
		case H:
			return 'H';
		case S:
			return 'S';
			
		}
		return 'x'; //should never come this far anyway
	}
	public int SuitToInt(){
		switch (this) {
		case C:
			return 0;
		case D:
			return 1;
		case H:
			return 2;
		case S:
			return 3;
			
		}
		return -1; //should never come this far anyway
	}
}
