package ch.uzh.bean;

public class Refactoring {

	private String type;
	private String sourceClass;
	private String systemVersion;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSourceClass() {
		return sourceClass;
	}
	public void setSourceClass(String sourceClass) {
		this.sourceClass = sourceClass;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	
	public String toString(){
		return this.type + " " + this.sourceClass + " " + this.systemVersion;
	}
	
}
