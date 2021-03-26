package pic.v19;

public class StrLenCommand extends Node {
	public Parser parser;
	public StrLenCommand() {}
	
	public StrLenCommand(Parser parser)
	{
		this.parser = parser;
	}

	public Object eval()
	{
		return Integer.valueOf(parser.getVariable("str").toString().length());
	}
}
