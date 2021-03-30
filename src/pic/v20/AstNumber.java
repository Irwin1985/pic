package pic.v20;

public class AstNumber extends AstNode {
	double value;
	
	public AstNumber() {
		// Nothing
	}
	
	public AstNumber(double value) {
		this.value = value;
	}
}
