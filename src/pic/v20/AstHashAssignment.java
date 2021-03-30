package pic.v20;
import java.util.HashMap;

public class AstHashAssignment extends AstNode {
	public String name;
	public HashMap<String, AstNode> elements;
	
	public AstHashAssignment(String name, HashMap<String, AstNode> elements) {
		this.name = name;
		this.elements = elements;
	}
}
