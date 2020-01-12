package ch.uzh.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.testing.summarization.PathParameters;
import nl.tudelft.jacoco.JaCoCoRunner;
import nl.tudelft.jacoco.JacocoResult;
import nl.tudelft.utils.commandline.TestCaseParser;
/**
 * Class responsible for the test coverage computation (using the Jacoco API).
 * 
 * @author @sebastiano panichella and Gabriela Lopez
 *
 */
public class TestCoverageComputation {

	// Production code (i.e., the Java class) to consider for the test coverage
	// computation
	Vector<ClassBean> productionClass = null;
	// Test class to consider for the test coverage computation
	Vector<ClassBean> testClass = null;

	// Map used to store the covered lines
	Map<String, List<Integer>> coverage = null;

	// number of test methods in 'test_case'
	List<String> listTestMethods = null;

	// folder binary code
	String pBinFolder = null;

	// folder for binary code of the test cases GGG
 	String testBinFolder = null;
 	
 	// folder containing the project jar file 
 	String jarProjectFolder = null;

	// outcome of the test coverage computation
	List<String> testsCoverage = null;

	// the list contains the fully classified path of the java class
	List<String> pathJavaClass = new ArrayList<String>();

	// the list contains the fully classified path of the test class
	List<String> pathTestClass = new ArrayList<String>();
	
	// prefixes of test methods in 'test_case' file 
	List<String> prefixTestMethods =  new ArrayList<String>();
	List<String> nameTestMethods =  new ArrayList<String>();

	
	public TestCoverageComputation(Vector<ClassBean> productionClass, Vector<ClassBean> testClass,
			PathParameters pathParameters) {
		super();
		this.productionClass = productionClass;
		this.testClass = testClass;
		this.pBinFolder = pathParameters.pBinFolder; //TODO GGG Rename to match names overall 
		this.pathJavaClass = pathParameters.classesFiles;
		this.pathTestClass = pathParameters.testsFiles;
		this.testBinFolder = pathParameters.testBinFolder;
		this.prefixTestMethods = pathParameters.prefixTestMethods;
		this.jarProjectFolder = pathParameters.jarProjectFolder;
		this.nameTestMethods = pathParameters.nameTestMethods;
		
		// extract the number of test methods in 'test_case'
		if (this.prefixTestMethods.isEmpty()) {
			this.listTestMethods  = TestCaseParser.findTestMethods(testBinFolder, convert2PackageNotation(pathTestClass.get(0)));
		} else {
			this.listTestMethods  = TestCaseParser.findTestMethods(testBinFolder, convert2PackageNotation(pathTestClass.get(0)), prefixTestMethods, nameTestMethods); //GGG delete this line, this will not be used pBinFolder
		}

		System.out.println("TestCoverageComputation listTestMethods: "+this.listTestMethods);
		
		 // initialization of the list that will 
		//  contain the outcome of the test coverage computation
		this.testsCoverage = new ArrayList<String>();
		
		// Map used to store the covered lines
		Map<String, List<Integer>> coverage = new HashMap<String, List<Integer>>();
		//get directory from system property
		String project_dir = System.getProperty("user.dir");
		List<File> libraryFolders = new ArrayList<File>();
		List<File> instrumentedJars = new ArrayList<File>();
		//jars.add(new File(pBinFolder)); //GGG here is meant the testbin folder
		instrumentedJars.add(new File(jarProjectFolder)); //GGG added for multiple folders with .class
		libraryFolders.add(new File(testBinFolder)); //GGG ???
		libraryFolders.add(new File(pBinFolder)); //GGG ???


		for (String tc : listTestMethods){
			String tmpString = "\temp1";
			if(System.getProperty("os.name").startsWith("Windows")) {
				tmpString = "\\temp1";
			}
			String temp_file = project_dir+tmpString;
			System.out.println("project_dir: "+project_dir);
			System.out.println("tmp_file: "+temp_file);
			
			JaCoCoRunner runner = new JaCoCoRunner(new File(temp_file), libraryFolders);
			System.out.println("1st part run: "+convert2PackageNotation(this.pathJavaClass.get(0))); //GGG del
			System.out.println("2nd part run: "+convert2PackageNotation(this.pathTestClass.get(0))+"#"+tc); //GGG del
			runner.run(convert2PackageNotation(this.pathJavaClass.get(0)), convert2PackageNotation(this.pathTestClass.get(0))+"#"+tc, instrumentedJars);

			JacocoResult results = runner.getJacocoResults();
			System.out.println(results.getBranchesCovered());
			coverage.put(tc, new ArrayList<Integer>(results.getCoveredLines()));

			String covered = results.getCoverageInfoAsString();
			//TODO GGG here only if something was covered is added, in the parser it expects a value for each
			//if (covered != null)
				testsCoverage.add(covered);
		}
		
	}
	
	protected static String convert2PackageNotation(String url){
		String path = url.replace(File.separator, ".");
		if (path.endsWith(".java"))
			path = path.substring(0, path.length()-5);
		return path;
	}

	
	public Map<String, List<Integer>> getCoverage() {
		return coverage;
	}



	public void setCoverage(Map<String, List<Integer>> coverage) {
		this.coverage = coverage;
	}



	public List<String> getListTestMethods() {
		return listTestMethods;
	}



	public void setListTestMethods(List<String> listTestMethods) {
		this.listTestMethods = listTestMethods;
	}



	public String getpBinFolder() {
		return pBinFolder;
	}



	public void setpBinFolder(String pBinFolder) {
		this.pBinFolder = pBinFolder;
	}



	public List<String> getTestsCoverage() {
		return testsCoverage;
	}



	public void setTestsCoverage(List<String> testsCoverage) {
		this.testsCoverage = testsCoverage;
	}



	public Vector<ClassBean> getProductionClass() {
		return productionClass;
	}
	
	public void setProductionClass(Vector<ClassBean> productionClass) {
		this.productionClass = productionClass;
	}
	
	public Vector<ClassBean> getTestClass() {
		return testClass;
	}
	
	public void setTestClass(Vector<ClassBean> testClass) {
		this.testClass = testClass;
	}

	
	
}
