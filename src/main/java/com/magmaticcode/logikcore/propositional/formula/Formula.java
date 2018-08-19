package com.magmaticcode.logikcore.propositional.formula;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Predicate;

import com.magmaticcode.logikcore.propositional.builder.FormulaBalancer;
import com.magmaticcode.logikcore.propositional.builder.FormulaBuilder;
import com.magmaticcode.logikcore.propositional.builder.FormulaCalculator;
import com.magmaticcode.logikcore.propositional.exception.PropositionalLogicException;
import com.magmaticcode.logikcore.propositional.node.Node;

public class Formula extends Node {
	
	private LinkedList<Action> attached = new LinkedList<Action>();
	private LinkedList<Node> nodes = new LinkedList<Node>();
	
	public Formula() {}
	
	public Formula(UUID uniqueID, LinkedList<Action> attached, Node...nodes) {
		
		super(uniqueID);
		
		for(Action attach : attached)
			if(attach.getAction().getType().isAttached()) {
				attach.setMaster(this);
				this.attached.add(attach);
			}
		
		if(nodes.length == 1) {
			
			nodes[0].setMaster(this);
			
			if(nodes[0].isFormula() && nodes[0].getFormula().isPremise())
				this.nodes = nodes[0].getFormula().getNodes();
			else
				this.nodes.add(nodes[0]);
			
		} else {
			
			for(Node node : nodes) {
				node.setMaster(this);
				this.nodes.add(node);
			}
			
		}
		
	}
	
	public Formula(LinkedList<Action> attached, Node...nodes) {
		
		for(Action attach : attached)
			if(attach.getAction().getType().isAttached()) {
				attach.setMaster(this);
				this.attached.add(attach);
			}
		
		if(nodes.length == 1) {
			
			nodes[0].setMaster(this);
			
			if(nodes[0].isFormula() && nodes[0].getFormula().isPremise())
				this.nodes = nodes[0].getFormula().getNodes();
			else
				this.nodes.add(nodes[0]);
			
		} else {
			
			for(Node node : nodes) {
				node.setMaster(this);
				this.nodes.add(node);
			}
			
		}
		
	}
	
	private Formula(LinkedList<Action> attached, LinkedList<Node> nodes) {
		
		for(Action attach : attached)
			if(attach.getAction().getType().isAttached()) {
				attach.setMaster(this);
				this.attached.add(attach.clone());
			}
		
		if(nodes.size() == 1) {
			
			nodes.get(0).setMaster(this);
			
			if(nodes.get(0).isFormula() && nodes.get(0).getFormula().isPremise())
				this.nodes = nodes.get(0).getFormula().clone().getNodes();
			else
				this.nodes.add(nodes.get(0).clone());
			
		} else {
			
			for(Node node : nodes) {
				node.setMaster(this);
				this.nodes.add(node.clone());
			}
			
		}
		
	}

	public Node getPrimary(boolean includeMaster) {
		
		if(includeMaster)
			if(attached.size() > 0)
				return attached.get(0);
		
		if(nodes.size() == 1)
			return nodes.get(0);
		
		if(nodes.size() == 2)
			if(nodes.get(0).isAction())
				if(!nodes.get(0).getAction().getType().isAttached())
					return nodes.get(0);
		
		if(isPremise())
			if(nodes.get(1).isAction())
				if(!nodes.get(1).getAction().getType().isAttached())
					return nodes.get(1);
		
		System.out.println("ISPREMISE: " + isPremise());
		System.out.println("ACTION: " + nodes.get(1) + " ISACTION: " + nodes.get(1).isAction());
		
		return this;
		
	}
	
	public Formula addAttach(Action attach) {
		
		if(attach.getType().isAttached()) {
			attach.setMaster(this);
			attached.addFirst(attach);
		}
		
		return this;
		
	}
	
	public Formula addAttach(Action attach, int quantity) {
		
		for(int i = 0; i < quantity; i++)
			if(attach.getType().isAttached()) {
				attach.setMaster(this);
				attached.add(attach.clone());
			}
		
		return this;
		
	}
	
	public boolean injectNode(Node node) {
		return nodes.add(node);
	}
	
	public Formula changeNodes(Node node) {
		return changeNodes(new LinkedList<Node>(Arrays.asList(node)));
	}
	
	public Formula changeNodes(LinkedList<Node> nodes) {
		
		this.nodes.clear();
		
		if(nodes.contains(this))
			throw new PropositionalLogicException("Una premisa no se puede contener así misma.");
		
		if(nodes.size() > 3 || nodes.size() == 2)
			throw new PropositionalLogicException("La formula no tiene coherencia.");
		
		if(nodes.size() == 1) {
			
			nodes.get(0).setMaster(this);
			
			if(nodes.get(0).isFormula() && nodes.get(0).getFormula().isPremise())
				this.nodes = nodes.get(0).getFormula().getNodes();
			else
				this.nodes = nodes;
			
		} else {
			
			for(Node node : nodes) {
				node.setMaster(this);
				this.nodes.add(node);
			}
			
		}
		
		return this;
		
	}
	
	public Formula addNodes(LinkedList<Node> nodes) {
		
		this.nodes.clear();
		
		if(nodes.contains(this))
			throw new PropositionalLogicException("Una premisa no se puede contener así misma.");
		
		if(nodes.size() > 3 || nodes.size() == 2)
			throw new PropositionalLogicException("La formula no tiene coherencia.");
		
		if(nodes.size() == 1) {
			
			nodes.get(0).setMaster(this);
			
			if(nodes.get(0).isFormula() && nodes.get(0).getFormula().isPremise())
				this.nodes = nodes.get(0).getFormula().getNodes();
			else
				this.nodes = nodes;
			
		} else {
			
			for(Node node : nodes) {
				node.setMaster(this);
				this.nodes.add(node);
			}
			
		}
		
		return this;
		
	}
	
	public Formula changeAttached(LinkedList<Action> attached) {
		
		attached.stream().forEach(k -> k.setMaster(this));
		this.attached = attached;
		
		return this;
		
	}
	
	public Formula changePrimary(ActionType action) {
		
		if(isPremise())
			this.nodes.get(1).getAction().setType(action);
			
		return this;
		
	}
	
	public Formula setNode(int index, Node node) {
		this.nodes.set(index, node.setMaster(this));
		return this;
	}
	
	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	public LinkedList<Action> getAttached() {
		return attached;
	}
	
	public LinkedList<Node> getNodes(){
		return nodes;
	}
	
	public Formula swapOpposite() {
		
		if(isNegative())
			this.attached.removeFirst();
		else
			this.attached.addFirst(new Action(ActionType.NO).setMaster(this).getAction());
		
		return this;
		
	}
	
	public boolean isNegative() {
		
		if(attached.size() == 0)
			return false;
		
		Formula formula = FormulaCalculator.getCalculator().doubleDenial(this.clone(), false, true);
		
		if(formula.getAttached().size() == 0)
			return false;
		
		if(formula.getAttached().get(0).getAction().getType() == ActionType.NO)
			return true;
		
		return false;
		
	}
	
	public boolean isPremise() {
		return nodes.size() == 3;
	}
	
	public boolean isAtomicPremise() {
		return isPremise() && !FormulaBalancer.haveFormulas(this);
	}
	
	public boolean contains(Node node) {
		
		for(Node n : nodes)
			if(n.equals(node)) {
				return true;
			} else if(n.isFormula())
				        if(n.getFormula().contains(node))
				        	 return true;
		
		return nodes.contains(node);
		
	}
	
	public boolean containsReplacer(Node node) {
		
		for(Node n : nodes)
			if(n.equals(node)) {
				
				nodes.set(nodes.indexOf(n), node);
				return nodes.contains(node);
				
			} else if(n.isFormula())
						if(n.getFormula().containsReplacer(node))
							return true;
		
		return nodes.contains(node);
		
	}
	
	public LinkedList<Formula> similarFormulas(Formula formula) {
		
		LinkedList<Formula> result = new LinkedList<Formula>();
		
		if(this.equals(formula))
			result.add(this);
		
		for(Node n : nodes)
			if(n.isFormula() &&  n.equals(formula))
				result.add(n.toFormula());
			else if(n.isFormula())
				result.addAll(n.getFormula().similarFormulas(formula));
		
		return result;
		
	}
	
	public boolean containsAlmost(Node node) {
		
		for(Node n : nodes)
			if(n.isFormula()) {
				
				if(n.equals(node) || n.getFormula().containsAlmost(node))
					return true;
					
			} else if(n.equals(node))
				return true;
		
		return nodes.contains(node);
		
	}
	
	public LinkedList<Constant> getAllConstant(boolean ignoreQuantifier){
		
		LinkedList<Constant> cache = new LinkedList<Constant>();
		
		for(Node n : nodes)
			if(n.isFormula()) {
				if(ignoreQuantifier) {
					if(!n.getFormula().getPrimary(true).getAction().getType().is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER))
						cache.addAll(n.getFormula().getAllConstant(ignoreQuantifier));
				} else {
					cache.addAll(n.getFormula().getAllConstant(ignoreQuantifier));
				}
			} else if(n.isVariable()) {
				cache.addAll(n.getVariable().getConstants());
			} else if(n.isConstant()) {
				cache.add(n.getConstant());
			}
		
		return cache;
		
	}
	
	public LinkedList<Constant> getAllConstant(boolean ignoreQuantifier, Predicate<Formula> filter){
		
		LinkedList<Constant> cache = new LinkedList<Constant>();
		
		for(Node n : nodes)
			if(n.isFormula() && filter.test(n.getFormula())) {
				if(ignoreQuantifier) {
					if(!n.getFormula().getPrimary(true).getAction().getType().is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER))
						cache.addAll(n.getFormula().getAllConstant(ignoreQuantifier));
				} else {
					cache.addAll(n.getFormula().getAllConstant(ignoreQuantifier));
				}
			} else if(n.isVariable()) {
				cache.addAll(n.getVariable().getConstants());
			} else if(n.isConstant()) {
				cache.add(n.getConstant());
			}
		
		return cache;
		
	}
	
	public LinkedList<Formula> getQuantifiedFormulas(){
		
		LinkedList<Formula> cache = new LinkedList<Formula>();
		
		for(Node n : nodes)
			if(n.isFormula()) {
				if(n.getFormula().getPrimary(true).getAction().getType().is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER)) {
					cache.add(n.getFormula());
					cache.addAll(n.getFormula().getQuantifiedFormulas());
				}
			}
		
		return cache;
		
	}
	
	public LinkedList<Formula> getQuantifiedFormulas(Predicate<Formula> filter){
		
		LinkedList<Formula> cache = new LinkedList<Formula>();
		
		for(Node n : nodes)
			if(n.isFormula())
				if(n.getFormula().getPrimary(true).getAction().getType().is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER))
					if(filter.test(n.getFormula())) {
						cache.add(n.getFormula());
						cache.addAll(n.getFormula().getQuantifiedFormulas());
					}
		
		return cache;
		
	}
	
	public Formula toPrimitiveFormula() {
		
		if(this.nodes.size() == 1)
			if(this.nodes.get(0).isFormula())
				return this.nodes.get(0).getFormula().toPrimitiveFormula();
		
		return this;
		
	}
	
	@Override
	public Formula clone() {
		return new Formula(attached, nodes);
	}
	
	@Override
	public boolean equals(Object object) {
		
		boolean value = true;
		
		if(object instanceof Node) {
			
			Node node = (Node) object;
			
			if(!node.isFormula())
				return false;
			
			if(node.getFormula() == this)
				value = true;
			
			if(node.getUniqueID() == uniqueID)
				value = true;
			
			Formula formula = node.getFormula();
			
			if(formula.getNodes().equals(this.getNodes()))
				value = true;
			
			if(formula.getNodes().size() != this.getNodes().size())
				return false;
			
			for(int i = 0; i < formula.getNodes().size(); i++)
				if(!formula.getNodes().get(i).equals(this.getNodes().get(i)))
					return false;
			
			if(formula.getAttached().equals(this.getAttached()))
				value = true;

			if(formula.getAttached().size() != this.getAttached().size())
				return false;
			
			for(int i = 0; i < formula.getAttached().size(); i++)
				if(!formula.getAttached().get(i).equals(this.getAttached().get(i)))
					return false;
					
			return value;
			
		}
		
		return false;
		
	}
	
	@Override
	public boolean isFormula() {
		return true;
	}
	
	public boolean isAtomic() {
		return nodes.size() == 1;
	}
	
	@Override
	public String toString() {
		return FormulaBuilder.getBuilder().getNodeFormula(this, true);
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
		return true;
	}
	
}
