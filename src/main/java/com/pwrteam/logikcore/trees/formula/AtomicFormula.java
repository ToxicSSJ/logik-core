package com.pwrteam.logikcore.trees.formula;

import java.util.LinkedList;

import com.pwrteam.logikcore.propositional.formula.Formula;

public class AtomicFormula extends Formula {
	
	private AtomicFormula father;
	private LinkedList<AtomicFormula> childs;
	
	public AtomicFormula getFather() {
		return father;
	}
	
	public void setFather(AtomicFormula father) {
		this.father = father;
	}
	
	public LinkedList<AtomicFormula> getChilds() {
		return childs;
	}
	
	public void setChilds(LinkedList<AtomicFormula> childs) {
		this.childs = childs;
	}
	
}
