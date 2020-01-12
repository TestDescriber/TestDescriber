package nl.tudelft.jacoco;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

/**
 * Class to store the coverage results from JaCoCo
 * 
 * @author annibale.panichella
 *
 */
public class JacocoResult {
	
	private int linesTotal;
	private int linesCovered;
	private int branchesTotal;
	private int branchesCovered;
	private int instructionsTotal;
	private int instructionsCovered;
	private int methodsTotal;
	private int methodsCovered;
	private int complexityTotal;
	private int complexityCovered;
	private Set<Integer> coveredLines;
	private Set<Integer> uncoveredLines;
	private Map<Integer, Boolean> coveredBranches;
	
	/**
	 * Constructor that stores the coverage results produced by JaCoCo
	 * @param cc an instance of the class {@link IClassCoverage}
	 */
	public JacocoResult(IClassCoverage cc){
		linesTotal = cc.getLineCounter().getTotalCount();
		linesCovered = cc.getLineCounter().getCoveredCount();
		branchesTotal = cc.getBranchCounter().getTotalCount();
		branchesCovered = cc.getBranchCounter().getCoveredCount();
		instructionsTotal = cc.getInstructionCounter().getTotalCount();
		instructionsCovered = cc.getInstructionCounter().getCoveredCount();
		methodsTotal = cc.getMethodCounter().getTotalCount();
		methodsCovered = cc.getMethodCounter().getCoveredCount();
		complexityTotal = cc.getComplexityCounter().getTotalCount();
		complexityCovered = cc.getComplexityCounter().getCoveredCount();
		coveredBranches = new HashMap<Integer, Boolean>();

		//let's derive the list uncovered lines
		coveredLines = new HashSet<Integer>();
		uncoveredLines = new HashSet<Integer>();
		for (int line = cc.getFirstLine(); line <= cc.getLastLine(); line++){
			if (cc.getLine(line).getStatus() == ICounter.FULLY_COVERED || cc.getLine(line).getStatus() == ICounter.PARTLY_COVERED){
				this.coveredLines.add(line);
				if (cc.getLine(line).getBranchCounter().getTotalCount()>0){
					int nextLineStatus = cc.getLine(line+1).getStatus();
					if (nextLineStatus == ICounter.FULLY_COVERED || nextLineStatus == ICounter.PARTLY_COVERED){
						System.out.println("It is a true branch");
						coveredBranches.put(line, true);
					} else {
						System.out.println("It is a false branch");
						coveredBranches.put(line, false);
					}
				}
			}
			else if (cc.getLine(line).getStatus() == ICounter.NOT_COVERED)
				this.uncoveredLines.add(line);
		}
	}

	public int getLinesTotal() {
		return linesTotal;
	}

	public int getLinesCovered() {
		return linesCovered;
	}

	public int getBranchesTotal() {
		return branchesTotal;
	}

	public int getBranchesCovered() {
		return branchesCovered;
	}

	public int getInstructionsTotal() {
		return instructionsTotal;
	}

	public int getInstructionsCovered() {
		return instructionsCovered;
	}

	public int getMethodsTotal() {
		return methodsTotal;
	}

	public int getMethodsCovered() {
		return methodsCovered;
	}

	public int getComplexityTotal() {
		return complexityTotal;
	}

	public int getComplexityCovered() {
		return complexityCovered;
	}
	
	public Set<Integer> getUncoveredLines(){
		return this.uncoveredLines;
	}
	
	public Set<Integer> getCoveredLines(){
		return this.coveredLines;
	}
	
	public Map<Integer, Boolean> getCoveredBranches(){
		return this.coveredBranches;
	}
	
	public void printResults(){
		System.out.println("Method coverage = "+ ((double) this.methodsCovered)/((double) this.methodsTotal));
		System.out.println("Line coverage = "+ ((double) this.linesCovered)/((double) this.linesTotal));
		System.out.println("Branch coverage = "+ ((double) this.branchesCovered)/((double) this.branchesTotal));
		System.out.println("Complexity coverage = "+ ((double) this.complexityCovered)/((double) this.complexityTotal));
	}

	public String getCoverageInfoAsString(){
		String covered = null;
		if (this.getCoveredLines().size() > 0) {
			covered = "";
			for (Integer line : this.getCoveredLines()){
				if (this.coveredBranches.containsKey(line))
					covered = covered + line+"-"+this.coveredBranches.get(line)+",";
				else
					covered = covered + line+",";
			}
			covered = covered.substring(0, covered.length()-1);
		}
		return covered;
	}

}
