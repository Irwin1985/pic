package pic.v19;

public class ObjNull extends SysObject {
	public ObjNull() {}
	
	public ObjType Type() {
		return ObjType.BOOL_OBJ;
	}
	
	public String Resolve() {
		return "null";
	}
}
