package pic.v19;

public class AssignmentNode extends Node {
	public String name;
	public Node value;
	public Parser parser;
	
	public AssignmentNode() {
		// Nothing
	}
	
	public AssignmentNode(String name, Node value, Parser parser) {
		this.name = name;
		this.value = value;
		this.parser = parser;
	}
}
