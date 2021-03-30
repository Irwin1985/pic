package pic.v20;

public class ObjError extends SysObject {
	public String message;
	
	public ObjError() {};
	
	public ObjError(String message) {
		this.message = message;
	}
	
	public ObjType Type() {
		return ObjType.ERROR_OBJ;
	}
	
	public String Resolve() {
		return "Error: " + message;
	}
}
