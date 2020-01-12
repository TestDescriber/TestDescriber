package ch.uzh.parser.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import nl.tudelft.javaparser.ConstructorBean;
import nl.tudelft.language.SpellCorrector;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;

public class ConstructorDescriptor extends Descriptor{

	
/**
 * 
 * @param line
 * @param executedClass
 * @param testCase
 * @param originalClass
 * @return
 */
	
	/**
	 * @param line
	 * @param executedClass
	 * @param testCase
	 * @param originalClass
	 * @param linesCovered
	 * @param description2Parameter
	 * @return
	 */
	public static String generateConstructorCommentsWithScenarious(String line, ClassBean executedClass, MethodBean testCase, ClassBean originalClass, List<String> linesCovered) {
		String description2 = "";
		//it will contain the description related to the call to other construction and/or the 
		// initialization of attributes of the class.
		String additionalConstructorDescription = "";
		// we extract the name of the constructor..
		
		ConstructorBean constrCall = new ConstructorBean(line);
		// we have to determine which constructor is used from the original class
		List<MethodBean> methods = (List<MethodBean>) originalClass.getMethods();
		MethodBean method = getMethodFromList(methods, constrCall);
		
		if (method!=null){
			// we found the exact constructor
			if (constrCall.getParameterValue().size() > 0) {
			/*	if(! ( 
						(description2Parameter.contains("// This statement instantiates")) && 
						(description2Parameter.contains("explicit arguments:")) 
					 ) )
				  {
				   description2 = description2 + "// This statement instantiates an "
						+ "\"" + constrCall.getName() + "\"" + "  with \"explicit arguments:\"  \" ";
				  }*/

				for (int nvar = 0; nvar < method.getParameters().size(); nvar++) {
					SingleVariableDeclaration parameter = method.getParameters()
							.get(nvar);
					String[] splittedWords = splitAndExpandIdentifier(parameter
							.getName().toString());
					description2 = description2 + "\" ";
					for (String word : splittedWords) {
						description2 = description2 + " " + word;
					}
					description2 = description2 + "\" equal to "
							+ constrCall.getParameterValue().get(nvar);
					if (nvar == method.getParameters().size() - 2)
						description2 = description2 + ", and ";
					else if (nvar < method.getParameters().size() - 1)
						description2 = description2 + ", ";
				}
				   description2 =  "// This statement instantiates a class "
						+ "\"" + constrCall.getName() + "\"" + "  with \"explicit arguments:\"  \" "+description2;
				   System.out.println("constructor: "+constrCall.getName() +" linesCoveredOfConstructor: "+executedClass.getTextContent());
				   additionalConstructorDescription = additionalConstructorDescription+generateAdditionalConstructorDescription(method, line, originalClass, executedClass, constrCall);
				   description2 = description2 +additionalConstructorDescription;
			} 
			
	
			if (constrCall.getParameterValue().size() == 0) {
				/*if(! ( 
						(description2Parameter.contains("// This statement instantiates")) && 
						(description2Parameter.contains("implicit arguments:")) 
					 ) )
				  {
					description2 = description2 + "// This statement instantiates an  ";
				  
				description2 = description2 + "\"" + constrCall.getName() + "\""
						+ " with the default configuration, thus ";
				description2 = description2 + " with \"implicit arguments:\" ";
				  }*/
				description2 = description2 +  generateDescriptionForImplicitArguments(method, line, originalClass, linesCovered, description2);
				description2 =  "// This statement instantiates a class "
						+ "\"" + constrCall.getName() + "\"" + "  with \"implicit arguments:\"  \" "+description2;
			}
			//String[] linesCoveredOfConstructor = method.getTextContent().split("\n");
			//description2 = description2 + ".\n";
//			description2 = getIndentation(line)+SpellCorrector.correctSentences(description2);
		}
		
		// now we extract the executed if condition from the executed class
		method = getMethodFromList((List<MethodBean>) executedClass.getMethods(), constrCall);
		if (method!=null){
			description2 = description2 + CriticalStatementsDescriptor.addDescriptionCriticalStatements(testCase, method).replace("*", "//");
			description2 = description2.replace("###", "constructor");
		} else {
			System.out.println("\tConstructor not found in Class Under Test. Proceed...");
		}
		return (description2);
	}
	
	
	/**
	 * @param method
	 * @param line
	 * @param executedClass
	 * @return
	 */
	public static String generateAdditionalConstructorDescription(MethodBean method, String line, ClassBean originalClass, ClassBean executedClass, ConstructorBean constrCall){
		// we have to find the implicit arguments
		System.out.println(" ****  " + method.toString());
		System.out.println(" *****  " + method.getUsedInstanceVariables());	
		System.out.println(" ******  " + line);		
		System.out.println(" ******  " + (method.getParameters().size()));
		System.out.println(" *******  " + (method.getTextContent()));
		String additionalConstructorDescription = "";
		String thisInformation = "";
		
		// we split the line lines Covered Of the Constructor
		String[] linesCoveredOfConstructor = method.getTextContent().split("\\r?\\n");
		// line composing a constructor or a method of the executed class
		String[] linesCoveredOfExecutedClass = null;
		// we obtain the list of method covered in the executed class
		List<MethodBean> methods = (List<MethodBean>) executedClass.getMethods();
		
		//we try to identify the line of this constructor calling other constructors and use this information
		//to generate further description...
		for (int l=0; l< linesCoveredOfConstructor.length;l++)
		{
			if(linesCoveredOfConstructor[l].contains("this(") )
			{
				//System.out.println(" ******** line: " + (linesCovered.get(l)));
				System.out.println(" *********-> linesCoveredOfConstructor1: " + linesCoveredOfConstructor[l]);
				thisInformation = linesCoveredOfConstructor[l];
				//thisInformation = thisInformation.replace("this",""+constrCall.getName() );
				System.out.println(" **********-> methods.size(): " + methods.size());
				//then for each constructor covered in the original class, we identify the one called by the "current constructor"  
/**/					for (int m=0; m< methods.size();m++)
							{
							//if it is a constructor of the class
							if(methods.get(m).getTextContent().contains(constrCall.getName() +"(") )
							{
								// we take the first line of this constructor
								linesCoveredOfExecutedClass = methods.get(m).getTextContent().split("\\r?\\n");
								System.out.println(" *********-> linesCoveredOfClass1: " + linesCoveredOfExecutedClass[0]+" && "+thisInformation);
								//if it is the same called (as "this(..)) in the current constructor...
								if(linesCoveredOfExecutedClass[0].split(",").length == thisInformation.split(",").length){
									//we generate the additional descriptions..
									additionalConstructorDescription = additionalConstructorDescription + linesCoveredOfExecutedClass[0].replace("public ","").replace("{","") ;
								additionalConstructorDescription = " - with this argument(s) the current constructor calls the constructor  \""+additionalConstructorDescription+"\" to assign concrete values to all the "+additionalConstructorDescription.split(",").length+" parameters";
								System.out.println(" *********-> additionalConstructorDescription1: " + additionalConstructorDescription);
								
								}
							  }
							}
							/**/
			}
			
		}
		
		return additionalConstructorDescription;
	}
	
	/**
	 * 
	 * @param method
	 * @param line
	 * @param executedClass
	 * @return
	 */
	public static String generateDescriptionForImplicitArguments(MethodBean method, String line, ClassBean executedClass, List<String> linesCovered, String description2){
		// we have to find the implicit arguments
		System.out.println(" ****  " + method.toString());
		System.out.println(" *****  " + method.getUsedInstanceVariables());	
		System.out.println(" ******  " + line);		
		System.out.println(" ******  " + (method.getParameters().size()));
		System.out.println(" *******  " + (method.getTextContent()));
		
		String[] linesCoveredOfConstructor = method.getTextContent().split("\n");
		if(executedClass.getInstanceVariables()!= null)
		if(executedClass.getInstanceVariables().size()>0)
		{
				for (int v=0; v< executedClass.getInstanceVariables().size();v++)
				{
					for (int l=0; l< linesCoveredOfConstructor.length;l++)
					{
						if(linesCoveredOfConstructor[l].contains("this."+executedClass.getInstanceVariables().get(v).getName()) )
						{
							//System.out.println(" ******** line: " + (linesCovered.get(l)));
							System.out.println(" ********* Attribute: " + (executedClass.getInstanceVariables().get(v).getName()));
							System.out.println(" *********-> linesCoveredOfConstructor0: " + linesCoveredOfConstructor[l]);
							description2 = description2 
									+ linesCoveredOfConstructor[l].replace("=", " equal to ").replace("this.", "").replace(";", "").replaceAll("  ", " ");
							
							if (l < linesCoveredOfConstructor.length - 2)
								{
								description2 = description2 + ", ";
								}
						}
					}
				}
			}
		description2 = description2 + ".";
		return description2;
	}
	
	/**
	 * 
	 * @param line
	 * @param executedClass
	 * @param testCase
	 * @param originalClass
	 * @return
	 */
	public static String generateConstructorComments(String line, ClassBean executedClass, MethodBean testCase, ClassBean originalClass) {
		String description2 = "";
		// we extract the name of the constructor..
		ConstructorBean constrCall = new ConstructorBean(line);
		// we have to determine which constructor is used from the original class
		List<MethodBean> methods = (List<MethodBean>) originalClass.getMethods();
		MethodBean method = getMethodFromList(methods, constrCall);
		if (method!=null){
			// we found the exact constructor
			if (constrCall.getParameterValue().size() > 0) {
				description2 = description2 + "// The test case instantiates a "
						+ "\"" + constrCall.getName() + "\"" + " with ";

				for (int nvar = 0; nvar < method.getParameters().size(); nvar++) {
					SingleVariableDeclaration parameter = method.getParameters()
							.get(nvar);
					String[] splittedWords = splitAndExpandIdentifier(parameter
							.getName().toString());
					for (String word : splittedWords) {
						description2 = description2 + " " + word;
					}
					description2 = description2 + " equal to "
							+ constrCall.getParameterValue().get(nvar);
					if (nvar == method.getParameters().size() - 2)
						description2 = description2 + ", and ";
					else if (nvar < method.getParameters().size() - 1)
						description2 = description2 + ", ";
				}
			} else {
				description2 = description2 + "// The test case instantiates a "
						+ "\"" + constrCall.getName() + "\""
						+ " with the default configuration";
			}
			
			description2 = description2 + ".\n";
//			description2 = getIndentation(line)+SpellCorrector.correctSentences(description2);
		}
		
		// now we extract the executed if condition from the executed class
		method = getMethodFromList((List<MethodBean>) executedClass.getMethods(), constrCall);
		if (method!=null){
			description2 = description2 + CriticalStatementsDescriptor.addDescriptionCriticalStatements(testCase, method).replace("*", "//");
			description2 = description2.replace("###", "constructor");
		} else {
			System.out.println("\tConstructor not found in Class Under Test. Proceed...");
		}
		return (description2);
	}

}

