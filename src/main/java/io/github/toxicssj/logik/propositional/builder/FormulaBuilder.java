package io.github.toxicssj.logik.propositional.builder;

import java.util.LinkedList;
import java.util.UUID;

import io.github.toxicssj.logik.propositional.formula.Action;
import io.github.toxicssj.logik.util.Strings;

import io.github.toxicssj.logik.propositional.formula.Constant;
import io.github.toxicssj.logik.propositional.formula.Formula;
import io.github.toxicssj.logik.propositional.formula.Variable;
import io.github.toxicssj.logik.propositional.node.Node;
import io.github.toxicssj.logik.propositional.node.Node.ActionType;

public class FormulaBuilder {

	private static final FormulaBuilder builder = new FormulaBuilder();
	
	public static FormulaBuilder getBuilder() {
		return builder;
	}
	
	public Action newAction(ActionType type) {
		return new Action(type);
	}
	
	public Variable newVariable(String variable) {
		return new Variable(variable.charAt(0));
	}
	
	public Constant newConstant(String constant) {
		return new Constant(constant.charAt(0));
	}
	
	public Formula newFormula(Node node) {
		return new Formula(new LinkedList<Action>(), node);
	}
	
	public Formula newFormula(Node node, UUID uniqueID) {
		return new Formula(uniqueID, new LinkedList<Action>(), node);
	}
	
	public Formula newFormula(LinkedList<Action> attached, Node node) {
		return new Formula(attached, node);
	}
	
	public Formula newFormula(LinkedList<Action> attached, Node node, UUID uniqueID) {
		return new Formula(uniqueID, attached, node);
	}
	
	public Formula action(Formula f1, Formula f2, ActionType type) {
		return !type.isAttached() ? new Formula(new LinkedList<Action>(), f1, type.getAction(), f2) : new Formula(new LinkedList<Action>(), f1);
	}
	
	public String getNodeFormula(Formula f1, boolean parentheses) {
		
		LinkedList<Node> nodes = f1.getNodes();
		LinkedList<Action> attached = f1.getAttached();
		
		StringBuilder cache = new StringBuilder();
		
		if(nodes.size() == 1)
			return Strings.join(attached, "") + (nodes.get(0).workWithParenthisis() && parentheses ? "(" + nodes.get(0).toString() + ")" : nodes.get(0).toString());
		
		for(int i = 0; i < nodes.size(); i++) {
			
			Node node = nodes.get(i);
			
			if(node.equals(f1))
				continue;
			
			if(node.isAction())
				if(node.getAction().getType().isAttached()) {
					
					cache.append(node.toString());
					continue;
					
				}
			
			cache.append(node.toString());
			cache.append((i + 1) < nodes.size() ? " " : "");
			continue;
			
		}
		
		return Strings.join(attached, "") + (parentheses ? "(" + cache.toString() + ")" : cache.toString());
		
	}
	
	public String cleanFormula(Formula f1) {
		
		String cache = "";
		String formula = f1.toString();
		
		if(FormulaBalancer.areParenthesesBalanced(formula.toString()))
			if(formula.startsWith("(") && formula.endsWith(")"))
				cache = formula.substring(Strings.indexOf(formula, "(") + 1, Strings.lastIndexOf(formula, ")"));
		
		for(Node n : f1.getNodes())
			if(!cache.contains(n.toString()))
				return formula;
			
		return cache;
		
	}
	
}
