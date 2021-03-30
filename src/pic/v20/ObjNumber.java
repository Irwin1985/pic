package pic.v20;

public class ObjNumber extends SysObject {
	public double value;

	public ObjNumber() {}
	
	public ObjNumber(double value) {
		this.value = value;
	}
	
	
	public ObjType Type() {
		return ObjType.NUMBER_OBJ;
	}
	
	public String Resolve() {
		int intPart = (int) value;
		if (value - intPart > 0) {
			return String.valueOf(value);
		} else {
			return String.valueOf((int)value);
		}
	}
}
