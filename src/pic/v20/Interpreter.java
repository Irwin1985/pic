package pic.v20;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Interpreter {

	public void Interpret(String source, boolean debug) {
		
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(source);
		Parser parser = new Parser(tokens);
		Evaluator evaluator = new Evaluator();
		
		if (debug) {
			DumpTokens(tokens);
		}
		Environment env = new Environment();
		AstNode program = parser.parseProgram();
		SysObject result = evaluator.Eval(program, env);

		if (result != null) {
			Util.Writeln(result.Resolve());
		}
	}
	
	public void DumpTokens(List<Token> tokens) {
		for (Token token : tokens) {
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
