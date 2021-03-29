package pic.v19;
import java.util.List;
import java.util.ArrayList;
public class BlockNode extends Node {
	public List<Node> statements;
	
	public BlockNode() {
		this.statements = new ArrayList<Node>();
	}
	
	public BlockNode(List<Node> statements) {
		this.statements = statements;
	}
}
