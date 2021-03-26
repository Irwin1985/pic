package pic.v19;

public class PrintlnCommand extends Node {
	public Parser parser;
	
	public PrintlnCommand() {
		// Nothing
	}
	
	public PrintlnCommand(Parser parser) {
		this.parser = parser;
	}
	
	public Object eval() {
		Object writee = parser.getVariable("writee");
		Util.Writeln(writee);
		
		return writee;
	}
}
