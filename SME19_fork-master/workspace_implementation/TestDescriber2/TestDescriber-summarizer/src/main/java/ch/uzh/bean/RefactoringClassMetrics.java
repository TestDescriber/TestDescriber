package ch.uzh.bean;

import java.util.regex.Pattern;

public class RefactoringClassMetrics implements Comparable{

	private String refactoringName;
	private String sourceClass;
	private String version;
	private int LOC;
	private int WMC;
	private int DIT;
	private int NOC;
	private int RFC;
	private int CBO;
	private int LCOM;
	private int NOA;
	private int NOO;
	private double CCBC;
	private double C3;
	
	public String getRefactoringName() {
		return refactoringName;
	}
	public void setRefactoringName(String refactoringName) {
		this.refactoringName = refactoringName;
	}
	public String getSourceClass() {
		return sourceClass;
	}
	public void setSourceClass(String sourceClass) {
		this.sourceClass = sourceClass;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getLOC() {
		return LOC;
	}
	public void setLOC(int lOC) {
		LOC = lOC;
	}
	public int getWMC() {
		return WMC;
	}
	public void setWMC(int wMC) {
		WMC = wMC;
	}
	public int getDIT() {
		return DIT;
	}
	public void setDIT(int dIT) {
		DIT = dIT;
	}
	public int getNOC() {
		return NOC;
	}
	public void setNOC(int nOC) {
		NOC = nOC;
	}
	public int getRFC() {
		return RFC;
	}
	public void setRFC(int rFC) {
		RFC = rFC;
	}
	public int getCBO() {
		return CBO;
	}
	public void setCBO(int cBO) {
		CBO = cBO;
	}
	public int getLCOM() {
		return LCOM;
	}
	public void setLCOM(int lCOM) {
		LCOM = lCOM;
	}
	public int getNOA() {
		return NOA;
	}
	public void setNOA(int nOA) {
		NOA = nOA;
	}
	public int getNOO() {
		return NOO;
	}
	public void setNOO(int nOO) {
		NOO = nOO;
	}
	public double getCCBC() {
		return CCBC;
	}
	public void setCCBC(double cCBC) {
		CCBC = cCBC;
	}
	public double getC3() {
		return C3;
	}
	public void setC3(double c3) {
		C3 = c3;
	}
	
	public int compareTo(Object o) {
		return this.refactoringName.compareTo(((RefactoringClassMetrics)o).refactoringName);
	}
	
	@Override
	public boolean equals(Object o) {
		
		Pattern dot = Pattern.compile("\\.");
		
		String[] tokensThis = dot.split(this.sourceClass);
		String thisName = null;
		if (tokensThis.length>1)
			thisName = tokensThis[tokensThis.length-1];
		else
			thisName = tokensThis[0];
		
		String[] tokensObject = dot.split(((RefactoringClassMetrics)o).sourceClass);
		String objectName = null;
		
		if (tokensObject.length>1)
			objectName = tokensObject[tokensObject.length-1];
		else
			objectName = tokensObject[0];
		
		
		if(thisName.equals(objectName) &&
				this.version.equals(((RefactoringClassMetrics)o).version))
				return true;
		
		return false;
	}
	
	//LOCbefore;WMCbefore;DITbefore;NOCbefore;RFCbefore;CBObefore;LCOMbefore;NOAbefore;NOObefore;CCBCbefore;C3before;
	public String toString(){
		return (this.getLOC() + ";" + this.getWMC() + ";" + this.getDIT() + ";" + this.getNOC() + ";" + this.getRFC() + ";" + this.getCBO() + ";" +
	this.getLCOM() + ";" + this.getNOA() + ";" + this.getNOO() + ";" + this.getCCBC() + ";" + (1-this.getC3()));
	}
	
}
