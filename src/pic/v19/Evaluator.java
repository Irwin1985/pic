package pic.v19;

import java.util.List;

public class Evaluator {
	
	private ObjBool TRUE = new ObjBool(true);
	private ObjBool FALSE = new ObjBool(false);
	private ObjNull NULL = new ObjNull();
	
	public SysObject Eval(Node node, Environment env) {
		if (node instanceof ProgramNode) {
			return EvalProgram((ProgramNode)node, env);
		} 
		else if (node instanceof BlockNode) {
			return EvalBlock((BlockNode)node, env);
		}
		else if (node instanceof NumberNode) {
			return new ObjInteger(((NumberNode)node).value);
		}
		else if (node instanceof StringNode) {
			return new ObjString(((StringNode)node).text);
		}
		else if (node instanceof BooleanNode) {
			return ((BooleanNode)node).value ? TRUE : FALSE;
		}
		else if (node instanceof NullNode) {
			return NULL;
		}
		else if (node instanceof BinOpNode) {
			BinOpNode binOp = (BinOpNode)node;
			if (binOp.op == TokenType.AND || binOp.op == TokenType.OR) {
				return EvalLogicalOperation(binOp, env);
			}

			return EvalBinaryOperation(binOp, env);			
		}
		else if (node instanceof AssignmentNode) {
			AssignmentNode assigment = (AssignmentNode)node;
			
			String identifier = assigment.name;
			SysObject value = Eval(assigment.value, env);
			
			if (value.Type() == ObjType.ERROR_OBJ) {
				return value;
			}
			
			env.set(identifier, value);

			// an assignment return the right hand expression.
			return value;
		}
		else if (node instanceof VariableNode) {
			String identifier = ((VariableNode)node).varName;
			SysObject val = env.get(identifier);

			if (val == null) {
				return new ObjError("identifier not found: " + identifier);
			}

			return val;
		}
		else if (node instanceof ArrayUpdateNode) {
			ArrayUpdateNode arrayUpdate = (ArrayUpdateNode)node;
			
			// get the elements from symbol table.
			SysObject value = env.get(arrayUpdate.arrayName);
			if (value == null) {
				return new ObjError("array does not exist: " + arrayUpdate.arrayName);
			}
			
			// cast elements
			ObjArray objArray = (ObjArray)value;
			
			// Resolve index
			value = Eval(arrayUpdate.indexExpression, env);
			
			if (value.Type() == ObjType.ERROR_OBJ) {
				return value;
			}
			// check for INTEGER_OBJ
			if (value.Type() != ObjType.INTEGER_OBJ) {
				return new ObjError("Array index must be resolved to INTEGER.");
			}
			// check for out of bounds
			int index = ((ObjInteger)value).value;
			if (index > objArray.elements.size() || index < 0) {
				return new ObjError("Array index out of bound.");
			}
			// update the array element at index (index)
			objArray.elements.set(index, arrayUpdate.rightSideExpression);
		}
		else if (node instanceof ArrayAssignmentNode) {
			// cast from node to ArrayAssignmentNode
			ArrayAssignmentNode array = (ArrayAssignmentNode)node;
			// create the array object
			ObjArray objArray = new ObjArray(array.arrayName, array.elements);
			// register the array into the symbol table.
			env.set(array.arrayName, objArray);
			
			return objArray;			
		}
		else if (node instanceof ArrayAccessNode) {
			// cast to LookUpNode
			ArrayAccessNode lookup = (ArrayAccessNode)node;
			
			// eval array index (must be an INTEGER_OBJ)
			SysObject index = Eval(lookup.arrayIndex, env);
			if (index.Type() == ObjType.ERROR_OBJ) {
				return index;
			}
			if (index.Type() != ObjType.INTEGER_OBJ) {
				return new ObjError("Array index must be resolved to INTEGER.");
			}
			
			// find the symbol (array identifier)
			String identifier = lookup.arrayName;
			SysObject value = env.get(identifier);
			if (value == null) {
				return new ObjError("Array does not exist: " + identifier);
			}
			// cast the ARRAY_OBJ
			ObjArray objArray = (ObjArray)value;
			ObjInteger arrayIndex = (ObjInteger)index;

			if (arrayIndex.value < 0 || 
					arrayIndex.value > objArray.elements.size()) {
				return new ObjError("Array index out of bound.");
			}

			return Eval(objArray.elements.get(arrayIndex.value), env);
		}
		return NULL;
	}
	
	private SysObject EvalProgram(ProgramNode program, Environment env) {
		SysObject result = null;
		
		for (Node statement : program.statements) {
			result = Eval(statement, env);
			if (result.Type() == ObjType.RETURN_OBJ) {
				return ((ObjReturn)result).value;
			}
			else if (result.Type() == ObjType.ERROR_OBJ) {
				return (ObjError)result;
			}
		}
		return result;
	}
	
	private SysObject EvalBlock(BlockNode block, Environment env) {
		SysObject result = null;
		
		for (Node statement : block.statements) {
			result = Eval(statement, env);
			if (result != null && 
					(result.Type() == ObjType.RETURN_OBJ ||
					 result.Type() == ObjType.ERROR_OBJ)) 
			{
				return result;
			}
		}
		
		return result;
	}
	
	private SysObject EvalBinaryOperation(BinOpNode node, Environment env) {
		SysObject left = Eval(node.left, env);
		if (left.Type() == ObjType.ERROR_OBJ) {
			return left;
		}
		
		SysObject right = Eval(node.right, env);
		if (right.Type() == ObjType.ERROR_OBJ) {
			return right;
		}
		
		if (left.Type() == ObjType.INTEGER_OBJ && right.Type() == ObjType.INTEGER_OBJ) {
			return IntegerExpression((ObjInteger)left, node.op, (ObjInteger)right);
		}
		else if (left.Type() == ObjType.STRING_OBJ && right.Type() == ObjType.STRING_OBJ) {
			return StringExpression((ObjString)left, node.op, (ObjString)right);
		}
		
		return NULL;
	}
	
	private SysObject IntegerExpression(ObjInteger left, TokenType op, ObjInteger right) {
		switch (op) {
		case ADD:
			return new ObjInteger(left.value + right.value);
		case SUBTRACT:
			return new ObjInteger(left.value - right.value);
		case MULTIPLY:
			return new ObjInteger(left.value - right.value);
		case DIVIDE:
			if (right.value == 0) {
				return new ObjError("Error: division by zero.");
			}
			return new ObjInteger(left.value / right.value);
		default:
			return new ObjError("Unsupported operator in INTEGER operation: " + op);
		}
	}
	
	private SysObject StringExpression(ObjString left, TokenType op, ObjString right) {
		switch(op) {
		case ADD:
			return new ObjString(left.text + right.text);
		default:
			return new ObjError("Unsupported operator in INTEGER operation: " + op);
		}
	}
	
	private SysObject EvalLogicalOperation(BinOpNode node, Environment env) {
		SysObject left = Eval(node.left, env);
		if (left.Type() == ObjType.ERROR_OBJ) {
			return left;
		}
		
		if (left.Type() != ObjType.BOOL_OBJ) {
			return new ObjError("Invalid type for logical operation: " + left.Type());
		}
		
		if (node.op == TokenType.AND) {
			// if left is false then no need evaluate the right side.
			if (!((ObjBool)left).value) {
				return FALSE;
			}
			// here the right side is required.
			SysObject right = Eval(node.right, env);
			if (right.Type() == ObjType.ERROR_OBJ) {
				return right;
			}
			if (right.Type() != ObjType.BOOL_OBJ) {
				return new ObjError("Invalid type for logical operation: " + right.Type());
			}
			return ((ObjBool)right).value ? TRUE : FALSE;
		}
		else if (node.op == TokenType.OR) {
			if (((ObjBool)left).value) {
				return TRUE;
			}
			// here the right side is required.
			SysObject right = Eval(node.right, env);
			if (right.Type() == ObjType.ERROR_OBJ) {
				return right;
			}
			if (right.Type() != ObjType.BOOL_OBJ) {
				return new ObjError("Invalid type for logical operation: " + right.Type());
			}
			return ((ObjBool)right).value ? TRUE : FALSE;
		}

		return NULL;
	}
}
