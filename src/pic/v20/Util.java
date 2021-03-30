package pic.v20;
import java.util.List;

public class Util {
	public static void Write(Object obj) {
		System.out.print(obj);
	}
	
	public static void Writeln(Object obj) {
		System.out.println(obj);
	}
	
	public static void Writeln() {
		System.out.println();
	}

	public static void PrettyPrint(List<Token> tokens) {
		for (Token token : tokens) {
			Writeln(token.toString());
		}
	}
	
	public static boolean IsError(SysObject valueObj) {
		return valueObj != null && valueObj.Type() == ObjType.ERROR_OBJ;
	}
}
