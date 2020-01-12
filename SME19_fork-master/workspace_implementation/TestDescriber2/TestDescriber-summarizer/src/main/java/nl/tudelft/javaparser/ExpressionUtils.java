package nl.tudelft.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;

public class ExpressionUtils {

	public static String getExpressionType(String exp) {
		String type = null;
		if (exp == null)
			return null;
		switch (exp) {
		case "StringLiteralExpr":
			type = "String";
			break;
		case "BooleanLiteralExpr":
			type = "boolean";
			break;
		case "CharLiteralExpr":
			type = "char";
			break;
		case "DoubleLiteralExpr":
			type = "double";
			break;
		case "IntegerLiteralExpr":
			type = "int";
			break;
		case "LongLiteralExpr":
			type = "long";
			break;
		case "NameExpr":
			type = exp.toString();
			break;
		}
		return type;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text =  "Rational rational0=new Rational(-291L,-291L);";
		try {
			BodyDeclaration exp = JavaParser.parseBodyDeclaration(text);
			System.out.println(exp);
			for (Node node : exp.getChildrenNodes()){
				System.out.println(node);
				System.out.println(node.getClass());
				for (Node node2 : node.getChildrenNodes()){
					System.out.println("-->"+node2);
					System.out.println("-->"+node2.getClass());
					for (Node node3 : node2.getChildrenNodes()){
						System.out.println("---->"+node3);
						System.out.println("---->"+node3.getClass());
						for (Node node4 : node3.getChildrenNodes()){
							System.out.println("------>"+node4);
							System.out.println("------>"+node4.getClass());
						}
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
