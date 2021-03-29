package pic.v19;
import java.util.List;

public class FunctionCallNode extends Node {
	public String name;
	public List<Node> actualParameters;
	
	public FunctionCallNode() {
		// Nothing
	}
	
	public FunctionCallNode(String name, List<Node> actualParameters) {
		this.name = name;
		this.actualParameters = actualParameters;
	}
}
