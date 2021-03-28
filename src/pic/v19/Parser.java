package pic.v19;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	public int currentTokenPosition = 0;
	public List<Token> tokens;
	public HashMap<String, Object> symbolTable = new HashMap<String, Object>();

	public Parser() {
		// Nothing
	}
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public List<Token> getTokens() {
		return tokens;
	}
	
	public Token GetToken(int offset) {
		if (currentTokenPosition + offset >= tokens.size()) {
			return new Token("", TokenType.EOF);
		}
		return tokens.get(currentTokenPosition + offset);
	}

	public Token CurrentToken() {
		return GetToken(0);
	}
	
	public Token NextToken() {
		return GetToken(1);
	}
	
	public void EatToken(int offset) {
		currentTokenPosition += offset;
	}
	
	public Token MatchAndEat(TokenType type) {
		Token token = CurrentToken();
		
		if (CurrentToken().type != type) {
			System.out.println("Saw " + token.type + " but " + type + " expected");
			System.exit(0);
		}
		EatToken(1);

		return token;
	}

	public Node Multiply() {
		MatchAndEat(TokenType.MULTIPLY);
		return Factor();
	}
	
	public Node Divide() {
		MatchAndEat(TokenType.DIVIDE);
		return Factor();
	}	

	public Node Add() {
		MatchAndEat(TokenType.ADD);
		return Term();
	}
	
	public Node Subtract() {
		MatchAndEat(TokenType.SUBTRACT);
		return Term();
	}
	
	public Node Factor() {
		Node result = null;
		if (CurrentToken().type == TokenType.LEFT_PAREN) {
			MatchAndEat(TokenType.LEFT_PAREN);
			result = Expression();
			MatchAndEat(TokenType.RIGHT_PAREN);
		} 
		else if (IsNumber()) {
			Token token = MatchAndEat(TokenType.NUMBER); 
			result = new NumberNode(Integer.valueOf(token.text));
		} 
		else if (IsString()) {
			Token token = MatchAndEat(TokenType.STRING);
			result = new StringNode(token.text);
		} 
		else if (IsBoolean()) {
			TokenType type = CurrentToken().type;
			result = new BooleanNode(type == TokenType.TRUE);
			MatchAndEat(type);
		}
		else if (CurrentToken().type == TokenType.NULL) {
			MatchAndEat(TokenType.NULL);
			result = new NullNode();
		}
		else if (IsIdentifier()) {
			result = Variable();
		}
		return result;
	}
	
	private boolean IsArrayAccess() {
		return CurrentToken().type == TokenType.IDENT && 
				NextToken().type == TokenType.LEFT_BRACKET;
	}
	
	private boolean IsBoolean() {
		TokenType type = CurrentToken().type;
		return type == TokenType.TRUE || type == TokenType.FALSE; 
	}
	
	public Node Variable() {
		Node node = null;
		if (NextToken().type == TokenType.LEFT_PAREN) {
			node = FunctionCall();
		} else {			
			Token token = MatchAndEat(TokenType.IDENT);
			VariableNode varNode = new VariableNode(token.text, this);
			
			// Handle array access here
			if (CurrentToken().type == TokenType.LEFT_BRACKET) {
				MatchAndEat(TokenType.LEFT_BRACKET);
				Node key = Expression();
				MatchAndEat(TokenType.RIGHT_BRACKET);				
				return new ArrayAccessNode(token.text, key);
			} else {			
				return varNode;
			}
		}
		return node;
	}
	
	public boolean IsRelOp(TokenType type) {
		boolean lgOps = type == TokenType.LESS || type == TokenType.GREATER;
		boolean eqOps = type == TokenType.EQUAL || type == TokenType.NOTEQUAL;
		boolean mdOps = type == TokenType.LESSEQUAL || type == TokenType.GREATEREQUAL;

		return eqOps || lgOps || mdOps;
	}
	
	public boolean IsNumber() {
		return CurrentToken().type == TokenType.NUMBER;
	}
	
	public boolean IsString() {
		return CurrentToken().type == TokenType.STRING;
	}
	
	public boolean IsIdentifier() {
		return CurrentToken().type == TokenType.IDENT;
	}
	
	public Node SignedFactor() {
		if (CurrentToken().type == TokenType.SUBTRACT) {
			MatchAndEat(TokenType.SUBTRACT);
			Node node = new NegOpNode(Factor());
			return node;
		}
		return Factor();
	}

	public Node Term() {
		Node node = SignedFactor();
		while (CurrentToken().type == TokenType.MULTIPLY ||
				CurrentToken().type == TokenType.DIVIDE) {
			switch(CurrentToken().type) {
			case MULTIPLY:
				node = new BinOpNode(TokenType.MULTIPLY, node, Multiply());
				break;
			case DIVIDE:
				node = new BinOpNode(TokenType.DIVIDE, node, Divide());
				break;
			default:
				System.out.println("Unknown operator: " + CurrentToken().type);
				System.exit(1);
				break;
			}
		}
		return node;
	}

	public Node ArithmeticExpression() {
		Node node = Term();
		while (CurrentToken().type == TokenType.ADD ||
				CurrentToken().type == TokenType.SUBTRACT) {			
			switch (CurrentToken().type) {
			case ADD:
				node = new BinOpNode(TokenType.ADD, node, Add());
				break;
			case SUBTRACT:
				node = new BinOpNode(TokenType.SUBTRACT, node, Subtract());
				break;
			default:
				System.out.println("Unknown operator: " + CurrentToken().type);
				System.exit(1);				
				break;
			}
		}
		return node;
	}
	
	public Node Relation() {
		Node node = ArithmeticExpression();
		TokenType type = CurrentToken().type;
		if (IsRelOp(CurrentToken().type)) {
			switch (type) {
			case LESS:
				node = Less(node);
				break;
			case LESSEQUAL:
				node = LessEqual(node);
				break;
			case EQUAL:
				node = Equal(node);
				break;
			case GREATER:
				node = Greater(node);
				break;
			case GREATEREQUAL:
				node = GreaterEqual(node);
				break;
			default:
				System.out.println("Unknown operator: " + type);
				System.exit(1);
				break;
			}
		}
		return node;
	}
	
	public Node Less(Node node) {
		MatchAndEat(TokenType.LESS);
		return new BinOpNode(TokenType.LESS, node, ArithmeticExpression());
	}
	
	public Node LessEqual(Node node) {
		MatchAndEat(TokenType.LESSEQUAL);
		return new BinOpNode(TokenType.LESSEQUAL, node, ArithmeticExpression());
	}
	
	public Node Equal(Node node) {
		MatchAndEat(TokenType.EQUAL);
		return new BinOpNode(TokenType.EQUAL, node, ArithmeticExpression());
	}
	
	public Node Greater(Node node) {
		MatchAndEat(TokenType.GREATER);
		return new BinOpNode(TokenType.GREATER, node, ArithmeticExpression());
	}
	
	public Node GreaterEqual(Node node) {
		MatchAndEat(TokenType.GREATEREQUAL);
		return new BinOpNode(TokenType.GREATEREQUAL, node, ArithmeticExpression());
	}
	
	public Node BooleanFactor() {
		return Relation();
	}
	
	public Node NotFactor() {
		if (CurrentToken().type == TokenType.NOT) {
			MatchAndEat(TokenType.NOT);
			Node node = BooleanFactor();
			return new NotOpNode(node);
		}
		return BooleanFactor();
	}
	
	public Node BooleanTerm() {
		Node node = NotFactor();
		while (CurrentToken().type == TokenType.AND) {
			MatchAndEat(TokenType.AND);
			node = new BinOpNode(TokenType.AND, node, BooleanFactor());
		}
		return node;
	}
	
	public Node BooleanExpression() {
		Node node = BooleanTerm();
		while (CurrentToken().type == TokenType.OR) {
			MatchAndEat(TokenType.OR);
			node = new BinOpNode(TokenType.OR, node, BooleanTerm());
		}
		return node;
	}
	
	private Node Expression() {
		Node exp = BooleanExpression();
		SkipNewLine();
		return exp;
	}
	
	public Node Statement() {
		Node node = null;
		if (IsAssignment()) {
			node = Assignment();
		}
		else if (IsWhile()) {
			node = While();
		}
		else if (IsIfElse()) {
			node = If();
		}
		else if (IsArrayUpdate()) {
			node = ArrayUpdate();
		}
		else if (IsFunctionDef()) {
			node = FunctionDefinition();
		}
		else if (IsFunctionCall()) {
			node = FunctionCall();
		}
		else {
			node = Expression();
		}
		return node;
	}	

	// ~~~~Symbol Table Methods Start~~~~
	public Object setVariable(String name, Object value) {
		symbolTable.put(name, value);
		return value;
	}
	
	public Object getVariable(String name) {
		Object value = symbolTable.get(name);
		if (value != null) {
			return value;
		}
		return null;
	}
	// ~~~~Symbol Table Methods End~~~~
	
	public boolean IsFunctionDef() {
		TokenType type = CurrentToken().type;
		TokenType nextType = NextToken().type;
		
		return type == TokenType.DEF && nextType == TokenType.IDENT;
	}	
	
	public boolean IsFunctionCall() {
		return CurrentToken().type == TokenType.IDENT && 
				NextToken().type == TokenType.LEFT_PAREN;
	}
	
	public boolean IsAssignment() {
		TokenType type = CurrentToken().type;
		TokenType nextType = NextToken().type;
		return type == TokenType.IDENT && nextType == TokenType.ASSIGMENT;
	}
	
	public boolean IsWhile() {
		return CurrentToken().type == TokenType.WHILE;
	}
	
	public boolean IsIfElse() {
		TokenType type = CurrentToken().type;
		return type == TokenType.IF || type == TokenType.ELSE;
	}

	public boolean IsArrayUpdate() {
		int currentPosition = currentTokenPosition; // Save current token position
		boolean result = false;
		int counter = 0;
		while (CurrentToken().type != TokenType.EOF && CurrentToken().type != TokenType.NEWLINE) {
			counter++;
			if (counter == 1) {
				if (CurrentToken().type == TokenType.IDENT) {					
					MatchAndEat(CurrentToken().type); // skip token
					continue; // first token IDENT ok.
				} else {
					break; // is not IDENT
				}
			} else if (counter == 2) {
				if (CurrentToken().type == TokenType.LEFT_BRACKET) {					
					MatchAndEat(CurrentToken().type); // skip token
					continue; // second token LEFT_BRACKET ok.
				} else {
					break; // is not LEFT_BRACKET
				}
			}
			if (CurrentToken().type == TokenType.ASSIGMENT) {
				result = true;
				break;
			}
			MatchAndEat(CurrentToken().type); // skip token
		}
		currentTokenPosition = currentPosition;
		return result;
	}
	
	public Node FunctionDefinition() {
		MatchAndEat(TokenType.DEF);
		String functionName = MatchAndEat(TokenType.IDENT).text;
		
		MatchAndEat(TokenType.LEFT_PAREN);
		List<Parameter> parameters = FunctionDefParameters();
		MatchAndEat(TokenType.RIGHT_PAREN);
		
		Node functionBody = Block();
		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, this);
		
		return functionVariable;
	}
	
	public Node FunctionCall() {
		String functionName = MatchAndEat(TokenType.IDENT).text;
		
		Node calleeFunctionName = new VariableNode(functionName, this);
		MatchAndEat(TokenType.LEFT_PAREN);
		List<Parameter> actualParameters = FunctionCallParameters();
		MatchAndEat(TokenType.RIGHT_PAREN);
		
		Node functionCallNode = new FunctionCallNode(calleeFunctionName, 
				actualParameters, this);
		
		return functionCallNode;
	}
	
	public Object ExecuteFunction(Function function, List<BoundParameter> boundParameters) {
		HashMap<String, Object> savedSymbolTable = new HashMap<String, Object> (symbolTable);
		
		for (int index = 0; index < boundParameters.size(); index++) {
			BoundParameter param = (BoundParameter) boundParameters.get(index);
			setVariable(param.getName(), param.getValue());
		}
		// Eval function
		Object ret = function.eval();
		
		// Restore symbolTable
		symbolTable = savedSymbolTable;
		
		return ret;
	}
	
	public List<Parameter> FunctionCallParameters() {
		List<Parameter> actualParameters = null;
		Node expression = Expression();
		if (expression != null) {
			actualParameters = new ArrayList<Parameter>();
			actualParameters.add(new Parameter(expression));
			while (CurrentToken().type == TokenType.COMMA) {
				MatchAndEat(TokenType.COMMA);
				actualParameters.add(new Parameter(Expression()));
			}
		}
		return actualParameters;
	}
	
	public List<Parameter> FunctionDefParameters() {
		List<Parameter> parameters = null;
		if (CurrentToken().type == TokenType.IDENT) {
			parameters = new ArrayList<Parameter>();
			parameters.add(new Parameter(MatchAndEat(TokenType.IDENT).text));
			
			while (CurrentToken().type == TokenType.COMMA) {
				MatchAndEat(TokenType.COMMA);
				parameters.add(new Parameter(MatchAndEat(TokenType.IDENT).text));
			}
		}
		return parameters;
	}
	
	public Node ArrayUpdate() {
		String arrayName = MatchAndEat(TokenType.IDENT).text;
		
		MatchAndEat(TokenType.LEFT_BRACKET);
		Node indexExpr = Expression();
		MatchAndEat(TokenType.RIGHT_BRACKET);
		
		MatchAndEat(TokenType.ASSIGMENT);
		// get the right hand side of the assignment
		Node rightSideExpr = Expression();
		
		return new ArrayUpdateNode(arrayName, indexExpr, rightSideExpr);
	}
	
	public Node If() {
		Node condition = null, thenPart = null, elsePart = null;
		
		MatchAndEat(TokenType.IF);
		condition = Expression();
		thenPart = Block();
		
		if (CurrentToken().type == TokenType.ELSE) {
			MatchAndEat(TokenType.ELSE);
			if (CurrentToken().type == TokenType.IF) {
				elsePart = If();
			} else {				
				elsePart = Block();
			}
		}
		return new IfNode(condition, thenPart, elsePart);
	}
	
	public Node While() {
		Node condition, body;
		MatchAndEat(TokenType.WHILE);
		
		condition = Expression();		
		body = Block();
		
		return new WhileNode(condition, body);		
	}	

	// assignment ::= identifier ('=' | '[') expression
	public Node Assignment() {
		Node node = null;
		String name = MatchAndEat(TokenType.IDENT).text;
		MatchAndEat(TokenType.ASSIGMENT);

		// check for array assignment
		if (CurrentToken().type == TokenType.LEFT_BRACKET) {
			node = ArrayDefinition(name);
		} else {			
			Node value = Expression();
			node = new AssignmentNode(name, value, this);
		}		
		
		return node;
	}
	
	public Node ArrayDefinition(String name) {
		List<Node> elements = new ArrayList<Node>();
		MatchAndEat(TokenType.LEFT_BRACKET);

		if (CurrentToken().type != TokenType.RIGHT_BRACKET) {
			elements.add(Expression());
			while (CurrentToken().type == TokenType.COMMA) {
				MatchAndEat(TokenType.COMMA);
				elements.add(Expression());
			}
		}
		MatchAndEat(TokenType.RIGHT_BRACKET);

		return new ArrayAssignmentNode(name, elements);		
	}
	
	public Node Program() {
		if (CurrentToken().type == TokenType.SCRIPT) {			
			MatchAndEat(TokenType.SCRIPT);
		}
		SkipNewLine();
		ProgramNode program = new ProgramNode();
		program.statements = new ArrayList<Node>();

		while (CurrentToken().type != TokenType.END && CurrentToken().type != TokenType.EOF) {
			Node stmt = Statement();
			if (stmt != null) {
				program.statements.add(stmt);
			}
		}
		if (CurrentToken().type == TokenType.END) {			
			MatchAndEat(TokenType.END);
		}
		
		return program;		
	}
	
	public BlockNode Block() {
		BlockNode block = new BlockNode();		
		SkipNewLine();
		
		while (CurrentToken().type != TokenType.END) {
			Node stmt = Statement();
			if (stmt != null) {				
				block.statements.add(stmt);
			}
		}
		MatchAndEat(TokenType.END);
		SkipNewLine();
		
		return block;
	}

	private void SkipNewLine() {
		if (CurrentToken().type == TokenType.NEWLINE) {
			MatchAndEat(TokenType.NEWLINE);
		}
	}
	
	public static void main(String args[]) {
		String expression = "!(-100 <= 100)";
		expression += " ";
		
		Parser parser = new Parser();
		Tokenizer tokenizer = new Tokenizer();
				
		Util.Writeln("Expression: " + expression);
		Util.Writeln("--------------------------");
		parser.tokens = tokenizer.Tokenize(expression);
		// this method should use the internal tokens property
		Util.PrettyPrint(parser.tokens);
		Util.Writeln("--------------------------");
		
		Node result = parser.Expression();
		Util.Writeln(result.eval());		
	}
}