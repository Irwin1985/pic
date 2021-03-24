package pic.v9;
import java.util.List;

public class Parser {
	public int currentTokenPosition = 0;
	public List<Token> tokens;
	
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
		}
		return result;
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