package pic.v19;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Tokenizer {

	// keywords
	private HashMap<String, TokenType> keywords;
	
	public Tokenizer() {
		// Fill keywords dictionary
		keywords = new HashMap<String, TokenType>();

		keywords.put("def", TokenType.DEF);
		keywords.put("if", TokenType.IF);
		keywords.put("else", TokenType.ELSE);
		keywords.put("while", TokenType.WHILE);
		keywords.put("script", TokenType.SCRIPT);
		keywords.put("end", TokenType.END);
		keywords.put("ident", TokenType.IDENT);
		keywords.put("true", TokenType.TRUE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("null", TokenType.NULL);
		keywords.put("and", TokenType.AND);
		keywords.put("or", TokenType.OR);
		keywords.put("return", TokenType.RETURN);
	}
	
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
		boolean brktOp = chr == '[' || chr == ']';
		boolean brcOp = chr == '{' || chr == '}';
		boolean puncOp = chr == ',';

		return prntOp || brktOp || puncOp || brcOp;
	}
	
	public boolean IsPunc(char chr) {
		boolean puncOp = chr == ',';
		boolean colonOp = chr == ':';

		return puncOp || colonOp;
	}
	
	public TokenType FindPuncType(char firstOperator) {
		TokenType type = TokenType.UNKNOWN;

		switch (firstOperator) {
		case ',':
			type = TokenType.COMMA;
			break;
		case ':':
			type = TokenType.COLON;
			break;
		default:
			break;
		}
		return type;
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
		case '[':
			type = TokenType.LEFT_BRACKET;
			break;
		case ']':
			type = TokenType.RIGHT_BRACKET;
			break;
		case '{':
			type = TokenType.LEFT_BRACE;
			break;
		case '}':
			type = TokenType.RIGHT_BRACE;
			break;
		case ',':
			type = TokenType.COMMA;
			break;		
		}
		return type;
	}
	
	public List<Token> Tokenize(String source){
		
		List<Token> tokens = new ArrayList<Token>();
		Token lastToken = null;
		
		source += " ";
		
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
					lastToken = new Token(Character.toString(chr), parenType); 
					tokens.add(lastToken);
				} else if (Character.isDigit(chr)) {
					tokenText += chr;
					state = TokenizeState.NUMBER;
				} else if (Character.isLetter(chr)) {
					tokenText += chr;
					state = TokenizeState.WORD;
				} else if (chr == '"') {
					state = TokenizeState.STRING;
				} else if (chr == '#') {
					state = TokenizeState.COMMENT;
				} else if (IsPunc(chr)) {
					TokenType puncType = FindPuncType(chr);
					lastToken = new Token(Character.toString(chr), puncType);
					tokens.add(lastToken);
				} else if (Character.isWhitespace(chr)) {
					if (chr == '\n') {
						boolean addToken = false;
						addToken = (lastToken != null && 
								lastToken.type != TokenType.NEWLINE);
						if (addToken) {							
							lastToken = new Token("NEWLINE", TokenType.NEWLINE);
							tokens.add(lastToken);
						}
					}
				}
				else {
					Util.Writeln("Unknown character: " + chr);
					System.exit(1);
				}
				break;
			case NUMBER:
				if (Character.isDigit(chr)) {
					tokenText += chr;
				} else {
					lastToken = new Token(tokenText, TokenType.NUMBER);
					tokens.add(lastToken);
					tokenText = "";
					state = TokenizeState.DEFAULT;
					index--; // ungetch the last character.
				}
				break;
			case OPERATOR:
				if (IsOp(chr)) {
					TokenType opType = FindOpType(firstOperator, chr);
					lastToken = new Token(Character.toString(firstOperator) + 
							Character.toString(chr), opType);
					token = lastToken;
				} else {
					tokens.add(token);
					state = TokenizeState.DEFAULT;
					index--;
				}
				break;
			case WORD:
				if (Character.isLetterOrDigit(chr)) {
					tokenText += chr;
				} else {					
					TokenType type = keywords.getOrDefault(tokenText, TokenType.IDENT);
					lastToken = new Token(tokenText, type);
					tokens.add(lastToken);
					tokenText = "";
					state = TokenizeState.DEFAULT;
					index--;
				}
				break;
			case STRING:
				if (chr == '"') {
					lastToken = new Token(tokenText, TokenType.STRING);
					tokens.add(lastToken);
					tokenText = "";
					state = TokenizeState.DEFAULT;
				} else {
					tokenText += chr;
				}
				break;
			case COMMENT:
				if (chr == '\n') {
					state = TokenizeState.DEFAULT;
				}
				break;
			default:
				Util.Writeln("Unknown state: " + state);
				System.exit(1);
				break;
			}
		}
		// add eof token
		tokens.add(new Token("", TokenType.EOF));
		
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
