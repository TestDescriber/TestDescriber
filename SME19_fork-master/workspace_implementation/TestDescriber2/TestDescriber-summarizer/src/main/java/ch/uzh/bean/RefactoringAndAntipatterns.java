package ch.uzh.bean;

public class RefactoringAndAntipatterns {

	
	private String type;
	private int blobRefactored;
	private int blobRemoved;
	private int CDSBPRefactored;
	private int CDSBPRemoved;
	private int ComplexClassRefactored;
	private int ComplexClassRemoved;
	private int LazyClassRefactored;
	private int LazyClassRemoved;
	private int LongMethodRefactored;
	private int LongMethodRemoved;
	private int LongParameterListRefactored;
	private int LongParameterListRemoved;
	private int MessageChainRefactored;
	private int MessageChainRemoved;
	private int RPBRefactored;
	private int RPBRemoved;
	private int SpaghettiCodeRefactored;
	private int SpaghettiCodeRemoved;
	private int SpeculativeGeneralityRefactored;
	private int SpeculativeGeneralityRemoved;
	private int InappropriateIntimacyRefactored;
	private int InappropriateIntimacyRemoved;
	private int FeatureEnvyRefactored;
	private int FeatureEnvyRemoved;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getBlobRefactored() {
		return blobRefactored;
	}
	public void setBlobRefactored(int refactored) {
		this.blobRefactored = refactored;
	}
	public int getBlobRemoved() {
		return blobRemoved;
	}
	public void setBlobRemoved(int removed) {
		this.blobRemoved = removed;
	}
	
	public boolean equals(Object arg){
		return this.getType().equals(((RefactoringAndAntipatterns)arg).getType());
	}
	public int getCDSBPRefactored() {
		return CDSBPRefactored;
	}
	public void setCDSBPRefactored(int cDSBPRefactored) {
		CDSBPRefactored = cDSBPRefactored;
	}
	public int getCDSBPRemoved() {
		return CDSBPRemoved;
	}
	public void setCDSBPRemoved(int cDSBPRemoved) {
		CDSBPRemoved = cDSBPRemoved;
	}
	public int getComplexClassRefactored() {
		return ComplexClassRefactored;
	}
	public void setComplexClassRefactored(int complexClassRefactored) {
		ComplexClassRefactored = complexClassRefactored;
	}
	public int getComplexClassRemoved() {
		return ComplexClassRemoved;
	}
	public void setComplexClassRemoved(int complexClassRemoved) {
		ComplexClassRemoved = complexClassRemoved;
	}
	public int getLazyClassRefactored() {
		return LazyClassRefactored;
	}
	public void setLazyClassRefactored(int lazyClassRefactored) {
		LazyClassRefactored = lazyClassRefactored;
	}
	public int getLazyClassRemoved() {
		return LazyClassRemoved;
	}
	public void setLazyClassRemoved(int lazyClassRemoved) {
		LazyClassRemoved = lazyClassRemoved;
	}
	public int getLongMethodRefactored() {
		return LongMethodRefactored;
	}
	public void setLongMethodRefactored(int longMethodRefactored) {
		LongMethodRefactored = longMethodRefactored;
	}
	public int getLongMethodRemoved() {
		return LongMethodRemoved;
	}
	public void setLongMethodRemoved(int longMethodRemoved) {
		LongMethodRemoved = longMethodRemoved;
	}
	public int getLongParameterListRefactored() {
		return LongParameterListRefactored;
	}
	public void setLongParameterListRefactored(int longParameterListRefactored) {
		LongParameterListRefactored = longParameterListRefactored;
	}
	public int getLongParameterListRemoved() {
		return LongParameterListRemoved;
	}
	public void setLongParameterListRemoved(int longParameterListRemoved) {
		LongParameterListRemoved = longParameterListRemoved;
	}
	public int getMessageChainRefactored() {
		return MessageChainRefactored;
	}
	public void setMessageChainRefactored(int messageChainRefactored) {
		MessageChainRefactored = messageChainRefactored;
	}
	public int getMessageChainRemoved() {
		return MessageChainRemoved;
	}
	public void setMessageChainRemoved(int messageChainRemoved) {
		MessageChainRemoved = messageChainRemoved;
	}
	public int getRPBRefactored() {
		return RPBRefactored;
	}
	public void setRPBRefactored(int rPBRefactored) {
		RPBRefactored = rPBRefactored;
	}
	public int getRPBRemoved() {
		return RPBRemoved;
	}
	public void setRPBRemoved(int rPBRemoved) {
		RPBRemoved = rPBRemoved;
	}
	public int getSpaghettiCodeRefactored() {
		return SpaghettiCodeRefactored;
	}
	public void setSpaghettiCodeRefactored(int spaghettiCodeRefactored) {
		SpaghettiCodeRefactored = spaghettiCodeRefactored;
	}
	public int getSpaghettiCodeRemoved() {
		return SpaghettiCodeRemoved;
	}
	public void setSpaghettiCodeRemoved(int spaghettiCodeRemoved) {
		SpaghettiCodeRemoved = spaghettiCodeRemoved;
	}
	public int getSpeculativeGeneralityRefactored() {
		return SpeculativeGeneralityRefactored;
	}
	public void setSpeculativeGeneralityRefactored(
			int speculativeGeneralityRefactored) {
		SpeculativeGeneralityRefactored = speculativeGeneralityRefactored;
	}
	public int getSpeculativeGeneralityRemoved() {
		return SpeculativeGeneralityRemoved;
	}
	public void setSpeculativeGeneralityRemoved(int speculativeGeneralityRemoved) {
		SpeculativeGeneralityRemoved = speculativeGeneralityRemoved;
	}
	public int getInappropriateIntimacyRefactored() {
		return InappropriateIntimacyRefactored;
	}
	public void setInappropriateIntimacyRefactored(
			int inappropriateIntimacyRefactored) {
		InappropriateIntimacyRefactored = inappropriateIntimacyRefactored;
	}
	public int getInappropriateIntimacyRemoved() {
		return InappropriateIntimacyRemoved;
	}
	public void setInappropriateIntimacyRemoved(int inappropriateIntimacyRemoved) {
		InappropriateIntimacyRemoved = inappropriateIntimacyRemoved;
	}
	public int getFeatureEnvyRefactored() {
		return FeatureEnvyRefactored;
	}
	public void setFeatureEnvyRefactored(int featureEnvyRefactored) {
		FeatureEnvyRefactored = featureEnvyRefactored;
	}
	public int getFeatureEnvyRemoved() {
		return FeatureEnvyRemoved;
	}
	public void setFeatureEnvyRemoved(int featureEnvyRemoved) {
		FeatureEnvyRemoved = featureEnvyRemoved;
	}
	
	
	public String toString(){
		return this.getType() + ";" + this.getBlobRefactored() + ";" + this.getCDSBPRefactored() + ";" + this.getComplexClassRefactored() + ";" + this.getLazyClassRefactored() + ";" +
				this.getLongMethodRefactored() + ";" + this.getLongParameterListRefactored() + ";" + this.getMessageChainRefactored() + ";" +
				this.getRPBRefactored() + ";" + this.getSpaghettiCodeRefactored() + ";" + this.getSpeculativeGeneralityRefactored() + ";" +
				this.getInappropriateIntimacyRefactored() + ";" + this.getFeatureEnvyRefactored();
	}
	
	public String toStringPercentage(){
		String toReturn = this.getType() + ";" + ((double)this.getBlobRemoved()/this.getBlobRefactored()) + ";" + ((double)this.getComplexClassRemoved())/this.getComplexClassRefactored() + ";" + ((double)this.getComplexClassRemoved()/this.getComplexClassRefactored())
				+ ";" + ((double)this.getLazyClassRemoved()/this.getLazyClassRefactored()) + ";" +
				((double)this.getLongMethodRemoved()/this.getLongMethodRefactored()) + ";" + ((double)this.getLongParameterListRemoved()/this.getLongParameterListRefactored()) + ";" + 
				((double)this.getMessageChainRemoved()/this.getMessageChainRefactored()) + ";" +
				((double)this.getRPBRemoved()/this.getRPBRefactored()) + ";" + ((double)this.getSpaghettiCodeRemoved()/this.getSpaghettiCodeRefactored()) + ";" + 
				((double)this.getSpeculativeGeneralityRemoved()/this.getSpeculativeGeneralityRefactored()) + ";" +
				((double)this.getInappropriateIntimacyRemoved()/this.getInappropriateIntimacyRefactored()) + ";" + ((double)this.getFeatureEnvyRemoved()/this.getFeatureEnvyRefactored());
		
		return (toReturn.replace("NaN", "0"));
	}
	
}
