package ch.uzh.parser.testing.summarization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import nl.tudelft.jacoco.JaCoCoRunner;
import nl.tudelft.jacoco.JacocoResult;
import nl.tudelft.language.SpellCorrector;
import nl.tudelft.utils.commandline.TestCaseParser;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;

import ch.uzh.parser.JavaFileParser;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.InstanceVariableBean;
import ch.uzh.parser.bean.MethodBean;
import ch.uzh.parser.descriptors.AssertionDescriptor;
import ch.uzh.parser.descriptors.ClassDescriptor;
import ch.uzh.parser.descriptors.ConstructorDescriptor;
import ch.uzh.parser.descriptors.MethodDescriptor;
import ch.uzh.utility.FileUtils;

/**
 * Main parser for Test case Summarization..
 * @author Sebastiano Panichella and Annibale Panichella
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 *
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ParseException {


		String sourceFolder = "/Users/setup/Desktop/Publication/Bachelor-and-Master-Thesis/Bahelor-Thesis/Antonio-Galluccio/workspace2-modified-implementation/Task1/src/";
        String pBinFolder = "/Users/setup/Desktop/Publication/Bachelor-and-Master-Thesis/Bahelor-Thesis/Antonio-Galluccio/workspace2-modified-implementation/Task1/bin/";
        

		//String sourceFolder = "/Users/annibale.panichella/Desktop/TestDescribe/testdescriber-tool/workspace/Task1/src/";
		//String pBinFolder = "/Users/annibale.panichella/Desktop/TestDescribe/testdescriber-tool/workspace/Task1/bin/";

        
		List<String> classesFiles = new ArrayList<String>();
		classesFiles.add("org/magee/math/Rational.java");

		List<String> testsFiles = new ArrayList<String>();
		testsFiles.add("org/magee/math/TestRational.java");

/*		
		List<String> classesFiles = new ArrayList<String>();
		//classesFiles.add("org/magee/math/Rational.java");
		classesFiles.add("org/magee/math/Rational.java");

		List<String> testsFiles = new ArrayList<String>();
		//testsFiles.add("org/magee/math/Rational_ESTest.java");
		testsFiles.add("org/magee/math/Rational_ESTest.java");
*/
		System.out.println("Step 1: Parsing JAVA CLASSES/JAVA TESTS");
		Vector<ClassBean> productionClass = JavaFileParser.parseJavaClasses(classesFiles, sourceFolder);
		Vector<ClassBean> testClass = JavaFileParser.parseJavaClasses(testsFiles, sourceFolder);

		System.out.println("Step 2: Running JaCoCo ");
		ClassBean classeTest = testClass.get(0);
		ClassBean clazz = productionClass.get(0);

		// Map used to store the covered lines
		Map<String, List<Integer>> coverage = new HashMap<String, List<Integer>>();

		// extract the number of test methods in 'test_case'
		List<String> list = TestCaseParser.findTestMethods(pBinFolder, convert2PackageNotation(testsFiles.get(0)));
		System.out.println(list);
		String project_dir = System.getProperty("user.dir");

		List<File> jars = new ArrayList<File>();
		jars.add(new File(pBinFolder));

		List<String> testsCoverage = new ArrayList<String>();

		// initialize class runner for Jacoco
		for (String tc : list){
			String temp_file = project_dir+"/temp1";
			JaCoCoRunner runner = new JaCoCoRunner(new File(temp_file), jars);
			runner.run(convert2PackageNotation(classesFiles.get(0)), convert2PackageNotation(testsFiles.get(0))+"#"+tc, jars);
			JacocoResult results = runner.getJacocoResults();
			System.out.println(results.getBranchesCovered());
			coverage.put(tc, new ArrayList<Integer>(results.getCoveredLines()));

			String covered = results.getCoverageInfoAsString();
			if (covered != null)
				testsCoverage.add(covered);
		}

		if (testsCoverage.size() == 0) {
			System.out.println("No coverage information are obatined "+testsCoverage);
			return;
		}

		List<MethodBean> testCases = (Vector<MethodBean>) classeTest.getMethods();

		System.out.println("Step 3: Parsing Covered Statements");
		List<String> textContentExecutedOriginalClass = null;
		for(int index=0; index<testCases.size(); index++) {
			try {
				textContentExecutedOriginalClass=SrcMLParser.parseSrcML(testCases, index, clazz, sourceFolder, classesFiles, 0, testsCoverage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Derive the import from the class
		clazz.setImports(clazz.extractImports(textContentExecutedOriginalClass));

		System.out.println("Step 4: Generating Summaries as code comments");

		System.out.println("Step 4.1: Generating a summary for each test method");
		for(int index=0; index<testCases.size(); index++) {
			// for each test method
			MethodBean testCase = testCases.get(index);

			ClassBean classUnderTest = testCase.getOriginalClassOfTestCase();
			ClassBean coveredCode = testCase.getClassExecutedByTheTestCase();
			List<String> linesCovered = testCase.getLinesExecutedOfOriginalClass();

			// we generate comments for each statement in the Test Method
			setSignatureAndMethodsComments(testCase,  classUnderTest, coveredCode);

			// we generate a block comment above test method declaration
			String methodWithComments = generateTestCaseDescription(testCase, classUnderTest, coveredCode, linesCovered, testCase.getAttributesTested());
			testCase.setTestCaseDescription(methodWithComments);

			// Now we update the (textual) content of the test class with
			// the new test method (enriched with summaries/comments)
			String newContent = replaceText(classeTest, testCase, methodWithComments);
			classeTest.setTextContent(newContent);
		} //for "c" 2

		System.out.println("Step 4.2: Generating a summary for the Class Under Test");
		// First we generate the comment
		String description=ClassDescriptor.generateClassComment(clazz);
		// Then, we add the generated comments just before the class declaration
		classeTest.setTextContent(classeTest.getTextContent().replace("public class ", description+"\npublic class "));


		// we change the name of the test class
		classeTest.setTextContent(classeTest.getTextContent().replace("public class "+classeTest.getName(), "public class "+classeTest.getName()+"withDescription"));
		// we save the new (renamed) test class
		String pathNewTextClass=sourceFolder+testsFiles.get(0).replace(".java","withDescription.java");
		FileUtils.writeFile(pathNewTextClass,  classeTest.getTextContent());
		System.out.println("GENERATED JUNIT CLASS");
		System.out.println(classeTest.getTextContent());
	}

	/**
	 * This method replace the (textual) content of the first input parameter ({@link ClassBean}). Specifically,
	 * it replace the original method {@link MethodBean} with a new method (enriched with summaries)
	 * @param testClass test class for which we want to replace the content
	 * @param testCase test method to replace
	 * @param methodWithComments new method to add
	 * @return
	 */
	public static String replaceText(ClassBean testClass, MethodBean testCase, String methodWithComments){
		String java = testClass.getTextContent();
		CompilationUnit cu = null;
		try {
			cu = JavaParser.parse(new ByteArrayInputStream(java.getBytes()));
			for (Node node : cu.getChildrenNodes()){
				if (!(node instanceof ClassOrInterfaceDeclaration))
					continue;

				ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
				for (int index = 0; index<clazz.getChildrenNodes().size(); index++){
					Node methodNode = clazz.getChildrenNodes().get(index);
					MethodDeclaration method = (MethodDeclaration) methodNode;
					if (method.getName().equals(testCase.getName())){
						MethodDeclaration newMethod = convertString2MethodDeclaration(methodWithComments);
						method.setBody(newMethod.getBody());
					}
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cu.toString();
	}

	/**
	 * This method convert a String representing a Java method in an object of the class {@link MethodDeclaration}
	 * @param method String to convert
	 * @return object of the class {@link MethodDeclaration}
	 */
	protected static MethodDeclaration convertString2MethodDeclaration(String method){
		MethodDeclaration newMethod = null;
		String clazz = "public class wrapper {"+method+"}";
		try {
			CompilationUnit cu = JavaParser.parse(new ByteArrayInputStream(clazz.getBytes()));
			ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) cu.getChildrenNodes().get(0);
			newMethod = (MethodDeclaration) classDeclaration.getChildrenNodes().get(0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newMethod;
	}

	/**
	 * This method generate the description of a specific test (method) case
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static String generateTestCaseDescription(MethodBean testCase, ClassBean originalClass, ClassBean executedClass,List<String> linesExecutedOfOriginalClass, double attributesTested) throws IOException, InterruptedException
	{
		//initialization
		String description2="*";
		String line="",level="";
		String[]  vectTextTestCase=testCase.getTextContent().replace("\r", "\n").split("\n");
		double coverage =0;
		description2="";
		//PART RELATED TO COVERAGE..

		coverage= Math.round((double) testCase.getLinesExecutedOfOriginalClass().size()/originalClass.getLOC()*100);
		if (coverage<=20)
		{level="low ";}
		else if (coverage>20 & coverage <=50)
		{level="substantial high ";}
		else if (coverage>50)
		{level="a high ";}
		description2=description2+" /** OVERVIEW: \n  *The test case \"" + testCase.getName() +"\" covers around "+coverage+"% ("+level+" percentage) of statements in \""+originalClass.getName()+"\"\n";
		description2=description2+" **/\n";
		//SUMMARY: "DESCRIPTION OF METHODS"\
		//WE READ LINE BY LINE THE CONTENT OF THE TEST CASE "testCase[c]..
		testCase.setCounterAssertions(0); //initialization..
		for(int r=0;r<vectTextTestCase.length;r++){
			line=vectTextTestCase[r];
			//CASE 1 there is a definition of variable (a constructor of the class);
			if(line.contains("=new ")){
				description2=description2+ConstructorDescriptor.generateConstructorComments( line,  executedClass, testCase, originalClass);
			}//if "new"
			//case 2: it evaluate an assertion
			if(line.contains("assert")) {
				description2=description2+AssertionDescriptor.generateAssertionsComments( line,  executedClass,  testCase, originalClass);
			}

			//CASE 2: is a method and or assert..
			//System.out.println("line.."+line);
			if(!line.contains("=new ") & !line.contains("@Test") & !line.contains("}") && !line.contains("assert"))
				if(line.contains("("))
					if(line.lastIndexOf(".")<line.lastIndexOf("("))
					{
						//TODO: SEBASTIANO
						description2=description2+MethodDescriptor.generateMethodComments(line,  executedClass,  testCase, originalClass);
					}//if "is a method..."

			//temp=temp+"\n"+line;
			description2="\n"+description2+" \n"+line+"\n";
		}// for "r"..

		description2=description2.replace("     *   *", "   //");
		description2=description2.replace("\"  // The test case (","\"\n  // The test case (");
		description2=description2.replace("null  ","  ");
		description2=description2.replace("@Test","");
		description2=description2.replace("//    ","\n   ");
		description2=description2.replace("//   ","\n   ");
		description2=description2.replace("\" }","\"\n }");
		description2=description2.replace("///// OVERVIEW","/** OVERVIEW");
		description2=description2.replace("/////","**/");
		description2=description2.replace("null\n","//");
		description2=description2.replace("// }","// \n}");
		//eliminate multiple newlines
		description2=description2.replaceAll("[ ]+\n","\n");
		description2=description2.replaceAll("[\n]+","\n");
		//to fix the problem of "@TEST"
		// (i) we remove all the remaing "@Test"
		description2 = description2.replace("@Test", "");
		// (ii) we add "@Test" each time in the text there is "*/"
		description2 = description2.replace("**/", "**/\n  @Test \n");
		return(description2);
	}


	public static void setSignatureAndMethodsComments(MethodBean testcase, ClassBean classe,ClassBean classeExecuted){
		Vector<MethodBean> coveredMethods = (Vector<MethodBean>) classeExecuted.getMethods();
		for(int methodIndex=0; methodIndex<coveredMethods.size();methodIndex++) {
			MethodBean coveredMethod = coveredMethods.get(methodIndex);
			String methodSignature= coveredMethod.getTextContent().split("\n")[0];
			methodSignature = methodSignature.substring(0,methodSignature.indexOf(")")+1);
			methodSignature = methodSignature.replace(",", ", ");
			coveredMethod.setSignature(methodSignature);
		}

		for(int methodIndex=0; methodIndex<coveredMethods.size();methodIndex++) {
			MethodBean coveredMethod = coveredMethods.get(methodIndex);
			String methodSignature= coveredMethod.getTextContent().split("\n")[0];
			methodSignature = methodSignature.substring(0,methodSignature.indexOf(")")+1);
			methodSignature = methodSignature.replace(",", ", ");

			// if to determine if this is a constructor or a method...
			coveredMethod.setConstructor(false);//Initialization
			if(methodSignature.contains("public "+classe.getName()+"(")){
				coveredMethod.setConstructor(true);
			}

			coveredMethod.generateMethodCommentsAnalyzingClassTextContent(classe.getTextContent());
		}
	}

	/*
	 * return the description of the class and set the imports in the java class specified as parameter
	 */
	public static String generateClassDescription(List<String> textContentExecutedOriginalClass, ClassBean classe){
		String classDescription="",word="";
		StringTokenizer tokens=null;
		boolean startDescription=false , endDescription=true;
		boolean foundImport=false;
		String line="";
		for(int i=0;i<textContentExecutedOriginalClass.size();i++){
			line=textContentExecutedOriginalClass.get(i);
			// we add the imports..
			if(line.contains("import ") & startDescription==false)
			{
				foundImport=true;
			}
			//if we are in the beginning of the description
			if(foundImport==true & line.contains("/**") )
			{
				startDescription=true;
			}

			if( (line.contains("@author") | line.contains("@see")) & foundImport==true)
			{
				startDescription = false;
				endDescription=true;
				foundImport=false;
				//classDescription=classDescription+"\r **/";
			}
			//if we are in the beginning of the description
			if(startDescription==true)
			{
				classDescription=classDescription+"\r"+textContentExecutedOriginalClass.get(i);
			}

		}

		classDescription=classDescription.replace("<p>","* ");

		tokens= new StringTokenizer(classDescription.replace("*", ""));
		if (tokens.hasMoreTokens()){
			tokens.nextToken();
			word = tokens.nextToken();// we take the first word
			//System.out.println("word.. "+word);
			//it verifies if the first words in in third person (ends with "s")
			if(word.substring(word.length()-1,word.length()).contains("s") )
			{
				classDescription=classDescription.replace("* "+word, "* "+word.toLowerCase());
			}
			else//if it is not in third person..
			{
				classDescription=classDescription.replace("* "+word, "* represents a "+word.toLowerCase());
			}
		}

		classDescription=classDescription.replace("/**","/** OVERVIEW: \r * This test case tests the class <code> "+classe.getName()+" </code>, which \r");
		classDescription=classDescription+"*/";
		//use spell checker
		classDescription = SpellCorrector.correctSentences(classDescription);
		return (classDescription);
	}


	public static void printClassContent(ClassBean classe){
		Vector<MethodBean> methods=null;
		MethodBean method=null;
		List<SingleVariableDeclaration> parameters=null;
		SingleVariableDeclaration parameter=null;
		Vector<MethodBean> methodsCalls=null;
		MethodBean methodCall=null;
		ArrayList<InstanceVariableBean> attributes=null;
		Vector<InstanceVariableBean> variables=null;
		InstanceVariableBean attribute =null;
		InstanceVariableBean variable=null;
		//System.out.println(classes.get(c).getTextContent());
		methods = (Vector<MethodBean>) classe.getMethods();
		List<String> classImports=null;
		System.out.println("CLASS NAME: \""+classe.getName()+"\"");
		System.out.println("CLASS IMPORTS:");
		classImports=(ArrayList<String>) classe.getImports();
		if(classImports != null)
		{
			for(int i=0;i<classImports.size();i++){

				System.out.println("PACKAGE NAME: "+classImports.get(i));
			}
		}
		System.out.println("CLASS ATTRIBUTES: ");
		attributes=classe.getInstanceVariables();
		if(attributes != null)
		{
			for(int a=0;a<attributes.size();a++){
				attribute=attributes.get(a);
				System.out.println("ATTRIBUTE NAME: \""+attribute.getName()+"\" type \""+attribute.getType()+"\"");
			}
		}


		System.out.println("CLASS METHODS:");
		for(int m=0;m<methods.size();m++){
			method=methods.get(m);
			System.out.println("METHOD NAME: "+method.getName());
			System.out.println(method.getTextContent());

			parameters=method.getParameters();
			for(int p=0;p<parameters.size();p++){
				parameter=parameters.get(p);
				System.out.println("METHOD Parameter -> "+parameter.getName()+" of type \""+parameter.getType()+"\"");
			}
			System.out.println("METHOD Return type: \""+method.getReturnType()+"\"");
			methodsCalls = (Vector<MethodBean>) method.getMethodCalls();
			for(int p=0;p<methodsCalls.size();p++){
				methodCall=methodsCalls.get(p);
				System.out.println("METHOD call to -> "+methodCall.getName());
			}
			variables=(Vector<InstanceVariableBean>) method.getUsedInstanceVariables();
			for(int v=0;v<variables.size();v++){
				variable=variables.get(v);
				System.out.println("METHOD variable -> "+variable.getName());
			}

		}
		System.out.println("END Print of CLASS: \""+classe.getName()+"\"");

	}

	public static void printMethodContent(MethodBean method){
		List<SingleVariableDeclaration> parameters=null;
		SingleVariableDeclaration parameter=null;
		Vector<MethodBean> methodsCalls=null;
		MethodBean methodCall=null;
		Vector<InstanceVariableBean> variables=null;
		InstanceVariableBean variable=null;
		//System.out.println(classes.get(c).getTextContent());

		System.out.println("METHOD NAME: "+method.getName());
		System.out.println(method.getTextContent());

		parameters=method.getParameters();
		for(int p=0;p<parameters.size();p++){
			parameter=parameters.get(p);
			System.out.println("METHOD Parameter -> "+parameter.getName()+" of type \""+parameter.getType()+"\"");
		}
		System.out.println("METHOD Return type: \""+method.getReturnType()+"\"");
		methodsCalls = (Vector<MethodBean>) method.getMethodCalls();
		for(int p=0;p<methodsCalls.size();p++){
			methodCall=methodsCalls.get(p);
			System.out.println("METHOD call to -> "+methodCall.getName());
		}
		variables=(Vector<InstanceVariableBean>) method.getUsedInstanceVariables();
		for(int v=0;v<variables.size();v++){
			variable=variables.get(v);
			System.out.println("METHOD variable -> "+variable.getName());
		}
	}

	protected static String convert2PackageNotation(String url){
		String path = url.replace(File.separator, ".");
		if (path.endsWith(".java"))
			path = path.substring(0, path.length()-5);
		return path;
	}
}

