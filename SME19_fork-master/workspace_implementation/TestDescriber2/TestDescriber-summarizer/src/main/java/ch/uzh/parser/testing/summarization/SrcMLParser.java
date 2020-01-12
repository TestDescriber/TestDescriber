package ch.uzh.parser.testing.summarization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.WithDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;

import ch.uzh.parser.JavaFileParser;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;

public class SrcMLParser {

	/**
	 * The parser is used to parse the Java code and detect main code elements,
	 * methods, attributes, parameters this enable then the possibility to generate descriptions toward such objects
	 * 
	 * @param testCases All test methods
	 * @param c Index from calling For-construct. (Parse every file separately)
	 * @param classe Production class belonging to test methods
	 * @param sourceFolder Path to production code source folder
	 * @param classesFiles Collection of all production code source files
	 * @param t Another Index, gets initialized with 0 and doesn't change value (magic number?)
	 * @param testsCoverage Lines covered in production code by the corresponding test. (Cobertura output)
	 * @return textContentExecutedOriginalClass The part of the code that get's executed by the test in the original class
	 * @throws Exception 
	 */
	//TODO GGG change type of Exception
	public static List<String> parseSrcML(List<MethodBean> testCases,int c,ClassBean classe, String sourceFolder,List<String>  classesFiles,int t,List<String>  testsCoverage) throws Exception{

		//GGG
//		if(testCases.size() != testsCoverage.size()) {
//			Exception e = new Exception("Different size testCases and testsCoverage: "+testCases.size()+"-"+testsCoverage.size());
//			throw e; 
//		}
		
		
		MethodBean testCase = testCases.get(c); 
		ClassBean classeExecuted = null;
		boolean areWeInAIfStatement=false;
		List<Integer> methodOrConstructorLinesIds=null;
		List<String> methodOrConstructorLines=null;
		List<Integer> linesExecutedOfOriginalClassId=null;
		String textContentOriginalClass=null,textContentExecutedVersionOfOriginalClass=null,
				testCoverage=null,testCoverageBooleanValues="",
				line="", lineInthEIntersection=null,attributeContent="";
		String[] vectTextContentOriginalClass=null;
		List<String> linesExecutedOfOriginalClass=null,linesExecutedOfOriginalClassIdWithBooleanValue=null;
		List<Integer> intersectionOfIds =null;
		ArrayList<Integer> startIfIds = null;
		ArrayList<Integer> endIfIds = null;
		List<String> textContentExecutedOriginalClass= new ArrayList<String>(); //ant0: List containing the return output of the method!
		double attributesTested=0;
		int indexOfIf=-1,indexOfEndIf=-1, lenghtVect=-1, pos=-1, posEnd=-1, incrementPos=-1 ;
		//WE PRINT THE TEST CASE..
		//printMethodContent(testCase);
		textContentOriginalClass= classe.getTextContent();
		//textContentSrcML=  ;
		//new analyzer instructions...
		String fullFilepath = sourceFolder+classesFiles.get(t);
		Node body2 = JavaParser.parse(new File(fullFilepath));
		Node temp=null;
		SrcASTParser astParser = new SrcASTParser(fullFilepath); // util method to find src code parts
		List<ConstructorDeclaration> constructorDeclarations = getConstructorDeclarations(body2);
		List<FieldDeclaration> attributes = detectAttributes( body2);
		// now "constructorDeclarations" is populated by the constructors of the class..
		List<MethodDeclaration> methods=detectMethods(body2);
		System.out.println("Number of METHODS: "+methods.size());	  
		List<IfStmt> ifStmts=detectIfStmts(methods,constructorDeclarations); 
		List<Integer> listIdsParsedClassInOriginalClass = computeCorrespondingIds( body2, sourceFolder, classesFiles.get(t));
		List<String> listStringsParsedClassInOriginalClass = computeCorrespondingStringIds( body2, sourceFolder, classesFiles.get(t));
		String ifToFind="";
		List<Integer> vectorWithStartAndEndOfanIfStatement=null;
		//System.out.println(commandSrcML);
		//System.out.println("########### outputCommandLine: START");
		//System.out.println(outputCommandLine);
		//System.out.println("########### outputCommandLine: DONE");

		//System.out.println(textContent);
		vectTextContentOriginalClass = textContentOriginalClass.split("\r");
		//vectTextContentOriginalClass = textContentOriginalClass.replace("\r","\n").split("\n"); 
		if (vectTextContentOriginalClass.length==1){
			vectTextContentOriginalClass = textContentOriginalClass.split("\n");
		}
		
		
		//System.out.println("\""+vectTextContentOriginalClass[0]+"\"->ORIGINAL");
		//System.out.println("\""+vectTextContentOriginalClassSrcML[0]+"\"->SRCML");

		//we collect information about the lines 
		//executed in the original class
		linesExecutedOfOriginalClassId=new ArrayList<Integer>();
		linesExecutedOfOriginalClass=new ArrayList<String>();
		linesExecutedOfOriginalClassIdWithBooleanValue=new ArrayList<String>();
		int counterCurrentLineInOriginalClass = 0 ;
		
		//ant0: Start Test coverage part 
		// Compare line numbers of given class with line numbers of 'testCoverage'. Keep the lines that are in 'testCoverage'.
		testCoverageBooleanValues= ","+testsCoverage.get(c)+","; // always contains [line number]-true | [line number]-false | [line number]
		testCoverage = ","+testsCoverage.get(c)+",";
		testCoverage=testCoverage.replace("-true", "").replace("-false", ""); // only contains a line number
		
		for(int o=0;o<vectTextContentOriginalClass.length;o++){
			counterCurrentLineInOriginalClass=o+1;

			if(testCoverage.contains(","+counterCurrentLineInOriginalClass+","))
			{ 
				linesExecutedOfOriginalClassId.add(counterCurrentLineInOriginalClass);
				linesExecutedOfOriginalClass.add(vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1]);
				//System.out.println("done -> "+vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1]);
				if(testCoverageBooleanValues.contains(","+counterCurrentLineInOriginalClass+"-true"))
				{
					linesExecutedOfOriginalClassIdWithBooleanValue.add(counterCurrentLineInOriginalClass+"-true");
				}

				//we try to update the number of attribute tested by the test case
				//if(vectTextContentOriginalClassSrcML[counterCurrentLineInOriginalClass-1].contains("<decl_stmt><decl><type><specifier>"))
				//new IF
				attributeContent=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
				attributeContent=attributeContent.replaceAll("( )+", " ");
				//System.out.println("line of an attribute: \""+attributeContent+"\"");
				if(containsTheAttribute( attributes, attributeContent))// if the current line is related to an attribute declaration..
				{
					attributesTested++;
					//System.out.println("line of an attribute: \""+attributeContent+"\"");
					//System.out.println("done -> "+vectTextContentOriginalClassSrcML[counterCurrentLineInOriginalClass-1]);
				}

				if(testCoverageBooleanValues.contains(","+counterCurrentLineInOriginalClass+"-false"))
				{
					linesExecutedOfOriginalClassIdWithBooleanValue.add(counterCurrentLineInOriginalClass+"-false");
				}
			}
		}
		
		attributesTested = howManyAttributesCovered(testsCoverage, astParser.createCompilationUnit(false).getUnit());
		//ant0: End Test Coverage part
		
		textContentExecutedOriginalClass = new ArrayList<String>();
		//now we put in the class the line until the class "parentesis"
		counterCurrentLineInOriginalClass=1;
		line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];//inizialization
		startIfIds=new ArrayList<Integer>();
		endIfIds=new ArrayList<Integer>();
		
		//Add the class declaration to the return output.
		//The line gets stripped from any whitespace and compared to different class declarations
		while(!line.replaceAll("( )+", " ").contains("public class ") && !line.replaceAll("( )+", " ").contains("public final class ") 
				&& !line.replaceAll("( )+", " ").contains("class") && !line.replaceAll("( )+", " ").contains("interface")){

			// skip javadoc comments
			// TODO refactor into method
			if(line.contains("/*")) {
				while(!line.contains("*/")) {
					line.replaceAll("/\\*|\\*|\\*/", "");
					counterCurrentLineInOriginalClass++;
					line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
				}
				line.replaceAll("\\*/", "");
			}
			
			textContentExecutedOriginalClass.add(line);
			//System.out.println("current line id -> "+counterCurrentLineInOriginalClass);
			//System.out.println("current line    -> "+line);
			counterCurrentLineInOriginalClass++;
			line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
			if(counterCurrentLineInOriginalClass==2){
				//System.out.println("########### srcml: START");
				//System.out.println(line2);
				//System.out.println("########### srcml: DONE");
			}
		}
		//we add the line of the constructor
		if(!line.contains("{")){
			line=line+"\r{";
		}
		textContentExecutedOriginalClass.add(line);
		//System.out.println("current line id -> "+counterCurrentLineInOriginalClass);
		//System.out.println("current line    -> "+line);

		//ant0: Look for an If-Statement in production code
		counterCurrentLineInOriginalClass++;
		for(int s=0;s<vectTextContentOriginalClass.length;s++)
		{
			line = vectTextContentOriginalClass[s];
			//we try to understand when an if statement start and finish
			if(!line.contains("//"))
				if(line.contains("if"))
					if(!line.contains("else if")) 
					{
						ifToFind=line;
						//System.out.println("vectTextContentOriginalClass[s]; : \""+ifToFind+"\"");
//						vectorWithStartAndEndOfanIfStatement=findStartAndEndOfanIfStatement(body2, ifStmts,  ifToFind,
//								listIdsParsedClassInOriginalClass,listStringsParsedClassInOriginalClass,vectTextContentOriginalClass);
						vectorWithStartAndEndOfanIfStatement = findStartAndEndOfanIfStatement(ifToFind, astParser.getUnit());
						//System.out.println("vectTextContentOriginalClass.size() : "+vectorWithStartAndEndOfanIfStatement.size());
						if(vectorWithStartAndEndOfanIfStatement.size()>0) {
//							if(vectorWithStartAndEndOfanIfStatement.get(0)!= -1 & vectorWithStartAndEndOfanIfStatement.get(1)!= -1){
								//	 	System.out.println("indexOfIf -> "+vectorWithStartAndEndOfanIfStatement.get(0)+"; indexOfEndIf -> "+vectorWithStartAndEndOfanIfStatement.get(1));
							startIfIds.add(vectorWithStartAndEndOfanIfStatement.get(0));
							endIfIds.add(vectorWithStartAndEndOfanIfStatement.get(1));
						}
					}
		}//for s

		//ant0: Find the lines of the production code that get executed by the single test case
		while(counterCurrentLineInOriginalClass<=vectTextContentOriginalClass.length)
		{
			line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
			//System.out.println("current line    -> "+line);
			// skip javadoc comments at beginning of method
			// TODO refactor into method
			if(line.contains("/*")) {
				while(!line.contains("*/")) {
					line.replaceAll("/\\*|\\*|\\*/", "");
					counterCurrentLineInOriginalClass++;
					line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
				}
				counterCurrentLineInOriginalClass++;
				line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];
			}
			
			if(	containsTheAttribute( attributes, line) & !linesExecutedOfOriginalClassId.contains(counterCurrentLineInOriginalClass))
			{
				textContentExecutedOriginalClass.add(line);
				//System.out.println("current line    -> "+line);
			}
			//UNTIL here replaced new (without SRCML)...
			//if(line2.contains("<constructor>") | line2.contains("<function>"))
			if(containsAConstructorOrAMethod( methods, constructorDeclarations,  line) && !line.replaceAll("\\s+", "").equals("") && !line.contains("@"))
			{
				methodOrConstructorLinesIds=new ArrayList<Integer>();
				methodOrConstructorLines=new ArrayList<String>();

				//System.out.println("(counterCurrentLineInOriginalClass) current line id -> "+counterCurrentLineInOriginalClass);
				//System.out.println("(counterCurrentLineInOriginalClass) current line    -> \""+line+"\"");
				// in "listStringsParsedClassInOriginalClass" we have all the string parsed 
				// (they are not the same of the original class, e.g., comments are some time discarded)..
				// we have to find the set of lines associated to the method
				temp = returnTheCorrespondingConstructorOrAMethod(methods, constructorDeclarations,  line);
				//System.out.println("(current method/constructor) -> \""+temp.toString()+"\"");
				lenghtVect=temp.toString().split("\n").length - 1;// length of the vector "split" using a separator "\n"
				pos = listIdsParsedClassInOriginalClass.indexOf(counterCurrentLineInOriginalClass-1);
				if(pos== -1){
					// if list doesn't contain (counterCurrentLineInOriginalClass - 1)-th Element...
					pos = listIdsParsedClassInOriginalClass.indexOf(counterCurrentLineInOriginalClass);// ...take the element at current position
				}
				//System.out.println("(listIdsParsedClassInOriginalClass) -> \""+listIdsParsedClassInOriginalClass+"\"");
				//System.out.println("(listStringsParsedClassInOriginalClass) -> \""+listStringsParsedClassInOriginalClass+"\"");
				//System.out.println("(current method/constructor) (pos="+pos+") ");
				//System.out.println("(current method/constructor) ("+pos+") first line-> \""+listStringsParsedClassInOriginalClass.get(pos)+"\"");
			//	posEnd=listIdsParsedClassInOriginalClass.get(pos+lenghtVect)-1;
				posEnd=listIdsParsedClassInOriginalClass.get(pos+lenghtVect)+1;
				// System.out.println("(current method/constructor new) END current line id -> "+(posEnd));
				//System.out.println("(current method/constructor new) END current line    -> \""+line+"\"");
				incrementPos= listIdsParsedClassInOriginalClass.get(pos);
				while(incrementPos!=posEnd){
					//System.out.println("(current method/constructor new) ADDED current line    ("+(incrementPos+1)+")-> \""+vectTextContentOriginalClass[incrementPos]+"\"");
					methodOrConstructorLines.add(vectTextContentOriginalClass[incrementPos-1]);
					methodOrConstructorLinesIds.add(incrementPos); //ant0: '+1' because the empty line before a method or constructor is included in 'incrementPos' but effective start of method is on next line.
					incrementPos++;
					counterCurrentLineInOriginalClass++;
					//System.out.println("(current method/constructor old) ADDED current line    ("+(counterCurrentLineInOriginalClass-1)+")-> \""+vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1]+"\"");
				}
				//System.out.println("current constructor/function   ID: "+methodOrConstructorLinesId.get(0));
				//System.out.println("current constructor/function     : "+methodOrConstructorLines.get(0));
				//System.out.println("current constructor/function size: "+methodOrConstructorLinesId.size());
				//System.out.println("(counterCurrentLineInOriginalClass) END current line id -> "+counterCurrentLineInOriginalClass);
				//System.out.println("(counterCurrentLineInOriginalClass) END current line    -> \""+line+"\"");
				intersectionOfIds = intersect(methodOrConstructorLinesIds,linesExecutedOfOriginalClassId );
				//System.out.println("current constructor/function size: "+intersectionOfIds.size());
				if(intersectionOfIds.size()>0)//the method or constructor must to be considered..
				{
					String[] tokenizedIf = null;
					
					//we verify whether the list contains the signature of the method/constructor
					// ant0: if the the right line number is found we jump to else-branch
					if(!linesExecutedOfOriginalClassId.contains(methodOrConstructorLinesIds.get(0)))
					{
						textContentExecutedOriginalClass.add(methodOrConstructorLines.get(0));
						//System.out.println("current line id -> "+methodOrConstructorLinesId.get(0));
						//System.out.println("current line    -> "+methodOrConstructorLines.get(0));

						if(!methodOrConstructorLines.get(0).contains("{"))
						{
							textContentExecutedOriginalClass.add("{");

							//System.out.println("current line    -> "+"    {");
						}
						for(int p=0;p<intersectionOfIds.size();p++){

							lineInthEIntersection=methodOrConstructorLines.get(methodOrConstructorLinesIds.indexOf(intersectionOfIds.get(p)));
							textContentExecutedOriginalClass.add(lineInthEIntersection);
							//System.out.println("current line id -> "+intersectionOfIds.get(p));
							//System.out.println("current line    -> "+lineInthEIntersection);
							if(lineInthEIntersection.contains("if(") | lineInthEIntersection.contains("if ("))
							{
								
								//FIXME: cascaded ifs not possible!
								tokenizedIf = tokenizeIfStatement(methodOrConstructorLines, lineInthEIntersection);
								
								//we populate the part of the class executed
								if(linesExecutedOfOriginalClassIdWithBooleanValue.contains(""+intersectionOfIds.get(p)+"-false"))
								{
									if (lineInthEIntersection.contains("{"))
										textContentExecutedOriginalClass.add("         }");
									else
										textContentExecutedOriginalClass.add("         {}");
									//System.out.println("current line    -> "+"         {}");	 
									//we populate the part of the method execute containing the if statements
								} else {
									areWeInAIfStatement=true;//useful to understand when write the braces of the if
									indexOfIf=(int) intersectionOfIds.get(p);
									if (startIfIds.indexOf(indexOfIf)>-1) // FIXME: sometimes startIfIds.indexOf(indexOfIf) can be ==-1
										indexOfEndIf=endIfIds.get(startIfIds.indexOf(indexOfIf));//here we have the id of the end of the if..
									//System.out.println("indexOfIf -> "+indexOfIf+"; indexOfEndIf -> "+indexOfEndIf);
									//System.out.println("startIfIds.size() -> "+startIfIds.size()+"; endIfIds.size() -> "+endIfIds.size());

									if (!lineInthEIntersection.contains("{")) {
										textContentExecutedOriginalClass.add("         {");
									}

									//System.out.println("current line    -> "+"    {");
									//we populate the part of the method execute containing the if statements
								}
							}
							//System.out.println(textContentExecutedOriginalClass);

							//we have to put the"end" brace of the if in the right place
							if(areWeInAIfStatement==true & intersectionOfIds.get(p)>= (indexOfEndIf-1)){
								textContentExecutedOriginalClass.add("         }");

								//System.out.println("current line    -> "+"         }");
								areWeInAIfStatement=false;
							}
							
							if(isLineInTheIntersectionLastLineOfIf(lineInthEIntersection, tokenizedIf)) {
								textContentExecutedOriginalClass.add("}");
							}
						}

						if(!lineInthEIntersection.contains("}")){
							textContentExecutedOriginalClass.add("}");

							//System.out.println("current line    -> "+"}");
						}
					}
					else{
						// ant0: 'linesExecutedOfOriginalClassId' contains the method-signature (or constructor definition) 
						
						for(int p=0;p<intersectionOfIds.size();p++){

							lineInthEIntersection=methodOrConstructorLines.get(methodOrConstructorLinesIds.indexOf(intersectionOfIds.get(p)));
							textContentExecutedOriginalClass.add(lineInthEIntersection);
							//System.out.println("current line id -> "+intersectionOfIds.get(p));
							//System.out.println("current line    -> "+lineInthEIntersection);

							if(p==0 & !lineInthEIntersection.contains("{"))
							{
								textContentExecutedOriginalClass.add("{");
								//System.out.println("current line id -> "+methodOrConstructorLinesId.get(0));
								//System.out.println("current line    -> "+"    {");
							}

							if(lineInthEIntersection.contains("if(") | lineInthEIntersection.contains("if ("))
							{
								
								//FIXME: cascaded ifs not possible!
								tokenizedIf = tokenizeIfStatement(methodOrConstructorLines, lineInthEIntersection);
								
								if(linesExecutedOfOriginalClassIdWithBooleanValue.contains(""+intersectionOfIds.get(p)+"-false"))
								{
									if (lineInthEIntersection.contains("{")) {
										textContentExecutedOriginalClass.add("         }");
										if(tokenizedIf.length > 1) {
											textContentExecutedOriginalClass.add("         {");
										}
									} else {
										textContentExecutedOriginalClass.add("         {}");
										
									}
									//we populate the part of the method execute containing the if statements

									//System.out.println("current line id -> "+counterCurrentLineInOriginalClass);
									//System.out.println("current line    -> "+"         {}");	 
								}

								if(linesExecutedOfOriginalClassIdWithBooleanValue.contains(""+intersectionOfIds.get(p)+"-true"))
								{
									areWeInAIfStatement=true;//useful to understand when write the braces of the if
									indexOfIf=intersectionOfIds.get(p);
									if (startIfIds.indexOf(indexOfIf)!=-1)
										indexOfEndIf=endIfIds.get(startIfIds.indexOf(indexOfIf));//here we have the id of the end of the if..
									//System.out.println("indexOfIf -> "+indexOfIf+"; indexOfEndIf -> "+indexOfEndIf);
									//System.out.println("startIfIds.size() -> "+startIfIds.size()+"; endIfIds.size() -> "+endIfIds.size());

									textContentExecutedOriginalClass.add("         {");

									//System.out.println("current line    -> "+"    {");
									//we populate the part of the method execute containing the if statements
								}

							}

							//we have to put the"end" brace of the if in the right place
							if(areWeInAIfStatement==true & intersectionOfIds.get(p)>= (indexOfEndIf-1)){
								textContentExecutedOriginalClass.add("         }");

								//System.out.println("current line    -> "+"         }");
								areWeInAIfStatement=false;
							}
							
							if(isLineInTheIntersectionLastLineOfIf(lineInthEIntersection, tokenizedIf)) {
								textContentExecutedOriginalClass.add("}");
							}
						}
						
						if(!lineInthEIntersection.contains("}")){
							textContentExecutedOriginalClass.add("}");
							//System.out.println("current line id -> "+"}");
							//System.out.println("current line    -> "+"}");
						}
						
					}//else

				}
			}

			counterCurrentLineInOriginalClass++;
		}//While class
		//brace of the class
		textContentExecutedOriginalClass.add("}");
		//System.out.println("current line id -> "+"}");
		//System.out.println("current line    -> "+"}");

		//NOW WE KNOW THE LINES EXECUTED (COVERED) OF THE ORIGINAL CLASS by the Test case 
		//initialization..
		textContentExecutedVersionOfOriginalClass="";
		System.out.println("LINES EXECUTED OF THE ORIGINAL CLASS IN  Test case "+testCase.getName()+"(Amount: "+textContentExecutedOriginalClass.size()+") :");
		for(int p=0;p<textContentExecutedOriginalClass.size();p++)
		{
			textContentExecutedVersionOfOriginalClass=textContentExecutedVersionOfOriginalClass+textContentExecutedOriginalClass.get(p)+"\r";
			//a) NOW WE PRINT the LINES EXECUTED (COVERED) OF ORIGINAL CLASS by the Test case   
			//System.out.println(textContentExecutedOriginalClass.get(p)+"\r\n");
		}

		//b) System.out.println(textContentExecutedVersionOfOriginalClass);
		classeExecuted=JavaFileParser.parseJavaClass(textContentExecutedVersionOfOriginalClass);
		classeExecuted.setImports(classe.extractImports(textContentExecutedOriginalClass)); //ant0: extract Imports of textContentExecutedOriginalClass and set them to 'classe' (implicit). Afterwards set them to 'classeExecuted' (explicit). 
		testCase.setOriginalClassOfTestCase(classe); // 'classe' has imports of 'textContentExecutedOriginalClass'
		testCase.setClassExecutedByTheTestCase(classeExecuted);
		testCase.setLinesExecutedOfOriginalClass(linesExecutedOfOriginalClassIdWithBooleanValue);
		testCase.setAttributesTested(attributesTested);

		return(textContentExecutedOriginalClass);
	}

	private static boolean isLineInTheIntersectionLastLineOfIf(String lineInthEIntersection, String[] tokenizedIf) {
		boolean isLastLine = false;
		
		if((tokenizedIf != null) && (tokenizedIf[tokenizedIf.length-1].endsWith(lineInthEIntersection.trim()))) {
			isLastLine = true;
		}

		return isLastLine;
	}

	/**
	 * The idea is to get the whole method body into one line, remove all the parts that do not belong to the if-statement and return the if-statement in separated parts.
	 * The returned if statement can then be used for orientation in if statement.
	 * @param methodOrConstructorLines the method from the source code
	 * @param lineInthEIntersection the current line holding an if-statement
	 * @return the if statement in parts as array
	 */
	private static String[] tokenizeIfStatement(List<String> methodOrConstructorLines, String lineInthEIntersection) {
		String currentMethod = methodOrConstructorLines.toString().replace("}]", "");
		String resultingMethod = "";
		for(String line1 : currentMethod.split(",")) {
			if(line1.startsWith("[")) {
				line1 = line1.replaceFirst("\\[",	"");
			}
			
			resultingMethod += line1.trim();
		}
		
		resultingMethod = resultingMethod.substring(resultingMethod.indexOf(lineInthEIntersection.trim()), resultingMethod.lastIndexOf("}"));
		String[] tokenizedIf = resultingMethod.split("}");
		
		return tokenizedIf;
	}
	
	/**
	 * Uses the Eclipse Java AST to find class attributes that are covered by cobertura.
	 * 
	 * @param testsCoverage parsed cobertura output (line numbers of production code)
	 * @param unit
	 * @return amount of attributes found
	 */
	private static Integer howManyAttributesCovered(List<String> testsCoverage, CompilationUnit unit) {
		Integer[] t = {0}; // holds return value
		
		unit.accept(new ASTVisitor() {
			
			public boolean visit(org.eclipse.jdt.core.dom.FieldDeclaration node) {
				
				for(String line : testsCoverage) {
					//GGG because we also have null values in coverage
					if (line != null) {
						String[] linesSeparated = line.split(",");
						for(String linenumber : linesSeparated) {
							if(unit.getLineNumber(node.getStartPosition()) == Integer.valueOf(linenumber.replace("-true","").replace("-false","").trim())) {
								t[0]++;
							}
						}
					} else {
						System.out.println("SrcMLParser howManyAttributesCovered: line is null");
					}
				}
				
				return true; // do continue
			}
		});
		
		return t[0];
	}
	
	private boolean isAttribute(String line, CompilationUnit unit) {
		Boolean[] isAttribute = {false};
		
		unit.accept(new ASTVisitor() {
			
			public boolean visit(org.eclipse.jdt.core.dom.FieldDeclaration node) {
			if(node.toString().contains(line)) {
				isAttribute[0] = true;
				return false; // do not continue
			} else
				return true; // do continue
			}
		});
		
		return isAttribute[0];
	}

	public static  Node returnTheCorrespondingConstructorOrAMethod(List<MethodDeclaration> methods, 
			List<ConstructorDeclaration> constructorDeclarations, String currentInstruction){
		
		WithDeclaration retVal = findTheCorrespondingConstructorOrMethod(new ArrayList<WithDeclaration>(methods), currentInstruction);

		if(retVal == null) {
			retVal = findTheCorrespondingConstructorOrMethod(new ArrayList<WithDeclaration>(constructorDeclarations), currentInstruction);
		}
		
		return (Node) retVal;
	}
	
	private static WithDeclaration findTheCorrespondingConstructorOrMethod(List<WithDeclaration> nodes, String currentInstruction) {
		String currentMethodOrConstructor="";
		currentInstruction=currentInstruction.replaceAll("\\s+", "");// we remove the occurrence of more than one single space
		
		for(int i=0;i< nodes.size();i++)
		{
			currentMethodOrConstructor=nodes.get(i).toString();
			currentMethodOrConstructor=currentMethodOrConstructor.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
			if(currentMethodOrConstructor.startsWith(currentInstruction)){
				return(nodes.get(i));
			}
		}
		
		return null;
	}
	
	
	public static  boolean containsAConstructorOrAMethod(List<MethodDeclaration> methods, 
			List<ConstructorDeclaration> constructorDeclarations, String currentInstruction){
		//ant0: Code duplicated! Only differs in small part.
		boolean res=false;
		String currentMethodOrConstructor="";
		currentInstruction=currentInstruction.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
		for(int i=0;i< methods.size();i++)
		{
			currentMethodOrConstructor=methods.get(i).toString();
			currentMethodOrConstructor=currentMethodOrConstructor.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
			if(currentMethodOrConstructor.startsWith(currentInstruction)){
				res=true;
				break;
			}
		}

		for(int i=0;i< constructorDeclarations.size();i++)
		{
			currentMethodOrConstructor=constructorDeclarations.get(i).toString();
			currentMethodOrConstructor=currentMethodOrConstructor.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
			if(currentMethodOrConstructor.startsWith(currentInstruction)){
				res=true;
				break;
			}
		}
		return res;
	}

	/**
	 * Look in children and children of children for the constructor declarations.
	 * 
	 * @param body2 Production code source parsed into a Compilation Unit
	 * @return constructor declarations
	 */
	public static List<ConstructorDeclaration> getConstructorDeclarations(Node body2){
		List<ConstructorDeclaration> constructorDeclarations=new ArrayList<ConstructorDeclaration>();
		for (Node node : body2.getChildrenNodes()){
			//System.out.println("node: "+node);
			//System.out.println("node class type: \""+node.getClass().getSimpleName()+"\"");
			// we add the ConstructorDeclaration in the list of of constructorDeclarations..
			if(node.getClass().getSimpleName().equals("ConstructorDeclaration")){
				constructorDeclarations.add((ConstructorDeclaration) node);
			}
			else{
				//System.out.println("#node: "+node);
				//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
			}
			for (Node node2 : node.getChildrenNodes()){
				//System.out.println("node: "+node2);
				//System.out.println("node class type: \""+node2.getClass().getSimpleName()+"\"");
				// we add the ConstructorDeclaration in the list of of constructorDeclarations..
				if(node2.getClass().getSimpleName().equals("ConstructorDeclaration")){
					constructorDeclarations.add((ConstructorDeclaration) node2);
				}
				else{
					//System.out.println("# node: "+node);
					//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
				}
			}
		}
		return constructorDeclarations;
	}

	public static  List<IfStmt> detectIfStmts(List<MethodDeclaration> methods,List<ConstructorDeclaration> constructorDeclarations){
		//ant0: Code duplicated! Only differs in small part.
		List<IfStmt> ifStmts=new ArrayList<IfStmt>();
		Node body2=null;

		//if contained in the constructors

		for(int b=0;b<constructorDeclarations.size();b++)
		{		
			body2=constructorDeclarations.get(b);
			//System.out.println(classContent);
			for (Node node : body2.getChildrenNodes()){
				//System.out.println("node: "+node);
				//System.out.println("node class type: \""+node.getClass().getSimpleName()+"\"");
				// we add the FieldDeclaration in the list of attributes..
				if(node.getClass().getSimpleName().equals("IfStmt")){
					ifStmts.add((IfStmt) node);
				}
				else{
					//System.out.println("#node: "+node);
					//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
				}
				for (Node node2 : node.getChildrenNodes()){
					//System.out.println("node: "+node2);
					//System.out.println("node class type: \""+node2.getClass().getSimpleName()+"\"");
					// we add the FieldDeclaration in the list of attributes..
					if(node2.getClass().getSimpleName().equals("IfStmt")){
						ifStmts.add((IfStmt) node2);
					}
					else{
						//System.out.println("#node: "+node);
						//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
					}
				}
			}
		}//for b
		// if in the methods
		for(int b=0;b<methods.size();b++)
		{		
			body2=methods.get(b);
			//System.out.println(classContent);
			for (Node node : body2.getChildrenNodes()){
				//System.out.println("node: "+node);
				//System.out.println("node class type: \""+node.getClass().getSimpleName()+"\"");
				// we add the FieldDeclaration in the list of attributes..
				if(node.getClass().getSimpleName().equals("IfStmt")){
					ifStmts.add((IfStmt) node);
				}
				else{
					//System.out.println("#node: "+node);
					//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
				}
				for (Node node2 : node.getChildrenNodes()){
					//System.out.println("node: "+node2);
					//System.out.println("node class type: \""+node2.getClass().getSimpleName()+"\"");
					// we add the FieldDeclaration in the list of attributes..
					if(node2.getClass().getSimpleName().equals("IfStmt")){
						ifStmts.add((IfStmt) node2);
					}
					else{
						//System.out.println("#node: "+node);
						//System.out.println("#node class type: \""+node.getClass().getSimpleName()+"\"");
					}
				}
			}
		}//for b
		return ifStmts;
	}

	public static  List<MethodDeclaration> detectMethods(Node body2){
		List<MethodDeclaration> methods=new ArrayList<MethodDeclaration>();
		String classContent=body2.toString();
		int incrementLinesCounter=0;
		while(classContent.contains("\n")){
			incrementLinesCounter++;
			classContent=classContent.replaceFirst("\n", "/*"+incrementLinesCounter+"#/");
		}
		classContent=classContent.replace("#/", "*/\n");
		//System.out.println(classContent);
		for (Node node : body2.getChildrenNodes()){
			//System.out.println("node: "+node);
			//System.out.println("node class type: \""+node.getClass().getSimpleName()+"\"");
			// we add the FieldDeclaration in the list of attributes..
			if(node.getClass().getSimpleName().equals("MethodDeclaration")){
				methods.add((MethodDeclaration) node);
			}
			for (Node node2 : node.getChildrenNodes()){
				//System.out.println("node: "+node2);
				//System.out.println("node class type: \""+node2.getClass().getSimpleName()+"\"");
				// we add the FieldDeclaration in the list of attributes..
				if(node2.getClass().getSimpleName().equals("MethodDeclaration")){
					methods.add((MethodDeclaration) node2);
				}
			}
		}
		return methods;
	}
	
	/**
	 * Method complements 'computeCorrespondingIds'. It additionally stores the lines themselves in the array.
	 * 
	 * @param body2
	 * @param sourceFolder
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<String> computeCorrespondingStringIds(Node body2,String sourceFolder, String file) throws IOException{
		//ant0: Code duplicated! Only differs in small part.
		String originalClassContent=readFile(sourceFolder+file);
		String classContent=body2.toString();
		String[] vectOriginalClassContent = originalClassContent.split("\n");
		if(vectOriginalClassContent.length==1){
			vectOriginalClassContent = originalClassContent.split("\r");
		}
		String[] vectClassContent = classContent.split("\n");
		if(vectClassContent.length==1){
			vectClassContent = originalClassContent.split("\r");
		}
		//System.out.println("originalClassContent:\""+vectOriginalClassContent[0]+"\"");
		//System.out.println("classContent:\""+vectClassContent[0]+"\"");
		int i=0,incrementPos=-1;
		List<Integer> listIdsParsedClassInOriginalClass=new ArrayList<Integer>();
		List<String> listStringsParsedClassInOriginalClass=new ArrayList<String>();
		while(i<vectOriginalClassContent.length & incrementPos<vectClassContent.length){
			vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " ");
			if( !vectOriginalClassContent[i].equals(""))
			{
				incrementPos++;
				if(incrementPos<vectClassContent.length)
				{
					while(vectClassContent[incrementPos].equals("")){
						{incrementPos++;}
					}

					vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " ");

					if(incrementPos<vectClassContent.length)
					{
						vectClassContent[incrementPos]=vectClassContent[incrementPos].replaceAll("( )+", " ").replaceAll("\\s+", " ");
						if(!(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", "")))) 
						{
							//System.out.println("\""+vectClassContent[incrementPos]+"\" 1!= \""+vectOriginalClassContent[i]+"\"");
							i++;
							//System.out.println("\""+vectClassContent[incrementPos]+"\" 2!= \""+vectOriginalClassContent[i]+"\"");
						}
						if(!(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", "")))) 
						{
							if(vectOriginalClassContent[i].contains("{"))
							{
								//System.out.println("\""+vectClassContent[incrementPos]+"\" 3!= \""+vectOriginalClassContent[i]+"\"");
								i++;
								//System.out.println("\""+vectClassContent[incrementPos]+"\" 4!= \""+vectOriginalClassContent[i]+"\"");
							}
						}

						vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " ");
						if(!vectOriginalClassContent[i].equals(""))
							if(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", ""))) 
							{
								//System.out.println("vectClassContent:\""+vectClassContent[incrementPos]+"\"");
								//System.out.println("\""+vectClassContent[incrementPos]+"\" == \""+vectOriginalClassContent[i]+"\"");
								listIdsParsedClassInOriginalClass.add(i);
								listStringsParsedClassInOriginalClass.add(vectClassContent[incrementPos]);
								//System.out.println("i:"+i);
							}
							else
							{
								//System.out.println("\""+vectClassContent[incrementPos]+"\" != \""+vectOriginalClassContent[i]+"\"");
							}
					}
				}
			}
			i++;
			// we only want to take the lines of source code from 'vectOriginalClassContent'
			incrementPos = i;
		}//end while

		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");
		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");

		return(listStringsParsedClassInOriginalClass);
	}

//	/**
//	 * Method finds lines that are the same in the source file as in the Node given as an argument.
//	 * Thus collecting all the line numbers that have actual code in them in each method.
//	 * Because the parsed source code out of the node argument is optimized we create an index of the production code
//	 * by comparing the original file with the parsed version and keeping the line numbers of the corresponding lines.
//	 * 
//	 * @param body2
//	 * @param sourceFolder
//	 * @param file
//	 * @return all the line numbers containing source code
//	 * @throws IOException
//	 */
//	public static List<Integer> computeCorrespondingIds(Node body2,String sourceFolder, String file) throws IOException{
//		String originalClassContent=readFile(sourceFolder+file);
//		String classContent=body2.toString();
//		String[] vectOriginalClassContent = originalClassContent.split("\n");
//		if(vectOriginalClassContent.length==1){
//			vectOriginalClassContent = originalClassContent.split("\r");
//		}
//		String[] vectClassContent = classContent.split("\n");
//		if(vectClassContent.length==1){
//			vectClassContent = originalClassContent.split("\r");
//		}
//		System.out.println("originalClassContent:\""+vectOriginalClassContent[0]+"\"");
//		System.out.println("classContent:\""+vectClassContent[0]+"\"");
//		int i=0,incrementPos=-1;
//		List<Integer> listIdsParsedClassInOriginalClass=new ArrayList<Integer>();
//		while(i<vectOriginalClassContent.length & incrementPos<vectClassContent.length){
//			vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " "); //ant0: this line acts as a filter
//			if( !vectOriginalClassContent[i].equals(""))
//			{
//				incrementPos++;
//				if(incrementPos<vectClassContent.length)
//				{
//					//ant0: We increment 'incrementPos' because we hope to find a non-empty line.
//					while(vectClassContent[incrementPos].equals("")){
//						{incrementPos++;}
//					}
//
//					vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " "); //ant0: this line is useless, as it already happened in line: 678
//
//					if(incrementPos<vectClassContent.length)
//					{
//						vectClassContent[incrementPos]=vectClassContent[incrementPos].replaceAll("( )+", " ").replaceAll("\\s+", " ");
//						if(!(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", "")))) 
//						{
//							//System.out.println("\""+vectClassContent[incrementPos]+"\" 1!= \""+vectOriginalClassContent[i]+"\"");
//							i++;
//							//System.out.println("\""+vectClassContent[incrementPos]+"\" 2!= \""+vectOriginalClassContent[i]+"\"");
//						}
//						if(!(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", "")))) 
//						{
//							if(vectOriginalClassContent[i].contains("{"))
//							{
//								// System.out.println("\""+vectClassContent[incrementPos]+"\" 3!= \""+vectOriginalClassContent[i]+"\"");
//								i++;
//								// System.out.println("\""+vectClassContent[incrementPos]+"\" 4!= \""+vectOriginalClassContent[i]+"\"");
//							}
//						}
//
//						String lineOriginalClassContent = vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " ");
//						String lineParsedClassContent = vectClassContent[incrementPos].replaceAll("( )+", " ").replaceAll("\\s+", " ");
//						
//						if(lineOriginalClassContent.isEmpty() && i < incrementPos) {
//							for(int deltaToIncrementPos=i; deltaToIncrementPos<=incrementPos; deltaToIncrementPos++) {
//								lineOriginalClassContent=vectOriginalClassContent[deltaToIncrementPos].replaceAll("( )+", " ").replaceAll("\\s+", " ");
//								if(i < incrementPos) { i++; }
//								if(lineOriginalClassContent.contains(lineParsedClassContent)) { break; }
//							}
//						}
//						
//						
//						if(!vectOriginalClassContent[i].equals(""))
//							if(lineOriginalClassContent.contains(lineParsedClassContent) || lineParsedClassContent.contains(lineOriginalClassContent)) 
//							{
//								//System.out.println("vectClassContent:\""+vectClassContent[incrementPos]+"\"");
//								//System.out.println("\""+vectClassContent[incrementPos]+"\" == \""+vectOriginalClassContent[i]+"\"");
//								int lineNumberInOriginalClass = i+1;
//								listIdsParsedClassInOriginalClass.add(lineNumberInOriginalClass);
//								//System.out.println("i:"+i);
//							}
//							else
//							{
//								//System.out.println("\""+vectClassContent[incrementPos]+"\" != \""+vectOriginalClassContent[i]+"\"");
//							}
//					}
//				}
//			}
//			// update both counter variables
//			i++;
//			// we only want to take the lines of source code from 'vectOriginalClassContent'
//			incrementPos = i;
//		}//end while
//
//		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");
//		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");
//
//		return(listIdsParsedClassInOriginalClass);
//	}
	
	/**
	 * This method returns the line number for each line with code in it (no javadoc, no comments, blank lines).
	 * 
	 * @param body2
	 * @param sourceFolder
	 * @param file
	 * @return an Array with the line numbers of all lines containing relevant code
	 * @throws IOException
	 */
	public static List<Integer> computeCorrespondingIds(Node body2,String sourceFolder, String file) throws IOException{
		String originalClassContent=readFile(sourceFolder+file);
		String[] vectOriginalClassContent = originalClassContent.split("\n");
		if(vectOriginalClassContent.length==1){
			vectOriginalClassContent = originalClassContent.split("\r");
		}
		
		int lineCounter = 1; // src files start with line 1
		List<Integer> listIdsParsedClassInOriginalClass=new ArrayList<Integer>();
		boolean javadoc = false;
		
		for(String line : vectOriginalClassContent) {			
			if(line.startsWith("//") || line.replaceAll("\\s*","").isEmpty()) {
				//do nothing
			} else if(line.startsWith("/*")) {
				javadoc = true;
			} else if(line.contains("*/")) {
				javadoc = false;
			} else if(javadoc) {
				// do nothing
			} else {
				listIdsParsedClassInOriginalClass.add(lineCounter);
			}
			
			lineCounter++;
		}
		
		return listIdsParsedClassInOriginalClass;
	} // end of NEW computeCorrespondingIds

	public static java.util.List<Integer> intersect(List<Integer> a, List<Integer> b){
		java.util.List<Integer> lstIntersectAB=new ArrayList<Integer>(a);
		lstIntersectAB.retainAll(b);
		return lstIntersectAB;
	}

	public static  List<FieldDeclaration> detectAttributes(Node body2){
		List<FieldDeclaration> attributes=new ArrayList<FieldDeclaration>();
		String classContent=body2.toString();
		int incrementLinesCounter=0;
		while(classContent.contains("\n")){
			incrementLinesCounter++;
			classContent=classContent.replaceFirst("\n", "/*"+incrementLinesCounter+"#/");
		}
		classContent=classContent.replace("#/", "*/\n");
		//System.out.println(classContent);
		for (Node node : body2.getChildrenNodes()){
			//System.out.println("node: "+node);
			//System.out.println("node class type: \""+node.getClass().getSimpleName()+"\"");
			// we add the FieldDeclaration in the list of attributes..
			if(node.getClass().getSimpleName().equals("FieldDeclaration")){
				attributes.add((FieldDeclaration) node);
				//System.out.println("added attribute: "+node);
			}
			for (Node node2 : node.getChildrenNodes()){
				//System.out.println("node: "+node2);
				//System.out.println("node class type: \""+node2.getClass().getSimpleName()+"\"");
				// we add the FieldDeclaration in the list of attributes..
				if(node2.getClass().getSimpleName().equals("FieldDeclaration")){
					attributes.add((FieldDeclaration) node2);
					//System.out.println("added attribute: "+node2);
				}
			}
		}
		return attributes;
	}

	/**
	 * 
	 * @param attributes All attributes found in class.
	 * @param attributeContent Attribute to look for.
	 * @return 
	 */
	public static  boolean containsTheAttribute(List<FieldDeclaration> attributes,String attributeContent){
		boolean res=false;
		String currentAttribute="";
		attributeContent=attributeContent.replaceAll("\\s+", "");//ant0: we remove the occurrence of more than one single space" " 
		for(int i=0;i<attributes.size();i++)
		{
			currentAttribute=attributes.get(i).toString();
			currentAttribute=currentAttribute.replaceAll("\\s+", "");//ant0: we remove the occurrence of more than one single space" " 
			if(currentAttribute.equals(attributeContent) || currentAttribute.equals(attributeContent)){
				res=true;
				break;
			}
		}
		return res;
	}

	public static String readFile(String nomeFile) throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;

		StringBuffer sb = new StringBuffer();
		char[] buf = new char[1024];
		int len;

		try {
			is = new FileInputStream(nomeFile);
			isr = new InputStreamReader(is);

			while ((len = isr.read(buf)) > 0)
				sb.append(buf, 0, len);

			return sb.toString();
		} finally {
			if (isr != null)
				isr.close();
		}
	}


	public static List<Integer> findStartAndEndOfanIfStatement(Node body2, List<IfStmt> ifStmts, String ifToFind,
			List<Integer> listIdsParsedClassInOriginalClass,
			List<String> listStringsParsedClassInOriginalClass,String[] vectTextContentOriginalClass){

		ifToFind = ifToFind.replaceAll("\\s+", "") ; 
		IfStmt if1 =null;
		String [] vectIf1 = null;
		String ifFirstLine = null;
		List<Integer> vectorWithStartAndEndOfanIfStatement=new ArrayList<Integer>();//initialization..
		int pos=-1,posInOriginalClass=-1;
		int endIf=-1;
		int c=0;
		int occurencessComments=0;
		for(int i=0;i<ifStmts.size();i++){   
			if1=ifStmts.get(i); 
			vectIf1 = if1.toString().split("\n");
			ifFirstLine = vectIf1[0].replaceAll("\\s+", "");
			occurencessComments=0;
			if(if1.toString().contains("//")){
				// it contains the number of comments in line 
				// in the body of the of the traversed branch by the conditional statement
				occurencessComments = if1.toString().split("//").length-1;
			}
			c=0;//inizialization
			if(ifFirstLine.contains("//")){
				occurencessComments = occurencessComments-1;
				while(ifFirstLine.contains("//"))
				{
					c++;
					ifFirstLine = vectIf1[c].replaceAll("\\s+", "");
				}
			}
			if(ifToFind.contains(ifFirstLine) || ifFirstLine.contains(ifToFind)){
				//System.out.println("this: \""+ifFirstLine+"\"");
				//System.out.println("this2: \""+ifToFind+"\"");

				//we try to find the startIfId and the endIfId...
				pos = findIdElementIntheVector(listStringsParsedClassInOriginalClass,  ifFirstLine);
				//System.out.println("listIdsParsedClassInOriginalClass "+listIdsParsedClassInOriginalClass.size() );
				posInOriginalClass = listIdsParsedClassInOriginalClass.get(pos);
				//System.out.println("pos if InOriginalClass: "+(posInOriginalClass+1));
				endIf=posInOriginalClass+vectIf1.length+occurencessComments-1;
				//System.out.println("pos end if InOriginalClass: "+(endIf+1));
				posInOriginalClass = posInOriginalClass+1;//start if in the original class
				if(!vectTextContentOriginalClass[posInOriginalClass-1].contains("if")){
					posInOriginalClass++;
				}
				if(vectTextContentOriginalClass[endIf].contains("else if")){
					endIf--;  
				}
				while(!vectTextContentOriginalClass[endIf].contains("}"))
				{
					endIf++;
				}
				endIf++;//to fix the bug for the end position
				vectorWithStartAndEndOfanIfStatement.add(posInOriginalClass);
				vectorWithStartAndEndOfanIfStatement.add(endIf);// end if in the original class
			}
			else{
				//System.out.println("ifToFind: \""+ifToFind+"\" != \""+ifFirstLine+"\"");
			}
		}
		return(vectorWithStartAndEndOfanIfStatement);
	}

	public static List<Integer> findStartAndEndOfanIfStatement(final String currentIfStmt, CompilationUnit unit) {
		List<Integer> vectorWithStartAndEndofIfStmt = new ArrayList<Integer>();
		
		unit.accept(new ASTVisitor() {
			
			public boolean visit(IfStatement node) {
				if(currentIfStmt.contains(node.getExpression().toString())) {
					vectorWithStartAndEndofIfStmt.add(unit.getLineNumber(node.getStartPosition()));
					int ifLength = 0;
					
					ifLength += checkIfPart(node.getThenStatement());
					ifLength += checkIfPart(node.getElseStatement());

					//Now we checked both parts of the If Stmt and we know how long the whole If-Stmt is
					int endingLineNumber = vectorWithStartAndEndofIfStmt.get(0).intValue() + ifLength;
					vectorWithStartAndEndofIfStmt.add(endingLineNumber);
					
					return false; // do not continue with search after visit
				
				} else {
					return true; // do continue with search after visit
				}
				
			}
			
			private int checkIfPart(Statement stmt) {
				int length = 0;
				
				if(stmt != null) {
					if(stmt.toString().split("\n").length == 1) {
						// FIXME The statement (single line) from If can be on same line as the If-Expression or on the next line!
						// therefore length could either be 1 or 2
						length += 2;
					} else {
						// Starting line number is always in If-Length included. Therefore subtract 1.
						length += stmt.toString().split("\n").length - 1;
					}
				}
				return length;
			}
			
		});
		return vectorWithStartAndEndofIfStmt;
	}
	
	/**
	 * 
	 * @param vect The whole source file
	 * @param element The If-Line we are looking for
	 * @return Position of the first found If-Line
	 */
	public static int findIdElementIntheVector(List<String> vect, String element){
		int pos =-1;
		String temp = "";
		for(int i=0;i<vect.size();i++)
		{
			temp = vect.get(i).replaceAll("( )+", "").replaceAll("\\s+", "");
			element=element.replaceAll("( )+", "").replaceAll("\\s+", "");
			if(temp.contains("if"))
				//System.out.println("vect: \""+vect[i]+"\"");
				//System.out.println("element: \""+element+"\"");
				if(temp.contains(element) || element.contains(temp) ){
					pos=i;
					break;
				}
		}
		return(pos);
	}

}

