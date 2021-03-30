package pic.v20;

public class AstCollectionUpdate extends AstNode {
	public String name;
	public AstNode key;
	public AstNode rightSideExpression;
	
	public AstCollectionUpdate(String arrayName, AstNode indexExpression, AstNode rightSideExpression) {
		this.name = arrayName;
		this.key = indexExpression;
		this.rightSideExpression = rightSideExpression;
	}
}
