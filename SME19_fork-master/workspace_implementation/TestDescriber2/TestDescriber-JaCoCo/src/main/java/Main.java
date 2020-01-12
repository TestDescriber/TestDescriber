

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.jacoco.JaCoCoRunner;
import nl.tudelft.jacoco.JacocoResult;
import nl.tudelft.utils.commandline.TestCaseParser;

/**
 * Example of execution using Cobertura for generating coverage information for
 * the class {@link Triangle} when running the JUnit test case {@link TriangleTest}
 * @author Annibale Panichella
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		// folder 'bin' containing compiled source files and tests 
		String project_dir = System.getProperty("user.dir");
		String binFolder = project_dir+"/TestDescriberJaCoCo/resources/Task1/bin";
		List<File> jars = new ArrayList<File>();
		jars.add(new File(binFolder));
		// name of the JUnit test case to run (+ path of the package)
		// String testCase = "nl.tudelft.junitexample.TriangleTest";
		String testCase = "org.magee.math.Rational_ESTest";

		String cut = "org.magee.math.Rational";

		// Map used to store the covered lines
		Map<String, List<Integer>> coverage = new HashMap<String, List<Integer>>();

		// extract the number of test methods in 'test_case'
		List<String> list = TestCaseParser.findTestMethods(binFolder, testCase);
		System.out.println(list);
		// initialize class runner for Jacoco
		for (String tc : list){ 
			String temp_file = project_dir+"/temp1";
			JaCoCoRunner runner = new JaCoCoRunner(new File(temp_file), jars);
			runner.run(cut, testCase+"#"+tc, jars);
			JacocoResult results = runner.getJacocoResults();
			coverage.put(tc, new ArrayList<Integer>(results.getCoveredLines()));

		}
		// print 'coverage' content
		System.out.println();
		System.out.println("#### COVERAGE INFORMATION ####");
		System.out.println();
		for (String test : coverage.keySet()){
			System.out.println("Coverage for "+test);
			for (Integer coveredLine : coverage.get(test)){
				System.out.print(coveredLine+", ");
			}
			System.out.println();
		}

	}
}
