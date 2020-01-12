package ch.uzh.parser.testing.summarization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;

import ch.uzh.parser.JavaFileParser;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;

public class SrcMLParserOld {


	public static List<String> parseSrcML(List<MethodBean> testCases,int c,ClassBean classe, String sourceFolder,List<String>  classesFiles,int t,List<String>  testsCoverage) throws IOException, InterruptedException, ParseException{

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
		List<String> textContentExecutedOriginalClass= new ArrayList<String>();
		double attributesTested=0;
		int indexOfIf=-1,indexOfEndIf=-1, lenghtVect=-1, pos=-1, posEnd=-1, incrementPos=-1 ;
		//WE PRINT THE TEST CASE..
		//printMethodContent(testCase);
		textContentOriginalClass= classe.getTextContent();
		//textContentSrcML=  ;
		//new analyzer instructions...
		Node body2 = JavaParser.parse(new File(sourceFolder+classesFiles.get(t)));
		Node temp=null;
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
		testCoverageBooleanValues= ","+testsCoverage.get(c)+",";
		testCoverage = ","+testsCoverage.get(c)+",";
		testCoverage=testCoverage.replace("-true", "").replace("-false", "");

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

		textContentExecutedOriginalClass = new ArrayList<String>();
		//now we put in the class the line until the class "parentesis"
		counterCurrentLineInOriginalClass=1;
		line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];//inizialization
		startIfIds=new ArrayList<Integer>();
		endIfIds=new ArrayList<Integer>();
		while(!line.replaceAll("( )+", " ").contains("public class ")){
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

		counterCurrentLineInOriginalClass++;
		for(int s=0;s<vectTextContentOriginalClass.length;s++)
		{
			line = vectTextContentOriginalClass[s];
			//we try to understand when an if statement start and finish
			if(!line.contains("//"))
				if(line.contains("if") )
					if(!line.contains("else if")) 
					{
						ifToFind=line;
						//System.out.println("vectTextContentOriginalClass[s]; : \""+ifToFind+"\"");
						vectorWithStartAndEndOfanIfStatement=findStartAndEndOfanIfStatement(body2, ifStmts,  ifToFind,
								listIdsParsedClassInOriginalClass,listStringsParsedClassInOriginalClass,vectTextContentOriginalClass);
						//System.out.println("vectTextContentOriginalClass.size() : "+vectorWithStartAndEndOfanIfStatement.size());
						if(vectorWithStartAndEndOfanIfStatement.size()>0)
							if(vectorWithStartAndEndOfanIfStatement.get(0)!= -1 & vectorWithStartAndEndOfanIfStatement.get(1)!= -1){
								//	 	System.out.println("indexOfIf -> "+vectorWithStartAndEndOfanIfStatement.get(0)+"; indexOfEndIf -> "+vectorWithStartAndEndOfanIfStatement.get(1));
								startIfIds.add(vectorWithStartAndEndOfanIfStatement.get(0));
								endIfIds.add(vectorWithStartAndEndOfanIfStatement.get(1));
							}
					}
		}//for s


		while(counterCurrentLineInOriginalClass<=vectTextContentOriginalClass.length)
		{
			line=vectTextContentOriginalClass[counterCurrentLineInOriginalClass-1];

			if(	containsTheAttribute( attributes, line) & !linesExecutedOfOriginalClassId.contains(counterCurrentLineInOriginalClass))
			{
				textContentExecutedOriginalClass.add(line);
				//System.out.println("current line    -> "+line);
			}
			//UNTIL here replaced new (without SRCML)...
			//if(line2.contains("<constructor>") | line2.contains("<function>"))
			if(containsAConstructorOrAMethod( methods, constructorDeclarations,  line) && !line.replaceAll("\\s+", "").equals(""))
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
				lenghtVect=temp.toString().split("\n").length;// length of the vector "split" using a separator "\n"
				pos = listIdsParsedClassInOriginalClass.indexOf(counterCurrentLineInOriginalClass-1);
				if(pos== -1){
					pos = listIdsParsedClassInOriginalClass.indexOf(counterCurrentLineInOriginalClass);// to fix the bug...
				}
				//System.out.println("(listIdsParsedClassInOriginalClass) -> \""+listIdsParsedClassInOriginalClass+"\"");
				//System.out.println("(listStringsParsedClassInOriginalClass) -> \""+listStringsParsedClassInOriginalClass+"\"");
				//System.out.println("(current method/constructor) (pos="+pos+") ");
				//System.out.println("(current method/constructor) ("+pos+") first line-> \""+listStringsParsedClassInOriginalClass.get(pos)+"\"");
				posEnd=listIdsParsedClassInOriginalClass.get(pos+lenghtVect)-1;
				// System.out.println("(current method/constructor new) END current line id -> "+(posEnd));
				//System.out.println("(current method/constructor new) END current line    -> \""+line+"\"");
				incrementPos= listIdsParsedClassInOriginalClass.get(pos);
				while(incrementPos!=posEnd){
					//System.out.println("(current method/constructor new) ADDED current line    ("+(incrementPos+1)+")-> \""+vectTextContentOriginalClass[incrementPos]+"\"");
					methodOrConstructorLines.add(vectTextContentOriginalClass[incrementPos]);
					methodOrConstructorLinesIds.add(incrementPos+1);
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
					//we verify whether the list contains the signature of the method/constructor
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

									textContentExecutedOriginalClass.add("         {");

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
						}

						if(!lineInthEIntersection.contains("}")){
							textContentExecutedOriginalClass.add("}");

							//System.out.println("current line    -> "+"}");
						}
					}
					else{

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
								if(linesExecutedOfOriginalClassIdWithBooleanValue.contains(""+intersectionOfIds.get(p)+"-false"))
								{
									if (lineInthEIntersection.contains("{"))
										textContentExecutedOriginalClass.add("         }");
									else
										textContentExecutedOriginalClass.add("         {}");
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
		System.out.println("\r\n LINES EXECUTED OF THE ORIGINAL CLASS IN  Test case "+testCase.getName()+":");
		for(int p=0;p<textContentExecutedOriginalClass.size();p++)
		{
			textContentExecutedVersionOfOriginalClass=textContentExecutedVersionOfOriginalClass+textContentExecutedOriginalClass.get(p)+"\r";
			//a) NOW WE PRINT the LINES EXECUTED (COVERED) OF ORIGINAL CLASS by the Test case   
			//System.out.println(textContentExecutedOriginalClass.get(p)+"\r\n");
		}

		//b) System.out.println(textContentExecutedVersionOfOriginalClass);
		classeExecuted=JavaFileParser.parseJavaClass(textContentExecutedVersionOfOriginalClass);
		classeExecuted.setImports(classe.extractImports(textContentExecutedOriginalClass));
		testCase.setOriginalClassOfTestCase(classe);
		testCase.setClassExecutedByTheTestCase(classeExecuted);
		testCase.setLinesExecutedOfOriginalClass(linesExecutedOfOriginalClassIdWithBooleanValue);
		testCase.setAttributesTested(attributesTested);

		return(textContentExecutedOriginalClass);
	}

	public static  Node returnTheCorrespondingConstructorOrAMethod(List<MethodDeclaration> methods, 
			List<ConstructorDeclaration> constructorDeclarations, String currentInstruction){
		String currentMethodOrConstructor="";
		currentInstruction=currentInstruction.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
		for(int i=0;i< methods.size();i++)
		{
			currentMethodOrConstructor=methods.get(i).toString();
			currentMethodOrConstructor=currentMethodOrConstructor.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
			if(currentMethodOrConstructor.startsWith(currentInstruction)){
				return(methods.get(i));
			}
			else{
				//System.out.println("This is not a contructor:\""+currentInstruction+"\"");
			}
		}

		for(int i=0;i< constructorDeclarations.size();i++)
		{
			currentMethodOrConstructor=constructorDeclarations.get(i).toString();
			currentMethodOrConstructor=currentMethodOrConstructor.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
			if(currentMethodOrConstructor.startsWith(currentInstruction)){
				return(constructorDeclarations.get(i));
			}
			else
			{
				//System.out.println("This is not a contructor:\""+currentInstruction+"\"");
			}
		}
		return null;
	}

	public static  boolean containsAConstructorOrAMethod(List<MethodDeclaration> methods, 
			List<ConstructorDeclaration> constructorDeclarations, String currentInstruction){
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
			else{
				//System.out.println("This is not a contructor:\""+currentInstruction+"\"");
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
			else
			{
				//System.out.println("This is not a contructor:\""+currentInstruction+"\"");
			}
		}
		return res;
	}

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

	public static List<String> computeCorrespondingStringIds(Node body2,String sourceFolder, String file) throws IOException{
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
		}//end while

		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");
		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");

		return(listStringsParsedClassInOriginalClass);
	}

	public static List<Integer> computeCorrespondingIds(Node body2,String sourceFolder, String file) throws IOException{
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
		System.out.println("originalClassContent:\""+vectOriginalClassContent[0]+"\"");
		System.out.println("classContent:\""+vectClassContent[0]+"\"");
		int i=0,incrementPos=-1;
		List<Integer> listIdsParsedClassInOriginalClass=new ArrayList<Integer>();
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
								// System.out.println("\""+vectClassContent[incrementPos]+"\" 3!= \""+vectOriginalClassContent[i]+"\"");
								i++;
								// System.out.println("\""+vectClassContent[incrementPos]+"\" 4!= \""+vectOriginalClassContent[i]+"\"");
							}
						}

						vectOriginalClassContent[i]=vectOriginalClassContent[i].replaceAll("( )+", " ").replaceAll("\\s+", " ");
						if(!vectOriginalClassContent[i].equals(""))
							if(vectOriginalClassContent[i].replaceAll("( )+", "").contains(vectClassContent[incrementPos].replaceAll("( )+", "")) || vectClassContent[incrementPos].replaceAll("( )+", "").contains(vectOriginalClassContent[i].replaceAll("( )+", ""))) 
							{
								//System.out.println("vectClassContent:\""+vectClassContent[incrementPos]+"\"");
								//System.out.println("\""+vectClassContent[incrementPos]+"\" == \""+vectOriginalClassContent[i]+"\"");
								listIdsParsedClassInOriginalClass.add(i);
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
		}//end while

		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");
		//System.out.println("size vectClassContent:\""+vectClassContent.length+"\"");

		return(listIdsParsedClassInOriginalClass);
	}

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

	public static  boolean containsTheAttribute(List<FieldDeclaration> attributes,String attributeContent){
		boolean res=false;
		String currentAttribute="";
		attributeContent=attributeContent.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
		for(int i=0;i<attributes.size();i++)
		{
			currentAttribute=attributes.get(i).toString();
			currentAttribute=currentAttribute.replaceAll("\\s+", "");// we remove the occurrence of more than one single space" " 
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

