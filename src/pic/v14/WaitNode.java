package pic.v14;

public class WaitNode extends Node {
	public Node interval;
	
	public WaitNode() {
		// Nothing
	}
	
	public WaitNode(Node interval) {
		this.interval = interval;
	}
	
	public Object eval() {
		Integer waitAmount = Integer.valueOf(interval.eval().toString());
		
		try {
			Thread.sleep(waitAmount.intValue());
		} catch(Exception e) {
			System.out.println("Error in WaitNode.eval() method: " + e);
		}
		return waitAmount;
	}
}
