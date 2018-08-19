package com.magmaticcode.logikcore.propositional.formula;

import java.util.LinkedList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.magmaticcode.logikcore.propositional.exception.formula.ConstantException;
import com.magmaticcode.logikcore.propositional.exception.formula.ConstantException.ConstantExceptionType;
import com.magmaticcode.logikcore.propositional.node.Node;

public class Variable extends Node {

	private LinkedList<Constant> constants = new LinkedList<Constant>();
	
	private char variableChar;
	
	private Variable(LinkedList<Constant> constants, char variableChar) {
		
		if(Character.isAlphabetic(variableChar)) {
			
			constants.forEach(k -> k.setMaster(this));
			
			this.variableChar = Character.toUpperCase(variableChar);
			this.constants = constants;
			
		} else { throw new ConstantException(ConstantExceptionType.INVALID_CHAR); }
		
	}
	
	public Variable(UUID uniqueID, char variableChar) {
		super(uniqueID);
		
		if(Character.isAlphabetic(variableChar)) {
			
			this.variableChar = Character.toUpperCase(variableChar);
			
		} else { throw new ConstantException(ConstantExceptionType.INVALID_CHAR); }
		
	}

	public Variable(char variableChar) {
		
		if(Character.isAlphabetic(variableChar)) {
			
			this.variableChar = Character.toUpperCase(variableChar);
			
		} else { throw new ConstantException(ConstantExceptionType.INVALID_CHAR); }
		
	}
	
	public Variable addConstant(Constant constant) {
		
		constant.setMaster(this);
		constants.add(constant);
		return this;
		
	}
	
	public LinkedList<Constant> getConstants() {
		return constants;
	}
	
	public char getConstantChar() {
		return variableChar;
	}

	public void setConstantChar(char variableChar) {
		this.variableChar = variableChar;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Node) {
			
			Node node = (Node) o;
			
			if(node.getUniqueID().equals(uniqueID))
				return true;
			
			if(node.isVariable()) {
				
				Variable var = node.getVariable();
				
				if(var.getConstantChar() == this.getConstantChar())
					if(var.getConstants().size() == this.getConstants().size()) {
						
						for(int i = 0; i < this.getConstants().size(); i++)
							if(!var.getConstants().get(i).equals(this.getConstants().get(i)))
								return false;
						
						return true;
						
					}
				
				return false;
				
			}
			
			if(node.isFormula()) {
				
				Formula form = node.getFormula();
				
				if(form.getNodes().size() == 1)
					if(form.getPrimary(false).isVariable())
						if(form.getPrimary(false).getVariable().getConstantChar() == this.getConstantChar())
							if(form.getPrimary(false).getVariable().getConstants().size() == this.getConstants().size()) {
								
								for(int i = 0; i < this.getConstants().size(); i++)
									if(!form.getPrimary(false).getVariable().getConstants().get(i).equals(this.getConstants().get(i)))
										return false;
								
								return true;
								
							}
				
			}
			
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return String.valueOf(variableChar + (constants.size() > 0 ? StringUtils.join(constants, "") : ""));
	}

	@Override
	public boolean isVariable() {
		return true;
	}
	
	@Override
	public Variable clone() {
		return new Variable(constants, variableChar);
	}
	
	@Override
	public boolean workWithParenthisis() {
		return false;
	}

	@Override
	public boolean workWithAttaches() {
		return true;
	}
	
	@Override
	public boolean canConvert() {
		return true;
	}
	
}
