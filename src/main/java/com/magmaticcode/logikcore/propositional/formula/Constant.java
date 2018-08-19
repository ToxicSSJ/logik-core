package com.magmaticcode.logikcore.propositional.formula;

import java.util.UUID;

import com.magmaticcode.logikcore.propositional.exception.formula.ConstantException;
import com.magmaticcode.logikcore.propositional.exception.formula.ConstantException.ConstantExceptionType;
import com.magmaticcode.logikcore.propositional.node.Node;

public class Constant extends Node {

	private ConstantType type;
	
	public Constant(UUID uniqueID, char constantChar) {
		super(uniqueID);
		
		if(Character.isAlphabetic(constantChar)) {
			
			this.type = ConstantType.valueOf(Character.toLowerCase(constantChar));
			
		} else { throw new ConstantException(ConstantExceptionType.INVALID_CHAR); }
		
	}

	public Constant(char constantChar) {
		
		if(Character.isAlphabetic(constantChar)) {
			
			this.type = ConstantType.valueOf(Character.toLowerCase(constantChar));
			
		} else { throw new ConstantException(ConstantExceptionType.INVALID_CHAR); }
		
	}
	
	public ConstantType getType() {
		return type;
	}

	public Constant setType(ConstantType type) {
		this.type = type;
		return this;
	}
	
	public Constant setConstantChar(char constantChar) {
		this.type = ConstantType.valueOf(Character.toLowerCase(constantChar));
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Node) {
			
			Node node = (Node) o;
			
			if(node.getUniqueID().equals(this.uniqueID))
				return true;
			
			if(node.isConstant())
				return node.getConstant().getType() == this.getType();
			
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return String.valueOf(type.getIdentifier());
	}

	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Constant clone() {
		return new Constant(type.getIdentifier());
	}
	
	@Override
	public boolean workWithParenthisis() {
		return true;
	}

	@Override
	public boolean canConvert() {
		return true;
	}
	
	@Override
	public boolean workWithAttaches() {
		return false;
	}
	
}
