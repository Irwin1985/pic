package pic.v19;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Interpreter {

	public void Interpret(String source, boolean debug) {
		
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser(tokenizer.Tokenize(source));
		Evaluator evaluator = new Evaluator();
		
		if (debug) {
			DumpTokens(parser);
		}
		Environment env = new Environment();
		Node program = parser.Program();
		SysObject result = evaluator.Eval(program, env);

		if (result != null) {
			Util.Writeln(result.Resolve());
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
