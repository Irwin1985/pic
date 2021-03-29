package pic.v19;
import java.util.HashMap;

public class Environment {
	public Environment parent;
	private HashMap<String, SysObject> symbolTable;
	
	public Environment() {
		symbolTable = new HashMap<String, SysObject>();
	}
	
	public Environment(Environment parent) {
		symbolTable = new HashMap<String, SysObject>();
		this.parent = parent;
	}
	
	public void set(String name, SysObject value) {
		symbolTable.put(name, value);
	}
	
	public SysObject get(String name) {
		SysObject result = symbolTable.get(name);

		if (result == null && parent != null) {
			result = parent.get(name);
		}

		return result;
	}
}
