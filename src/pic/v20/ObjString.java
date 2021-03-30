package pic.v20;

public class ObjString extends SysObject {
	public String text;
	
	public ObjString() {}
	
	public ObjString(String text) {
		this.text = text;
	}
	
	public ObjType Type() {
		return ObjType.STRING_OBJ;
	}
	
	public String Resolve() {
		return text;
	}
}
