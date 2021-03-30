package pic.v20;
import java.util.List;
public class BuiltinWait implements BuiltinBase {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		if (args.get(0).Type() != ObjType.NUMBER_OBJ) {
			return new ObjError("Incompatible data type argument for wait function.");
		}
		try {			
			Thread.sleep((long)((ObjNumber)args.get(0)).value);
			return new ObjNull();
		} catch (Exception e) {			
			return new ObjError("Error in wait function: " + e);
		}
	}
}
