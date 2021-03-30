package pic.v20;

public class ObjNull extends SysObject {
	public ObjNull() {}
	
	public ObjType Type() {
		return ObjType.BOOL_OBJ;
	}
	
	public String Resolve() {
		return "null";
	}
}
