package pic.v20;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
public class Evaluator {
	HashMap<String, ObjBuiltin> builtins;
	
	public Evaluator() {
		// Prepare and register builtin functions
		builtins = new HashMap<String, ObjBuiltin>();
		builtins.put("print", new ObjBuiltin(new BuiltinPrint()));
		builtins.put("println", new ObjBuiltin(new BuiltinPrintLn()));
		builtins.put("wait", new ObjBuiltin(new BuiltinWait()));
		builtins.put("arraySize", new ObjBuiltin(new BuiltinArraySize()));
		builtins.put("strLen", new ObjBuiltin(new BuiltinStrLen()));
		builtins.put("seconds", new ObjBuiltin(new BuiltinSeconds()));
	}
	
	private ObjBool TRUE = new ObjBool(true);
	private ObjBool FALSE = new ObjBool(false);
	private ObjNull NULL = new ObjNull();
	
	public SysObject Eval(AstNode node, Environment env) {
		if (node instanceof AstProgram) {
			return EvalProgram((AstProgram)node, env);
		} 
		else if (node instanceof AstBlock) {
			return EvalBlock((AstBlock)node, env);
		}
		else if (node instanceof AstNumber) {
			AstNumber astNum = (AstNumber)node;
			return new ObjNumber(astNum.value);
		}
		else if (node instanceof AstString) {
			return new ObjString(((AstString)node).text);
		}
		else if (node instanceof AstBoolean) {
			return ((AstBoolean)node).value ? TRUE : FALSE;
		}
		else if (node instanceof AstNull) {
			return NULL;
		}
		else if (node instanceof AstBinOp) {
			AstBinOp binOp = (AstBinOp)node;
			if (binOp.op == TokenType.AND || binOp.op == TokenType.OR) {
				return EvalLogicalOperation(binOp, env);
			}

			return EvalBinaryOperation(binOp, env);			
		}
		else if (node instanceof AstAssignment) {
			AstAssignment assigment = (AstAssignment)node;
			
			String identifier = assigment.name;
			SysObject value = Eval(assigment.value, env);
			
			if (Util.IsError(value)) {
				return value;
			}
			
			env.set(identifier, value);

			// an assignment return the right hand expression.
			return value;
		}
		else if (node instanceof AstIdentifier) {
			String identifier = ((AstIdentifier)node).varName;
			SysObject val = env.get(identifier);

			if (val == null) {
				return new ObjError("identifier not found: " + identifier);
			}
			// Check for function type
			if (val.Type() == ObjType.FUNC_OBJ) {
				ObjFunction objFunc = (ObjFunction)val; 
				// check for function arity
				if (objFunc.arity > 0) {
					return new ObjError("missing arguments in function call.");
				}
				return executeFunction((ObjFunction)val);
			}
			return val;
		}
		else if (node instanceof AstCollectionUpdate) {
			AstCollectionUpdate collectionNode = (AstCollectionUpdate)node;
			
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
				if (value.Type() != ObjType.NUMBER_OBJ) {
					return new ObjError("Array index must be resolved to INTEGER.");
				}
				// check for out of bounds
				int index = (int)((ObjNumber)value).value;
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
		else if (node instanceof AstArrayAssignment) {
			// cast from node to ArrayAssignmentNode
			AstArrayAssignment array = (AstArrayAssignment)node;
			// create the array object
			ObjArray objArray = new ObjArray(array.arrayName, array.elements);
			// register the array into the symbol table.
			env.set(array.arrayName, objArray);
			
			return objArray;			
		}
		else if (node instanceof AstHashAssignment) {
			// cast from node to DictionaryAssignmentNode
			AstHashAssignment dict = (AstHashAssignment)node;
			// create the dictionary object
			ObjDictionary objDict = new ObjDictionary(dict.name, dict.elements);
			// register the array into the symbol table.
			env.set(dict.name, objDict);

			return objDict;
		}
		else if (node instanceof AstLookupCollection) {
			// cast to LookUpNode
			AstLookupCollection lookup = (AstLookupCollection)node;
			
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
				if (objAccessor.Type() != ObjType.NUMBER_OBJ) {
					return new ObjError("Array index must be resolved to INTEGER.");
				}
				// cast the ARRAY_OBJ
				ObjArray objArray = (ObjArray)value;
				ObjNumber arrayIndex = (ObjNumber)objAccessor;
				
				if (arrayIndex.value < 0 || 
						arrayIndex.value >= objArray.elements.size()) {
					return new ObjError("Array index out of bound.");
				}
				
				return Eval(objArray.elements.get((int)arrayIndex.value), env);
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
				AstNode element = objDict.elements.get(key);
				if (element == null) {
					return new ObjError("Key element does not exist: " + key);
				}
				return Eval(element, env);
			} else {
				return new ObjError("Cannot access a non Collection type.");
			}
			
			
		}
		else if (node instanceof AstFunction) {
			AstFunction funcNode = (AstFunction)node;			
			ObjFunction objFunc = new ObjFunction(funcNode.name, funcNode.parameters, funcNode.body, env);

			return objFunc;
		}
		else if (node instanceof AstFunctionCall) {
			AstFunctionCall callNode = (AstFunctionCall)node;
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
				if (callNode.actualParameters != null) {
					for (AstNode argument : callNode.actualParameters) {
						SysObject objArg = Eval(argument, env);
						if (Util.IsError(objArg)) {
							return objArg;
						}
						arguments.add(objArg);
					}
				}

				// extends the env from FUNCTION env.
				Environment extendedEnv = new Environment(objFunc.env);
				// register arguments in new environment
				if (arguments.size() > 0) {
					int index = 0;
					for (String param : objFunc.params) {
						extendedEnv.set(param, arguments.get(index));
						index++;
					}
				}
				// execute function
				SysObject result = Eval(objFunc.body, extendedEnv);
				if (result != null) {					
					if (result.Type() == ObjType.RETURN_OBJ) {
						return ((ObjReturn)result).value;
					}
				}
				return result;
			}
			else if (value.Type() == ObjType.BUILTIN_OBJ) {
				// cast to ObjBuiltin
				ObjBuiltin objBuiltin = (ObjBuiltin)value;

				// Evaluate and fill the arguments
				List<SysObject> arguments = new ArrayList<SysObject>();
				if (callNode.actualParameters != null) {					
					for (AstNode argument : callNode.actualParameters) {
						SysObject objArg = Eval(argument, env);
						if (Util.IsError(objArg)) {
							return objArg;
						}
						arguments.add(objArg);
					}
				}
				return objBuiltin.function.Execute(arguments);
			}			
		}
		else if (node instanceof AstReturn){
			AstReturn returnNode = (AstReturn)node;
			if (returnNode.returnValue != null) {
				SysObject result = Eval(returnNode.returnValue, env);
				if (Util.IsError(result)) {
					return result;
				}
				return new ObjReturn(result);
			}
		}
		else if (node instanceof AstIf) {
			AstIf ifNode = (AstIf)node;
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
		else if (node instanceof AstWhile) {
			AstWhile whileNode = (AstWhile)node;
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
	
	private SysObject EvalProgram(AstProgram program, Environment env) {
		SysObject result = null;
		
		for (AstNode statement : program.statements) {
			result = Eval(statement, env);
			if (result != null) {				
				if (result.Type() == ObjType.RETURN_OBJ) {
					return ((ObjReturn)result).value;
				}
				else if (Util.IsError(result)) {
					return (ObjError)result;
				}
			}
		}
		return result;
	}
	
	private SysObject EvalBlock(AstBlock block, Environment env) {
		SysObject result = null;
		
		for (AstNode statement : block.statements) {
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
	
	private SysObject EvalBinaryOperation(AstBinOp node, Environment env) {
		SysObject left = Eval(node.left, env);
		if (Util.IsError(left)) {
			return left;
		}
		
		SysObject right = Eval(node.right, env);
		if (Util.IsError(right)) {
			return right;
		}
		
		if (left.Type() == ObjType.NUMBER_OBJ && right.Type() == ObjType.NUMBER_OBJ) {
			return IntegerExpression((ObjNumber)left, node.op, (ObjNumber)right);
		}
		else if (left.Type() == ObjType.STRING_OBJ && right.Type() == ObjType.STRING_OBJ) {
			return StringExpression((ObjString)left, node.op, (ObjString)right);
		}
		else if(left.Type() == ObjType.BOOL_OBJ && right.Type() == ObjType.BOOL_OBJ) {
			return BooleanExpression((ObjBool)left, node.op, (ObjBool)right);
		}
		
		return NULL;
	}
	
	private SysObject IntegerExpression(ObjNumber left, TokenType op, ObjNumber right) {
		switch (op) {
		case ADD:
			return new ObjNumber(left.value + right.value);
		case SUBTRACT:
			return new ObjNumber(left.value - right.value);
		case MULTIPLY:
			return new ObjNumber(left.value * right.value);
		case DIVIDE:
			if (right.value == 0) {
				return new ObjError("Error: division by zero.");
			}
			return new ObjNumber(left.value / right.value);
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
	
	private SysObject EvalLogicalOperation(AstBinOp node, Environment env) {
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
	private SysObject executeFunction(ObjFunction objFunc) {
		// create an environment for the function.
		Environment extendedEnv = new Environment(objFunc.env);
		// execute function
		SysObject result = Eval(objFunc.body, extendedEnv);
		if (result != null) {					
			if (result.Type() == ObjType.RETURN_OBJ) {
				return ((ObjReturn)result).value;
			}
		}
		return result;
	}
}
