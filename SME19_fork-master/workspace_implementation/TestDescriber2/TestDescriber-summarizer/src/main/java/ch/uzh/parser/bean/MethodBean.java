package ch.uzh.parser.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MethodBean implements Comparable {

	private String name;
	private String textContent;
	private String methodComments;
	private String testCaseDescription;
	private Collection<InstanceVariableBean> usedInstanceVariables;
	private Vector<MethodBean> methodCalls;
	private Type returnType;
	private List<SingleVariableDeclaration> parameters;
	private String signature;
	private ClassBean classBeanContenitor ;
	private ClassBean originalClassOfTestCase ;
	private ClassBean classExecutedByTheTestCase ;
	private List<String>linesExecutedOfOriginalClass;
	private double attributesTested;
	private boolean isConstructor;
	private int assertionCounter;//used to count the number of assertion found in the test case
    


	public MethodBean() {
		usedInstanceVariables = new Vector<InstanceVariableBean>();
		methodCalls = new Vector<MethodBean>();
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String pTextContent) {
		textContent = pTextContent;
	}

	public String getMethodComments() {
		return methodComments;
	}


	public String getTestCaseDescription() {
		return testCaseDescription;
	}

	public void setTestCaseDescription(String testCaseDescription) {
		this.testCaseDescription = testCaseDescription;
	}

	
	public void generateMethodCommentsAnalyzingClassTextContent(String classTextContent) {
		String methodComments="",word="",word2="",level="";
		StringTokenizer tokens=null;
		String[] vectTextContentOriginalClass = classTextContent.split("\n");
		if (vectTextContentOriginalClass.length==1)
			vectTextContentOriginalClass = classTextContent.split("\r");
		Vector<MethodBean> calls = null;
		boolean isSimpleMethod = false;//to verify if the method is "get", "set" or "has"
		// we want to make shorter the description for method "get", "set" or "has"
		if(this.name.startsWith("set") | this.name.startsWith("get") | this.name.startsWith("has"))
		{
			isSimpleMethod = true;
		}

		int v=0;
		for( v=0; v< vectTextContentOriginalClass.length ; v++)
		{
			if(vectTextContentOriginalClass[v].contains(this.signature))
			{
				break;
			}
		}
		if (v > 0) { // FIXME :  when the methos has no comment, then v=0 (v denotes the line
			// where the comment ends
			v = v - 1;
				// PART related to the description of the method:
				if(isSimpleMethod)//if the method is a simple method (i.e. it is a get,set, has method)
				{
					if(this.name.startsWith("get"))//if it is a get method..
					{
						word=describeGetMethod(splitIdentifiers(this.name));
						methodComments=methodComments+" "+word;
					}


					if(this.name.startsWith("set"))//if it is a set method..
					{
						word=describeSetMethod(splitIdentifiers(this.name));
						methodComments=methodComments+" "+word;
					}
					//if it is a has method..
					if(this.name.startsWith("has") & this.returnType.toString().contains("boolean"))
					{
						word=describeHasMethod(splitIdentifiers(this.name));
						methodComments=methodComments+" "+word;
					}
					else
						if(!isSimpleMethod){
							word=describeAGenericMethod(splitIdentifiers(this.name));
							methodComments=methodComments+" "+word;
						}
				}

				if(isSimpleMethod==false){
					word=describeAGenericMethod(splitIdentifiers(this.name));
					methodComments=methodComments+" "+word;
				}

				// PART add description of the parameters..
				if(this.parameters.size()>0)
				{
					//Start description of parameters
					level="method";//initialization
					if(this.isConstructor==true){
						level="constructor";
					}
					word2 =word2+ "\n //Parameters description:";
					for(int p=0; p< this.parameters.size(); p++){
						word2 = "\n   // -> parameter "+(p+1)+" is a \""+parameters.get(p).getType()+"\":"+describeParameterOfMethod(splitIdentifiers(parameters.get(p).getName()+""))+" \n";
					}
					methodComments=methodComments+"\n  //"+word2;
				}

				// PART METHODS CALLS:  
				if(this.getMethodCalls().size()>0)
				{
					methodComments = methodComments+"      * METHODS INVOKATIONS in method \""+this.getName()+"\":";
					//case of one invokation
					if(this.getMethodCalls().size()==1)
					{
						//methodComments = methodComments+"\n      * This method called by the test case invokes the method:";
					}
					//case of more than one invokation
					if(this.getMethodCalls().size()>1)
					{
						//methodComments = methodComments+"\n      * This method called by the test case invokes the following methods:";
					}
					calls = (Vector<MethodBean>) this.getMethodCalls();
					for(int c=0; c< this.getMethodCalls().size(); c++){
						if(c!=this.getMethodCalls().size()-1)
						{
							methodComments = methodComments+"\n        * m" +(c+1)+") \"."+ calls.get(c).getName()+"()\"" ; 
						}
						else{
							methodComments = methodComments+"\n        * m" +(c+1)+") \"."+ calls.get(c).getName()+"()\"" ; 
						}
					}
				}
			
		}
		//System.out.println("Method comment: "+methodComments);
		setMethodComments(methodComments+"\n");
	}


	private String describeGetMethod(String[] identifiersWords){
		String description = "";
		String wordsCollapsed="";
		//we start from "1" because we want to discard the "get word"
		for(int i=1;i<identifiersWords.length;i++)
		{
			wordsCollapsed= wordsCollapsed+" "+identifiersWords[i].replaceAll("[0-9]","");
		}
		description = " //  This method gets the "+wordsCollapsed.toLowerCase().replace("[0-9]", "")+" of the instantiated object";
		description = description+" thus, it returns a variable of type \""+this.getReturnType()+"\". \n";

		return description;
	}

	private String describeHasMethod(String[] identifiersWords){
		String description = "";
		String wordsCollapsed="";
		//we start from "1" because we want to descard the "get word"
		for(int i=1;i<identifiersWords.length;i++)
		{
			wordsCollapsed= wordsCollapsed+" "+identifiersWords[i].replaceAll("[0-9]","");
		}
		description = " //  Query to see if the instantiated object has an "+wordsCollapsed.toLowerCase().replace("[0-9]", "")+".";
		description = description+"\n  // returns a boolean flag indicating if it has a "+wordsCollapsed.toLowerCase()+". \n";

		return description;
	}

	private String describeAGenericMethod(String[] identifiersWords){
		String description = "", wordsCollapsed="",toAdd="";
		//we start from "1" because we want to descard the "get word"
		for(int i=0;i<identifiersWords.length;i++)
		{
			wordsCollapsed= wordsCollapsed+" "+identifiersWords[i].replaceAll("[0-9]","");
		}
		if(this.parameters.size()>0)
		{
			toAdd=" using "+parameters.size()+" parameters";
		}
		description = " //  This method "+wordsCollapsed.toLowerCase().replace("[0-9]", "")+toAdd+"";
		description = description+" and returns a variable of type \""+this.getReturnType()+"\". \n";

		return description;
	}

	private String describeSetMethod(String[] identifiersWords){
		String description = "";
		String wordsCollapsed="";
		//we start from "1" because we want to descard the "get word"
		for(int i=1;i<identifiersWords.length;i++)
		{
			wordsCollapsed= wordsCollapsed+" "+identifiersWords[i].replaceAll("[0-9]","");
		}
		description = " //  This method sets the "+wordsCollapsed.toLowerCase().replace("[0-9]", "")+" of the instantiated object. \n";

		return description;
	}

	private String describeParameterOfMethod(String[] identifiersWords){
		String description = "";
		String wordsCollapsed="";
		//we start from "1" because we want to descard the "get word"
		for(int i=0;i<identifiersWords.length;i++)
		{
			wordsCollapsed= wordsCollapsed+" "+identifiersWords[i].replaceAll("[0-9]","");
		}
		description = " it represents the "+wordsCollapsed.toLowerCase().replace("[0-9]", "")+" of the instantiated object. \n";

		return description;
	}

	private String[] splitIdentifiers(String Identifiers){
		String [] words = Identifiers.split("(?<!^)(?=[A-Z])");
		return words;
	}

	public void setMethodComments(String methodComments) {
		this.methodComments = methodComments;
	}

	public Collection<InstanceVariableBean> getUsedInstanceVariables() {
		return usedInstanceVariables;
	}

	public void setUsedInstanceVariables(Collection<InstanceVariableBean> pUsedInstanceVariables) {
		usedInstanceVariables = pUsedInstanceVariables;
	}

	public void addUsedInstanceVariables(InstanceVariableBean pInstanceVariable) {
		usedInstanceVariables.add(pInstanceVariable);
	}

	public void removeUsedInstanceVariables(InstanceVariableBean pInstanceVariable) {
		usedInstanceVariables.remove(pInstanceVariable);
	}

	public Collection<MethodBean> getMethodCalls() {
		return methodCalls;
	}

	public void setMethodCalls(Collection<MethodBean> definedInvocations) {
		methodCalls = (Vector<MethodBean>) definedInvocations;
	}

	public void addMethodCalls(MethodBean pMethodCall) {
		methodCalls.add(pMethodCall);
	}

	public void removeMethodCalls(MethodBean pMethodCall) {
		methodCalls.remove(pMethodCall);
	}

	public String toString() {

		String string =
				"(" + name + "|" +
						(textContent.length() > 10 ? textContent.replace("\n", " ").replace("\t", "").substring(0, 10).concat("...") : "") + "|";

		for (InstanceVariableBean usedInstanceVariable : usedInstanceVariables)
			string += usedInstanceVariable.getName() + ",";
		string = string.substring(0, string.length() - 1);
		string += "|";

		for (MethodBean methodCall : methodCalls)
			string += methodCall.getName() + ",";
		string = string.substring(0, string.length() - 1);
		string += ")";

		return string;

	}

	public int compareTo(Object o) {
		return this.getName().compareTo(((MethodBean)o).getName());
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public List<SingleVariableDeclaration> getParameters() {
		return parameters;
	}

	public void setParameters(List<SingleVariableDeclaration> parameters) {
		this.parameters = parameters;
	}

	public boolean equals(Object arg){
		return(this.getName().equals(((MethodBean)arg).getName()));
	}

	public void setSignature(String methodSignature) {
		this.signature=methodSignature;
	}

	public String getSignature() {
		return signature;
	}

	public ClassBean getClassBeanContenitor() {
		return classBeanContenitor;
	}

	public void setClassBeanContenitor(ClassBean classBeanContenitor) {
		this.classBeanContenitor = classBeanContenitor;
	}

	public ClassBean getOriginalClassOfTestCase() {
		return originalClassOfTestCase;
	}

	public void setOriginalClassOfTestCase(ClassBean originalClassOfTestCase) {
		this.originalClassOfTestCase = originalClassOfTestCase;
	}

	public ClassBean getClassExecutedByTheTestCase() {
		return classExecutedByTheTestCase;
	}

	public void setClassExecutedByTheTestCase(ClassBean classExecutedByTheTestCase) {
		this.classExecutedByTheTestCase = classExecutedByTheTestCase;
	}

	public List<String> getLinesExecutedOfOriginalClass() {
		return linesExecutedOfOriginalClass;
	}

	public void setLinesExecutedOfOriginalClass(
			List<String> linesExecutedOfOriginalClass) {
		this.linesExecutedOfOriginalClass = linesExecutedOfOriginalClass;
	}

	public double getAttributesTested() {
		return attributesTested;
	}

	public void setAttributesTested(double attributesTested) {
		this.attributesTested = attributesTested;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public int getCounterAssertions() {
		return assertionCounter;
	}

	public void setCounterAssertions(int counterAssertion) {
		this.assertionCounter = counterAssertion;
	}

	
}
