package pic.v10;

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
	
	public int ToInt(Node node) {
		Object res = node.eval();
		return Integer.valueOf(res.toString());
	}
	
	public boolean ToBoolean(Node node) {
		Object res = node.eval();
		return Boolean.valueOf(res.toString());
	}
	
	public Object ToObject(Node node) {
		return node.eval();
	}
	
	public Object eval() {
		Object result = null;
		switch(op) {
		case ADD:
			result = Integer.valueOf(ToInt(left) + ToInt(right));
			break;
		case SUBTRACT:
			result = Integer.valueOf(ToInt(left) - ToInt(right));
			break;
		case MULTIPLY:
			result = Integer.valueOf(ToInt(left) * ToInt(right));
			break;
		case DIVIDE:
			if (ToInt(right) == 0) {
				System.out.println("Error: Division by Zero!");
				System.exit(0);
			}
			result = Integer.valueOf(ToInt(left) / ToInt(right));
			break;
		case LESS:
			result = Boolean.valueOf(ToInt(left) < ToInt(right));
			break;
		case GREATER:
			result = Boolean.valueOf(ToInt(left) > ToInt(right));
			break;
		case LESSEQUAL:
			result = Boolean.valueOf(ToInt(left) <= ToInt(right));
			break;
		case GREATEREQUAL:
			result = Boolean.valueOf(ToInt(left) >= ToInt(right));
			break;
		case EQUAL:
			result = Boolean.valueOf(ToObject(left).equals(ToObject(right)));
			break;
		case NOTEQUAL:
			result = Boolean.valueOf(!ToObject(left).equals(ToObject(right)));
			break;
		case OR:
			result = Boolean.valueOf(ToBoolean(left) || ToBoolean(right));
			break;
		case AND:
			result = Boolean.valueOf(ToBoolean(left) && ToBoolean(right));
			break;
		}
		return result;
	}
	
	public static void main(String args[]) {
		NumberNode firstNumber = new NumberNode(100);
		NumberNode secondNumber = new NumberNode(200);
		Node sumNode = new BinOpNode(TokenType.ADD, firstNumber, secondNumber);
		System.out.println("100 + 200 = " + sumNode.eval());
		
		Node compareNode = new BinOpNode(TokenType.LESS, firstNumber, secondNumber);
		System.out.println("100 < 200 = " + compareNode.eval());
	}
}
