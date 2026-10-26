package io.github.toxicssj.logik.propositional.builder;

import java.util.LinkedList;

import io.github.toxicssj.logik.util.Strings;

import io.github.toxicssj.logik.propositional.bundle.proof.Proof;
import io.github.toxicssj.logik.propositional.bundle.proof.ProofLine;
import io.github.toxicssj.logik.propositional.formula.Formula;

public class ProofParser {

	public static final FormulaBuilder builder = FormulaBuilder.getBuilder();
	public static final FormulaCalculator calculator = FormulaCalculator.getCalculator();
	
	public static Proof fromString(String data) {
		
		String strexcercise = Strings.substringBetween(data, "{", "}");
		
		String[] strcomponent = strexcercise.split("=>");
		String[] strpremises = strcomponent[0].split("//");
		
		String strconclusion = strcomponent[1];
		Formula conclusion = FormulaParser.fromString(strconclusion);
		
		LinkedList<Formula> premises = new LinkedList<Formula>();
		
		for(String k : strpremises)
			premises.add(FormulaParser.fromString(k));
		
		return new Proof(premises, conclusion);
		
	}
	
	public static ProofLine fromString(String premise, String rulesinfo) {
		return ProofLine.fromString(String.join("#", premise, rulesinfo));
	}
	
}
