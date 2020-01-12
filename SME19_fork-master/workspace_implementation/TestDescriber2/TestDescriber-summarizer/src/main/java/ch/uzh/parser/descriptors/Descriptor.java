package ch.uzh.parser.descriptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.en.EnglishTagger;

import ch.uzh.parser.bean.MethodBean;
import nl.tudelft.identifiers.IdentifierExpansion;
import nl.tudelft.javaparser.Bean;

public class Descriptor {
	
	public static String[] splitAndExpandIdentifier(String identifiers) {
		String[] words = identifiers
				.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
		for (int i = 0; i < words.length; i++) {
			String word = words[i].toLowerCase();
			words[i] = IdentifierExpansion.expand(word);
		}
		return words;
	}
	
	protected static String getIndentation(String line){
		String indentation=" ";
		StringTokenizer tokenizer = new StringTokenizer(line);
		if (tokenizer.hasMoreTokens()){
			String firstToken = tokenizer.nextToken();
			indentation = indentation + line.substring(0, line.indexOf(firstToken));
		}
		return indentation;
	}
	
	public static boolean containsVerb(String identifiers){
		boolean contains = false;
		EnglishTagger  tagger = new EnglishTagger();
		StringTokenizer tokenizer = new StringTokenizer(identifiers);
		List<String> words = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()){
			words.add(tokenizer.nextToken());
		}
		List<AnalyzedTokenReadings> taggedWord;
		try {
			taggedWord = tagger.tag(words);
			for (AnalyzedTokenReadings tagged : taggedWord){
				String tag = tagged.getReadings().get(0).getPOSTag();
				String token = tagged.getReadings().get(0).getToken();
				//System.out.println(token+" ---> "+tag);
				if (tag!=null){
					if (tag.equals("VBZ") || tag.equals("VB") ){
						contains = true;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contains;
	}
	
	public static boolean containsPassiveVoice(String identifiers){
		boolean contains = false;
		EnglishTagger  tagger = new EnglishTagger();
		StringTokenizer tokenizer = new StringTokenizer(identifiers);
		List<String> words = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()){
			words.add(tokenizer.nextToken());
		}
		List<AnalyzedTokenReadings> taggedWord;
		try {
			taggedWord = tagger.tag(words);
			for (AnalyzedTokenReadings tagged : taggedWord){
				String tag = tagged.getReadings().get(0).getPOSTag();
				String token = tagged.getReadings().get(0).getToken();
				//System.out.println(token+" ---> "+tag);
				if (tag!=null){
					if (tag.equals("VBZ")){
						contains = true;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contains;
	}
	
	public static boolean containsActiveVoice(String identifiers){
		boolean contains = false;
		EnglishTagger  tagger = new EnglishTagger();
		StringTokenizer tokenizer = new StringTokenizer(identifiers);
		List<String> words = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()){
			words.add(tokenizer.nextToken());
		}
		List<AnalyzedTokenReadings> taggedWord;
		try {
			taggedWord = tagger.tag(words);
			for (AnalyzedTokenReadings tagged : taggedWord){
				String tag = tagged.getReadings().get(0).getPOSTag();
				String token = tagged.getReadings().get(0).getToken();
				//System.out.println(token+" ---> "+tag);
				if (tag!=null){
					if (tag.equals("VBP")){
						contains = true;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contains;
	}
	
	/**
	 * This method looks for a MethodBean in a given list
	 * @param methods list of MethodBeans where looking for a given methodBean
	 * @param bean MethodBean we are looking for
	 * @return the required MethodBean; null if it is not found
	 */
	public static MethodBean getMethodFromList(List<MethodBean> methods, Bean bean){
		MethodBean exactMethod = null;
		for (int i = 0; i < methods.size(); i++) {
			MethodBean method = methods.get(i);
			// when we find the constructor..
			// if the called constructor and the actual constructor have the same name
			if (method.getName().equals(bean.getName())) { 
				System.out.println(method.getName()+ " -- "+ bean.getName());
				// if they have the same number of parameters
				if (method.getParameters().size() == bean.getParameters().size()) {
					System.out.println(method.getParameters().size()+ " -- "+ bean.getParameters().size());
					// if the parameters types are identical
					boolean sameParameters = true;
					for (int nvar=0; nvar<method.getParameters().size(); nvar++){
						SingleVariableDeclaration var = method.getParameters().get(nvar);
						// case in which the parameter is passed as an int variable compabile with the long type...
						if (
								(var.getType().toString().equals("long") || var.getType().toString().equals("int")) 
								&&
								(bean.getParameters().get(nvar).equals("long") || bean.getParameters().get(nvar).equals("int"))
							) {
							sameParameters = true;
							break;// we found the exact constructor...
						   }
						// case in which the parameter is not an actual value...
						if (bean.getParameters().get(nvar).equals("NameExpr")) {
							sameParameters = true;
							break;// we found the exact constructor...
						   }
						
						else {
							   if (!var.getType().toString().equals(bean.getParameters().get(nvar)) 
						
								&& !var.getType().toString().contains("Object")){
							   sameParameters = sameParameters && false;
						      }
						 }
						System.out.println(var.getType().toString()+ " -$- "+ bean.getParameters().get(nvar)+ " or "+bean.getParameters().get(nvar));
					}
					System.out.println("Same signature? "+sameParameters);
					if (sameParameters){
						exactMethod  = method;
						System.out.println("we found the exact constructor");
						break;// we found the exact constructor...
					}
				}
			}
		}
		return exactMethod;
	}

}
