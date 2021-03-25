package pic.v18;
import java.util.List;
import java.util.ArrayList;

public class FunctionCallNode extends Node {
	public Node name;
	public List<Parameter> actualParameters;
	public Parser parser;
	
	public FunctionCallNode() {
		// Nothing
	}
	
	public FunctionCallNode(Node name, List<Parameter> actualParameters, Parser parser) {
		this.name = name;
		this.actualParameters = actualParameters;
		this.parser = parser;
	}
	
	public Object eval() {
		Function function = (Function) name.eval();
		
		List<BoundParameter> boundParameters = new ArrayList();
		if (function.getParameters() != null) {
			if (actualParameters != null) {
				if (actualParameters.size() < function.getParameters().size()) {
					Util.Writeln("Too Few Parameters in function Call: " + function.getName());
					System.exit(1);
				} else if (actualParameters.size() > function.getParameters().size()) {
					Util.Writeln("Too Many Parameters in function Call: " + function.getName());
					System.exit(1);					
				} else {
					for (int index = 0; index < actualParameters.size(); index++) {
						String name = function.getParameters().get(index).getName();
						Object value = actualParameters.get(index).getValue();
						
						if (value instanceof Function) {
							value = ((Function) value).eval();
						}
						boundParameters.add(new BoundParameter(name, value));
					}
				}
			}
		}
		return parser.ExecuteFunction(function, boundParameters);
	}
}
