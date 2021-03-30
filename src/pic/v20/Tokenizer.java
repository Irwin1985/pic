package pic.v20;

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
		keywords.put("class", TokenType.CLASS);
		keywords.put("self", TokenType.SELF);
	}
	
	private boolean isOp(char chr) {
		boolean addOp = chr == '+' || chr == '-';
		boolean mulOp = chr == '*' || chr == '/';
		boolean compOp = chr == '<' || chr == '>' || chr == '=';
		boolean lgicOp = chr == '!' || chr == '&';
		
		return addOp || mulOp || compOp || lgicOp;
	}
	
	private TokenType findOpType(char firstOperator, char nextChar) {
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
	private boolean isLetter(char chr) {
		return Character.isLetterOrDigit(chr) || chr == '_';
	}
	private boolean isParen(char chr) {
		boolean prntOp = chr == '(' || chr == ')';
		boolean brktOp = chr == '[' || chr == ']';
		boolean brcOp = chr == '{' || chr == '}';
		boolean puncOp = chr == ',';

		return prntOp || brktOp || puncOp || brcOp;
	}
	
	private boolean isPunc(char chr) {
		boolean puncOp = chr == ',';
		boolean colonOp = chr == ':';
		boolean dotOp = chr == '.';

		return puncOp || colonOp || dotOp;
	}
	
	private TokenType findPuncType(char firstOperator) {
		TokenType type = TokenType.UNKNOWN;

		switch (firstOperator) {
		case ',':
			type = TokenType.COMMA;
			break;
		case ':':
			type = TokenType.COLON;
			break;
		case '.':
			type = TokenType.DOT;
			break;
		default:
			break;
		}
		return type;
	}
	
	private TokenType findParenType(char chr) {
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
	
	public List<Token> tokenize(String source){
		
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
				if (isOp(chr)) {
					firstOperator = chr;
					TokenType opType = findOpType(firstOperator, '\0');
					token = new Token(Character.toString(chr), opType);

					state = TokenizeState.OPERATOR;
				} else if (isParen(chr)) {
					TokenType parenType = findParenType(chr);
					lastToken = new Token(Character.toString(chr), parenType); 
					tokens.add(lastToken);
				} else if (Character.isDigit(chr)) {
					tokenText += chr;
					state = TokenizeState.NUMBER;
				} else if (Character.isLetter(chr) || chr == '_') {
					tokenText += chr;
					state = TokenizeState.WORD;
				} else if (chr == '"') {
					state = TokenizeState.STRING;
				} else if (chr == '#') {
					state = TokenizeState.COMMENT;
				} else if (isPunc(chr)) {
					TokenType puncType = findPuncType(chr);
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
				if (Character.isDigit(chr) || chr == '.') {
					tokenText += chr;
				} else {
					TokenType type = TokenType.INTEGER;
					if (tokenText.contains(".")) {
						type = TokenType.NUMBER;
					}
					lastToken = new Token(tokenText, type);
					tokens.add(lastToken);
					tokenText = "";
					state = TokenizeState.DEFAULT;
					index--; // ungetch the last character.
				}
				break;
			case OPERATOR:
				if (isOp(chr)) {
					TokenType opType = findOpType(firstOperator, chr);
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
				if (isLetter(chr)) {
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
}
