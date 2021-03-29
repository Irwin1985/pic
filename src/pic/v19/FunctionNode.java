package pic.v19;
import java.util.List;

public class FunctionNode extends Node {
	public String name;
	public List<String> parameters;
	public BlockNode body;
	
	public FunctionNode(String name, BlockNode body, List<String> parameters) {
		this.name = name;
		this.body = body;
		this.parameters = parameters;
	}
}
