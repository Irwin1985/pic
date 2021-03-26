package pic.v19;

public class PrintCommand extends Node {
	public Parser parser;
	
	public PrintCommand() {
		// Nothing
	}
	
	public PrintCommand(Parser parser) {
		this.parser = parser;
	}
	
	public Object eval() {
		Object writee = parser.getVariable("writee");
		Util.Write(writee);

		return writee;
	}
}
