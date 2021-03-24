package pic.v5;

public class Token {
	public String text;
	public String type;
	
	public Token(String text, String type) {
		this.text = text;
		this.type = type;
	}
	
	public String toString() {
		return " Type: " + type + " Text: " + text;
	}
}
