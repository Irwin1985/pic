package pic.v19;

public class BinOpNode extends Node {
	public TokenType op;
	public Node left;
	public Node right;
	
	public BinOpNode() {
		// Nothing
	}
	
	public BinOpNode(TokenType op, Node left, Node right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
}
