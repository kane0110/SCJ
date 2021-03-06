package scj.compiler.analysis.schedule.extraction;

import java.util.HashSet;
import java.util.Set;

import com.ibm.wala.ssa.ISSABasicBlock;

public class LoopContext {
	
	private final Set<BackEdgeFlowData> backEdges = new HashSet<BackEdgeFlowData>();
	
	private LoopContext() {
		
	}
	
	public static LoopContext emptyLoopContext() {
		return new LoopContext();
	}
	
	public boolean isEmpty() {
		return backEdges.isEmpty();
	}
	
	LoopContext contextByAddingLoop(BackEdgeFlowData backEdge) {
		if(backEdges.contains(backEdge))
			return this;
		
		LoopContext lc = new LoopContext();
		lc.backEdges.addAll(backEdges);
		lc.backEdges.add(backEdge);
		
		return lc;
	}
	
	@Override
	public boolean equals(Object otherObj) {
		return (otherObj == this) || (otherObj instanceof LoopContext && ((LoopContext)otherObj).backEdges.equals(backEdges));
	}

	@Override
	public int hashCode() {
		return backEdges.hashCode();
	}
	
	@Override
	public String toString() {
		return "{" + backEdges.toString() + "}";
	}

	//not sure if that shortest paths thing is the most elegant or if there is some other property i could/have to use
	//(e.g., dominance or something)
	public boolean isCurrentAtBlock(ISSABasicBlock basicBlock, TaskScheduleSolver solver) {		
		int[][] paths = solver.cfgShortestPaths;
		int current = basicBlock.getGraphNodeId();
		//if the basic block is "after" any to-block of any loop head, this loop context is not current
		for(BackEdgeFlowData edge : backEdges) {
			int loophead = edge.to.getGraphNodeId();
			if(basicBlock != edge.to && paths[current][loophead] == Integer.MAX_VALUE) {
				//System.out.println("LoopContext: isCurrentAtBlock returns false for block " + basicBlock + " and loop context " + this);
				return false;
			}			
		}
		//System.out.println("LoopContext: checking isCurrentAtBlock returns true for block " + basicBlock + " and loop context " + this);
		return true;
	}
}

