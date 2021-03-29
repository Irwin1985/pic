package pic.v19;
import java.util.List;
public class PrintFunc implements IBuiltin {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		Util.Write(args.get(0).Resolve());

		return new ObjNull();
	}
}
