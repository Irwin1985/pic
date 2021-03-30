package pic.v20;
import java.util.HashMap;

public class ObjDictionary extends SysObject {
	public String name;
	public HashMap<String, AstNode> elements;
	
	public ObjDictionary(String name, HashMap<String, AstNode> elements) {
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
