package com.magmaticcode.logikcore.propositional.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.node.Node;
import com.magmaticcode.logikcore.propositional.node.Node.ActionType;

public class FormulaBalancer {

	public static final int BALANCED = 1;
    public static final int UNBALANCED = 0;
	
	public static boolean areOpposites(Formula f1, Formula f2) {
		
		f1 = f1.clone();
		f2 = f2.clone();
		
		if(f1.isNegative() && !f2.isNegative())
			return true;
		
		if(f2.isNegative() && !f1.isNegative())
			return true;
		
		if(f1.getPrimary(false).getAction().getType() == f2.getPrimary(false).getAction().getType()) {
			
			LinkedList<Action> attached1 = FormulaCalculator.getCalculator().doubleDenial(f1, false, true).getAttached();
			LinkedList<Action> attached2 = FormulaCalculator.getCalculator().doubleDenial(f2, false, true).getAttached();
			
			LinkedList<LinkedList<Action>> attacheds = new LinkedList<LinkedList<Action>>();
			
			attacheds.add(attached1);
			attacheds.add(attached2);
			
			int opposite = (attached1.size() > 0 && attached1.get(0).getAction().getType() == ActionType.NO) ? 0 :
						   (attached2.size() > 0 && attached2.get(0).getAction().getType() == ActionType.NO ? 1 :
						   (-1));
			
			if(opposite == -1)
				return false;
			
			attacheds.get(opposite).removeFirst();
			
			if(attached1.size() != attached2.size())
				return false;
			
			return true;
			
		}
		
		return false;
		
	}
	
	public static LinkedList<Formula> getAllPremises(Formula f1){
		
		LinkedList<Formula> list = new LinkedList<Formula>();
		
		if(f1.isPremise())
			list.add(f1);
		
		for(Node node : f1.getNodes())
			if(node.isFormula())
				if(node.getFormula().isPremise()) {
					
					list.add(node.getFormula());
					list.addAll(getAllPremises(node.getFormula()));
					continue;
					
				}
		
		return list;
		
	}
	
	public static LinkedList<Formula> getAllPremises(Formula f1, Predicate<Formula> filter, boolean clone){
		
		LinkedList<Formula> list = new LinkedList<Formula>();
		
		if(f1.isPremise() && filter.test(f1))
			list.add(clone ? f1.clone() : f1);
		
		for(Node node : f1.getNodes())
			if(node.isFormula())
				if(node.getFormula().isPremise())
					if(filter.test(node.getFormula())) {
						list.add(clone ? node.getFormula().clone() : node.getFormula());
						list.addAll(getAllPremises(node.getFormula(), filter, clone));
						continue;
					}
		
		return list;
		
	}
	
	public static LinkedList<Formula> getAllFormulas(Formula f1, Predicate<Formula> filter, boolean clone){
		
		LinkedList<Formula> list = new LinkedList<Formula>();
		
		if(filter.test(f1))
			list.add(clone ? f1.clone() : f1);
		
		for(Node node : f1.getNodes())
			if(node.isFormula())
				if(filter.test(node.getFormula())) {
					list.add(clone ? node.getFormula().clone() : node.getFormula());
					list.addAll(getAllFormulas(node.getFormula(), filter, clone));
					continue;
				}
		
		return list;
		
	}
	
	public static LinkedList<Formula> getAtomicFormulas(Formula f1, boolean clone){
		
		LinkedList<Formula> list = new LinkedList<Formula>();
		list.add(clone ? f1.clone() : f1);
		
		for(Node node : f1.getNodes())
			if(!node.isAction())
				if(node.toFormula().isAtomic()) {
					list.add(clone ? node.getFormula().clone() : node.getFormula());
					list.addAll(getAtomicFormulas(node.getFormula(), clone));
					continue;
				}
		
		return list;
		
	}
	
	public static boolean haveFormulas(Formula f1) {
		return haveFormulas(f1.getNodes());
	}
	
	public static boolean haveFormulas(LinkedList<Node> nodes) {
		
		for(Node n : nodes)
			if(n.isFormula())
				if(!n.getFormula().isAtomic())
					return true;
		
		return false;
		
	}
	
	public static boolean areParenthesesBalanced(String str) {
		
        if(str.isEmpty()) 
        	return true;

        Stack<Character> stack = new Stack<>(str.length());
        NestedValidatorUtil util = new NestedValidatorUtil();

        for (char c : str.toCharArray()) {
            if (stack.isEmpty()){
                if (util.isOpener(c)) {
                    stack.push(c);
                } else {
                    return false;
                }
            } else {
                if(util.isOpener(c)) {
                    stack.push(c);
                } else if(util.getOpenerForGivenCloser(c) == stack.peek()){
                    stack.pop();
                } else {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    public static class NestedValidatorUtil {
    	
        @SuppressWarnings("serial")
		private Map<Character, Character> language = new HashMap<Character, Character>(){{
            put(')','(');
            put(']','[');
            put('}','{');
            }};

        public boolean isOpener(Character c) {
            return language.values().contains(c);
        }

        public Character getOpenerForGivenCloser(Character closer) {
            return language.get(closer);
        }
        
    }

    public static class Stack<T> {
          public List<T> stack;

          public Stack(int capacity) {
              stack = new ArrayList<>(capacity);
          }

          public void push(T item) {
              stack.add(item);
          }

          public T pop() {
              T item = peek();
              stack.remove(stack.size() - 1);
              return item;
          }

          public T peek() {
              int position = stack.size();
              T item = stack.get(position - 1);
              return item;
          }

          public boolean isEmpty() {
              return stack.isEmpty();
          }
          
    }
	
}
