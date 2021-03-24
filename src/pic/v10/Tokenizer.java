package pic.v10;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {

	public boolean IsOp(char chr) {
		boolean addOp = chr == '+' || chr == '-';
		boolean mulOp = chr == '*' || chr == '/';
		boolean compOp = chr == '<' || chr == '>' || chr == '=';
		boolean lgicOp = chr == '!' || chr == '&';
		
		return addOp || mulOp || compOp || lgicOp;
	}
	
	public TokenType FindOpType(char firstOperator, char nextChar) {
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
		case '<':
			type = TokenType.LESS;
			if (nextChar == '=') {
				type = TokenType.LESSEQUAL;
			}
			break;
		case '>':
			type = TokenType.GREATER;
			if (nextChar == '=') {
				type = TokenType.GREATEREQUAL;
			}
			break;
		case '=':
			type = TokenType.ASSIGMENT;
			if (nextChar == '=') {
				type = TokenType.EQUAL;
			}
			break;
		case '!':
			type = TokenType.NOT;
			if (nextChar == '=') {
				type = TokenType.NOTEQUAL;
			}
			break;
		case '|':
			type = TokenType.OR;
			break;
		case '&':
			type = TokenType.AND;
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
		
		Token token = null;
		String tokenText = "";
		char firstOperator = '\0';
		TokenizeState state = TokenizeState.DEFAULT;
		
		for (int index = 0; index < source.length(); index++) {
			
			char chr = source.charAt(index);
			
			switch(state) {
			case DEFAULT:
				if (IsOp(chr)) {
					firstOperator = chr;
					TokenType opType = FindOpType(firstOperator, '\0');
					token = new Token(Character.toString(chr), opType);

					state = TokenizeState.OPERATOR;
				} else if (IsParen(chr)) {
					TokenType parenType = FindParenType(chr);
					tokens.add(new Token(Character.toString(chr), parenType));
				} else if (Character.isDigit(chr)) {
					tokenText += chr;
					state = TokenizeState.NUMBER;
				}
				break;
			case NUMBER:
				if (Character.isDigit(chr)) {
					tokenText += chr;
				} else {
					tokens.add(new Token(tokenText, TokenType.NUMBER));
					tokenText = "";
					state = TokenizeState.DEFAULT;
					index--; // ungetch the last character.
				}
				break;
			case OPERATOR:
				if (IsOp(chr)) {
					TokenType opType = FindOpType(firstOperator, chr);
					token = new Token(Character.toString(firstOperator) + 
							Character.toString(chr), opType);
				} else {
					tokens.add(token);
					state = TokenizeState.DEFAULT;
					index--;
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
