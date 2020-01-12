package ch.uzh.parser.testing.summarization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nl.tudelft.jacoco.JaCoCoRunner;
import nl.tudelft.jacoco.JacocoResult;
import nl.tudelft.utils.commandline.TestCaseParser;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.comments.BlockComment;

import ch.uzh.parser.JavaFileParser;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.MethodBean;
import ch.uzh.utility.TestCoverageComputation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Main parser for Test case Summarization..
 * @author Sebastiano Panichella and Annibale Panichella and Gabriela Lopez Maga�a
 *
 */
public class Main_TD_2019 extends SrcSummarization {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 *
	 */
	//TODO GGG change type of Exception
	public static void main(String[] args) throws IOException, InterruptedException, ParseException, Exception {
		PathParameters pathParameters = null;
		boolean verbose = false;
		PrintStream originalStream = System.out;
		PrintStream dummyStream = new PrintStream(new OutputStream(){
		    public void write(int b) {
		    }
		});
		
		if(args.length!=0) {
			String sourceFolder = "";
			String pBinFolder = "";
			String testSrcFolder = "";
			String testBinFolder = "";
			List<String> classesFiles = new ArrayList<String>();
			List<String> testsFiles = new ArrayList<String>();
				/*reader = new BufferedReader(new FileReader(args[0]));
				String line = reader.readLine();*/
				//set-up
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
				DocumentBuilder db = dbf.newDocumentBuilder();  
				Document doc = db.parse(new File(args[0]));  
				doc.getDocumentElement().normalize(); 
				//end set-up
				
				//read two nodes
				NodeList nList = doc.getElementsByTagName("path");
				NodeList verboseList = doc.getElementsByTagName("verbose");
				//end read
				Node nNode = nList.item(0); //sub tree	
				Element eElement = (Element) nNode;

	
				sourceFolder = eElement.getElementsByTagName("src").item(0).getTextContent();
				pBinFolder = eElement.getElementsByTagName("bin").item(0).getTextContent();	
				classesFiles.add(eElement.getElementsByTagName("class").item(0).getTextContent());
				testsFiles.add(eElement.getElementsByTagName("testClass").item(0).getTextContent());

				if(verboseList.item(0).getTextContent().equals("TRUE")){
					verbose=true;
				}
				
				
			
		
			String jarProjectFolder = pBinFolder;
			testSrcFolder = sourceFolder;
			testBinFolder = pBinFolder;
			pathParameters = new PathParameters(sourceFolder, pBinFolder, testSrcFolder, testBinFolder, classesFiles, testsFiles, jarProjectFolder);
		}else {
			pathParameters = PathParameters.createPathParameters_task1();
		}
		
		
		
		//PathParameters pathParameters = PathParametersSilvia.createPathParameters_task1();
		//PathParameters pathParameters = PathParametersSilvia.createPathParametersSilvia_ofbiz();
		
		System.setOut(originalStream);
		System.out.println("Step 1: Parsing JAVA CLASSES/JAVA TESTS");
		setMyOut(verbose, originalStream, dummyStream);
		Vector<ClassBean> productionClass = JavaFileParser.parseJavaClasses(pathParameters.classesFiles, pathParameters.sourceFolder);
		Vector<ClassBean> testClass = JavaFileParser.parseJavaClasses(pathParameters.testsFiles, pathParameters.testSrcFolder);

		System.setOut(originalStream);
		System.out.println("Step 2: Running JaCoCo ");
		setMyOut(verbose, originalStream, dummyStream);
		ClassBean classeTest = testClass.get(0);
		ClassBean clazz = productionClass.get(0);
		
		// class responsible for the test coverage computation (using the Jacoco API).
		// this will return only methods that have coverage....
		TestCoverageComputation testCoverageComputation = new TestCoverageComputation(productionClass, testClass, pathParameters);

		List<String> testsCoverage = testCoverageComputation.getTestsCoverage();

		if (testsCoverage.size() == 0) {
			System.out.println("No coverage information are obtained "+testsCoverage);
			return;
		}

		System.out.println("testsCoverage: "+testsCoverage);
		
		
		List<MethodBean> testCases = new Vector<MethodBean>(); // = (Vector<MethodBean>) classeTest.getMethods(); //FIXME GGG ?? why not only the Test Methods?
		List<String> listTestMethods = new ArrayList<String>();
		
		//GGG here we only select test methods START
		if(pathParameters.prefixTestMethods.isEmpty()) {
			listTestMethods  = TestCaseParser.findTestMethods(pathParameters.testBinFolder, convert2PackageNotation(pathParameters.testsFiles.get(0)));
		} else {
			listTestMethods  = TestCaseParser.findTestMethods(pathParameters.testBinFolder, convert2PackageNotation(pathParameters.testsFiles.get(0)), pathParameters.prefixTestMethods, pathParameters.nameTestMethods);
		}
		System.out.println("Size of listTestMethods: "+listTestMethods.size());
		System.out.println(listTestMethods);
		System.out.println("Size of classeTest.getMethods()"+classeTest.getMethods().size());
		// GGG here we only select test methods END
		
		// GGG we will only include methods in the testCases list that are in the list of test cases (listTestMethods)
		// GGG and also have coverage, else we have an exception in SrcMLParser.howManyAttributesCovered
		for(MethodBean m : classeTest.getMethods()) {
			System.out.println(m.getName());
			for (String tcName : listTestMethods) {
				if(tcName.trim().equals(m.getName().toString().trim())) {
					testCases.add(m);
				}
			}
		}
		//FIXME GGG it should be able to work on methods that don't provide coverage of the CUT!!!! 

		System.setOut(originalStream);
		System.out.println("Step 3: Parsing Covered Statements");
		setMyOut(verbose, originalStream, dummyStream);
		List<String> textContentExecutedOriginalClass = null;
		for(int index=0; index<testCases.size(); index++) {
			System.out.println("\r\n parseSrcML for: "+testCases.get(index).getName());
			if(testsCoverage.get(index) != null) {
				textContentExecutedOriginalClass=SrcMLParser.parseSrcML(testCases, index, clazz, pathParameters.sourceFolder, pathParameters.classesFiles, 0, testsCoverage);
			} else {
				System.out.println("testsCoverage is null for "+index+"(!)");
			}
		}

		// Derive the import from the class
		clazz.setImports(clazz.extractImports(textContentExecutedOriginalClass));

		System.setOut(originalStream);
		System.out.println("Step 4: Generating Summaries as code comments");
		setMyOut(verbose, originalStream, dummyStream);

		generateTestMethodsSummary(classeTest, testCases, textContentExecutedOriginalClass);

		generateClassUnderTestSummary(pathParameters, classeTest, clazz);
		System.setOut(originalStream);
	}
	
	protected static void setMyOut(boolean verbose, PrintStream original, PrintStream dummy) {
		if(!verbose) {
			System.setOut(dummy);
		}
	}

	
	protected static String convert2PackageNotation(String url){
		String path = url.replace(File.separator, ".");
		if (path.endsWith(".java"))
			path = path.substring(0, path.length()-5);
		return path;
	}
}

