package pic.v20;

public class ObjBuiltin extends SysObject {
	BuiltinBase function;
	
	public ObjBuiltin(BuiltinBase function) {
		this.function = function;
	}
	
	public ObjType Type() {
		return ObjType.BUILTIN_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
