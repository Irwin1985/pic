package pic.v19;

public class ObjInteger extends SysObject {
	public Integer value;

	public ObjInteger() {}
	
	public ObjInteger(int value) {
		this.value = value;
	}
	
	public ObjType Type() {
		return ObjType.INTEGER_OBJ;
	}
	
	public String Resolve() {
		return value.toString();
	}
}
