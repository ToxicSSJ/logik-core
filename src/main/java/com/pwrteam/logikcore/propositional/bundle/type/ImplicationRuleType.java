package com.pwrteam.logikcore.propositional.bundle.type;

import com.pwrteam.logikcore.propositional.builder.FormulaBalancer;
import com.pwrteam.logikcore.propositional.builder.FormulaBuilder;
import com.pwrteam.logikcore.propositional.bundle.type.CategoryType.Rule;
import com.pwrteam.logikcore.propositional.formula.Action;
import com.pwrteam.logikcore.propositional.formula.Formula;
import com.pwrteam.logikcore.propositional.node.Node.ActionType;

public enum ImplicationRuleType implements Rule {
	
	MODUS_PONENDO_PONENS(new ImplicationRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			f2 = f2.clone();
			
			if(f1.isPremise())
				if(f1.getPrimary(true).isAction() && f1.getPrimary(true).getAction().getType() == ActionType.SO)
					if(f1.getNodes().get(0).equals(f2))
						return f1.getNodes().get(2).toFormula();
			
			return f1;
			
		}
		
	}),
	MODUS_PONENDO_TOLLENS(new ImplicationRuleActioner() {
		
		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			f2 = f2.clone();
			
			if(f1.isPremise())
				if(f1.getPrimary(true).isAction() && f1.getPrimary(true).getAction().getType() == ActionType.SO)
					if(FormulaBalancer.areOpposites(f1.getNodes().get(2).toFormula(), f2))
						return f1.getNodes().get(0).toFormula().addAttach(new Action(ActionType.NO));
			
			return f1;
			
		}
		
	}),
	DISJUNCTIVE_SYLLOGISM(new ImplicationRuleActioner() {
		
		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			f2 = f2.clone();
			
			if(f1.isPremise())
				if(f1.getPrimary(true).isAction() && f1.getPrimary(true).getAction().getType() == ActionType.OR)
					if(FormulaBalancer.areOpposites(f1.getNodes().get(0).toFormula(), f2))
						return f1.getNodes().get(2).toFormula();
			
			return f1;
			
		}
		
	}),
	HYPOTETICAL_SYLLOGISM(new ImplicationRuleActioner() {

		@Override
		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			f2 = f2.clone();
			
			if(f1.isPremise() && f2.isPremise())
				if(f1.getPrimary(true).isAction() && f1.getPrimary(true).getAction().getType() == ActionType.SO)
					if(f2.getPrimary(true).isAction() && f2.getPrimary(true).getAction().getType() == ActionType.SO)
						if(f1.getNodes().get(2).toFormula().equals(f2.getNodes().get(0))) {
							
							f1.getNodes().set(2, f2.getNodes().get(2).toFormula());
							return f1;
							
						}
			
			return f1;
		}
		
	}),
	CONSTRUCTIVE_DILEMA(new ImplicationRuleActioner() {

		@Override
		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			f2 = f2.clone();
			
			if(f1.isPremise() && f2.isPremise())
				if(f1.getPrimary(true).isAction() && f1.getPrimary(true).getAction().getType() == ActionType.AND)
					if(f2.getPrimary(true).isAction() && f2.getPrimary(true).getAction().getType() == ActionType.OR) {
						
						Formula fleft = f1.getNodes().get(0).toFormula().clone();
						Formula fright = f1.getNodes().get(2).toFormula().clone();
						
						if(fleft.isPremise() && fright.isPremise())
							if(fleft.getPrimary(true).isAction() && fleft.getPrimary(true).getAction().getType() == ActionType.SO)
								if(fright.getPrimary(true).isAction() && fright.getPrimary(true).getAction().getType() == ActionType.SO)
									if(fleft.getNodes().get(0).equals(f2.getNodes().get(0)))
										if(fright.getNodes().get(0).equals(f2.getNodes().get(2)))
											return FormulaBuilder.getBuilder().action(
													fleft.getNodes().get(2).toFormula().clone(),
													fright.getNodes().get(2).toFormula().clone(),
													ActionType.OR);
						
					}
						
			
			return f1;
		}
		
	})
	
	;
	
	private ImplicationRuleActioner actioner;
	
	ImplicationRuleType(ImplicationRuleActioner actioner) {
		this.actioner = actioner;
	}
	
	public ImplicationRuleActioner getActioner() {
		return actioner;
	}
	
	public static interface ImplicationRuleActioner {
		
		public Formula action(Formula f1, Formula f2);
		
	}

	@Override
	public CategoryType getCategory() {
		return CategoryType.IMPLICATION_RULE;
	}
	
}
