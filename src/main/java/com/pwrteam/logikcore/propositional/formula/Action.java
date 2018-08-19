package com.pwrteam.logikcore.propositional.formula;

import java.util.UUID;

import com.pwrteam.logikcore.propositional.node.Node;

public class Action extends Node {

	private ActionType type;
	private Constant constant = new Constant('x');
	
	public Action(UUID uniqueID, ActionType type) {
		super(uniqueID);
		
		this.type = type;
		
	}
	
	public Action(ActionType type) {
		this.type = type;
	}
	
	public Action(ActionType type, ConstantType ctype) {
		this.type = type;
		this.constant = ctype.getConstant();
	}
	
	public Constant getQuantifier() {
		return constant;
	}
	
	public Action setQuantifier(Constant constant) {
		this.constant = constant;
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Node) {
			
			Node node = (Node) o;
			
			if(node.getUniqueID().equals(this.uniqueID))
				return true;
			
			if(node.isAction())
				return node.getAction().getType() == this.getType();
			
		}
		
		return false;
		
	}
	
	public boolean isQuantifier() {
		return type.is(ActionType.UNIVERSAL_QUANTIFIER, ActionType.EXISTENTIAL_QUANTIFIER);
	}
	
	@Override
	public String toString() {
		return isQuantifier() ? ("(" + type.getFormula() + constant.toString() + ")") : type.getFormula();
	}
	
	@Override
	public boolean isAction() {
		return true;
	}
	
	@Override
	public Action clone() {
		return new Action(type, constant.getType());
	}
	
	@Override
	public boolean workWithParenthisis() {
		return false;
	}

	@Override
	public boolean workWithAttaches() {
		return false;
	}
	
	@Override
	public boolean canConvert() {
		return false;
	}
	
	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

}
