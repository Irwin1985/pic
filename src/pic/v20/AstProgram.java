package pic.v20;
import java.util.List;

public class AstProgram extends AstNode {
	public List<AstNode> statements;
	
	public AstProgram() {
		// Nothing
	}
	
	public AstProgram(List<AstNode> statements) {
		this.statements = statements;
	}
}
