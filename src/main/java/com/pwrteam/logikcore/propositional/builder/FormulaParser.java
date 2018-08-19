package com.pwrteam.logikcore.propositional.builder;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.pwrteam.logikcore.propositional.builder.parser.ParserStatus;
import com.pwrteam.logikcore.propositional.bundle.type.CategoryType.RuleType;
import com.pwrteam.logikcore.propositional.exception.PropositionalLogicException;
import com.pwrteam.logikcore.propositional.formula.Action;
import com.pwrteam.logikcore.propositional.formula.Constant;
import com.pwrteam.logikcore.propositional.formula.Formula;
import com.pwrteam.logikcore.propositional.formula.Variable;
import com.pwrteam.logikcore.propositional.node.Node;
import com.pwrteam.logikcore.propositional.node.Node.ActionType;

public class FormulaParser {

	public static Formula fromString(String str) {
		
		str = "(" + str.replaceAll("\\{",  "(")
				 .replaceAll("}", ")")
				 .replaceAll("\\[", "(")
				 .replaceAll("]", ")") + ")";
		
		Formula result = new Formula();
		
		LinkedList<Action> consumeAttached = new LinkedList<Action>();
		
		LinkedList<Action> cacheActions = new LinkedList<Action>();
		LinkedList<Variable> cacheVariables = new LinkedList<Variable>();
		LinkedList<Formula> cacheFormulas = new LinkedList<Formula>();
		
		Formula lastFormula = result;
		cacheFormulas.add(result);
		
		Node lastNode = null;
		
		for(int i = 0; i < str.length(); i++) {
			
			char ch = str.charAt(i);
			String token = String.valueOf(ch);
			
			for(ActionType act : ActionType.values())
				if(!act.is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER)) {
					if(act.getIdentifier().equals(token)) {
						
						if(act.isAttached()) {
							
							lastNode = new Action(act);
							consumeAttached.addFirst(lastNode.getAction());
							
							continue;
							
						}
						
						lastNode = new Action(act);
						
						lastFormula.injectNode(lastNode);
						cacheActions.add(lastNode.getAction());
						
						continue;
						
					}
				}
				
			if(token.matches("[A-Z]")) {
				
				lastNode = new Variable(ch);
				cacheVariables.add(lastNode.getVariable());
				
				if(consumeAttached.size() > 0) {
					
					Formula subf = new Formula();
					
					consumeAttached.stream().forEach(k -> subf.addAttach(k));
					consumeAttached.clear();
					
					subf.injectNode(lastNode);
					lastFormula.injectNode(subf.toFormula());
					continue;
					
				}
				
				lastFormula.injectNode(lastNode.toFormula());
				continue;
				
			}
			
			if(token.matches("[a-z]")) {
				
				lastNode = new Constant(ch);
				cacheVariables.getLast().addConstant(lastNode.getConstant());
				
				continue;
				
			}
			
			if(ch == '(') {
				
				if(i + 3 < str.length() && str.charAt(i + 1) == '∃' && str.charAt(i + 3) == ')') {
					
					lastNode = new Action(ActionType.EXISTENTIAL_QUANTIFIER).setQuantifier(new Constant(str.charAt(i + 2)));
					consumeAttached.addFirst(lastNode.getAction());
					
					i += 3;
					continue;
					
				}
				
				if(i + 3 < str.length() && str.charAt(i + 1) == '∀' && str.charAt(i + 3) == ')') {
					
					lastNode = new Action(ActionType.UNIVERSAL_QUANTIFIER).setQuantifier(new Constant(str.charAt(i + 2)));
					consumeAttached.addFirst(lastNode.getAction());
					
					i += 3;
					continue;
					
				}
				
				Formula subf = new Formula();
				
				consumeAttached.stream().forEach(k -> subf.addAttach(k));
				consumeAttached.clear();
				
				lastFormula = subf;
				cacheFormulas.add(subf);
				continue;
				
			}
			
			if(token.matches("\\)")) {
				
				Formula cacheF = cacheFormulas.getLast();
				cacheFormulas.removeLast();
				
				lastFormula = cacheFormulas.getLast();
				lastFormula.injectNode(cacheF.toFormula());
				continue;
				
			}
			
		}
		
		//System.out.println("SIZE: " + cacheFormulas.size());
		//System.out.println("RESULT: " + cacheFormulas.toString());
		
		return cacheFormulas.getFirst().toPrimitiveFormula();
		
	}
	
	public static ParserStatus checkToken(String all, int pos) {
		
		if(pos + 1 >= all.length())
			return ParserStatus.NULL;
		
		String token = String.valueOf(all.charAt(pos + 1));
		
		if(token.matches("\\)"))
			return ParserStatus.IN_CLOSE_BRACKETS;
		
		if(token.matches("\\(")) {
			
			if(pos + 4 < all.length() && all.charAt(pos + 2) == '∃' && all.charAt(pos + 4) == ')')
				return ParserStatus.IN_EXISTENTIAL_QUANTIFIER;
			
			if(pos + 4 < all.length() && all.charAt(pos + 2) == '∀' && all.charAt(pos + 4) == ')')
				return ParserStatus.IN_UNIVERSAL_QUANTIFIER;
			
			return ParserStatus.IN_OPEN_BRACKETS;
			
		}
		
		for(ActionType act : ActionType.values())
			if(!act.is(ActionType.EXISTENTIAL_QUANTIFIER, ActionType.UNIVERSAL_QUANTIFIER))
				if(act.getIdentifier().equals(token))
					if(act.isAttached())
						return ParserStatus.IN_ATTACHED;
					else
						return ParserStatus.IN_ACTION;
		
		return ParserStatus.NULL;
		
	}
	
	public static Entry<RuleType, String> getRuleInfo(String rulestr){
		
		String instruction = rulestr.split(" ")[0];
		RuleType checkType = RuleType.fromID(rulestr.split(" ")[1]);
		
		if(checkType == null)
			throw new PropositionalLogicException("La regla está mal formulada.");
		
		return new AbstractMap.SimpleEntry<RuleType, String>(checkType, instruction);
		
	}
	
}
