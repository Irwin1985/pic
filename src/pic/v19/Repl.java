package pic.v19;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Repl {
	
	public static void main(String args[]) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		while (true) {
			System.out.print("> ");
			line = reader.readLine();
			if (line.length() > 0) {
				Tokenizer tokenizer = new Tokenizer();
				List<Token> tokens = tokenizer.Tokenize(line);
				for (Token token : tokens) {
					Util.Writeln(token.toString());
				}
			}			
		}
				
	}
}
