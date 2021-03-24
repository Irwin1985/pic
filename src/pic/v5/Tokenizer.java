package pic.v5;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {

	public boolean IsOp(char chr) {
		return  chr == '+' || chr == '-' || 
				chr == '*' || chr == '/' || 
				chr == '(' || chr == ')';
	}
	
	public String FindOpType(char chr) {
		String type = "UNKNOWN";
		switch(chr) {
		case '+':
			type = "ADD";
			break;
		case '-':
			type = "SUBTRACT";
			break;
		case '*':
			type = "MULTIPLY";
			break;
		case '/':
			type = "DIVIDE";
			break;
		case '(':
			type = "LEFT_PAREN";
			break;
		case ')':
			type = "RIGHT_PAREN";
			break;			
		}
		return type;
	}
	
	public List<Token> Tokenize(String source){
		List<Token> tokens = new ArrayList<Token>();
		String token = "";
		String state = "DEFAULT";
		
		for (int index = 0; index < source.length(); index++) {
			char chr = source.charAt(index);
			switch(state) {
			case "DEFAULT":
				String opType = FindOpType(chr);
				if (IsOp(chr)) {
					tokens.add(new Token(Character.toString(chr), opType));					
				} else if (Character.isDigit(chr)) {
					token += chr;
					state = "NUMBER";
				}
				break;
			case "NUMBER":
				if (Character.isDigit(chr)) {
					token += chr;
				} else {
					tokens.add(new Token(token, "NUMBER"));
					token = "";
					state = "DEFAULT";
					index -= 1; // ungetch the last character.
				}
				break;
			}
		}
		return tokens;
	}
	
	public void PrettyPrint(List<Token> tokens) {
		int numberCount = 0;
		int opCount = 0;
		for (Token token : tokens) {
			if (token.type.equals("NUMBER")) {
				System.out.println("Number....: " + token.text);
				numberCount += 1;
			} else {
				System.out.println("Operator..: " + token.type);
				opCount += 1;
			}
		}
		System.out.println("You have got " + numberCount +
				" different number and " + opCount +
				" operators.");
	}
	
	public static void main(String args[]) {
		String expression = "219+341+19";
		expression += " ";
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.Tokenize(expression);
		
		System.out.println("--------------");
		tokenizer.PrettyPrint(tokens);		
	}
}
