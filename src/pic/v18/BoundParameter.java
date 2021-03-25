package pic.v18;

public class BoundParameter {
	private String name;
	private Object value;
	
	public BoundParameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
}
