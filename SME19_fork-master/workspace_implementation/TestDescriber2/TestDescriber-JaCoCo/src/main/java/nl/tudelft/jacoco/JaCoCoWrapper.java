package nl.tudelft.jacoco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Wrapper for JaCoCo
 * @author annibale.panichella
 *
 */
public class JaCoCoWrapper {

	private String temp_folder;
	private String targetClass;
	private List<String> testCases;

	private LinkedList<String> required_libraries; // it has to contain the CP required to run the tests

	private List<File> jar_to_instrument;
	private List<File> instrumented_jar;

	private JacocoResult results;

	public static final int TEST_TIMEOUT = 60000;
	public static final int MAX_THREAD = 1;

	public JaCoCoWrapper(String pTempFile){
		this.temp_folder = pTempFile;
	}

	public void setTestCase(List<String> testCases){
		this.testCases = testCases;
	}

	public void setTargetClass(String CUT){
		this.targetClass = CUT.replace('.', '/');
	}

	/**
	 * This method stores all the libraries required to run the test against the SUT
	 * @param classpath String containing all required jars separated by ":"
	 */
	public void setClassPath(String classpath){
		required_libraries = new LinkedList<String>();
		String join_symbol = ":"; 

		if(System.getProperty("os.name").startsWith("Windows")) {
			join_symbol = "&"; 
		}
		
		String[] libraries = classpath.split(join_symbol);
		//System.out.print("libraries"+libraries);//GGG del
		for (String s : libraries){
			s = s.replace(join_symbol, "");
			if (s.length() > 0)
				required_libraries.addLast(s);
		}
	}

	/**
	 * Set the jar or the folders with *.class files (SUT) to instrument with JaCoCo
	 * @param path of the SUTs to instrument
	 */
	public void setJarInstrument(List<File> jars){
		jar_to_instrument = new LinkedList<File>();

		instrumented_jar = new LinkedList<File>();

		for (File file : jars){
			if (!file.exists())
				try {
					throw new FileNotFoundException();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			jar_to_instrument.add(file);

			// name of the jar where to store the instrumented classes
			String jarName = file.getName();
			if (jarName.endsWith(".jar"))
				jarName = jarName.replaceAll(".jar", "_instrumented.jar");
			else
				jarName = jarName+"_instrumented";
			instrumented_jar.add(new File(this.temp_folder+"/"+jarName));
		}
	}


	private String generateClassPath() throws MalformedURLException{
		String cp ="";
		String join_symbol = ":"; 
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			join_symbol = "&";
		}
		
		// first we add the instrumented jar
		for (int index = 0; index < instrumented_jar.size(); index++){
			cp = cp + instrumented_jar.get(index).getAbsolutePath()+join_symbol;
		}

		// then we add all the other required library
		for (int index = 0; index < required_libraries.size(); index++){
			cp = cp + required_libraries.get(index)+join_symbol;
		}

		return cp;
	}

	/** 
	 * This method invokes JaCoCo and collect the corresponding coverage results
	 * @throws Exception
	 */
	public void runJaCoCo() throws Exception{
		// For instrumentation and runtime we need a IRuntime instance
		// to collect execution data:
		final IRuntime runtime = new LoggerRuntime();
		final Instrumenter instr = new Instrumenter(runtime);

		int tot_instrumented_class = 0;

		for (int index = 0; index<jar_to_instrument.size(); index++) {

			// if it is a directory, then we instrument only the Class Under Test (CUT)
			if (jar_to_instrument.get(index).isDirectory()) {
				// create the directory for the instrumented classes
				instrumented_jar.get(index).mkdir();

				// copy all classes in the new directory temp_folder
				List<File> files = (List<File>) FileUtils.listFiles(jar_to_instrument.get(index), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
//GGG				System.out.println("files to copy: "+files);
				String CUT = targetClass.substring(targetClass.lastIndexOf("/")+1, targetClass.length())+".class";
				System.out.println("CUT: "+CUT);
				File instrumented_CUT=null;
				for(File file : files){
					if (file.getName().equals(CUT)){
						String instrumented_path = file.getAbsolutePath();
						instrumented_path = instrumented_path.replace(jar_to_instrument.get(index).getAbsolutePath(), instrumented_jar.get(index).getAbsolutePath());
						FileUtils.copyFile(file, new File(instrumented_path));
						System.out.println("Instrumenting the class: "+file.getAbsolutePath());
						instrumented_CUT = file;

						//TODO GGG this following stuff was outside the for loop, why?
						String fileName = instrumented_CUT.getAbsolutePath();
						System.out.println("fileName" + fileName); //GGG del

						fileName = fileName.replace(jar_to_instrument.get(index).getAbsolutePath(), instrumented_jar.get(index).getAbsolutePath());
						System.out.println("new filename: "+fileName); //GGG del

						// jar of the SUT
						InputStream input = new FileInputStream(instrumented_CUT.getAbsolutePath());
						System.out.println("input: "+input); //GGG del

						OutputStream output = new FileOutputStream(fileName);
						System.out.println("output: "+output); //GGG del
						tot_instrumented_class += instr.instrumentAll(input, output, "");
					}
				}

				//}
			} else{
				// jar of the SUT
				InputStream input = new FileInputStream(jar_to_instrument.get(index));
				System.out.println("input else: "+input); //GGG del
				// jar with instrumented classes
				OutputStream output = new FileOutputStream(instrumented_jar.get(index));
				System.out.println("output else: "+output); //GGG del
				tot_instrumented_class += instr.instrumentAll(input, output, "");
			}
		}
		System.out.println("Number of instrumented file = " + tot_instrumented_class);

		// Now we're ready to run our instrumented class and need to startup the
		// runtime first:
		final RuntimeData data = new RuntimeData();
		runtime.startup(data);

		String cp = generateClassPath();

		System.out.println("Running tests with the following classpath: \n"+cp);

		// to run the test we use the class ExecutorService to perform the execution of the test in a
		// different, independent thread
		ExecutorService service = Executors.newFixedThreadPool(2);

		TestExecutionTask executor = new TestExecutionTask(cp, testCases);
		FutureTask<List<Result>> task = (FutureTask<List<Result>>) service.submit(executor);
		List<Result> l = task.get(TEST_TIMEOUT, TimeUnit.MILLISECONDS); // run task
		for (Result r : l){
			if (r.getFailures().size()>0)
				System.out.println("The test method "+testCases+" failed: ");
			for (Failure f : r.getFailures()){
				if (f.getTrace().contains("java.lang.NoClassDefFoundError")){
					service.shutdown();
					throw new NoClassDefFoundError("The test case is executed with an incomplete class path: \n"+f.getTrace());
				} else {
					System.out.println(f.getTrace());
					System.out.println("--- End failure info ---");
				}
			}
		}

		service.shutdown();
		service.awaitTermination(5, TimeUnit.MINUTES);

		// At the end of test execution we collect execution data and shutdown
		// the runtime:
		final ExecutionDataStore executionData = new ExecutionDataStore();
		final SessionInfoStore sessionInfos = new SessionInfoStore();
		data.collect(executionData, sessionInfos, false);
		runtime.shutdown();
		

		for (int index = 0; index<jar_to_instrument.size(); index++) {
			// Together with the original class definition we can calculate coverage
			// information:
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
			int n = analyzer.analyzeAll(jar_to_instrument.get(index));
			System.out.println("Number of file with coverage information = " + n);
			/**/
			// Let's dump some metrics and line coverage information:
			for (final IClassCoverage cc : coverageBuilder.getClasses()) {
				if (cc.getName().equals(targetClass)){
					System.out.println("Extracted coverage data for the class " + targetClass);
					results = new JacocoResult(cc);
					results.printResults();
				}
				if (cc.getLineCounter().getCoveredCount()>0) {
					System.out.println("Class " + cc.getName() + ", line coverage: "+cc.getLineCounter().getCoveredCount()+"/"+cc.getLineCounter().getTotalCount());
				}
			}
		}
		// delete instrumented files
		for (File file : this.instrumented_jar){
			if (file.exists()){
				if (file.isDirectory())
					try {
						//TODO GGG alternative?
						FileUtils.deleteDirectory(file);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else 
					file.deleteOnExit();
			}
		}
	}

	public JacocoResult getResults() {
		return results;
	}

	public boolean isTestFile(String FileName){
		if (FileName.contains("test") || FileName.contains("Test") || FileName.contains("TEST"))
			return true;
		else
			return false;
	}
}
