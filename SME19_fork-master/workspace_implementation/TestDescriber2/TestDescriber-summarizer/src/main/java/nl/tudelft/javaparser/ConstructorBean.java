package nl.tudelft.javaparser;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class ConstructorBean implements Bean{

	private String constructor;
	private Node node;
	private List<String> parameterValues = new ArrayList<String>();
	private List<String> parameterTypes = new ArrayList<String>();

	/**
	 *  This method recursively search for call to Constructor inside a test method
	 * @param node it is the statement where we are looking for the constructor (node in AST)
	 * @return a ObjectCreationExpr representing the AST node for the constructor call
	 */
	public ObjectCreationExpr getConstructor(Node node){
		ObjectCreationExpr constructor = null;
		if (node instanceof ObjectCreationExpr){
			constructor = (ObjectCreationExpr) node;
		}
		else {
			for (Node child : node.getChildrenNodes()){
				constructor = getConstructor(child);
			}
		}
		return constructor;
	}

	/**
	 * Method to create a "bean"encapsulating a constructor call inside the statement under analysis
	 * @param line statement where looking for the constructor
	 */
	public ConstructorBean(String line){
		try {
			// we use the library JavaParser for finding the proper constructor
			BlockStmt block = JavaParser.parseBlock("{"+line+"}");
			System.out.println(line);
			Node node = block.getChildrenNodes().get(0);
//			if (node instanceof ExpressionStmt){
				// let's find the constructor
				ObjectCreationExpr obj = getConstructor(node);
				// then, we find the exact constructor name
				ClassOrInterfaceType clazz = (ClassOrInterfaceType) obj.getChildrenNodes().get(0);
				this.constructor = clazz.getName();
				// parameters for the constrcutor
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
					}
				}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getName(){
		return this.constructor;
	}

	public List<String> getParameters(){
		return this.parameterTypes;
	}

	public List<String> getParameterValue(){
		return this.parameterValues;
	}

	public Node getBodyDeclaration(){
		return this.node;
	}
}
