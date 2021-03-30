package pic.v20;

public class AstLookupCollection extends AstNode {
	public String name;
	public AstNode key;
	
	public AstLookupCollection(String arrayName, AstNode keyNode) {
		this.name = arrayName;
		this.key = keyNode;
	}
}
