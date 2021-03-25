package pic.v18;

public class StrConcatCommand extends Node {
	public Parser parser;
	public StrConcatCommand() {}

	public StrConcatCommand(Parser parser)
	{
		this.parser = parser;
	}

	public Object eval()
	{
		String str1 = (String) parser.getVariable("str1");
		String str2 = (String) parser.getVariable("str2");
		StringBuffer sb = new StringBuffer(str1);
		return sb.append(str2);
	}
}
