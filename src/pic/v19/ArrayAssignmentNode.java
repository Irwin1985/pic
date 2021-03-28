package pic.v19;
import java.util.List;
import java.util.ArrayList;

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
	
	public Object eval() {
		List<Object> items = new ArrayList<Object>(elements.size());
		
		for (Node node : elements) {
			items.add(node.eval());
		}
		
		return items;
	}
}
