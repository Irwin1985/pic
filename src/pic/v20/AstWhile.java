package pic.v20;

public class AstWhile extends AstNode {
	public AstNode condition;
	public AstNode body;
	
	public AstWhile() {
		// Nothing
	}
	
	public AstWhile(AstNode condition, AstNode body) {
		this.condition = condition;
		this.body = body;
	}
}
