package nl.tudelft.utils.commandline;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parse the content of a compiled JUnit test case and extracts the list of 
 * test methods inside 
 * 
 * @author Annibale Panichella
 *
 */
public class TestCaseParser {
	
	/**
	 * Method to extract test methods from a compiled JUnit test case. In particular, it uses the 'javap' 
	 * command to derive the API from the compiled class. Then, it looks for test methods, whose
	 * signature start with 'public void test'.
	 * @param test_bin_directory directory containing the test case to parse
	 * @param test_case compiled JUnit test case (*.class) to parse
	 * @return list of test methods name inside the first input parameter
	 */
	public static List<String> findTestMethods(String test_bin_directory, String test_case){
		
		String temp = test_case;
		while (temp.contains(".")){
			temp = temp.replace(".", "/");
		}
		String command = "javap "+test_bin_directory+"/"+temp+".class";
		if (System.getProperty("os.name").startsWith("Windows")) {
			command = "javap "+test_bin_directory+temp+".class";
		}

		String result=null;
		try {
			result = CommandLine.commandLine(command, "parser", "temp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> test_methods = new ArrayList<String>();
		if (result!=null){
			String[] list = result.split("[{|;]");
			for (int i=0; i<list.length; i++){
				String line = list[i];
				if (line.contains("public void test")){
					line = line.substring(line.indexOf("test"),line.length());
					line = line.substring(0, line.indexOf("("));
					if(!test_methods.contains(line)) {
						test_methods.add(line);
					}
				}
			}
		}
		
		//System.out.println("#### Test Cases ####");
		//for (String method : test_methods){
		//	System.out.println(method);
		//}
		return test_methods;
	}

	public static List<String> findTestMethods(String test_bin_directory, String test_case, List<String> prefixTestMethods, List<String> nameTestMethods){
		
		String temp = test_case;
		while (temp.contains(".")){
			temp = temp.replace(".", "/");
		}

		String command = "javap "+test_bin_directory+"/"+temp+".class";
		if (System.getProperty("os.name").startsWith("Windows")) {
			command = "javap "+test_bin_directory+temp+".class";
		}
		System.out.println(command);
		
		String result=null;
		try {
			result = CommandLine.commandLine(command, "parser", "temp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> test_methods = new ArrayList<String>();
		if (result!=null){
			String[] list = result.split("[{|;]");
			for (int i=0; i<list.length; i++){
				String line = list[i];
				int idxPrf = 0;
				for(String prf : prefixTestMethods) {
					if (line.contains(prf)){
						line = line.substring(line.indexOf(nameTestMethods.get(idxPrf)),line.length());
						line = line.substring(0, line.indexOf("("));
						if(!test_methods.contains(line)) {
							test_methods.add(line);
						}
					}
					idxPrf ++;
				}
			}
		}
		
		System.out.println("#### Test Cases ####");
		for (String method : test_methods){
			System.out.println(method);
		}
		return test_methods;
	}	

}
