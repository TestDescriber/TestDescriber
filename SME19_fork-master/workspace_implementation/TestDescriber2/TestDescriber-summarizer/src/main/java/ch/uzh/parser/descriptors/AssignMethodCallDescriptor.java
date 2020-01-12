package ch.uzh.parser.descriptors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;
import nl.tudelft.javaparser.MethodCallBean;
import nl.tudelft.language.SpellCorrector;

public class AssignMethodCallDescriptor extends Descriptor{

	private static String describeGetMethod(MethodCallBean methodCall, MethodBean method){
		String description = " the ";
		String methodCorpus = method.getTextContent();
		methodCorpus = methodCorpus.substring(methodCorpus.indexOf("{"), methodCorpus.length());
		try {
			BlockStmt body = JavaParser.parseBlock(methodCorpus);
			if (body.getChildrenNodes().size()>1){
				String methodName = methodCall.getName().replaceFirst("get", "");
				String[] words = splitAndExpandIdentifier(methodName);
				for(String word : words) {
					description= description+" "+word.replaceAll("[0-9]","");
				}
			} else {
				List<Statement> methodBody = body.getStmts();
				for(Statement bodyStatement : methodBody) {
					bodyStatement.toStringWithoutComments();
				}
				// original line
				Node blockStmt = body.getChildrenNodes().get(0);
				ReturnStmt stmt = null;
				try {
					stmt = (ReturnStmt) blockStmt;
				} catch(ClassCastException e) {
					// Cast Exception means there seems to be a nested structure. Find first Return Statement.
					stmt = findReturnStatementInIf(blockStmt).get(0);
				}
				Node child = stmt.getChildrenNodes().get(0);
				if (child instanceof NameExpr){
					String[] words = splitAndExpandIdentifier(((NameExpr) child).getName());
					for(String word : words) {
						description= description+" "+word.replaceAll("[0-9]","");
					}
				} else { 
					String methodName = methodCall.getName().replaceFirst("get", "");
					String[] words = splitAndExpandIdentifier(methodName);
					for(String word : words) {
						description= description+" "+word.replaceAll("[0-9]","");
					}
				}
			}			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: ANNIBALE -> adding here the info about the returned class attribute
		//String content = method.getClassBeanContenitor().getTextContent();
		description = description +" of "+methodCall.getCaller();
		//description = description+" thus, it returns a variable of type \""+method.getReturnType()+"\". \n";
		return description;
	}

	private static List<ReturnStmt> findReturnStatementInIf(Node stmt) {
		List<ReturnStmt> retStmt = new ArrayList<ReturnStmt>();
		
		for(Node innerStmt : stmt.getChildrenNodes()) {
			
			if(innerStmt instanceof ReturnStmt) {
				retStmt.add((ReturnStmt) innerStmt);
				
			} else if(stmt.getChildrenNodes().size() == 0){
				return null;
				
			} else {	
				retStmt.addAll(findReturnStatementInIf(innerStmt));
			}
		}
		return retStmt;
	}
	
	private static String describeHasMethod(MethodCallBean methodCall){
		String description = methodCall.getCaller() + " ";
		String methodName = methodCall.getName();
		String[] words = splitAndExpandIdentifier(methodName);
		//we start from "1" because we want to descard the "get word"
		for(String word : words) {
			description= description+" "+word.replaceAll("[0-9]","");
		}
		//description = description+"\n  // returns a boolean flag indicating if it has a "+wordsCollapsed.toLowerCase()+". \n";
		return description;
	}
	
	private static String describeIsMethod(MethodCallBean methodCall){
		String description = methodCall.getCaller() + " ";
		String methodName = methodCall.getName();
		String[] words = splitAndExpandIdentifier(methodName);
		//we start from "1" because we want to descard the "get word"
		for(String word : words) {
			description= description+" "+word.replaceAll("[0-9]","");
		}
		//description = description+"\n  // returns a boolean flag indicating if it has a "+wordsCollapsed.toLowerCase()+". \n";
		return description;
	}

	private static String describeToStringMethod(MethodCallBean methodCall, MethodBean exactMethod){
		String description = "";
		ClassBean classe = exactMethod.getClassBeanContenitor();
		String methodName = methodCall.getName();
		String[] words = splitAndExpandIdentifier(methodName);
		//we start from "1" because we want to discard the "get word"
		for(String word : words) {
			description= " "+word.replaceAll("[0-9]","");
		}
		description = " the string form of "+classe.getName().toLowerCase()+" \n";
		return description;
	}
	
	private static String describeAGenericMethod(MethodCallBean methodCall, MethodBean exactMethod){
		String description = " ";
		String methodNameSplitted = "";
		String methodName = methodCall.getName();
		String[] words = splitAndExpandIdentifier(methodName);
		//we start from "1" because we want to discard the "get word"
		for(String word : words) {
			methodNameSplitted= methodNameSplitted+" "+word.replaceAll("[0-9]","");
		}
		
		if(exactMethod.getName().equals("equals")){
			description = " "+methodCall.getCaller()+" is equal to "+methodCall.getParameterValue().get(0)+" \n";
		} else {
				//let's split the method names
				if (exactMethod.getParameters().size()==0){
					description = " whose value is equal to the "+ methodNameSplitted + " of \""+methodCall.getCaller()+"\" ";
				} else if(exactMethod.getParameters().size()==1){
					if (containsVerb(methodNameSplitted)){//SEE ACTIVE AND PASSIVE VERB IS BETTER
						description = " whose value is obtained by "+ methodNameSplitted +"ing "
								+ exactMethod.getParameters().get(0)+" to "+methodCall.getCaller();
					} else {
						description = " whose value is the"+ methodNameSplitted + " of \""+methodCall.getCaller()+"\" ";
					}
				} else {
					for (int nvar=0; nvar<exactMethod.getParameters().size(); nvar++){
						SingleVariableDeclaration parameter = exactMethod.getParameters().get(nvar);
						String[] splittedWords = splitAndExpandIdentifier(parameter.getName().toString());
						for (String word : splittedWords){
							methodNameSplitted = methodNameSplitted + " "+word;
						}
					}
				}
		}
		description = " "+SpellCorrector.correctSentences(description);
		description = description.toLowerCase();
		return description+" ";
	}
	
	private static String describeExternalMethod(MethodCallBean methodCall){
		String description = "";
		String wordsCollapsed = "";
		String methodName = methodCall.getName();
		String[] words = splitAndExpandIdentifier(methodName);
		//we start from "1" because we want to discard the "get word"
		for(String word : words) {
			wordsCollapsed= wordsCollapsed+" "+word.replaceAll("[0-9]","");
		}
		// TODO : improve the message for external method (i.e., method not belonging to the same class
		if (methodCall.getParameterValue().size()==0) {
			description = " equal to the "+ wordsCollapsed + " of \""+methodCall.getCaller()+"\" \n";
		}else if (methodCall.getParameterValue().size()==1)
				description = "// the method "+ wordsCollapsed 
				+" "+methodCall.getParameterValue().get(0)+ " to the object \""+methodCall.getCaller()+"\" \n";
		else {
			description = ""+ wordsCollapsed + " for the object \""+methodCall.getCaller()+"\" \n";
		}
		//description = description+" and returns a variable of type \""+exactMethod.getReturnType()+"\". \n";
		return description;
	}


	public static String generateDescriptor(String statement, ClassBean executedClass, MethodBean testCase, ClassBean originalClass){
		String description="";
		description = description + generateDescriptorWithotCritical(statement, testCase, originalClass);
		description = description + " \n"+ deriveCriticalStatement(statement, executedClass, testCase);
		return(description);
	}

	public static String generateDescriptorWithotCritical(String statement, MethodBean testCase, ClassBean originalClass){
		String description="";
		// TODO: we have an exception from  JavaParser if the test case
		// contains a "catch" clause. For now we simply add an "if"
		// condition to avoid this situation
		if (!statement.contains("catch (") && !statement.contains("catch(") && !statement.contains("fail(") && !statement.contains("fail (")){
			MethodCallBean methodCall=new MethodCallBean(statement);
			// we extract the parameters for this method call
			MethodBean method = Descriptor.getMethodFromList((List<MethodBean>) originalClass.getMethods(), methodCall);
			if(method!=null){ 
				String nameMethod = method.getName();
				System.out.println("METHOD FOUND? "+ method);
				// we want to make shorter the description for method "get", "set" or "has"
				if(nameMethod.startsWith("get"))//if it is a get method..
				{
					description=description+" "+describeGetMethod(methodCall, method);
				}

				else if(nameMethod.startsWith("has") & method.getReturnType().toString().contains("boolean")) {   //if it is a has method..
					description=description+" "+describeHasMethod(methodCall);
				}
				
				else if(nameMethod.startsWith("is") & method.getReturnType().toString().contains("boolean")) {   //if it is a has method..
					description=description+" "+describeIsMethod(methodCall);
				}

				else if( nameMethod.equals("toString")) {
					//if it is a toString method...
					description=description+" "+describeToStringMethod(methodCall,method);
				}
				else {
					description=description+" "+describeAGenericMethod(methodCall,method);
				}
			} else {
				// if the method is not found within the class under test, this means that the method comes
				// from another class
				if (!methodCall.getName().contains("fail"))
					description=description+" "+describeExternalMethod(methodCall);

			}
		}
		return(description);
	}
	
	public static String deriveCriticalStatement(String statement, ClassBean executedClass, MethodBean testCase){
		String description="";
		// TODO: we have an exception from  JavaParser if the test case
		// contains a "catch" clause. For now we simply add an "if"
		// condition to avoid this situation
		if (!statement.contains("catch (") && !statement.contains("catch(")){
			MethodCallBean methodCall=new MethodCallBean(statement);
			// we extract the parameters for this method call
			MethodBean method = Descriptor.getMethodFromList((List<MethodBean>) executedClass.getMethods(), methodCall);
			if(method!=null){ 
				description = description + CriticalStatementsDescriptor.addDescriptionCriticalStatements(testCase, method).replace("*", "//");
				description = description.replace("###", "method call on the right side of the assignment");
				}
			}
		return(description+" \n");
	}
	
	public static int getNumParameters(Node node){
		int numParameters=0;
		if (node instanceof MethodCallExpr){
			MethodCallExpr call = (MethodCallExpr) node;
			//System.out.println("Number of parameters "+call.getArgs().size());
			if(call.getArgs()==null)
			{
				numParameters = 0;
			}
			else{
				numParameters = call.getArgs().size();
			}
		}
		else {
			for (Node child : node.getChildrenNodes()){
				numParameters = getNumParameters(child); //FIXME: are u sure about that!
			}
		}
		return numParameters;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = "rational1.floatValue();";
		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = generateDescriptor(text, null, testCase, null);
		System.out.println(description);
	}
}

