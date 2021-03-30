package pic.v20;
import java.util.List;
public class ObjFunction extends SysObject {
	public String name;
	public Environment env;
	public List<String> params;
	public AstBlock body;
	public int arity;
	
	public ObjFunction(String name, List<String> params, AstBlock body, Environment env) {
		this.name = name;
		this.params = params;
		this.body = body;
		this.env = env;
		if (params != null) {			
			this.arity = params.size();
		}
	}
	
	public ObjType Type() {
		return ObjType.FUNC_OBJ;
	}
	
	public String Resolve() {
		return "ok";
	}
}
