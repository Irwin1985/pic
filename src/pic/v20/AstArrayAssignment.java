package pic.v20;
import java.util.List;

public class AstArrayAssignment extends AstNode {
	public String arrayName;
	public List<AstNode> elements;
	
	public AstArrayAssignment() {
		// Nothing
	}
	
	public AstArrayAssignment(String arrayName, List<AstNode> elements) {
		this.arrayName = arrayName;
		this.elements = elements;
	}
}
