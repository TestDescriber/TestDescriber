package nl.tudelft.javaparser;

import java.util.List;

import com.github.javaparser.ast.Node;

public interface Bean {
	
	public String getName();

	public List<String> getParameters();

	public List<String> getParameterValue();
	
	public Node getBodyDeclaration();

}
