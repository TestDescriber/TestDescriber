package ch.uzh.parser.testing.summarization;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Utility class that reads in a source file and creates an abstract syntax tree (AST) from it. 
 * Used to help in step 3 of Testdescriber Tool for identifying code parts.
 * 
 * @author devuser
 *
 */
public class SrcASTParser {
	private String fullFilepath;
	private File file;
	char[] inFile;
	CompilationUnit unit;
	
	public SrcASTParser(String fullFilepath) {
		this.fullFilepath = fullFilepath;
		this.inFile = readSrcFile();
	}
	
	public char[] readSrcFile() {
		file = new File(fullFilepath);
		// use character based file stream because parser.setSource(...) accepts char[] array.
		Reader reader;
		inFile = new char[(int) file.length()];
		int c;
		try {
			reader = new FileReader(fullFilepath);
			 for(int i = 0; (c = reader.read()) != -1; i++) {
				 inFile[i] = (char) c;
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return inFile;
	}
	
	public SrcASTParser createCompilationUnit(boolean resolveBindings) {
		// different and newer standards are available however 'JLS4' was already used in project 
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(resolveBindings);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(inFile);
		this.unit = ((CompilationUnit) parser.createAST(null));
		
		return this;
	}
	
	public CompilationUnit getUnit() {
		return this.unit;
	}
}
