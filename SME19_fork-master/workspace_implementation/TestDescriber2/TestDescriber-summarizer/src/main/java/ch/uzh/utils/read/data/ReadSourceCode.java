package ch.uzh.utils.read.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ch.uzh.parser.ClassParser;
import ch.uzh.parser.ClassVisitor;
import ch.uzh.parser.bean.ClassBean;

public class ReadSourceCode {

	public static Vector<ClassBean> readSourceCode(File path, Vector<ClassBean> classes) throws IOException{
				
		if (path.isDirectory() && !path.getName().equals(".DS_Store") && !path.getName().equals("bin")){
			for (File f : path.listFiles()){
					readSourceCode(f, classes);
			}
		} else {
			if(path.getName().endsWith(".java")){
			String source = readFile(path.getAbsolutePath());
			//Get the package
			String regex = "package .*;";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(source);
			
			String belongingPackage ="";
			
			if(matcher.find()){
				belongingPackage = matcher.group();
			}
			
			belongingPackage = belongingPackage.replace("package ", "");
			belongingPackage = belongingPackage.replace(";", "");
			
			regex = "import .*;";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(source);
			
			Vector<String> imports = new Vector<String>();
			
			while(matcher.find()){
				String tmpImport = matcher.group();
				if(!tmpImport.startsWith("java."))
					imports.add(tmpImport);
			}
						
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(source.toCharArray());
			
			CompilationUnit unit = null;//(CompilationUnit) parser.createAST(null);
			
			Collection<TypeDeclaration> classNodes = new Vector<TypeDeclaration>();		
			unit.accept(new ClassVisitor(classNodes));
			
			Vector<ClassBean> classBeans = new Vector<ClassBean>();
			for (TypeDeclaration classNode : classNodes){
				classBeans.add(ClassParser.parse(classNode, belongingPackage, imports));
			}
			
			if (classBeans.size()>0)
				classes.add(((Vector<ClassBean>)classBeans).get(0));
			
			}
		}
		
		return classes;
		
	}
	
	public static String readFile(String nomeFile) throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;

		StringBuffer sb = new StringBuffer();
		char[] buf = new char[1024];
		int len;

		try {
			is = new FileInputStream(nomeFile);
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
