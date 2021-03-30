package pic.v20;

public class AstAssignment extends AstNode {
	public String name;
	public AstNode value;
	public Parser parser;
	
	public AstAssignment() {
		// Nothing
	}
	
	public AstAssignment(String name, AstNode value, Parser parser) {
		this.name = name;
		this.value = value;
		this.parser = parser;
	}
}
