package pic.v5;
import java.util.List;

public class Calculator {
	public int currentTokenPosition = 0;
	public List<Token> tokens;
	
	public Token GetToken(int offset) {
		if (currentTokenPosition + offset >= tokens.size()) {
			return new Token("", "NO_TOKEN");
		}
		return tokens.get(currentTokenPosition + offset);
	}

	public Token CurrentToken() {
		return GetToken(0);
	}
	
	public void EatToken(int offset) {
		currentTokenPosition += offset;
	}
	
	public Token MatchAndEat(String type) {
		Token token = CurrentToken();
		
		if (!CurrentToken().type.equals(type)) {
			System.out.println("Saw " + token.type + " but " + type + " expected");
			System.exit(0);
		}
		EatToken(1);

		return token;
	}

	public int Multiply() {
		MatchAndEat("MULTIPLY");
		return Factor();
	}
	
	public int Divide() {
		MatchAndEat("DIVIDE");
		return Factor();
	}	

	public int Add() {
		MatchAndEat("ADD");
		return Term();
	}
	
	public int Subtract() {
		MatchAndEat("SUBTRACT");
		return Term();
	}
	
	public int Factor() {
		int result = 0;
		if (CurrentToken().type.equals("LEFT_PAREN")) {
			MatchAndEat("LEFT_PAREN");
			result = ArithmeticExpression();
			MatchAndEat("RIGHT_PAREN");
		} else if (CurrentToken().type.equals("NUMBER")) {
			result = Integer.valueOf(CurrentToken().text);
			MatchAndEat("NUMBER");
		}
		return result;
	}

	public int Term() {
		int result = Factor();
		while (CurrentToken().type.equals("MULTIPLY") ||
				CurrentToken().type.equals("DIVIDE")) {
			switch(CurrentToken().type) {
			case "MULTIPLY":
				result = result * Multiply();
				break;
			case "DIVIDE":
				result = result / Divide();
				break;
			}
		}
		return result;
	}

	public int ArithmeticExpression() {
		int result = Term();
		while (CurrentToken().type.equals("ADD") ||
				CurrentToken().type.equals("SUBTRACT")) {			
			switch (CurrentToken().type) {
			case "ADD":
				result = result + Add();
				break;
			case "SUBTRACT":
				result = result - Subtract();
				break;		
			}
		}
		return result;
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
				" different number and " + opCount + " operators.");
	}
	
	public static void main(String args[]) {
		String expression = "((853+92*5)*10-20/2+771)";
		expression += " ";
		Calculator calc = new Calculator();
		Tokenizer tokenizer = new Tokenizer();
				
		System.out.println("Expression: " + expression);
		System.out.println("--------------------------");
		calc.tokens = tokenizer.Tokenize(expression);
		// this method should use the internal tokens property
		calc.PrettyPrint(calc.tokens);
		System.out.println("--------------------------");
		System.out.println("Expression Result: " + calc.ArithmeticExpression());
		
	}
}