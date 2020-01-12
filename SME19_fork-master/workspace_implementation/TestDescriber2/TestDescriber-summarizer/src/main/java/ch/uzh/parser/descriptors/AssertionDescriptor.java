package ch.uzh.parser.descriptors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class AssertionDescriptor extends Descriptor{


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


	public static String generateAssertionsComments(String statement, ClassBean executedClass, MethodBean testCase, ClassBean originalClass){
		String description="";
		// System.out.println("Assert statement:"+statement);
		// we increment the number of found assertion
		testCase.setCounterAssertions(testCase.getCounterAssertions()+1);
		if(testCase.getCounterAssertions()==1) {
			description="  // Then, it tests: \n";
		}

		try {
			Expression body = JavaParser.parseExpression(statement);
			//System.out.println("assertName "+ getCallMethodName(body3));
			MethodCallExpr methodCall=getMethodCallExpr(body);
			String assertName = methodCall.getName();
			description=description+"  //"+testCase.getCounterAssertions()+") whether ";
			if (body.getChildrenNodes().size()==1){
				//assert with only one parameters (e.g., assertTrue, assertTrue, assertNull, etc.)
				Node child=body.getChildrenNodes().get(0);
				if (child instanceof MethodCallExpr){
					assertName = assertName.replace("assert","");
					String callDescr = AssertMethodCallDescriptor.generateDescriptorWithotCritical(child.toString()+";",testCase, originalClass);
					//if (assertName.contains("False") && callDescr.contains("equal")){
					//	description = description + callDescr.replace("is", "is not")+" \n";
					//} else {
					description = description + callDescr+" \n";
					//}
				} else if (child instanceof NameExpr){
					assertName = assertName.replace("assert","");
					description = description+" \""+child.toString()+"\" is ";
					String[] words = splitAndExpandIdentifier(assertName);
					for (String word : words)
						description = description+" "+word;
					description = description+";\n";
				} else {
					description = description + "// NO SUGGESTED COMMENT FOR THIS ASSERTION \n";
				}
			} else if (body.getChildrenNodes().size()==2){
				//assert with only two parameters (e.g., assertEquals, assertNotEquals, etc.)
				Node nodeLeftSideAssert=body.getChildrenNodes().get(0);//
				Node nodeRightSideAssert=body.getChildrenNodes().get(1);//
				if (nodeRightSideAssert instanceof MethodCallExpr){
					assertName = assertName.replace("assert","");
					String callDescr = AssertMethodCallDescriptor.generateDescriptorWithotCritical(nodeRightSideAssert.toString()+";",testCase, originalClass);
					if (callDescr.contains(" has ")){
						description = description +" "+callDescr;
						if (description.contains("false")){
							description = description.replace("has", "has not");
						} else
							description = description.replace("is equal to true", "");
						description = description + " \n";
					} else if (callDescr.contains(" is ")){
						description = description +" "+callDescr;
						if (description.contains("false")){
							description = description.replace("is", "is not");
						} else
							description = description.replace("is equal to true", "");
						description = description + " \n";
					}
					else {
						callDescr = callDescr.replace("\n", "");
						description =  description +callDescr+" is ";

						String[] words = splitAndExpandIdentifier(assertName);
						for (String word : words)
							description = description+" "+word;
						description = description+" to "+ nodeLeftSideAssert+"; ";
						description = description + AssertMethodCallDescriptor.deriveCriticalStatement(nodeRightSideAssert.toString()+";", executedClass, testCase);
					}
				} else  if (nodeRightSideAssert instanceof NameExpr){
					assertName = assertName.replace("assert","");
					description = description+" \""+nodeRightSideAssert.toString()+"\" is ";
					String[] words = splitAndExpandIdentifier(assertName);
					for (String word : words)
						description = description+" "+word;
					description = description+" to "+ nodeLeftSideAssert+";\n";
				} else if (nodeRightSideAssert instanceof FieldAccessExpr){
					FieldAccessExpr field = (FieldAccessExpr) nodeRightSideAssert;
					assertName = assertName.replace("assert","");
					description = description+" the "+field.getField()+" of "+field.getScope()+" is ";
					String[] words = splitAndExpandIdentifier(assertName);
					for (String word : words)
						description = description+" "+word;
					description = description+" to "+ nodeLeftSideAssert+"; \n";
				} else {
					description = description + "// NO SUGGESTED COMMENT FOR THIS ASSERTION \n";
				}
			} else if (body.getChildrenNodes().size()==3){
				//assert with only three parameters (only one case for float parameters)
				Node nodeLeftSideAssert=body.getChildrenNodes().get(0);
				Node nodeRightSideAssert=body.getChildrenNodes().get(1);
				Node delta=body.getChildrenNodes().get(2);
				if (nodeRightSideAssert instanceof MethodCallExpr){
					assertName = assertName.replace("assert","");
					String callDescr = AssertMethodCallDescriptor.generateDescriptorWithotCritical(nodeRightSideAssert.toString()+";",testCase, originalClass);
					description =  description +" "+callDescr+" is ";
					String[] words = splitAndExpandIdentifier(assertName);
					for (String word : words)
						description = description+" "+word;
					description = description+" to "+ nodeLeftSideAssert
							+ " with delta equal to " + delta.toString()
							+"; ";
					description = description.replaceAll("\n", " ");
					description = description + AssertMethodCallDescriptor.deriveCriticalStatement(nodeRightSideAssert.toString()+";", executedClass, testCase);
					description =  description+" \n";
				} else if (nodeRightSideAssert instanceof NameExpr){
					assertName = assertName.replace("assert","");
					description = description+" \""+nodeRightSideAssert.toString()+"\" is ";
					String[] words = splitAndExpandIdentifier(assertName);
					for (String word : words)
						description = description+" "+word;
					description = description+" to "+ nodeLeftSideAssert
							+ " with delta equal to " + delta.toString()
							+";\n";
				} else {
					description = description + "//  NO SUGGESTED COMMENT FOR THIS ASSERTION \n";
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		description = description.replaceAll("[ ]+", " ");
		description = description.replaceAll("equals", "equal");
		return description;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = "assertEquals(-291L,rational1.denominator);";
		try {
			Expression body = JavaParser.parseExpression(text);
			for (Node node : body.getChildrenNodes()){
				System.out.println(node);
				System.out.println(node.getClass());
				for (Node node2 : node.getChildrenNodes()){
					System.out.println("->"+node2);
					System.out.println("->"+node2.getClass());
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = AssertionDescriptor.generateAssertionsComments(text, null, testCase, null);
		System.out.println(description);

	}

}
