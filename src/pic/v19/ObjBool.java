package pic.v19;

public class ObjBool extends SysObject {
	public boolean value;

	public ObjBool() {}
	
	public ObjBool(boolean value) {
		this.value = value;
	}
	
	public ObjType Type() {
		return ObjType.BOOL_OBJ;
	}
	
	public String Resolve() {
		return value ? "true" : "false";
	}
}
