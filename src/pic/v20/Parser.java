package pic.v20;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	public int currentTokenPosition = 0;
	public List<Token> tokens;

	public Parser() {}
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
		
	private Token getToken(int offset) {
		if (currentTokenPosition + offset >= tokens.size()) {
			return new Token("", TokenType.EOF);
		}
		return tokens.get(currentTokenPosition + offset);
	}

	private Token currentToken() {
		return getToken(0);
	}
	
	private Token nextToken() {
		return getToken(1);
	}
	
	private void eatToken(int offset) {
		currentTokenPosition += offset;
	}
	
	private Token matchAndEat(TokenType type) {
		Token token = currentToken();
		
		if (currentToken().type != type) {
			System.out.println("Saw " + token.type + " but " + type + " expected");
			System.exit(0);
		}
		eatToken(1);

		return token;
	}

	private AstNode multiply() {
		matchAndEat(TokenType.MULTIPLY);
		return factor();
	}
	
	private AstNode divide() {
		matchAndEat(TokenType.DIVIDE);
		return factor();
	}	

	private AstNode add() {
		matchAndEat(TokenType.ADD);
		return term();
	}
	
	private AstNode subtract() {
		matchAndEat(TokenType.SUBTRACT);
		return term();
	}
	
	private AstNode factor() {
		AstNode result = null;
		if (currentToken().type == TokenType.LEFT_PAREN) {
			matchAndEat(TokenType.LEFT_PAREN);
			result = parseExpression();
			matchAndEat(TokenType.RIGHT_PAREN);
		} 
		else if (isNumber()) {
			Token token = matchAndEat(currentToken().type); 
			result = new AstNumber(Double.valueOf(token.text));
		} 
		else if (isString()) {
			Token token = matchAndEat(TokenType.STRING);
			result = new AstString(token.text);
		} 
		else if (isBoolean()) {
			TokenType type = currentToken().type;
			result = new AstBoolean(type == TokenType.TRUE);
			matchAndEat(type);
		}
		else if (currentToken().type == TokenType.NULL) {
			matchAndEat(TokenType.NULL);
			result = new AstNull();
		}
		else if (isIdentifier()) {
			result = variable();
		}
		return result;
	}
	
	private boolean isBoolean() {
		TokenType type = currentToken().type;
		return type == TokenType.TRUE || type == TokenType.FALSE; 
	}
	
	private AstNode variable() {
		AstNode node = null;
		if (nextToken().type == TokenType.LEFT_PAREN) {
			node = parseFunctionCall();
		} else {			
			Token token = matchAndEat(TokenType.IDENT);
			AstIdentifier varNode = new AstIdentifier(token.text);
			
			// Handle array access here
			if (currentToken().type == TokenType.LEFT_BRACKET) {
				matchAndEat(TokenType.LEFT_BRACKET);
				AstNode key = parseExpression();
				matchAndEat(TokenType.RIGHT_BRACKET);				
				return new AstLookupCollection(token.text, key);
			} else {			
				return varNode;
			}
		}
		return node;
	}
	
	private boolean isRelOp(TokenType type) {
		boolean lgOps = type == TokenType.LESS || type == TokenType.GREATER;
		boolean eqOps = type == TokenType.EQUAL || type == TokenType.NOTEQUAL;
		boolean mdOps = type == TokenType.LESSEQUAL || type == TokenType.GREATEREQUAL;

		return eqOps || lgOps || mdOps;
	}
	
	private boolean isNumber() {
		return currentToken().type == TokenType.NUMBER ||
				currentToken().type == TokenType.INTEGER;
	}
	
	private boolean isString() {
		return currentToken().type == TokenType.STRING;
	}
	
	private boolean isIdentifier() {
		return currentToken().type == TokenType.IDENT;
	}
	
	private AstNode signedFactor() {
		if (currentToken().type == TokenType.SUBTRACT) {
			matchAndEat(TokenType.SUBTRACT);
			AstNode node = new AstNegOp(factor());
			return node;
		}
		return factor();
	}

	private AstNode term() {
		AstNode node = signedFactor();
		while (currentToken().type == TokenType.MULTIPLY ||
				currentToken().type == TokenType.DIVIDE) {
			switch(currentToken().type) {
			case MULTIPLY:
				node = new AstBinOp(TokenType.MULTIPLY, node, multiply());
				break;
			case DIVIDE:
				node = new AstBinOp(TokenType.DIVIDE, node, divide());
				break;
			default:
				System.out.println("Unknown operator: " + currentToken().type);
				System.exit(1);
				break;
			}
		}
		return node;
	}

	private AstNode arithmeticExpression() {
		AstNode node = term();
		while (currentToken().type == TokenType.ADD ||
				currentToken().type == TokenType.SUBTRACT) {			
			switch (currentToken().type) {
			case ADD:
				node = new AstBinOp(TokenType.ADD, node, add());
				break;
			case SUBTRACT:
				node = new AstBinOp(TokenType.SUBTRACT, node, subtract());
				break;
			default:
				System.out.println("Unknown operator: " + currentToken().type);
				System.exit(1);				
				break;
			}
		}
		return node;
	}
	
	private AstNode relation() {
		AstNode node = arithmeticExpression();
		TokenType type = currentToken().type;
		if (isRelOp(currentToken().type)) {
			switch (type) {
			case LESS:
				node = less(node);
				break;
			case LESSEQUAL:
				node = lessEqual(node);
				break;
			case EQUAL:
				node = equal(node);
				break;
			case NOTEQUAL:
				node = notEqual(node);
				break;
			case GREATER:
				node = greater(node);
				break;
			case GREATEREQUAL:
				node = greaterEqual(node);
				break;
			default:
				System.out.println("Unknown operator: " + type);
				System.exit(1);
				break;
			}
		}
		return node;
	}
	
	private AstNode less(AstNode node) {
		matchAndEat(TokenType.LESS);
		return new AstBinOp(TokenType.LESS, node, arithmeticExpression());
	}
	
	private AstNode lessEqual(AstNode node) {
		matchAndEat(TokenType.LESSEQUAL);
		return new AstBinOp(TokenType.LESSEQUAL, node, arithmeticExpression());
	}
	
	private AstNode equal(AstNode node) {
		matchAndEat(TokenType.EQUAL);
		return new AstBinOp(TokenType.EQUAL, node, arithmeticExpression());
	}
	
	private AstNode notEqual(AstNode node) {
		matchAndEat(TokenType.NOTEQUAL);
		return new AstBinOp(TokenType.NOTEQUAL, node, arithmeticExpression());
	}
	
	private AstNode greater(AstNode node) {
		matchAndEat(TokenType.GREATER);
		return new AstBinOp(TokenType.GREATER, node, arithmeticExpression());
	}
	
	private AstNode greaterEqual(AstNode node) {
		matchAndEat(TokenType.GREATEREQUAL);
		return new AstBinOp(TokenType.GREATEREQUAL, node, arithmeticExpression());
	}
	
	private AstNode booleanFactor() {
		return relation();
	}
	
	private AstNode notFactor() {
		if (currentToken().type == TokenType.NOT) {
			matchAndEat(TokenType.NOT);
			AstNode node = booleanFactor();
			return new AstNotOp(node);
		}
		return booleanFactor();
	}
	
	private AstNode booleanTerm() {
		AstNode node = notFactor();
		while (currentToken().type == TokenType.AND) {
			matchAndEat(TokenType.AND);
			node = new AstBinOp(TokenType.AND, node, booleanFactor());
		}
		return node;
	}
	
	private AstNode booleanExpression() {
		AstNode node = booleanTerm();
		while (currentToken().type == TokenType.OR) {
			matchAndEat(TokenType.OR);
			node = new AstBinOp(TokenType.OR, node, booleanTerm());
		}
		return node;
	}
	
	private AstNode parseExpression() {
		AstNode exp = booleanExpression();
		SkipNewLine();
		return exp;
	}
	
	private AstNode statement() {
		AstNode node = null;
		if (isAssignment()) {
			node = parseAssignment();
		}
		else if (isWhile()) {
			node = parseWhile();
		}
		else if (isIfElse()) {
			node = parseIf();
		}
		else if (isCollectionUpdate()) {
			node = parseCollectionUpdate();
		}
		else if (isFunctionDef()) {
			node = parseFunctionDefinition();
		}
		else if (currentToken().type == TokenType.RETURN) {
			matchAndEat(TokenType.RETURN);
			node = new AstReturn(parseExpression());
		}
		else if (isClass()) {
			node = parseClass();
		}
		else {
			node = parseExpression();
		}
		return node;
	}
	
	private boolean isClass() {
		return currentToken().type == TokenType.CLASS;		
	}
	
	private AstNode parseClass() {
		return null;
	}
	
	private boolean isFunctionDef() {
		TokenType type = currentToken().type;
		TokenType nextType = nextToken().type;
		
		return type == TokenType.DEF && nextType == TokenType.IDENT;
	}
	
	private boolean isAssignment() {
		TokenType type = currentToken().type;
		TokenType nextType = nextToken().type;
		return type == TokenType.IDENT && nextType == TokenType.ASSIGMENT;
	}
	
	private boolean isWhile() {
		return currentToken().type == TokenType.WHILE;
	}
	
	private boolean isIfElse() {
		return currentToken().type == TokenType.IF;
	}

	private boolean isCollectionUpdate() {
		int currentPosition = currentTokenPosition; // Save current token position
		boolean result = false;
		int counter = 0;
		while (currentToken().type != TokenType.EOF && currentToken().type != TokenType.NEWLINE) {
			counter++;
			if (counter == 1) {
				if (currentToken().type == TokenType.IDENT) {					
					matchAndEat(currentToken().type); // skip token
					continue; // first token IDENT ok.
				} else {
					break; // is not IDENT
				}
			} else if (counter == 2) {
				if (currentToken().type == TokenType.LEFT_BRACKET) {					
					matchAndEat(currentToken().type); // skip token
					continue; // second token LEFT_BRACKET ok.
				} else {
					break; // is not LEFT_BRACKET
				}
			}
			if (currentToken().type == TokenType.ASSIGMENT) {
				result = true;
				break;
			}
			matchAndEat(currentToken().type); // skip token
		}
		currentTokenPosition = currentPosition;
		return result;
	}
	
	private AstNode parseFunctionDefinition() {
		matchAndEat(TokenType.DEF);
		String functionName = matchAndEat(TokenType.IDENT).text;
		List<String> parameters = null;

		// optional parentesis
		if (currentToken().type == TokenType.LEFT_PAREN) {			
			matchAndEat(TokenType.LEFT_PAREN);
		}
		if (currentToken().type != TokenType.RIGHT_PAREN) {			
			parameters = parseFunctionDefParameters();
		}
		if (currentToken().type == TokenType.RIGHT_PAREN) {			
			matchAndEat(TokenType.RIGHT_PAREN);
		}

		AstBlock functionBody = parseBlock();
		AstFunction function = new AstFunction(functionName, functionBody, parameters);

		AstNode functionVariable = new AstAssignment(functionName, function, this);		
		return functionVariable;
	}
	
	private AstNode parseFunctionCall() {
		String functionName = matchAndEat(TokenType.IDENT).text;
		List<AstNode> actualParameters = null;

		matchAndEat(TokenType.LEFT_PAREN);
		if (currentToken().type != TokenType.RIGHT_PAREN) {			
			actualParameters = parseFunctionCallParameters();
		}
		matchAndEat(TokenType.RIGHT_PAREN);
		
		return new AstFunctionCall(functionName, actualParameters);
	}

	private List<AstNode> parseFunctionCallParameters() {
		List<AstNode> actualParameters = null;
		AstNode expression = parseExpression();
		if (expression != null) {
			actualParameters = new ArrayList<AstNode>();
			actualParameters.add(expression);
			while (currentToken().type == TokenType.COMMA) {
				matchAndEat(TokenType.COMMA);
				actualParameters.add(parseExpression());
			}
		}
		return actualParameters;
	}
	
	private List<String> parseFunctionDefParameters() {
		List<String> parameters = null;
		if (currentToken().type == TokenType.IDENT) {
			parameters = new ArrayList<String>();
			parameters.add(matchAndEat(TokenType.IDENT).text);
			
			while (currentToken().type == TokenType.COMMA) {
				matchAndEat(TokenType.COMMA);
				parameters.add(matchAndEat(TokenType.IDENT).text);
			}
		}
		return parameters;
	}
	
	private AstNode parseCollectionUpdate() {
		String name = matchAndEat(TokenType.IDENT).text;
		
		matchAndEat(TokenType.LEFT_BRACKET);
		AstNode key = parseExpression();
		matchAndEat(TokenType.RIGHT_BRACKET);
		
		matchAndEat(TokenType.ASSIGMENT);
		// get the right hand side of the assignment
		AstNode rightSideExpr = parseExpression();
		
		return new AstCollectionUpdate(name, key, rightSideExpr);
	}
	
	private AstNode parseIf() {
		AstNode condition = null;
		AstBlock thenPart = null, elsePart = null;
		
		matchAndEat(TokenType.IF);
		condition = parseExpression();
		// fill the thenPart node block
		SkipNewLine();
		thenPart = new AstBlock();
		while (currentToken().type != TokenType.EOF) {
			TokenType type = currentToken().type; 
			if (type == TokenType.ELSE || type == TokenType.END) {
				if (type == TokenType.END) {
					matchAndEat(TokenType.END);
				}
				break;
			}
			AstNode stmt = statement();
			if (stmt != null) {
				thenPart.statements.add(stmt);
			}
		}
		
		if (currentToken().type == TokenType.ELSE) {
			matchAndEat(TokenType.ELSE);
			if (currentToken().type == TokenType.IF) {
				List<AstNode> elseBlock = new ArrayList<AstNode>();
				elseBlock.add(parseIf());
				elsePart = new AstBlock(elseBlock);
			} else {				
				elsePart = parseBlock();
			}
		}
		return new AstIf(condition, thenPart, elsePart);
	}
	
	private AstNode parseWhile() {
		AstNode condition, body;
		matchAndEat(TokenType.WHILE);
		
		condition = parseExpression();		
		body = parseBlock();
		
		return new AstWhile(condition, body);		
	}	

	// assignment ::= identifier ('=' | '[') expression
	private AstNode parseAssignment() {
		AstNode node = null;
		String name = matchAndEat(TokenType.IDENT).text;
		matchAndEat(TokenType.ASSIGMENT);

		if (currentToken().type == TokenType.LEFT_BRACKET) {
			// Array assignment
			node = parseArrayDefinition(name);
		} 
		else if (currentToken().type == TokenType.LEFT_BRACE) {
			// Dictionary assignment
			node = parseDictionaryDefinition(name);
		}
		else {
			// Variable assignment
			AstNode value = parseExpression();
			node = new AstAssignment(name, value, this);
		}	
		
		return node;
	}
	// dictionary   ::= identifier '=' '{' dictElements? '}'
	// dictElements ::= keyValuePair (',' keyValuePair)*
	// keyValuePair ::= string ':' expression
	private AstNode parseDictionaryDefinition(String name) {
		HashMap<String, AstNode> elements = new HashMap<String, AstNode>();
		matchAndEat(TokenType.LEFT_BRACE);
		
		if (currentToken().type != TokenType.RIGHT_BRACE) {
			AstString key = (AstString)parseExpression();
			matchAndEat(TokenType.COLON);
			elements.put(key.text, parseExpression());
			
			while (currentToken().type == TokenType.COMMA) {
				matchAndEat(TokenType.COMMA);
				key = (AstString)parseExpression();
				matchAndEat(TokenType.COLON);
				elements.put(key.text, parseExpression());
			}
		}
		matchAndEat(TokenType.RIGHT_BRACE);

		return new AstHashAssignment(name, elements);
	}
	
	private AstNode parseArrayDefinition(String name) {
		List<AstNode> elements = new ArrayList<AstNode>();
		matchAndEat(TokenType.LEFT_BRACKET);

		if (currentToken().type != TokenType.RIGHT_BRACKET) {
			elements.add(parseExpression());
			while (currentToken().type == TokenType.COMMA) {
				matchAndEat(TokenType.COMMA);
				elements.add(parseExpression());
			}
		}
		matchAndEat(TokenType.RIGHT_BRACKET);

		return new AstArrayAssignment(name, elements);		
	}
	
	public AstNode parseProgram() {
		if (currentToken().type == TokenType.SCRIPT) {			
			matchAndEat(TokenType.SCRIPT);
		}
		SkipNewLine();
		AstProgram program = new AstProgram();
		program.statements = new ArrayList<AstNode>();

		while (currentToken().type != TokenType.END && currentToken().type != TokenType.EOF) {
			AstNode stmt = statement();
			if (stmt != null) {
				program.statements.add(stmt);
			}
		}
		if (currentToken().type == TokenType.END) {			
			matchAndEat(TokenType.END);
		}
		
		return program;		
	}
	
	private AstBlock parseBlock() {
		AstBlock block = new AstBlock();		
		SkipNewLine();
		
		while (currentToken().type != TokenType.END) {
			AstNode stmt = statement();
			if (stmt != null) {				
				block.statements.add(stmt);
			}
		}
		matchAndEat(TokenType.END);
		SkipNewLine();
		
		return block;
	}

	private void SkipNewLine() {
		if (currentToken().type == TokenType.NEWLINE) {
			matchAndEat(TokenType.NEWLINE);
		}
	}
}