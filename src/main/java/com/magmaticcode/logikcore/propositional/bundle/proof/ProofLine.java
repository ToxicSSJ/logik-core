package com.magmaticcode.logikcore.propositional.bundle.proof;

import java.util.LinkedList;
import java.util.Map;

import com.magmaticcode.logikcore.propositional.builder.FormulaParser;
import com.magmaticcode.logikcore.propositional.bundle.type.CategoryType;
import com.magmaticcode.logikcore.propositional.exception.PropositionalLogicException;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import org.apache.commons.lang3.StringUtils;

public class ProofLine {

	private Formula formula;
	private CategoryType.RuleType type;
	
	private LinkedList<ProofStep> steps;
	
	public ProofLine(Formula formula, CategoryType.RuleType type, LinkedList<ProofStep> steps) {
		
		this.formula = formula;
		this.type = type;
		
		this.steps = steps;
		
	}
	
	public LinkedList<ProofStep> getSteps() {
		return steps;
	}
	
	public CategoryType.RuleType getType() {
		return type;
	}
	
	public Formula getFormula() {
		return formula;
	}
	
	public static ProofLine fromString(String proof) {
		
		if(proof.contains("#"))
			if(StringUtils.countMatches(proof, "#") != 1)
				throw new PropositionalLogicException("La sintaxis está mal formulada.");
		
		String[] splice = proof.split("#");
		
		Formula formula = FormulaParser.fromString(splice[0]);
		Map.Entry<CategoryType.RuleType, String> ruleinfo = FormulaParser.getRuleInfo(splice[1]);
		
		return new ProofLine(formula, ruleinfo.getKey(), ProofStep.fromString(ruleinfo.getValue()));
		
	}
	
	public static class ProofStep {
		
		private int step;
		
		public ProofStep(int step) {
			this.step = step;
		}

		public int getStep() {
			return step;
		}

		public void setStep(int step) {
			this.step = step;
		}
		
		@Override
		public String toString() {
			return String.valueOf(step);
		}
		
		public static LinkedList<ProofStep> fromString(String steps){
			
			LinkedList<ProofStep> cache = new LinkedList<ProofStep>();
			
			for(String k : steps.split("\\D+")) {
				
				try {
					
					cache.add(new ProofStep(Integer.parseInt(k)));
					
				} catch(Exception e) { continue; }
				
			}
			
			return cache;
			
		}
		
	}
	
}
