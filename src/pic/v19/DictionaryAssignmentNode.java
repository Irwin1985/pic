package pic.v19;
import java.util.HashMap;

public class DictionaryAssignmentNode extends Node {
	public String name;
	public HashMap<String, Node> elements;
	
	public DictionaryAssignmentNode(String name, HashMap<String, Node> elements) {
		this.name = name;
		this.elements = elements;
	}
}
