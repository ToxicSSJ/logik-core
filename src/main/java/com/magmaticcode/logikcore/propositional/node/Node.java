package com.magmaticcode.logikcore.propositional.node;

import java.util.UUID;

import com.magmaticcode.logikcore.propositional.builder.FormulaBuilder;
import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Constant;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.formula.Variable;

public abstract class Node {

	public Node master;
	public UUID uniqueID;
	
	public Node() {
		this.uniqueID = UUID.randomUUID();
	}
	
	public Node(UUID uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	public UUID getUniqueID() {
		return uniqueID;
	}
	
	public Node setMaster(Node node) {
		master = node;
		return this;
	}
	
	public Node getMaster() {
		return master;
	}
	
	public Formula toFormula() {
		return isFormula() ? (Formula) this : FormulaBuilder.getBuilder().newFormula(this, uniqueID);
	}
	
	public boolean isConstant() {
		return false;
	}
	
	public Constant getConstant() {
		return isConstant() ? (Constant) this : null;
	}
	
	public boolean isVariable() {
		return false;
	}
	
	public Variable getVariable() {
		return isVariable() ? (Variable) this : null;
	}
	
	public boolean isFormula() {
		return false;
	}
	
	public Formula getFormula() {
		return isFormula() ? (Formula) this : null;
	}
	
	public boolean isAction() {
		
		if(isFormula())
			if(getFormula().isAtomic())
				return getFormula().getNode(0).isAction();
		
		return false;
	}
	
	public Action getAction() {
		return isAction() ? (Action) this : ActionType.UNKNOW.getAction();
	}
	
	public abstract Node clone();
	
	public abstract boolean workWithAttaches();
	
	public abstract boolean workWithParenthisis();
	
	public abstract boolean canConvert();
	
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof Node) {
			
			Node node = (Node) object;
			
			if(node.getUniqueID().equals(uniqueID))
				return true;
			
			if(node.isVariable() && isVariable())
				return getVariable().equals(node.getVariable());
			
			if(node.isConstant() && isConstant())
				return getConstant().equals(node.getConstant());
			
			if(node.isAction() && isAction())
				return getAction().equals(node.getAction());
			
			if(node.isFormula() && isFormula())
				return getFormula().equals(node.getFormula());
			
		}
		
		return false;
		
	}
	
	public static enum ActionType {
		
		UNKNOW("§", true),
		NO("~", true),
		
		UNIVERSAL_QUANTIFIER("∀", true),
		EXISTENTIAL_QUANTIFIER("∃", true),
		
		AND("&", false),
		OR("|", false),
		SO("→", false),
		BICONDITIONAL("\u2194", false),
		
		;
		
		private String identifier;
		private boolean attached;
		
		ActionType(String identifier, boolean attached){
			
			this.identifier = identifier;
			this.attached = attached;
			
		}
		
		public boolean isAttached() {
			return attached;
		}
		
		public String getIdentifier() {
			return identifier;
		}
		
		public String getFormula() {
			return String.valueOf(identifier);
		}
		
		public Action getAction() {
			return FormulaBuilder.getBuilder().newAction(this);
		}
		
		public boolean is(ActionType...types) {
			
			for(ActionType type : types)
				if(this == type)
					return true;
			
			return false;
			
		}
		
	}
	
	public static enum ConstantType {
		
		A,
		B,
		C,
		D,
		E,
		F,
		G,
		H,
		I,
		J,
		K,
		L,
		M,
		N,
		O,
		P,
		Q,
		R,
		S,
		T,
		U,
		V,
		W,
		
		X,
		Y,
		Z;
		
		public boolean isVar() {
			return this == X || this == Y || this == Z;
		}
		
		public boolean isConst() {
			return this != X && this != Y && this != Z;
		}
		
		public char getIdentifier() {
			return this.name().toLowerCase().charAt(0);
		}
		
		public Constant getConstant() {
			return new Constant(getIdentifier());
		}
		
		public static ConstantType valueOf(char constant) {
			
			for(ConstantType type : ConstantType.values())
				if(type.name().equalsIgnoreCase(String.valueOf(constant)))
					return type;
			
			return null;
			
		}
		
	}
	
}
