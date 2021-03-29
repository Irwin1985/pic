package pic.v19;
import java.util.HashMap;

public class ObjDictionary extends SysObject {
	public String name;
	public HashMap<String, Node> elements;
	
	public ObjDictionary(String name, HashMap<String, Node> elements) {
		this.name = name;
		this.elements = elements;
	}
	
	public ObjType Type() {
		return ObjType.DICT_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
