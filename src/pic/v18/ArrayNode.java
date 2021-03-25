package pic.v18;
import java.util.List;
import java.util.ArrayList;

public class ArrayNode extends Node {
	public List<Node> elements;
	
	public ArrayNode() {
		// Nothing
	}
	
	public ArrayNode(List<Node> elements) {
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
