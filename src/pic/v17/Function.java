package pic.v17;
import java.util.List;

public class Function extends Node {
	private Node body;
	private List<Parameter> parameters;
	private String name;
	
	public Function(String name, Node body, List<Parameter> parameters) {
		this.name = name;
		this.body = body;
		this.parameters = parameters;
	}
	
	public Object eval() {
		return body.eval();
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public Node getBody() {
		return body;
	}
	
	public String getName() {
		return name;
	}
}
