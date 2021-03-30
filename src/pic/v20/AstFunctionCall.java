package pic.v20;
import java.util.List;

public class AstFunctionCall extends AstNode {
	public String name;
	public List<AstNode> actualParameters;
	
	public AstFunctionCall() {
		// Nothing
	}
	
	public AstFunctionCall(String name, List<AstNode> actualParameters) {
		this.name = name;
		this.actualParameters = actualParameters;
	}
}
