package pic.v16;
import java.util.List;
import java.util.LinkedList;

public class StatementsDemo {
	public static void main(String args[]) {
		Node firstMsg = new PrintNode(new NumberNode(1), "newline");
		Node secondMsg = new PrintNode(new NumberNode(2), "newline");
		
		Node wait = new WaitNode(new NumberNode(2000));
		
		List<Node> script = new LinkedList<Node>();
		script.add(firstMsg);
		script.add(wait);
		script.add(secondMsg);
		
		for (Node statement : script) {
			statement.eval();
		}
	}
}
