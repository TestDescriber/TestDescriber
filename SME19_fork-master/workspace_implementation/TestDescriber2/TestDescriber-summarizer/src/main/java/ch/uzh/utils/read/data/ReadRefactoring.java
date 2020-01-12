package ch.uzh.utils.read.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Pattern;

import ch.uzh.bean.Refactoring;

public class ReadRefactoring {

	
	public static Vector<Refactoring> readRefactoring(String filePath, String systemName) throws IOException{
		
		Vector<Refactoring> result = new Vector<Refactoring>();
		
		File inputFile = new File(filePath);
		FileInputStream fsQ;
		fsQ = new FileInputStream(inputFile);
		InputStreamReader isrQ=new InputStreamReader(fsQ);
		BufferedReader brQ=new BufferedReader(isrQ);
		String tmpLine = null;
		
		Pattern comma = Pattern.compile(";");
		Pattern space = Pattern.compile(" ");
		Pattern slash = Pattern.compile("/");
		
		String[] filePathTokens = slash.split(filePath);
		String systemVersion = space.split(filePathTokens[filePathTokens.length-1])[0].replace(systemName, "");
		
		tmpLine = brQ.readLine();
		tmpLine = brQ.readLine();
		
		while(tmpLine != null){
			String[] lineTokens = comma.split(tmpLine);
			String refactoringType = lineTokens[2];
			String sourceClass = lineTokens[3].replace("%", "");
			if(sourceClass.startsWith("org.argouml")){
				Refactoring refactoring = new Refactoring();
				refactoring.setSourceClass(sourceClass);
				refactoring.setSystemVersion(systemVersion);
				refactoring.setType(refactoringType);
				result.add(refactoring);
			}
			
			tmpLine = brQ.readLine();
		}
		
		return result;
		
	}
	
	
}
