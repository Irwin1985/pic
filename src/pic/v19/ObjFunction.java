package pic.v19;
import java.util.List;
public class ObjFunction extends SysObject {
	public String name;
	public Environment env;
	public List<String> params;
	public BlockNode body;
	
	public ObjFunction(String name, List<String> params, BlockNode body, Environment env) {
		this.name = name;
		this.params = params;
		this.body = body;
		this.env = env;
	}
	
	public ObjType Type() {
		return ObjType.FUNC_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
