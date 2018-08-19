package com.pwrteam.logikcore.propositional.bundle.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pwrteam.logikcore.propositional.bundle.type.CategoryType.Rule;
import com.pwrteam.logikcore.propositional.formula.Action;
import com.pwrteam.logikcore.propositional.formula.Constant;
import com.pwrteam.logikcore.propositional.formula.Formula;
import com.pwrteam.logikcore.propositional.node.Node.ActionType;
import com.pwrteam.logikcore.propositional.node.Node.ConstantType;

public enum QuantifierGeneralizationRuleType implements Rule {
	
	UNIVERSAL_GENERALIZATION(new QuantifierGeneralizationRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			
			if(!f2.getPrimary(true).getAction().getType().is(ActionType.UNIVERSAL_QUANTIFIER))
				return f1;

			Formula test = null;
			LinkedList<Formula> generalizations = getGeneralizations(true, f1);

			for(Formula f : generalizations)
				if(f.getPrimary(true).getAction().getQuantifier().getType() == f2.getPrimary(true).getAction().getQuantifier().getType())
					test = f;
			
			if(test == null)
				return f1;
			
			ConstantType to = test.getPrimary(true).getAction().getQuantifier().getType();
			
			Set<Constant> prohibited = new HashSet<Constant>();
			LinkedList<Formula> formulas = f1.getQuantifiedFormulas(t -> t.getAttached().stream().filter(k -> k.getType().is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER)).findAny().orElse(null) != null);
			
			formulas.removeIf(k -> {
				
				for(Action c : k.getAttached())
					if(c.getType().is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER))
						if(c.getQuantifier().getType() == to)
							return false;
					
				return true;
				
			});
			
			formulas.stream().forEach(k -> {
				
				for(Constant c : k.getAllConstant(false))
					prohibited.add(c);
				
			});
			
			for(ConstantType k : ConstantType.values())
				if(k.isConst()) {
					
					Formula clon = test.clone();
					
					Map<UUID, ConstantType> oldTypes = new HashMap<UUID, ConstantType>();
					LinkedList<Constant> constants = clon.getAllConstant(false);
					
					for(Constant constant : constants)
						if(constant.getType() == k)
							if(!prohibited.contains(constant)) {
								oldTypes.put(constant.uniqueID, k);
								constant.setType(to);
							}
					
					if(clon.equals(f2)) {

                        LinkedList<Formula> quantifiedFormulas = f1.getQuantifiedFormulas(f -> {

                            System.out.println("DEBUG :: FORMULA :: " + f);

                            for(Action action : f.getAttached())
                                if(action.getType().is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER))
                                    if (action.getQuantifier().getType() == k)
                                        return true;

                            return false;

                        });

                        System.out.println("DEBUG :: TAMAÑO DE quantifiedFormulas : " + quantifiedFormulas.size());

                        if(quantifiedFormulas.size() > 0)
                            return f1;

                        return f2;

                    }

					for(Map.Entry<UUID, ConstantType> entry : oldTypes.entrySet()) {
						
						Constant c = constants.stream().filter(cache -> cache.uniqueID == entry.getKey()).findAny().orElse(null);
						
						if(c != null)
							c.setType(entry.getValue());
						
					}
					
				}	
			
			return f1;
			
		}
		
	}),
	
	EXISTENTIAL_GENERALIZATION(new QuantifierGeneralizationRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			
			if(!f2.getPrimary(true).getAction().getType().is(ActionType.EXISTENTIAL_QUANTIFIER))
				return f1;
			
			Formula test = null;
			LinkedList<Formula> generalizations = getGeneralizations(false, f1);
			
			for(Formula f : generalizations)
				if(f.getPrimary(true).getAction().getQuantifier().getType() == f2.getPrimary(true).getAction().getQuantifier().getType())
					test = f;
			
			if(test == null)
				return f1;
			
			ConstantType to = test.getPrimary(true).getAction().getQuantifier().getType();
			
			Set<Constant> prohibited = new HashSet<Constant>();
			LinkedList<Formula> formulas = f1.getQuantifiedFormulas(t -> t.getAttached().stream().filter(k -> k.getType().is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER)).findAny().orElse(null) != null);
			
			formulas.removeIf(k -> {
				
				for(Action c : k.getAttached())
					if(c.getType().is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER))
						if(c.getQuantifier().getType() == to)
							return false;
					
				return true;
				
			});
			
			formulas.stream().forEach(k -> {
				
				for(Constant c : k.getAllConstant(false))
					prohibited.add(c);
				
			});
			
			for(ConstantType k : ConstantType.values())
				if(k.isConst()) {
					
					Formula clon = test.clone();
					
					Map<UUID, ConstantType> oldTypes = new HashMap<UUID, ConstantType>();
					LinkedList<Constant> constants = clon.getAllConstant(false);
					
					for(Constant constant : constants)
						if(constant.getType() == k)
							if(!prohibited.contains(constant)) {
								oldTypes.put(constant.uniqueID, k);
								constant.setType(to);
							}
					
					if(clon.equals(f2))
						return f2;
					
					for(Map.Entry<UUID, ConstantType> entry : oldTypes.entrySet()) {
						
						Constant c = constants.stream().filter(cache -> cache.uniqueID == entry.getKey()).findAny().orElse(null);
						
						if(c != null)
							c.setType(entry.getValue());
						
					}
					
				}	
			
			return f1;
			
		}
		
	})
	
	;
	
	private QuantifierGeneralizationRuleActioner actioner;
	
	QuantifierGeneralizationRuleType(QuantifierGeneralizationRuleActioner actioner) {
		this.actioner = actioner;
	}
	
	public QuantifierGeneralizationRuleActioner getActioner() {
		return actioner;
	}
	
	public static LinkedList<Formula> getGeneralizations(boolean universal, Formula f1){
		
		LinkedList<Formula> result = new LinkedList<Formula>();
		
		for(ConstantType t : ConstantType.values())
			if(t.isVar())
				result.add(f1.clone().addAttach(new Action(universal ? ActionType.UNIVERSAL_QUANTIFIER : ActionType.EXISTENTIAL_QUANTIFIER, t)));
		
		return result;
		
	}
	
	public static interface QuantifierGeneralizationRuleActioner {
		
		public Formula action(Formula f1, Formula f2);
		
	}

	@Override
	public CategoryType getCategory() {
		return CategoryType.QUANTIFIER_GENERALIZATION_RULE;
	}
	
}

