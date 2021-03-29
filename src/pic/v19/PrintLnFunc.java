package pic.v19;
import java.util.List;
public class PrintLnFunc implements IBuiltin {
	public SysObject Execute(List<SysObject> args) {
		if (args.size() != 1) {
			return new ObjError(
					String.format(
							"expected %d parameter but found %d", 
							1, 
							args.size()));
		}
		Util.Writeln(args.get(0).Resolve());

		return new ObjNull();
	}
}
