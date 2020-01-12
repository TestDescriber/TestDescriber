package ch.uzh.parser.descriptors;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;

import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;

public class MethodDescriptor extends Descriptor{

	public static String generateMethodComments(String statement, ClassBean executedClass, MethodBean testCase, ClassBean originalClass) {
		String description2="";
		//we have to detect the method...
		//System.out.println("statement:"+statement);
			//case 1: "variable declaration (done invoking a particular method of the class) "
			//(without assert)
			if(statement.contains("=") & !statement.contains("assert")) {
				
				description2=variableDeclarationDescription(statement, executedClass, testCase, originalClass);
			} else if(!statement.contains("assert") & !statement.contains("fail(") & !statement.contains("catch"))
				//it is not a declaration
			{  
				description2=MethodCallDescriptor.generateDescriptor(statement, executedClass, testCase, originalClass);
			}		    

		//description2 = description2 + CriticalStatementsDescriptor.addDescriptionCriticalStatements(description, testCase, method)
		return(description2);
	}

	public static String variableDeclarationDescription(String statement, ClassBean executedClass, MethodBean testCase, ClassBean originalClass){
		//String 
		String description="";
		BodyDeclaration stmt;
		try {
			stmt = JavaParser.parseBodyDeclaration(statement);
			// the first node is the reference of the declaration
			Node leftNode = stmt.getChildrenNodes().get(0);
			if (leftNode instanceof PrimitiveType){
				PrimitiveType type = (PrimitiveType) leftNode;
				description = description + "// The test case declares a "+type.toString();
			} else if (leftNode instanceof ReferenceType){
				ReferenceType reference = (ReferenceType) leftNode;
				description = description + "// The test case declares an object of the class \""+reference.toString()+"\"";
			}
			// the second node is a VariableDeclarator
			VariableDeclarator declaration = (VariableDeclarator) stmt.getChildrenNodes().get(1);
			
			if (declaration.getChildrenNodes().get(1) instanceof MethodCallExpr){
				    MethodCallExpr expr = (MethodCallExpr) declaration.getChildrenNodes().get(1);
				    String call=AssignMethodCallDescriptor.generateDescriptorWithotCritical(expr.toString()+";", testCase, originalClass);
				    description = description +"  "+call;
				    description = description +" "+ AssignMethodCallDescriptor.deriveCriticalStatement(statement, executedClass, testCase);
			} else if (declaration.getChildrenNodes().get(1) instanceof CastExpr){
				CastExpr cast =(CastExpr) declaration.getChildrenNodes().get(1);
				description = description + " using a casting ("+cast+")";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (description=="")
			description = "// To Complete : MethodDescriptor.variableDeclarationDescription(...)";
		//description = SpellCorrector.correctSentences(description);
		return(description+" \n");
	}

}
