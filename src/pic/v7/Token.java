package pic.v7;

public class Token {
	public final String text;
	public final TokenType type;
	
	public Token(String text, TokenType type) {
		this.text = text;
		this.type = type;
	}
	
	public String toString() {
		return " Type: " + type + " Text: " + text;
	}
}
