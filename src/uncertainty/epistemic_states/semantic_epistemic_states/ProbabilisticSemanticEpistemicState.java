package uncertainty.epistemic_states.semantic_epistemic_states;

import java.util.Map;

import uncertainty.epistemic_states.SemanticEpistemicState;
import uncertainty.epistemic_states.World;
import agentspeak.LogicalExpression;
import agentspeak.logical_expressions.BeliefAtom;
import agentspeak.logical_expressions.operations.binary_operations.PlausibilityGE;
import agentspeak.logical_expressions.operations.binary_operations.PlausibilityGT;
import agentspeak.logical_expressions.terminals.Primitive;
import agentspeak.logical_expressions.terminals.primitives.Contradiction;
import agentspeak.logical_expressions.terminals.primitives.Tautology;
import data_structures.AdvancedSet;
import exceptions.NotGroundException;

public class ProbabilisticSemanticEpistemicState extends SemanticEpistemicState {

	public ProbabilisticSemanticEpistemicState(AdvancedSet<BeliefAtom> a) throws NotGroundException {
		super(a);
	}
	
	@Override
	public double getMinWeight() {
		return 0;
	}
	
	@Override
	public double getMaxWeight() {
		return 1;
	}
	
	public double lambda(LogicalExpression f) throws Exception {
		if(!f.isGround()) {
			throw new NotGroundException("formula must be ground");
		}
		LogicalExpression formula;
		if(f.inNNF()) {
			formula = this.pare(f);
		} else {
			formula = this.pare(f.toNNF());
		}
		double sum = 0;
		for(Map.Entry<World, Double> entry : this.getBeliefBase().entrySet()) {
			World world = entry.getKey();
			if(world.models(formula)) {
				sum += entry.getValue();
			}
		}
		return sum;
	}
	
	@Override
	public Primitive pare(PlausibilityGE f) throws Exception {
		if(this.lambda(f.getLeft()) >= this.lambda(f.getRight())) {
			return new Tautology();
		} else {
			return new Contradiction();
		}
	}
	
	@Override
	public Primitive pare(PlausibilityGT f) throws Exception {
		if(this.lambda(f.getLeft()) > this.lambda(f.getRight())) {
			return new Tautology();
		} else {
			return new Contradiction();
		}
	}
	
	/*
	 * Standard.
	 */
	@Override
	public String toString() {
		String output = "{";
		String delim = "";
        for(Map.Entry<World, Double> entry : this.getBeliefBase().entrySet()) {
        	output += delim + "P(" + entry.getKey().toString() + ")=" + String.format("%.2f", entry.getValue());
        	delim = ", ";
        }
        output += "}";
        return output;
	}
	
}
