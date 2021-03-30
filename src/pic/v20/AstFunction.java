package pic.v20;
import java.util.List;

public class AstFunction extends AstNode {
	public String name;
	public List<String> parameters;
	public AstBlock body;
	
	public AstFunction(String name, AstBlock body, List<String> parameters) {
		this.name = name;
		this.body = body;
		this.parameters = parameters;
	}
}
