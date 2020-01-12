package ch.uzh.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileUtils {
	
	/**
	 * This method write a file in the specified path with the specified textual context
	 * @param pathFile path (including file name) where to save the file
	 * @param textContent textual content of the file
	 * @throws IOException
	 */
	public static void writeFile(String pathFile, String textContent) throws IOException{
		File fileOutput = new File(pathFile);
		//System.out.println(pathOut);
		fileOutput.createNewFile();
		PrintWriter pwOutput = new PrintWriter(fileOutput);
		pwOutput.println(textContent);
		pwOutput.close();
	}

	/**
	 * This methods read the context of a file and return its content as String 
	 * @param fileName file to read
	 * @return String representing the content of the file
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;

		StringBuffer sb = new StringBuffer();
		char[] buf = new char[1024];
		int len;

		try {
			is = new FileInputStream(fileName);
			isr = new InputStreamReader(is);

			while ((len = isr.read(buf)) > 0)
				sb.append(buf, 0, len);

			return sb.toString();
		} finally {
			if (isr != null)
				isr.close();
		}
	}
}
