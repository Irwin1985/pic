package pic.v6;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {

	public boolean IsOp(char chr) {
		boolean aritOp = chr == '+' || chr == '-' || 
						 chr == '*' || chr == '/';
		return aritOp;
	}
	
	public TokenType FindOpType(char firstOperator) {
		TokenType type = TokenType.UNKNOWN;
		switch(firstOperator) {
		case '+':
			type = TokenType.ADD;
			break;
		case '-':
			type = TokenType.SUBTRACT;
			break;
		case '*':
			type = TokenType.MULTIPLY;
			break;
		case '/':
			type = TokenType.DIVIDE;
			break;			
		}
		return type;
	}
	
	public boolean IsParen(char chr) {
		boolean prntOp = chr == '(' || chr == ')';
		return prntOp;
	}
	
	public TokenType FindParenType(char chr) {
		TokenType type = TokenType.UNKNOWN;
		switch(chr) {
		case '(':
			type = TokenType.LEFT_PAREN;
			break;
		case ')':
			type = TokenType.RIGHT_PAREN;
			break;
		}
		return type;
	}
	
	public List<Token> Tokenize(String source){
		
		List<Token> tokens = new ArrayList<Token>();
		String token = "";
		TokenizeState state = TokenizeState.DEFAULT;
		
		for (int i = 0; i < source.length(); i++) {
			
			char chr = source.charAt(i);
			
			switch(state) {
			case DEFAULT:
				TokenType opType = FindOpType(chr);
				if (IsOp(chr)) {
					tokens.add(new Token(Character.toString(chr), opType));					
				} else if (IsParen(chr)) {
					TokenType parenType = FindParenType(chr);
					tokens.add(new Token(Character.toString(chr), parenType));
				} else if (Character.isDigit(chr)) {
					token += chr;
					state = TokenizeState.NUMBER;
				}
				break;
			case NUMBER:
				if (Character.isDigit(chr)) {
					token += chr;
				} else {
					tokens.add(new Token(token, TokenType.NUMBER));
					token = "";
					state = TokenizeState.DEFAULT;
					i -= 1; // ungetch the last character.
				}
				break;
			default:
				System.out.println("Unknown state: " + state);
				System.exit(1);
				break;
			}
		}
		return tokens;
	}
	
	public void PrettyPrint(List<Token> tokens) {
		int numberCount = 0;
		int opCount = 0;
		for (Token token : tokens) {
			if (token.type == TokenType.NUMBER) {
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
