package nl.tudelft.jacoco;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.tudelft.utils.Utilities;

public class JaCoCoRunner {

	private List<File> extraClassPath = null;

	private JacocoResult jacoco_result;

	private File temporary_directory;

	/**
	 * Runner for JaCoCo tasks. All files that would be generated by JaCoCo will be
	 * stored in the folder specified by the first parameter
	 * 
	 * @param temp_directory
	 * @param classpath
	 */
	public JaCoCoRunner(File temp_directory, List<File> classpath) {

		if (classpath == null) {
			extraClassPath = Collections.emptyList();
		} else {
			extraClassPath = new ArrayList<File>(classpath);
		}

		try {
			temporary_directory = temp_directory;
			Utilities.cleanDirectory(temporary_directory);
		} catch (IOException ioe) {
			System.out.println("Unable to clean bin directory! Aborting test execution!");
			return;
		}

	}

	/**
	 * This method invokes {@link JaCoCoWrapper} to derive the coverage information
	 * for the specified CUT (first parameter) when running the specified test case
	 * (second parameter)
	 * 
	 * @param cut             class under test
	 * @param testCase        test case to run (the name has to include the
	 *                        packages)
	 * @param instrumentedJar (folder or jar containing all *.class files)
	 */
	public void run(String cut, String testCase, List<File> instrumentedJar) {
		try {
			System.out.println("\n=== Run Jacoco for Coverage ===");
			System.out.println("temporary_directory: " + temporary_directory.getAbsolutePath()); // GGG del
			JaCoCoWrapper wrapper = new JaCoCoWrapper(temporary_directory.getAbsolutePath());

			// TODO GGG not wokring for other 1:1 structures, refactor, testcases must
			// include whole path
			List<String> testClasses = new ArrayList<String>();

			// for Windows, change path character
			if (System.getProperty("os.name").startsWith("Windows")) {
				System.out.println("orig testCase: " + testCase);
				testCase = testCase.replace("/", "\\");
				// GGG testCase = (String) "org\\magee\\math\\TestRational#test0";//GGG
				// testCase = (String)
				// "test\\java\\org\\mockito\\internal\\creation\\bytebuddy\\AbstractByteBuddyMockMakerTest#should_create_mock_from_interface";
				System.out.println("new testCase: " + testCase);
			}

			testClasses.add(testCase);
			wrapper.setTestCase(testClasses);

			System.out.println("JaCoCoRunner.run wrapper.setJarInstrument: " + instrumentedJar);
			wrapper.setJarInstrument(instrumentedJar);

			String cp = new Utilities.CPBuilder().and(extraClassPath).build(); // TODO GGG here all the other folders
																				// should be included
			Path currentWorkingDir = Paths.get("").toAbsolutePath();// GGG del
			System.out.println("currentWorkingDir: " + currentWorkingDir.normalize().toString());// GGG del
			// GGG System.out.println("cp: " + cp);// GGG del
			if (System.getProperty("os.name").startsWith("Windows")) {
				cp = cp.replaceAll(":C", "&C"); //TODO GGG detect root name in windowss
			} else {
				cp = cp.replaceAll("::", ":");
			}

			//GGG too much log when including the whole project!!! System.out.println("new cp: " + cp);// GGG del
			//GGG too much log when including the whole project!!! System.out.println("JaCoCoRunner.run wrapper.setClassPath (required_libraries): " + cp);
			wrapper.setClassPath(cp);
			wrapper.setTargetClass(cut);

			wrapper.runJaCoCo();
			JacocoResult results = wrapper.getResults();
			results.printResults();

			this.jacoco_result = results;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JacocoResult getJacocoResults() {
		return this.jacoco_result;
	}
}
