package nl.tudelft.utils.commandline;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Utility class used for running script in the Terminal
 * 
 * @author Annibale Panichella, Sebastiano Panichella
 *
 */
public class CommandLine {

	/**
	 * This class allows to execute commands in the Terminal
	 * 
	 * @param command
	 *            command to run
	 * @param directory
	 *            directory running the command
	 * @param pathOut
	 *            file used where to store the output messages from the Terminal
	 * @return String representing the output of the command
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String commandLine(String command, String directory,
			String pathOut) throws IOException, InterruptedException {
		String outputCommandLine = "";
		int counterLine = 0;
		// we create a ".bat" file to build the log file of the git system
		// considered
		String fileName = directory + "execute.sh";
		String cmd = " sh " + directory + "execute.sh";
		// String cmd = " ls ";
		if (System.getProperty("os.name").startsWith("Windows")) {
			fileName = directory + "execute.bat";
			cmd =  directory + "execute.bat";
		}
		
		File commands = new File(fileName);
		commands.createNewFile();
		PrintWriter pw = new PrintWriter(commands);
		pw.println(command);
		// pw.println("");
		pw.close();

		File fileOutput = null;
		PrintWriter pwOutput = null;

		Runtime rt = Runtime.getRuntime();

		System.out.println(CommandLine.class.getClass().getCanonicalName() + "- Execute command: " + cmd);
		Process process = rt.exec(cmd);
		String line = null;

		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		while ((line = stdoutReader.readLine()) != null) {
			// System.out.println(line);
			if (line.contains("standalone=\"yes\"?>")) {
				// do nothing..
			} else {
				if (counterLine == 0) {
					outputCommandLine = line;
				} else {
					outputCommandLine = outputCommandLine + "\r" + line;
				}
				counterLine++;
			}
		}

		fileOutput = new File(pathOut);
		// System.out.println(pathOut);
		fileOutput.createNewFile();
		pwOutput = new PrintWriter(fileOutput);
		pwOutput.println(outputCommandLine);
		pwOutput.close();

		// outputCommandLine=readFile(pathOut);

		// System.out.println(outputCommandLine.split("\n")[0]);

		BufferedReader stderrReader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		while ((line = stderrReader.readLine()) != null) {
			System.out.println(line);
		}
		process.waitFor();

		return (outputCommandLine);
	}
}
