package pic.v18;

public class Parameter extends Node {
	private String name;
	private Node value;
	
	public Parameter(Node value) {
		this.value = value;
	}
	
	public Parameter(String name, Node value) {
		this.name = name;
		this.value = value;
	}
	
	public Parameter(String name) {
		this.name = name;
	}
	
	public Object eval() {
		return value.eval();
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value.eval();
	}
}
