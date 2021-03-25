package pic.v15;

import java.util.List;
import java.util.LinkedList;
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
		} else if (IsNumber()) {
			Token token = MatchAndEat(TokenType.NUMBER); 
			result = new NumberNode(Integer.valueOf(token.text));
		} else if (IsString()) {
			Token token = MatchAndEat(TokenType.STRING);
			result = new StringNode(token.text);
		} else if (IskeyWord()) {
			result = Variable();
		}
		return result;
	}
	
	public Node Variable() {
		Token token = MatchAndEat(TokenType.KEYWORD);
		Node node = new VariableNode(token.text, this);
		
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
	
	public boolean IskeyWord() {
		return CurrentToken().type == TokenType.KEYWORD;
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
	
	public Node Expression() {
		return BooleanExpression();
	}

	public BlockNode Block() {
		List<Node> statements = new LinkedList<Node>();
		while (CurrentToken().type != TokenType.END) {
			statements.add(Statement());
		}
		MatchAndEat(TokenType.END);
		
		return new BlockNode(statements);
	}
	
	public Node Statement() {
		Node node = null;
		TokenType type = CurrentToken().type;
		if (IsAssignment()) {
			node = Assignment();
		} else if (type == TokenType.PRINT) {
			MatchAndEat(TokenType.PRINT);
			node = new PrintNode(Expression(), "sameline");
		} 
		else if (type == TokenType.PRINTLN) {
			MatchAndEat(TokenType.PRINTLN);
			node = new PrintNode(Expression(), "newline");			
		}
		else if (type == TokenType.WAIT) {
			MatchAndEat(TokenType.WAIT);
			node = new WaitNode(Expression());
		}
		else if (IsWhile()) {
			node = While();
		}
		else if (IsIfElse()) {
			node = If();
		}
		else {
			Util.Writeln("Unknown language construct: " + CurrentToken().type.name());
			System.exit(1);
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
	
	public boolean IsAssignment() {
		return CurrentToken().type == TokenType.KEYWORD && 
				NextToken().type == TokenType.ASSIGMENT;
	}
	
	public boolean IsWhile() {
		return CurrentToken().type == TokenType.WHILE;
	}
	
	public boolean IsIfElse() {
		TokenType type = CurrentToken().type;
		return type == TokenType.IF || type == TokenType.ELSE;
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
	
	public Node Assignment() {
		Node node = null;
		String name = MatchAndEat(TokenType.KEYWORD).text;
		MatchAndEat(TokenType.ASSIGMENT);
		
		Node value = Expression();
		node = new AssignmentNode(name, value, this);
		
		return node;
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