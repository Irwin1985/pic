package pic.v13;

public class VariableNode extends Node {
	public String varName;
	public Parser parser;
	
	public VariableNode() {
		// Nothing
	}
	
	public VariableNode(String varName, Parser parser) {
		this.varName = varName;
		this.parser = parser;
	}
	
	public Object eval() {
		Object varValue = parser.getVariable(varName);
		if (varValue == null) {
			Util.Writeln("Undefined variable... Var Name: " + varName);
			System.exit(1);
		}
		return varValue;
	}
}
