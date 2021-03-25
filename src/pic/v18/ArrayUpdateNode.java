package pic.v18;
import java.util.List;

public class ArrayUpdateNode extends Node {
	private Node array;
	private Node indexExpression;
	private Node rightSideExpression;
	
	public ArrayUpdateNode(Node array, Node indexExpression, Node rightSideExpression) {
		this.array = array;
		this.indexExpression = indexExpression;
		this.rightSideExpression = rightSideExpression;
	}
	
	public int ToInt(Node node) {
		Object res = node.eval();
		return Integer.valueOf(res.toString());
	}
	
	public Object eval() {
		Object arrayVariable = array.eval();
		int index = ToInt(indexExpression);
		Object newValue = rightSideExpression.eval();
		
		Object ret = ((List<Object>) arrayVariable).set(index, newValue);
		
		return ret;
	}
}
