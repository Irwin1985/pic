package pic.v19;

public class WaitCommand extends Node {
	public Parser parser;
	
	public WaitCommand() {
		// Nothing
	}
	
	public WaitCommand(Parser parser) {
		this.parser = parser;
	}
	
	public Object eval() {
		Integer waitAmount = Integer.valueOf(parser.getVariable("interval").toString());
		try {
			Thread.sleep(waitAmount.intValue());
		} catch (Exception e) {
			Util.Writeln("Error in WaitNode.eval() method " + e);
		}
		return waitAmount;
	}
}
