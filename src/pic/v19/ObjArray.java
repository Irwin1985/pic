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
		StringBuilder out = new StringBuilder();
		
		if (elements != null) {
			int count = 0;
			for (Node element : elements) {
				count++;
				if (count > 1) {
					out.append(",");
				}
				out.append(element.toString());
			}
		}
		
		return out.toString();
	}
}
