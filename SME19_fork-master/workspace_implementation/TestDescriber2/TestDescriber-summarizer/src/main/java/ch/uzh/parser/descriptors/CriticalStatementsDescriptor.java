package ch.uzh.parser.descriptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.en.EnglishTagger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import ch.uzh.parser.bean.InstanceVariableBean;
import ch.uzh.parser.bean.MethodBean;

public class CriticalStatementsDescriptor extends Descriptor{

	public static String addDescriptionCriticalStatements (MethodBean testCase, MethodBean method){
		String outcomeEvaluation="", usedParametersDescription="",usedAttributesDescription="", 
				usedMethodsDescription="",ifStatement="",descriptionCriticalStatement="", description = ""; 
		if (method!=null){
			String[] vectTextcontent=method.getTextContent().split("\n"); 
			int numberOfIfStatement=0;
			ArrayList<InstanceVariableBean> attributes = testCase.getOriginalClassOfTestCase().getInstanceVariables();
			List<SingleVariableDeclaration> parameters = method.getParameters();
			//if the test case contains if statements..
			if(method.getTextContent().contains("if(") | method.getTextContent().contains("if ("))
			{
				for(int i=0;i<vectTextcontent.length;i++){
					String line=vectTextcontent[i];
					if(line.contains("if(") | line.contains("if ("))
					{	
						numberOfIfStatement++;//we increment the number of if statement counter..

						if(vectTextcontent[i+1].contains("  }"))
						{outcomeEvaluation="FALSE";}
						else
						{outcomeEvaluation="TRUE";}
						System.out.println(line);
						System.out.println(outcomeEvaluation);
						System.out.println(vectTextcontent[i+1]);
						ifStatement=line.substring(line.indexOf("if "), line.lastIndexOf(")")+1);// we extract the "if statement from the current line.
						usedParametersDescription=descriptionOfUsedParametersInIfStatement(ifStatement, parameters, method);
						usedAttributesDescription=descriptionOfUsedAttributesInIfStatement(ifStatement, attributes, method,testCase);
						usedMethodsDescription=descriptionOfUsedMethodsOfOriginalClassInIfStatement(ifStatement,testCase);

						String generatedDescr = parseIfStatement(ifStatement+"{}", Boolean.getBoolean(outcomeEvaluation.toLowerCase()));
						descriptionCriticalStatement = descriptionCriticalStatement
									+getIndentation(description)
									+"// - the condition "+generatedDescr+";\n";//\""+usedParametersDescription+""+usedAttributesDescription+":\n";
						//descriptionCriticalStatement = descriptionCriticalStatement+"  -->  the outcome of the evaluation of this if statement is "+outcomeEvaluation+" \n";
					}
				}
				description = description+"\n\n"+getIndentation(description)
						+" \n // The execution of this ### implicitly covers the following "
						+numberOfIfStatement+" conditions:\n"
						+descriptionCriticalStatement;
			}
			//description = SpellCorrector.correctSentences(description);
		}
		return(description+" \n");
	}

	private static String  descriptionOfUsedAttributesInIfStatement(String ifStatement,List<InstanceVariableBean> attributes,MethodBean method,MethodBean testCase){
		List<InstanceVariableBean> usedAttributes=new ArrayList<InstanceVariableBean>();
		String usedAttributesDescription="";
		String attributesDescription="(i.e.";
		for(int p=0;p<attributes.size();p++){
			if(ifStatement.contains(attributes.get(p).getName()+""))
			{
				usedAttributes.add(attributes.get(p));
				attributesDescription=attributesDescription+" "+attributes.get(p).getType()+":"+attributes.get(p).getName();
			}
		}
		attributesDescription=attributesDescription+")";
		if(usedAttributes.size()>0)
		{
			usedAttributesDescription =" uses "+usedAttributes.size()+" attribute(s) "+attributesDescription+"\n       * of the class \""+testCase.getOriginalClassOfTestCase().getName()+"\"";
		}

		return(usedAttributesDescription);
	}

	private static String  descriptionOfUsedMethodsOfOriginalClassInIfStatement(String ifStatement,MethodBean testCase){
		List<MethodBean> usedMethods=new ArrayList<MethodBean>();
		String usedMethodsDescription="";
		String methodsDescription="(i.e.";
		for(int p=0;p<usedMethods.size();p++){
			if(ifStatement.contains("."+usedMethods.get(p).getName()+"(") | ifStatement.contains("("+usedMethods.get(p).getName()+"("))
			{
				usedMethods.add(usedMethods.get(p));
				methodsDescription=methodsDescription+" \""+testCase.getOriginalClassOfTestCase().getName()+"."+usedMethods.get(p).getName()+"\"";
			}
		}
		methodsDescription=methodsDescription+")";
		if(usedMethods.size()>0)
		{
			usedMethodsDescription =" uses "+usedMethods.size()+" method(s) "+methodsDescription+" \n       * of the class \""+testCase.getOriginalClassOfTestCase().getName()+"\"";
		}

		return(usedMethodsDescription);
	}


	private static String  descriptionOfUsedParametersInIfStatement(String ifStatement,List<SingleVariableDeclaration> parameters,MethodBean method){
		List<SingleVariableDeclaration> usedParameters=new ArrayList<SingleVariableDeclaration>();
		String usedParametersDescription="",parametersDescription="(i.e.";
		for(int p=0;p<parameters.size();p++){
			if(ifStatement.contains(parameters.get(p).getName()+""))
			{
				usedParameters.add(parameters.get(p));
				parametersDescription=parametersDescription+" "+parameters.get(p).getType()+":"+parameters.get(p).getName();

			}
		}
		parametersDescription=parametersDescription+")";
		if(usedParameters.size()>0)
		{
			usedParametersDescription =" uses "+usedParameters.size()+" parameter(s) "+parametersDescription+"\n       * of method \""+method.getName()+"\"";
		}

		return(usedParametersDescription);
	}


	public static String parseIfStatement(String stmt, boolean result){
		String ifComment="";
		//let use the library JavaParser
		try {
			Statement block = JavaParser.parseStatement(stmt);
			if (block instanceof IfStmt){
				for (Node node :  block.getChildrenNodes()){
					if (node instanceof BinaryExpr){
						BinaryExpr exp = (BinaryExpr) node;
						if (exp.getLeft() instanceof NameExpr){
							// left side of the condition
							ifComment = ifComment + " \"";
							String[] words = splitAndExpandIdentifier(exp.getLeft().toString());
							for (String word : words)
								ifComment=ifComment +" "+ word;
							// boolean operator
							words = splitAndExpandIdentifier(exp.getOperator().toString());
							for (String word : words){
								ifComment=ifComment +" "+ word;
							}
							if (ifComment.contains("equal"))
								ifComment=ifComment +" to "+ exp.getRight().toString()+"\" is "+(new String(""+result)).toUpperCase();
							else
								ifComment=ifComment +" than "+ exp.getRight().toString()+"\" is "+(new String(""+result)).toUpperCase();
						}
					} else if (node instanceof MethodCallExpr){
						MethodCallExpr call = (MethodCallExpr) node;
						String[] words = splitAndExpandIdentifier(call.getName());
						ifComment = "the current object ";
						for (String word : words)
							ifComment=ifComment +" "+ word;
						if (!result)
							ifComment = negateCondition(ifComment);
					} 
				} // for cycle
			} // if statement
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ifComment = ifComment.replaceAll("[ ]+", " ");
		if (ifComment=="")
			ifComment=stmt+" "+result;
		return ifComment;
	}

	private static String negateCondition(String description){
		System.out.println("QUI To NEGATE : "+ description);
		if (description.contains("not"))
			description = description.replace("not", "");
		else {
			try {
				EnglishTagger  tagger = new EnglishTagger();
				StringTokenizer tokenizer = new StringTokenizer(description);
				List<String> words = new ArrayList<String>();
				while (tokenizer.hasMoreTokens()){
					words.add(tokenizer.nextToken());
				}
				List<AnalyzedTokenReadings> taggedWord = tagger.tag(words);
				for (AnalyzedTokenReadings tagged : taggedWord){
					String tag = tagged.getReadings().get(0).getPOSTag();
					String token = tagged.getReadings().get(0).getToken();
					//System.out.println(token+" ---> "+tag);
					if (tag!=null){
						if (tag.equals("VBZ")){
							description = description.replace(token, token+ " not");
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("QUI NEGATED : "+ description);
		return description;
	}

}
