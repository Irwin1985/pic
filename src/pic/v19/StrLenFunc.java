package pic.v19;
import java.util.List;
public class StrLenFunc implements IBuiltin {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		if (args.get(0).Type() != ObjType.STRING_OBJ) {
			return new ObjError("The argument must be a String.");
		}
		
		return new ObjInteger(((ObjString)args.get(0)).text.length());
	}
}
