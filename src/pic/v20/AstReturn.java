package pic.v20;

public class AstReturn extends AstNode {
	public AstNode returnValue;
	
	public AstReturn(AstNode returnValue) {
		this.returnValue = returnValue;
	}
}
