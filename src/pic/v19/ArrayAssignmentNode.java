package pic.v19;
import java.util.List;

public class ArrayAssignmentNode extends Node {
	public String arrayName;
	public List<Node> elements;
	
	public ArrayAssignmentNode() {
		// Nothing
	}
	
	public ArrayAssignmentNode(String arrayName, List<Node> elements) {
		this.arrayName = arrayName;
		this.elements = elements;
	}
}
