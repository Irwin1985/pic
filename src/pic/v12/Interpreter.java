package pic.v12;

import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Interpreter {
	public void Interpret(String source, boolean debug) {
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser(tokenizer.Tokenize(source));
		
		if (debug) {
			DumpTokens(parser);
		}
		
		parser.MatchAndEat(TokenType.SCRIPT);
		List<Node> script = new LinkedList<Node>();
		script = parser.Block();
		
		for (Node statement : script) {
			statement.eval();
		}
	}
	
	public void DumpTokens(Parser parser) {
		for (Token token : parser.getTokens()) {
			Util.Writeln(token.toString());
		}
	}
	
	public String ReadFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public static void main(String args[]) {
		boolean debug = false;
		
		if (args.length < 1) {
			Util.Writeln("Usage: Demo <script>");
			return;
		} else if (args.length > 1){
			if (args[1].equals("debug")) {
				debug = true;
			}
		}
		
		Interpreter interpreter = new Interpreter();
		String sourceCode = "";
		try {			
			sourceCode = interpreter.ReadFile(args[0]);
		} catch (Exception e) {
			Util.Writeln("Error while reading the script.con: " + e);
		}
		interpreter.Interpret(sourceCode, debug);
	}
}
