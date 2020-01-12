package ch.uzh.parser.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class ClassBean implements Comparable{

	private String name;
	private ArrayList<InstanceVariableBean> instanceVariables;
	private Collection<MethodBean> methods;
	private Collection<String> imports;
	private String textContent;
	private int LOC;
	private String superclass;
	private String belongingPackage;


	public int getLOC() {
		return LOC;
	}

	public void setLOC(int lOC) {
		LOC = lOC;
	}

	public ClassBean() {
		name = null;
		instanceVariables = new ArrayList<InstanceVariableBean>();
		methods = new Vector<MethodBean>();
		setImports(new Vector<String>());
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public ArrayList<InstanceVariableBean> getInstanceVariables() {
		return instanceVariables;
	}

	public void setInstanceVariables(Collection<InstanceVariableBean> pInstanceVariables) {
		instanceVariables = (ArrayList<InstanceVariableBean>) pInstanceVariables;
	}

	public void addInstanceVariables(InstanceVariableBean pInstanceVariable) {
		instanceVariables.add(pInstanceVariable);
	}

	public void removeInstanceVariables(InstanceVariableBean pInstanceVariable) {
		instanceVariables.remove(pInstanceVariable);
	}

	public Collection<MethodBean> getMethods() {
		return methods;
	}

	public void setMethods(Collection<MethodBean> pMethods) {
		methods = pMethods;
	}

	public void addMethod(MethodBean pMethod) {
		methods.add(pMethod);
	}

	public void removeMethod(MethodBean pMethod) {
		methods.remove(pMethod);
	}

	public String toString() {
		return 
				"name = " + name + "\n" +
				"instanceVariables = " + instanceVariables + "\n" +
				"methods = " + methods + "\n";
	}

	public int compareTo(Object pClassBean) {
		return this.getName().compareTo(((ClassBean)pClassBean).getName());
	}

	public Collection<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports=imports;
	}

	/*
	 * return the description of the class and set the imports in the java class specified as parameter 
	 */
	public List<String> extractImports(List<String> textContentExecutedOriginalClass) {
		List<String> imports=new ArrayList<String>();
		boolean startDescription=false;
		String line="";
		for(int i=0;i<textContentExecutedOriginalClass.size();i++){
			line=textContentExecutedOriginalClass.get(i);
			// we add the imports..
			if(line.contains("import ") & startDescription==false){
				imports.add(line);
				//System.out.println("Import: "+line);
			}

		}
		this.imports=imports;
		return(imports);
	}


	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getSuperclass() {
		return superclass;
	}

	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	public String getBelongingPackage() {
		return belongingPackage;
	}

	public void setBelongingPackage(String belongingPackage) {
		this.belongingPackage = belongingPackage;
	}

	public boolean equals(Object arg){
		if(this.getName().equals(((ClassBean)arg).getName()) &&
				this.getBelongingPackage().equals(((ClassBean)arg).getBelongingPackage())){
			return true;
		}

		return false;
	}

}
