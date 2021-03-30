package pic.v20;
import java.util.List;

public class ObjArray extends SysObject {
	public String arrayName;
	public List<AstNode> elements;
	
	public ObjArray(String arrayName, List<AstNode> elements) {
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
