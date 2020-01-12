package ch.uzh.parser.descriptors;

import java.util.List;

import nl.tudelft.identifiers.IdentifierExpansion;
import nl.tudelft.language.SpellCorrector;
import ch.uzh.parser.bean.ClassBean;
import ch.uzh.parser.bean.InstanceVariableBean;

public class ClassDescriptor extends Descriptor{

	public static String generateClassComment(ClassBean pClass) {
		String description = "The main class under test is "
				+ pClass.getName() + ".\n It describes a single ";
		
		String splittedClassName = "";
		String[] words =splitAndExpandIdentifier(pClass.getName().toString());
		for (String word : words){
			splittedClassName = splittedClassName + " " + word;
		}
		description = description + " " + splittedClassName + " and maintains information regarding: \n";
		List<InstanceVariableBean> list = pClass.getInstanceVariables();
		for (InstanceVariableBean attribute : list) {
			String attributeName = attribute.getName();
			if (!attributeName.toLowerCase().contains("serial")
					&& !attributeName.toLowerCase().contains("version")) {
				if (attribute.getType().equals("boolean")) {
					description = description + " - whether it has ";
				} else {
					description = description + " - the ";
				}

				String[] split = splitAndExpandIdentifier(attribute.getName());
				if (split.length == 1 && !attribute.getType().equals("boolean")) {
					description = description + " " + split[0];
					if (!split[0].equalsIgnoreCase(pClass.getName())) {
						description = description + " of the "
								+ splittedClassName;
					}
				} else {
					for (String word : split) {
						description = description + " " + word;
					}
				}
				description = description + ";\n";
			}
		}

		description = SpellCorrector.correctSentences(description);
		description = "/** \n * "+description.replaceAll("\n", "\n * ");
		description = description + "*/\n";
		return description;
	}
}
