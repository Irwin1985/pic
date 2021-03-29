package pic.v19;

public class ObjBuiltin extends SysObject {
	IBuiltin function;
	
	public ObjBuiltin(IBuiltin function) {
		this.function = function;
	}
	
	public ObjType Type() {
		return ObjType.BUILTIN_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
