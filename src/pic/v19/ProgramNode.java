package pic.v19;
import java.util.List;

public class ProgramNode extends Node {
	public List<Node> statements;
	
	public ProgramNode() {
		// Nothing
	}
	
	public ProgramNode(List<Node> statements) {
		this.statements = statements;
	}
	
	public Object eval() {
		Object ret = null;
		
		for (Node statement : statements) {
			ret = statement.eval();
		}
		
		return ret;
	}
}
