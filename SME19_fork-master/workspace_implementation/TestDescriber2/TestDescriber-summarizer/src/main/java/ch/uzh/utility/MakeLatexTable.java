package ch.uzh.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class MakeLatexTable {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Pattern split = Pattern.compile("&");
		
		File toRead = new File("/Users/Gabriele/Desktop/odds.txt");
		FileInputStream fsQ;
		fsQ = new FileInputStream(toRead);
		InputStreamReader isrQ=new InputStreamReader(fsQ);
		BufferedReader brQ=new BufferedReader(isrQ);
		String tmpLine = null;
		
		File pValues = new File("/Users/Gabriele/Desktop/pvalues.txt");
		FileInputStream fsP;
		fsP = new FileInputStream(pValues);
		InputStreamReader isrP=new InputStreamReader(fsP);
		BufferedReader brP=new BufferedReader(isrP);
		String tmpLineP = null;

		
		File output = new File("/Users/Gabriele/Desktop/table.txt");
		output.createNewFile(); 
		PrintWriter pw = new PrintWriter(output);
		
		tmpLine = brQ.readLine();
		pw.println(tmpLine);
		tmpLine = brQ.readLine();
		tmpLineP = brP.readLine();
		tmpLineP = brP.readLine();
		while(tmpLine != null){
			String[] tokensOdds = split.split(tmpLine);
			String[] tokensPValues = split.split(tmpLineP);
			
			String toPrint = tokensOdds[0].replace("_", " ") + " & " + tokensOdds[1] + " & ";
			for(int i=2; i<tokensOdds.length; i++){
				if(i<tokensOdds.length-1){
					if(Double.valueOf(tokensPValues[i])>9){
						toPrint += "\\textbf{" + tokensOdds[i].replace(" ", "") + "} & ";
					} else {
						toPrint +=  tokensOdds[i].replace(" ", "") + " & ";
					}
				} else {
					if(Double.valueOf(tokensPValues[i].replace("\\", ""))>9){
						toPrint += "\\textbf{" + tokensOdds[i].replace(" ", "").replace("\\", "") + "}\\\\";
					} else {
						toPrint +=  tokensOdds[i].replace(" ", "").replace("\\", "") + "\\\\";
					}
				}
			}
			
			pw.println(toPrint);
			
			tmpLine = brQ.readLine();
			tmpLineP = brP.readLine();
		}
		
		
		pw.close();
	}

}
