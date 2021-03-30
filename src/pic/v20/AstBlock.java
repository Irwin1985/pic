package pic.v20;
import java.util.List;
import java.util.ArrayList;
public class AstBlock extends AstNode {
	public List<AstNode> statements;
	
	public AstBlock() {
		this.statements = new ArrayList<AstNode>();
	}
	
	public AstBlock(List<AstNode> statements) {
		this.statements = statements;
	}
}
