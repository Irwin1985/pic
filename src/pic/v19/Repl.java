package pic.v19;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Repl {
	
	public static void main(String args[]) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		Environment env = new Environment();
		Util.Writeln("Hi there! I'm a programming language!");
		Util.Writeln("Please type some commands below!");
		while (true) {
			Util.Write("> ");
			line = reader.readLine();
			if (line.length() > 0) {
				Tokenizer tokenizer = new Tokenizer();
				List<Token> tokens = tokenizer.Tokenize(line);
				//Util.PrettyPrint(tokens);
				
				Parser parser = new Parser(tokens);
				Evaluator evaluator = new Evaluator();
				Node program = parser.Program();
				// TODO: check for errors
				SysObject result = evaluator.Eval(program, env);
				if (result != null) {					
					Util.Writeln(result.Resolve());				
				}
			}			
		}
				
	}
}
