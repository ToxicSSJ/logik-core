package com.magmaticcode.logikcore.propositional.bundle.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Constant;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.node.Node;

public enum QuantifierInstantiationRuleType implements CategoryType.Rule {
	
	UNIVERSAL_INSTATIATION(new QuantifierInstantiationRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			f1 = f1.clone();
			
			if(!f1.getPrimary(true).getAction().getType().is(Node.ActionType.UNIVERSAL_QUANTIFIER))
				return f1;
			
			Constant to = f1.getPrimary(true).getAction().getQuantifier();
			Formula test = f1.clone();
			
			test.getAttached().removeFirst();
			
			Set<Constant> prohibited = new HashSet<Constant>();
			LinkedList<Formula> formulas = f1.getQuantifiedFormulas(t -> t.getAttached().stream().filter(k -> k.getType().is(Node.ActionType.UNIVERSAL_QUANTIFIER, Node.ActionType.EXISTENTIAL_QUANTIFIER)).findAny().orElse(null) != null);
			
			formulas.removeIf(k -> {
				
				for(Action c : k.getAttached())
					if(c.getType().is(Node.ActionType.UNIVERSAL_QUANTIFIER, Node.ActionType.EXISTENTIAL_QUANTIFIER))
						if(c.getQuantifier().getType() == to.getType())
							return false;
					
				return true;
				
			});
			
			formulas.stream().forEach(k -> {
				
				for(Constant c : k.getAllConstant(false))
					prohibited.add(c);
				
			});
			
			for(Node.ConstantType k : Node.ConstantType.values()) {
				
				Formula clon = test.clone();
				
				Map<UUID, Node.ConstantType> oldTypes = new HashMap<UUID, Node.ConstantType>();
				LinkedList<Constant> constants = clon.getAllConstant(false);
				
				for(Constant constant : constants)
					if(constant.getType() == to.getType())
						if(!prohibited.contains(constant)) {
							oldTypes.put(constant.uniqueID, constant.getType());
							constant.setType(k);
						}
				
				if(clon.equals(f2))
					return f2;
				
				for(Map.Entry<UUID, Node.ConstantType> entry : oldTypes.entrySet()) {
					
					Constant c = constants.stream().filter(cache -> cache.uniqueID == entry.getKey()).findAny().orElse(null);
					
					if(c != null)
						c.setType(entry.getValue());
					
				}
				
			}
			
			return f1;
			
		}
		
	}),
	
	EXISTENTIAL_INSTATIATION(new QuantifierInstantiationRuleActioner() {

		public Formula action(Formula f1, Formula f2, LinkedList<Formula> tested) {
			
			f1 = f1.clone();
			
			if(!f1.getPrimary(true).getAction().getType().is(Node.ActionType.EXISTENTIAL_QUANTIFIER))
				return f1;
			
			Set<Node.ConstantType> restringed = new HashSet<Node.ConstantType>();
			
			tested.stream().forEach(formula -> {
				
				for(Constant c : formula.getAllConstant(true))
					if(c.getType().isConst())
						restringed.add(c.getType());
				
			});
			
			for(Constant c : f1.getAllConstant(true))
				if(c.getType().isConst())
					restringed.add(c.getType());
			
			Constant to = f1.getPrimary(true).getAction().getQuantifier();
			Formula test = f1.clone();
			
			test.getAttached().removeFirst();
			
			Set<Constant> prohibited = new HashSet<Constant>();
			LinkedList<Formula> formulas = f1.getQuantifiedFormulas(t -> t.getAttached().stream().filter(k -> k.getType().is(Node.ActionType.UNIVERSAL_QUANTIFIER, Node.ActionType.EXISTENTIAL_QUANTIFIER)).findAny().orElse(null) != null);
			
			formulas.removeIf(k -> {
				
				for(Action c : k.getAttached())
					if(c.getType().is(Node.ActionType.UNIVERSAL_QUANTIFIER, Node.ActionType.EXISTENTIAL_QUANTIFIER))
						if(c.getQuantifier().getType() == to.getType())
							return false;
					
				return true;
				
			});
			
			formulas.stream().forEach(k -> {
				
				for(Constant c : k.getAllConstant(false))
					prohibited.add(c);
				
			});
			
			for(Node.ConstantType k : Node.ConstantType.values()) {
				
				Formula clon = test.clone();
				
				Map<UUID, Node.ConstantType> oldTypes = new HashMap<UUID, Node.ConstantType>();
				LinkedList<Constant> constants = clon.getAllConstant(false);
				
				for(Constant constant : constants)
					if(constant.getType() == to.getType())
						if(!restringed.contains(k) && !prohibited.contains(constant)) {
							oldTypes.put(constant.uniqueID, constant.getType());
							constant.setType(k);
						}
				
				if(clon.equals(f2))
					return f2;
				
				for(Map.Entry<UUID, Node.ConstantType> entry : oldTypes.entrySet()) {
					
					Constant c = constants.stream().filter(cache -> cache.uniqueID == entry.getKey()).findAny().orElse(null);
					
					if(c != null)
						c.setType(entry.getValue());
					
				}
				
			}
			
			return f1;
			
		}
		
	}),
	
	;
	
	private QuantifierInstantiationRuleActioner actioner;
	
	QuantifierInstantiationRuleType(QuantifierInstantiationRuleActioner actioner) {
		this.actioner = actioner;
	}
	
	public QuantifierInstantiationRuleActioner getActioner() {
		return actioner;
	}
	
	public static interface QuantifierInstantiationRuleActioner {
		
		public default Formula action(Formula f1, Formula f2) {
			return f1;
		}
		
		public default Formula action(Formula f1, Formula f2, LinkedList<Formula> tested) {
			return f1;
		}
		
	}

	@Override
	public CategoryType getCategory() {
		return CategoryType.QUANTIFIER_INSTANTIATION_RULE;
	}
	
}
