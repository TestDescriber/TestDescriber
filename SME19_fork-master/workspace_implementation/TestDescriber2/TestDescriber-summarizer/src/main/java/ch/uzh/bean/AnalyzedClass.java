package ch.uzh.bean;

public class AnalyzedClass {
	
	public String version;
	public String classPath;
	public int LOC;
	public int WMC;
	public int DIT;
	public int NOC;
	public int RFC;
	public int CBO;
	public int LCOM;
	public int NOM;
	public int NOA;
	public int NOO;
	public double CCBC;
	public double C3;
	public boolean BLOB;
	public boolean CDSBP;
	public boolean ComplexClass;
	public boolean LazyClass;
	public boolean LongMethod;
	public boolean LongParamenterList;
	public boolean MessageChain;
	public boolean RPB;
	public boolean SpaghettiCode;
	public boolean SpeculativeGenerality;
	public boolean InappropriateIntimacy;
	public boolean FeatureEnvy;
	
	
	public String toString(){
		return (version+";"+classPath+";"+LOC+";"+WMC+";"+DIT+";"+NOC+";"+RFC+";"+CBO+";"+LCOM+";"+NOM+";"+NOA+";"+NOO+";"+CCBC+";"+C3+";"+BLOB+";"+
	CDSBP+";"+ComplexClass+";"+LazyClass+";"+LongMethod+";"+LongParamenterList+";"+MessageChain+";"+RPB+";"+SpaghettiCode+";"+SpeculativeGenerality+";"+
				InappropriateIntimacy+";"+FeatureEnvy);
	}
	
	public boolean equals(Object arg){
		return this.classPath.equals(((AnalyzedClass)arg).classPath);
	}
	
}
