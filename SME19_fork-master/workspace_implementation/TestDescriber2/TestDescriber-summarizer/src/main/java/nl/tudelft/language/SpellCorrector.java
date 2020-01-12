package nl.tudelft.language;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

public class SpellCorrector {

	public static String correctSentences(String text) {
		try {
			// create object of class JLanguageTool
			JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
			// Split text in line
			String[] split = text.split("\n");
			// for each line in 'split'
			for (int i = 0; i < split.length; i++) {
				String line = split[i].replaceAll("[ ]+", " "); //replace multiple space
				String lineCopy = line.toString();
				List<RuleMatch> matches = langTool.check(line);
				// for each error we correct replace the misspelled/wrong word
				// with the correct one
				for (RuleMatch match : matches) {
					if (true){
						String substring = line.substring(match.getFromPos());
						StringTokenizer tokenizer = new StringTokenizer(substring);
						if (tokenizer.hasMoreTokens()) {
							String wrongWord = tokenizer.nextToken();
							List<String> suggestions = match.getSuggestedReplacements();
							//System.out.println("Potential error at line " +
							//		match.getLine() + ", column " +
							//		match.getColumn() + ": " + match.getMessage()+
							//		"Wrong word "+wrongWord);
							if (!wrongWord.contains("@")
									&& !wrongWord.contains("*")
									&& !wrongWord.contains("//")
									&& !wrongWord.contains("''")
									&& !wrongWord.contains("\"")
									&& !wrongWord.contains("-")
									&& suggestions.size() > 0) {
								String firstPart = lineCopy.substring(0, Math.max(match.getFromPos()-1,0));
								String secondPart = lineCopy.substring(Math.max(match.getFromPos()-1,0), lineCopy.length());
								secondPart = secondPart.replaceFirst(wrongWord, suggestions.get(0));
								lineCopy = firstPart + secondPart;
							}
						}
					}
				}
				if (lineCopy.trim()=="//")
					lineCopy="";
				split[i] = lineCopy;
			}

			// rebuild the original string with corrections applied
			text = "";
			for (String line : split) {
				text = text + line + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = "* This test case tests the class \"Option\". * Specifically, such class  describes a single command-line option.  It maintains information regarding the short-name of the option, the long-name,  if any exists, a flag indicating if an argument is required for this option, and a self-documenting description of the option. An Option is not created independantly, but is create through an instance of {@link Options}. \n It have been reportid. \n ";
		String text2 = " It declare a object of the class \"Rational\" equals to rational1";
		System.out.println(correctSentences(text2));
	}

}
