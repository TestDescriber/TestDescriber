package nl.tudelft.javaparser;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

public class MethodCallBean implements Bean{

	private String methodName;
	private Node body;
	private String caller;
	private List<String> parameterValues = new ArrayList<String>();
	private List<String> parameterTypes = new ArrayList<String>();

	/**
	 *  This method recursively search for call to method call inside a test method
	 * @param node it is the statement where we are looking for the method call (node in AST)
	 * @return a ObjectCreationExpr representing the AST node for the method call
	 */
	public static MethodCallExpr getMethodCallExpr(Node node){
		MethodCallExpr call = null;
		if (node instanceof MethodCallExpr){
			call = (MethodCallExpr) node;
		}
		else {
			for (Node child : node.getChildrenNodes()){
				call = getMethodCallExpr(child);
			}
		}
		return call;
	}

	/**
	 * Method to create a "bean"encapsulating a method call inside the statement under analysis
	 * @param line statement where looking for the method call
	 */
	public MethodCallBean(String line){
		try {
			MethodCallExpr obj=null;
			// we use the library JavaParser for finding the proper constructor
			if (line.contains("=")){
//				body = JavaParser.parseBodyDeclaration(line); // line that throws exception
				try {
					body = JavaParser.parseBodyDeclaration(line); // line that throws exception
					
				} catch(ParseException e) {
					//e.printStackTrace(System.err);
					System.err.print(e.getMessage());
					System.err.println("  >> Proceed with parsing as statement");
					// If the body declaration can't be parsed handle line as simple statement
					body = JavaParser.parseStatement(line);
				}
				
			} else {
				body = JavaParser.parseStatement(line);
//				try {
//					body = JavaParser.parseStatement(line);
//				} catch(ParseException e) {
//					System.err.println(e.getMessage());
//					body = JavaParser.parseBodyDeclaration(line);
//				}
			}
			// let's find the constructor
			obj = getMethodCallExpr(body);
			if(obj == null) {
				// could not extract a method from body. initialize as empty bean.
				this.methodName = "";
				this.caller = "";
				return;
			}
			
			this.methodName = obj.getName();
			this.caller = obj.getChildrenNodes().get(0).toString();
			//this.call = clazz.getName();
			// parameters for the constructor
			List<Expression> args = obj.getArgs();
			if (args != null){
				for (Expression exp : args){
					if(exp instanceof EnclosedExpr) {
						exp = (Expression) exp.getChildrenNodes().get(0);
					}
					if (exp instanceof UnaryExpr){
						UnaryExpr unary = (UnaryExpr) exp;
						Node child = unary.getChildrenNodes().get(0);
						this.parameterTypes.add(""+ExpressionUtils.getExpressionType(child.getClass().getSimpleName()));
					} else {
						this.parameterTypes.add(""+ExpressionUtils.getExpressionType(exp.getClass().getSimpleName()));
					}
					this.parameterValues.add(""+exp);
					//Character.
				}
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.err.println("  >> Error in Javaparser, create empty MethodBean.");
			
			// Set fields to empty
			this.methodName = "";
			this.caller = "";
		}
	}

	public String getName(){
		return this.methodName;
	}

	public List<String> getParameters(){
		return this.parameterTypes;
	}

	public List<String> getParameterValue(){
		return this.parameterValues;
	}
	
	public Node getBodyDeclaration(){
		return this.body;
	}

	public String getCaller() {
		return caller;
	}
	
	
}
