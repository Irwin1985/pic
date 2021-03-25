package pic.v18;
import java.util.List;

public class ArraySizeCommand extends Node {
	public Parser parser;

	public ArraySizeCommand() {}

	public ArraySizeCommand(Parser parser)
	{
		this.parser = parser;
	}
	
	public Object eval()
	{
		List array = (List) parser.getVariable("array");
		return array.size();
	}
}
