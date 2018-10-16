public class gameData {
	//class to store the number for players to guess, also more infos for thats
	int digit;
	String number;
	
	//constructor
	public gameData(int digit, String number) {
		this.digit=digit;
		this.number=number;
	}
	
	//getter and setter can be used if needed
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
}
