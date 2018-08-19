package com.magmaticcode.logikcore.propositional.builder;

import java.util.LinkedList;

import com.magmaticcode.logikcore.propositional.bundle.type.ImplicationRuleType;
import com.magmaticcode.logikcore.propositional.bundle.type.QuantifierGeneralizationRuleType;
import com.magmaticcode.logikcore.propositional.bundle.type.QuantifierInstantiationRuleType;
import com.magmaticcode.logikcore.propositional.bundle.type.ReplacementRuleType;
import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.node.Node.ActionType;

public class FormulaCalculator {

	private static final FormulaCalculator calculator = new FormulaCalculator();
	
	public static FormulaCalculator getCalculator() {
		return calculator;
	}
	
	public Formula quantifier(Formula f1, Formula f2, QuantifierInstantiationRuleType type) {
		return type.getActioner().action(f1, f2);
	}
	
	public Formula quantifier(Formula f1, Formula f2, QuantifierGeneralizationRuleType type) {
		return type.getActioner().action(f1, f2);
	}
	
	public Formula replacement(Formula f1, Formula f2, ReplacementRuleType type) {
		return type.getActioner().action(f1, f2);
	}
	
	public Formula implication(Formula f1, Formula f2, ImplicationRuleType type) {
		return type.getActioner().action(f1, f2);
	}
	
	public Formula simplification(Formula f1) {
		
		//System.out.println(f1.getPrimary(false).getFormula().getPrimary(false).isAction());
		
		if(f1.isPremise())
			if(f1.getPrimary(false).isAction())
				if(f1.getPrimary(false).getAction().getType() == ActionType.AND)
					return f1.getNodes().get(0).toFormula();
		
		return f1;
		
	}
	
	public Formula conjunction(Formula f1, Formula f2) {
		return FormulaBuilder.getBuilder().action(f1, f2, ActionType.AND);
	}
	
	public Formula addition(Formula f1, Formula f2) {
		return FormulaBuilder.getBuilder().action(f1, f2, ActionType.OR);
	}
	
	public Formula doubleDenial(Formula f1, boolean add, boolean all) {
		
		final LinkedList<Action> newAttached = new LinkedList<Action>();
		
		if(!add) {
			
			boolean founded = false;
			
			for(int i = 0; i < f1.getAttached().size(); i++) {
				
				if(!founded && (i + 1) < f1.getAttached().size() && f1.getAttached().get(i).isAction() && f1.getAttached().get(i + 1).isAction())
					if(f1.getAttached().get(i).getAction().getType() == ActionType.NO)
						if(f1.getAttached().get(i + 1).getAction().getType() == ActionType.NO) {
							
							if(!all)
								founded = true;
							
							i += 1;
							continue;
							
						}
				
				newAttached.add(f1.getAttached().get(i));
				continue;
				
			}
			
		} else {
			
			newAttached.addAll(f1.getAttached());
			
			for(int i = 0; i < 2; i++)
				newAttached.addFirst(ActionType.NO.getAction().setMaster(f1).getAction());
			
		}
		
		return f1.changeAttached(newAttached);
		
	}
	
}
