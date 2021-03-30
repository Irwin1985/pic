package pic.v20;

public class AstIf extends AstNode {
	public AstNode condition;
	public AstNode thenPart;
	public AstNode elsePart;
	
	public AstIf() {
		// Nothing
	}
	
	public AstIf(AstNode condition, AstNode thenPart, AstNode elsePart) {
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}
}
