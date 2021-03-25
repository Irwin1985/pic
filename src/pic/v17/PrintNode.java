package pic.v17;

public class PrintNode extends Node {
	public Node expression;
	public String type;
	
	public PrintNode() {
		// Nothing
	}
	
	public PrintNode(Node expression, String type) {
		this.expression = expression;
		this.type = type;
	}
	
	public Object eval() {
		Object writee = expression.eval();
		if (type.equals("sameline")) {
			System.out.print(writee);
		} else if (type.equals("newline")) {
			System.out.println(writee);
		}
		return writee;
	}
}
