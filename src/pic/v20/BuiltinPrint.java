package pic.v20;
import java.util.List;
public class BuiltinPrint implements BuiltinBase {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		Util.Write(args.get(0).Resolve());

		return null;
	}
}
