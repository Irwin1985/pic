package pic.v20;
import java.util.List;
public class BuiltinSeconds implements BuiltinBase {
	public SysObject Execute(List<SysObject> args) {
		return new ObjNumber((double)System.currentTimeMillis() / 1000.0);
	}
}
