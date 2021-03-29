package pic.v19;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
public class Evaluator {
	HashMap<String, ObjBuiltin> builtins;
	
	public Evaluator() {
		// Prepare and register builtin functions
		builtins = new HashMap<String, ObjBuiltin>();
		builtins.put("print", new ObjBuiltin(new PrintFunc()));
		builtins.put("println", new ObjBuiltin(new PrintLnFunc()));
		builtins.put("wait", new ObjBuiltin(new WaitFunc()));
		builtins.put("arraySize", new ObjBuiltin(new ArraySizeFunc()));
		builtins.put("strLen", new ObjBuiltin(new StrLenFunc()));
	}
	
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
			
			if (Util.IsError(value)) {
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
		else if (node instanceof CollectionUpdateNode) {
			CollectionUpdateNode collectionNode = (CollectionUpdateNode)node;
			
			// get the elements from symbol table.
			SysObject value = env.get(collectionNode.name);
			if (value == null) {
				return new ObjError("array does not exist: " + collectionNode.name);
			}
			if (value.Type() == ObjType.ARRAY_OBJ) {
				
				// cast to ARRAY_OBJ
				ObjArray objArray = (ObjArray)value;
				
				// Resolve index
				value = Eval(collectionNode.key, env);
				
				if (Util.IsError(value)) {
					return value;
				}
				// check for INTEGER_OBJ
				if (value.Type() != ObjType.INTEGER_OBJ) {
					return new ObjError("Array index must be resolved to INTEGER.");
				}
				// check for out of bounds
				int index = ((ObjInteger)value).value;
				if (index >= objArray.elements.size() || index < 0) {
					return new ObjError("Array index out of bound.");
				}
				// update the array element at index (index)
				objArray.elements.set(index, collectionNode.rightSideExpression);
			} else if (value.Type() == ObjType.DICT_OBJ) {
				// cast to DICT_OBJ
				ObjDictionary objDict = (ObjDictionary)value;
				// Resolve index
				value = Eval(collectionNode.key, env);
				if (Util.IsError(value)) {
					return value;
				}
				// check for STRING_OBJ
				if (value.Type() != ObjType.STRING_OBJ) {
					return new ObjError("Dictionary Accessor must be resolved to STRING.");
				}
				// update or create the element
				objDict.elements.put(((ObjString)value).text, collectionNode.rightSideExpression);				
			} else {
				return new ObjError("The identifier is not a collection type");
			}
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
		else if (node instanceof DictionaryAssignmentNode) {
			// cast from node to DictionaryAssignmentNode
			DictionaryAssignmentNode dict = (DictionaryAssignmentNode)node;
			// create the dictionary object
			ObjDictionary objDict = new ObjDictionary(dict.name, dict.elements);
			// register the array into the symbol table.
			env.set(dict.name, objDict);

			return objDict;
		}
		else if (node instanceof LookupNode) {
			// cast to LookUpNode
			LookupNode lookup = (LookupNode)node;
			
			// eval element accessor (key, index)
			SysObject objAccessor = Eval(lookup.key, env);
			if (Util.IsError(objAccessor)) {
				return objAccessor;
			}
			
			// fetch the saved symbol
			String identifier = lookup.name;
			SysObject value = env.get(identifier);
			if (value == null) {
				return new ObjError("Identifier does not exist: " + identifier);
			}
			
			// check the type of symbol
			if (value.Type() == ObjType.ARRAY_OBJ) {				
				// accessor must be an integer object
				if (objAccessor.Type() != ObjType.INTEGER_OBJ) {
					return new ObjError("Array index must be resolved to INTEGER.");
				}
				// cast the ARRAY_OBJ
				ObjArray objArray = (ObjArray)value;
				ObjInteger arrayIndex = (ObjInteger)objAccessor;
				
				if (arrayIndex.value < 0 || 
						arrayIndex.value >= objArray.elements.size()) {
					return new ObjError("Array index out of bound.");
				}
				
				return Eval(objArray.elements.get(arrayIndex.value), env);
			}
			else if (value.Type() == ObjType.DICT_OBJ) {
				// accessor must be a string object
				if (objAccessor.Type() != ObjType.STRING_OBJ) {
					return new ObjError("The key accessor must be a valid string.");
				}
				// cast the DICT_OBJ
				ObjDictionary objDict = (ObjDictionary)value;
				// try get the key value
				String key = ((ObjString)objAccessor).text;
				Node element = objDict.elements.get(key);
				if (element == null) {
					return new ObjError("Key element does not exist: " + key);
				}
				return Eval(element, env);
			} else {
				return new ObjError("Cannot access a non Collection type.");
			}
			
			
		}
		else if (node instanceof FunctionNode) {
			FunctionNode funcNode = (FunctionNode)node;			
			ObjFunction objFunc = new ObjFunction(funcNode.name, funcNode.parameters, funcNode.body, env);

			return objFunc;
		}
		else if (node instanceof FunctionCallNode) {
			FunctionCallNode callNode = (FunctionCallNode)node;
			// get the saved function symbol
			SysObject value = env.get(callNode.name);
			if (value == null) {
				// try find the function as builtin
				value = builtins.get(callNode.name);
				if (value == null) {					
					return new ObjError("Function does not exist: " + callNode.name);
				}
			}
			if (value.Type() == ObjType.FUNC_OBJ) {
				// cast to FUNCTION_OBJ
				ObjFunction objFunc = (ObjFunction)value;
				
				// Evaluate and fill the arguments
				List<SysObject> arguments = new ArrayList<SysObject>();
				for (Node argument : callNode.actualParameters) {
					SysObject objArg = Eval(argument, env);
					if (Util.IsError(objArg)) {
						return objArg;
					}
					arguments.add(objArg);
				}			

				// extends the env from FUNCTION env.
				Environment extendedEnv = new Environment(objFunc.env);
				// register arguments in new environment
				int index = 0;
				for (String param : objFunc.params) {
					extendedEnv.set(param, arguments.get(index));
					index++;
				}
				// execute function
				SysObject result = Eval(objFunc.body, extendedEnv);
				if (result.Type() == ObjType.RETURN_OBJ) {
					return ((ObjReturn)result).value;
				}
				return result;
			}
			else if (value.Type() == ObjType.BUILTIN_OBJ) {
				// cast to ObjBuiltin
				ObjBuiltin objBuiltin = (ObjBuiltin)value;

				// Evaluate and fill the arguments
				List<SysObject> arguments = new ArrayList<SysObject>();
				for (Node argument : callNode.actualParameters) {
					SysObject objArg = Eval(argument, env);
					if (Util.IsError(objArg)) {
						return objArg;
					}
					arguments.add(objArg);
				}
				return objBuiltin.function.Execute(arguments);
			}			
		}
		else if (node instanceof ReturnNode){
			ReturnNode returnNode = (ReturnNode)node;
			if (returnNode.returnValue != null) {
				SysObject result = Eval(returnNode.returnValue, env);
				if (Util.IsError(result)) {
					return result;
				}
				return new ObjReturn(result);
			}
		}
		else if (node instanceof IfNode) {
			IfNode ifNode = (IfNode)node;
			SysObject condition = Eval(ifNode.condition, env);
			if (Util.IsError(condition)) {
				return condition;
			}			
			if (condition.Type() != ObjType.BOOL_OBJ) {
				return new ObjError("Condition must be evaluated to BOOLEAN.");
			}
			if (((ObjBool)condition).value) {
				return Eval(ifNode.thenPart, env);
			} else if (ifNode.elsePart != null) {
				return Eval(ifNode.elsePart, env);
			}
		}
		else if (node instanceof WhileNode) {
			WhileNode whileNode = (WhileNode)node;
			while (true) {
				SysObject condition = Eval(whileNode.condition, env);
				if (Util.IsError(condition)) {
					return condition;
				}				
				if (condition.Type() != ObjType.BOOL_OBJ) {
					return new ObjError("Condition must be evaluated to BOOLEAN.");
				}
				if (((ObjBool)condition).value) {
					SysObject result = Eval(whileNode.body, env);
					if (result != null) {
						if (Util.IsError(result)) {
							return result;
						} else if (result.Type() == ObjType.RETURN_OBJ) {
							return ((ObjReturn)result).value;
						}
					}
				} else {
					break;
				}
			}
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
			else if (Util.IsError(result)) {
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
		if (Util.IsError(left)) {
			return left;
		}
		
		SysObject right = Eval(node.right, env);
		if (Util.IsError(right)) {
			return right;
		}
		
		if (left.Type() == ObjType.INTEGER_OBJ && right.Type() == ObjType.INTEGER_OBJ) {
			return IntegerExpression((ObjInteger)left, node.op, (ObjInteger)right);
		}
		else if (left.Type() == ObjType.STRING_OBJ && right.Type() == ObjType.STRING_OBJ) {
			return StringExpression((ObjString)left, node.op, (ObjString)right);
		}
		else if(left.Type() == ObjType.BOOL_OBJ && right.Type() == ObjType.BOOL_OBJ) {
			return BooleanExpression((ObjBool)left, node.op, (ObjBool)right);
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
			return new ObjInteger(left.value * right.value);
		case DIVIDE:
			if (right.value == 0) {
				return new ObjError("Error: division by zero.");
			}
			return new ObjInteger(left.value / right.value);
		case LESS:
			return new ObjBool(left.value < right.value);
		case GREATER:
			return new ObjBool(left.value > right.value);
		case LESSEQUAL:
			return new ObjBool(left.value <= right.value);
		case GREATEREQUAL:
			return new ObjBool(left.value >= right.value);
		case EQUAL:
			return new ObjBool(left.value == right.value);
		case NOTEQUAL:
			return new ObjBool(left.value != right.value);
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
	
	private SysObject BooleanExpression(ObjBool left, TokenType op, ObjBool right) {
		switch(op) {
		case EQUAL:
			return new ObjBool(left.value == right.value);
		case NOTEQUAL:
			return new ObjBool(left.value != right.value);
		case LESS:
		{
			int leftInt = left.value ? 1 : 0;
			int rightInt = right.value ? 1 : 0;
			return new ObjBool(leftInt < rightInt);			
		}
		case GREATER:
		{
			int leftInt = left.value ? 1 : 0;
			int rightInt = right.value ? 1 : 0;
			return new ObjBool(leftInt > rightInt);			
		}
		case LESSEQUAL:
		{
			int leftInt = left.value ? 1 : 0;
			int rightInt = right.value ? 1 : 0;
			return new ObjBool(leftInt <= rightInt);			
		}
		case GREATEREQUAL:
		{
			int leftInt = left.value ? 1 : 0;
			int rightInt = right.value ? 1 : 0;
			return new ObjBool(leftInt >= rightInt);			
		}
		default:
			return new ObjError("Invalid operator for Boolean Comparison: " + op);
		}
	}
	
	private SysObject EvalLogicalOperation(BinOpNode node, Environment env) {
		SysObject left = Eval(node.left, env);
		if (Util.IsError(left)) {
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
			if (Util.IsError(right)) {
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
			if (Util.IsError(right)) {
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
