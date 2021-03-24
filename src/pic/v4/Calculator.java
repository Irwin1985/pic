package pic.v4;

public class Calculator {
	public static String expression = "";
	public static int currentCharPosition = 0;
	public static char Look;
	
	public static void GetChar() {
		if (currentCharPosition < expression.length()) {
			Look = expression.charAt(currentCharPosition);
		}
		currentCharPosition++;
	}
	
	public static int GetNum() {
		int number = 0;
		String tempStr = "";
		if (!Character.isDigit(Look)) {
			System.out.println("Error: Numbers expected.");
			System.exit(0);
		}
		while (Character.isDigit(Look)) {
			tempStr += Look;
			GetChar();
		}
		number = Integer.valueOf(tempStr);
		return number;
	}
	
	public static void Init() {
		GetChar();
	}

	public static void MatchAndEat(char chr) {
		if (Look == chr) {
			GetChar();
		} else {
			System.out.println("Error: Unexpected character " + chr);
			System.exit(0);
		}
	}
	
	public static int Term() {
		int result = Factor();
		while (Look == '*' || Look == '/') {
			switch(Look) {
			case '*':
				result = result * Multiply();
				break;
			case '/':
				result = result / Divide();
				break;
			}
		}
		return result;
	}
	
	public static int Add() {
		MatchAndEat('+');
		return Term();
	}
	
	public static int Subtract() {
		MatchAndEat('-');
		return Term();
	}
	
	public static int ArithmeticExpression() {
		int result = Term();
		while (Look == '+' || Look == '-') {			
			switch (Look) {
			case '+':
				result = result + Add();
				break;
			case '-':
				result = result - Subtract();
				break;		
			}
		}
		return result;
	}	
	
	public static int Factor() {
		int result = 0;
		if (Look == '(') {
			MatchAndEat('(');
			result = ArithmeticExpression();
			MatchAndEat(')');
		} else {
			result = GetNum();
		}
		return result;
	}
	
	public static int Multiply() {
		MatchAndEat('*');
		return Factor();
	}
	
	public static int Divide() {
		MatchAndEat('/');
		return Factor();
	}
	
	public static void main(String args[]) {
		expression = "853+92*10-20/2+771";
		expression += " ";
		System.out.println("Expression: " + expression);
		Init(); // Advances the first char
		int result = ArithmeticExpression();
		System.out.println("Calculation Result: " + result);
	}
}
