package nl.tudelft.identifiers;

public class IdentifierExpansion {
	
	public static String expand (String word){
		String expanded = word;
		
		switch (word){
		case "opt" : expanded = "option"; break;
		case "arg" : expanded = "argument"; break;
		case "args" : expanded = "arguments"; break;
		case "descr" : expanded = "description"; break;
		case "sep" : expanded = "separator"; break;
		case "abs" : expanded = "absolute value"; break;
		case "int" : expanded = "integer"; break;
		case "coefs" : expanded = "coefficients"; break;
		case "poly" : expanded = "polynomial"; break;
		case "func" : expanded = "function"; break;
		}
		
		return expanded;
		
	}

}
