package pic.v19;
import java.util.List;

public class ObjArray extends SysObject {
	public String arrayName;
	public List<Node> elements;
	
	public ObjArray(String arrayName, List<Node> elements) {
		this.arrayName = arrayName;
		this.elements = elements;
	}
	
	public ObjType Type() {
		return ObjType.ARRAY_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
