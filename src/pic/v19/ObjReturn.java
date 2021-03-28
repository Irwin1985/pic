package pic.v19;

public class ObjReturn extends SysObject {
	public SysObject value;
	
	public ObjReturn() {
		// Nothing
	}
	
	public ObjReturn(SysObject value) {
		this.value = value;
	}
	
	public ObjType Type() {
		return ObjType.RETURN_OBJ;
	}
	
	public String Resolve() {
		return value.Resolve();
	}
}
