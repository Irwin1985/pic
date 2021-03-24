package pic.v7;
import java.util.List;

public class Calculator {
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

	public int Multiply() {
		MatchAndEat(TokenType.MULTIPLY);
		return Factor();
	}
	
	public int Divide() {
		MatchAndEat(TokenType.DIVIDE);
		return Factor();
	}	

	public int Add() {
		MatchAndEat(TokenType.ADD);
		return Term();
	}
	
	public int Subtract() {
		MatchAndEat(TokenType.SUBTRACT);
		return Term();
	}
	
	public int Factor() {
		int result = 0;
		if (CurrentToken().type == TokenType.LEFT_PAREN) {
			MatchAndEat(TokenType.LEFT_PAREN);
			result = ArithmeticExpression();
			MatchAndEat(TokenType.RIGHT_PAREN);
		} else if (CurrentToken().type == TokenType.NUMBER) {
			result = Integer.valueOf(CurrentToken().text);
			MatchAndEat(TokenType.NUMBER);
		}
		return result;
	}

	public int Term() {
		int result = Factor();
		while (CurrentToken().type == TokenType.MULTIPLY ||
				CurrentToken().type == TokenType.DIVIDE) {
			switch(CurrentToken().type) {
			case MULTIPLY:
				result = result * Multiply();
				break;
			case DIVIDE:
				result = result / Divide();
				break;
			default:
				System.out.println("Unknown operator: " + CurrentToken().type);
				System.exit(1);
				break;
			}
		}
		return result;
	}

	public int ArithmeticExpression() {
		int result = Term();
		while (CurrentToken().type == TokenType.ADD ||
				CurrentToken().type == TokenType.SUBTRACT) {			
			switch (CurrentToken().type) {
			case ADD:
				result = result + Add();
				break;
			case SUBTRACT:
				result = result - Subtract();
				break;
			default:
				System.out.println("Unknown operator: " + CurrentToken().type);
				System.exit(1);				
				break;
			}
		}

		return result;
	}
	
	public boolean Relation() {
		int leftExpressionResult = ArithmeticExpression();
		boolean result = false;
		TokenType type = CurrentToken().type;
		if (type == TokenType.EQUAL || type == TokenType.LESS ||
				type == TokenType.GREATER || type == TokenType.LESSEQUAL ||
				type == TokenType.GREATEREQUAL) {
			switch (type) {
			case LESS:
				result = Less(leftExpressionResult);
				break;
			case LESSEQUAL:
				result = LessEqual(leftExpressionResult);
				break;
			case EQUAL:
				result = Equal(leftExpressionResult);
				break;
			case GREATER:
				result = Greater(leftExpressionResult);
				break;
			case GREATEREQUAL:
				result = GreaterEqual(leftExpressionResult);
				break;
			default:
				System.out.println("Unknown operator: " + type);
				System.exit(1);
				break;
			}
		}
		return result;
	}
	
	public boolean Less(int leftExpressionResult) {
		MatchAndEat(TokenType.LESS);
		return leftExpressionResult < ArithmeticExpression();
	}
	
	public boolean LessEqual(int leftExpressionResult) {
		MatchAndEat(TokenType.LESSEQUAL);
		return leftExpressionResult <= ArithmeticExpression();
	}
	
	public boolean Equal(int leftExpressionResult) {
		MatchAndEat(TokenType.EQUAL);
		return leftExpressionResult == ArithmeticExpression();
	}
	
	public boolean Greater(int leftExpressionResult) {
		MatchAndEat(TokenType.GREATER);
		return leftExpressionResult > ArithmeticExpression();
	}
	
	public boolean GreaterEqual(int leftExpressionResult) {
		MatchAndEat(TokenType.GREATEREQUAL);
		return leftExpressionResult >= ArithmeticExpression();
	}
	
	public boolean BooleanFactor() {
		return Relation();
	}
	
	public boolean BooleanTerm() {
		boolean result = BooleanFactor();
		while (CurrentToken().type == TokenType.AND) {
			MatchAndEat(TokenType.AND);
			result = result && BooleanFactor();
		}
		return result;
	}
	
	public boolean BooleanExpression() {
		boolean result = BooleanTerm();
		while (CurrentToken().type == TokenType.OR) {
			MatchAndEat(TokenType.OR);
			result = result || BooleanTerm();
		}
		return result;
	}
	
	public boolean Expression() {
		return BooleanExpression();
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
				" different number and " + opCount + " operators.");
	}
	
	public static void main(String args[]) {
		String expression = "5+7";
		expression += " ";
		
		Calculator calc = new Calculator();
		Tokenizer tokenizer = new Tokenizer();
				
		System.out.println("Expression: " + expression);
		System.out.println("--------------------------");
		calc.tokens = tokenizer.Tokenize(expression);
		// this method should use the internal tokens property
		calc.PrettyPrint(calc.tokens);
		System.out.println("--------------------------");
		
		System.out.println("Result: " + calc.Expression());		
	}
}