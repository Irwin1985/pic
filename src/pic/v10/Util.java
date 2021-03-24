package pic.v10;

import java.util.List;

public class Util {
	public static void Write(Object obj) {
		System.out.println(obj);
	}
	
	public static void Writeln(Object obj) {
		System.out.println(obj);
	}
	
	public static void Writeln() {
		System.out.println();
	}

	public static void PrettyPrint(List<Token> tokens) {
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
}
