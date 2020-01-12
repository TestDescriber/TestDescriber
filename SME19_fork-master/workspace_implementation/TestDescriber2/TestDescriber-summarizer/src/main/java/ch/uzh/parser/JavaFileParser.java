package ch.uzh.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ch.uzh.parser.bean.ClassBean;
import ch.uzh.utility.FileUtils;

public class JavaFileParser {
	
	private static final String regex_package = "package .*;";
	
	public static Vector<ClassBean> parseJavaClasses(List<String> classesFiles, String sourceFolder) throws IOException{
		Pattern pattern = Pattern.compile(regex_package);
		Vector<ClassBean> classBeans = new Vector<ClassBean>();

		for (int i=0; i<classesFiles.size(); i++){
			String classeFile=classesFiles.get(i);
			String source = FileUtils.readFile(sourceFolder + classeFile);
			source.replace("@Test", "");
			//System.out.println(source);
			//Get the package
			Matcher matcher = pattern.matcher(source);

			String belongingPackage = "";

			while (matcher.find()){
				belongingPackage = matcher.group();
			}

			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(source.toCharArray());

			CompilationUnit unit = (CompilationUnit) parser.createAST(null);
			Collection<TypeDeclaration> classNodes = new Vector<TypeDeclaration>();
			unit.accept(new ClassVisitor(classNodes));
			
			for (TypeDeclaration classNode : classNodes){
				ClassBean classe = ClassParser.parse(classNode, belongingPackage, null);
				classe.setTextContent(source);
				classBeans.add(classe);
			}
		}	
		return(classBeans);
	}
	
	public static ClassBean parseJavaClass(String source) throws IOException{
		String regex=null,belongingPackage=null;
		Pattern pattern =null;
		Matcher matcher = null;
		ASTParser parser =null;
		CompilationUnit unit = null;
		Collection<TypeDeclaration> classNodes =null;
		Vector<ClassBean> classBeans =null;
		ClassBean classe=null;

		//System.out.println(source);
		//Get the package
		regex = "package .*;";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(source);

		belongingPackage ="";

		while (matcher.find()){
			belongingPackage = matcher.group();
		}

		parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toCharArray());

		unit = (CompilationUnit) parser.createAST(null);
		classNodes = new Vector<TypeDeclaration>();
		unit.accept(new ClassVisitor(classNodes));
		classBeans = new Vector<ClassBean>();
		for (TypeDeclaration classNode : classNodes){
			classe = ClassParser.parse(classNode, belongingPackage, null);
			classe.setTextContent(source);
			classBeans.add(classe);
		}

		//System.out.println(classe.getTextContent());

		//printClassContent(classe);

		return(classe);
	}
}
