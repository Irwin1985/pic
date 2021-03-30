package pic.v20;
import java.util.List;
public class BuiltinArraySize implements BuiltinBase {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		if (args.get(0).Type() != ObjType.ARRAY_OBJ) {
			return new ObjError("The argument must be an Array.");
		}
		
		return new ObjNumber(((ObjArray)args.get(0)).elements.size() * 1.0);
	}
}
