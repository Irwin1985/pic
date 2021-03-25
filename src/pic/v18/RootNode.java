package pic.v18;
import java.util.List;
import java.util.LinkedList;

public class RootNode extends BlockNode {

	
	public RootNode(BlockNode block, List<Node> inlineFunctions) {
		super(block.getStatements());
		
		for (Node inlineFunction : inlineFunctions) {
			((LinkedList) getStatements()).addFirst(inlineFunction);
		}
	}
}
