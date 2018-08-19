package com.magmaticcode.logikcore.propositional.bundle.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.magmaticcode.logikcore.propositional.builder.FormulaBalancer;
import com.magmaticcode.logikcore.propositional.builder.FormulaBuilder;
import com.magmaticcode.logikcore.propositional.formula.Action;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.node.Node;

public enum ReplacementRuleType implements CategoryType.Rule {
	
	TAUTOLOGY(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllFormulas(cache, (formula) -> formula.getPrimary(false).isAction(), false)) {
				
				List<Formula> taut = new ArrayList<Formula>();
				
				if(f.isPremise())
					if(f.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR)) {
						
						if(f.getNode(0).toFormula().equals(f.getNode(2).toFormula()))
							taut.add(new Formula(f.getAttached(), 
								    			 f.getNode(0)));
						
						for(Formula changeto : taut)
							for(Formula replaceto : cache.similarFormulas(f)) {
								
								changeto = changeto.toPrimitiveFormula();
								replaceto = replaceto.toPrimitiveFormula();
								
								Formula copy = replaceto.clone();
								replaceto.changeAttached(changeto.getAttached());
								replaceto.changeNodes(changeto.getNodes());
								
								if(cache.equals(f2))
									return true;
								
								replaceto.changeNodes(copy.getNodes());
								continue;
								
							}
						
					}
				
			}
			
			return false;
			
		}
		
	}),
	
	COMMUTATIVITY(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			if(f1.isAtomicPremise()) {
				if(f1.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR)) {
					Formula swap = new Formula(f1.getAttached(), f1.getNodes().get(2), f1.getNodes().get(1), f1.getNodes().get(0));
					return swap.equals(f2);
				}
				
				return false;
				
			}
			
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(false).isAction(), false)) {
				
				if(f.isPremise()) {
					if(f.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR)) {
						
						Formula swap = new Formula(f.getAttached(), f.getNodes().get(2), f.getNodes().get(1), f.getNodes().get(0));
						Formula copy = f.clone();
						
						cache.containsReplacer(f);
						f.changeNodes(swap.getNodes());
						
						if(!cache.equals(f2)) {
							
							f.changeNodes(copy.getNodes());
							continue;
							
						}
						
						return true;
						
					}
					
				}
				
			}
			
			return false;
		}
		
	}),
	
	TRANSPOSITION(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {

			Formula cache = f1.clone();
			LinkedList<Formula> premises = FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(false).isAction(), false);
			
			for(Formula f : premises) {
				
				List<Formula> trans = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					
					if(f.getPrimary(false).getAction().getType().is(Node.ActionType.SO)) {
						
						trans.add(new Formula(f.clone().getAttached(), 
										      f.getNode(2).toFormula().clone().swapOpposite(),
										      Node.ActionType.SO.getAction(),
										      f.getNode(0).toFormula().clone().swapOpposite()));
						
					}
					
					for(Formula changeto : trans) {
						
						cache.containsReplacer(f);
						Formula copy = f.clone();
						
						f.changeAttached(changeto.getAttached());
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeAttached(copy.getAttached());
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
		}
		
	}),
	
	DISTRIBUTION(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			FormulaBuilder builder = FormulaBuilder.getBuilder();
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(true).isAction() && formula.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR), false)) {
				
				List<Formula> dist = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					
					if(f.getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
						
						if(f.getNode(0).toFormula().isPremise() && f.getNode(2).toFormula().isPremise() && f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR) && f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
							
							dist.add(new Formula(f.getAttached(), 
					                 f.getNode(0).toFormula().getNode(0).toFormula(),
					                 Node.ActionType.OR.getAction(),
					                 builder.action(f.getNode(0).toFormula().getNode(2).toFormula(), f.getNode(2).toFormula().getNode(2).toFormula(), Node.ActionType.AND)));
							
						} else if(f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
							
							dist.add(new Formula(f.getAttached(), 
								                 builder.action(f.getNode(0).toFormula().getNode(0).toFormula(), f.getNode(2).toFormula().getNode(0).toFormula(), Node.ActionType.AND),
								                 Node.ActionType.OR.getAction(),
								                 builder.action(f.getNode(0).toFormula().getNode(0).toFormula(), f.getNode(2).toFormula().getNode(2).toFormula(), Node.ActionType.AND)));
							
						}
						
					} else {
						
						if(f.getNode(0).toFormula().isPremise() && f.getNode(2).toFormula().isPremise() && f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND) && f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
							
							dist.add(new Formula(f.getAttached(), 
					                 f.getNode(0).toFormula().getNode(0).toFormula(),
					                 Node.ActionType.AND.getAction(),
					                 builder.action(f.getNode(0).toFormula().getNode(2).toFormula(), f.getNode(2).toFormula().getNode(2).toFormula(), Node.ActionType.OR)));
							
						} else if(f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
							
							dist.add(new Formula(f.getAttached(), 
								                 builder.action(f.getNode(0).toFormula().getNode(0).toFormula(), f.getNode(2).toFormula().getNode(0).toFormula(), Node.ActionType.OR),
								                 Node.ActionType.AND.getAction(),
								                 builder.action(f.getNode(0).toFormula().getNode(0).toFormula(), f.getNode(2).toFormula().getNode(2).toFormula(), Node.ActionType.OR)));
							
						}
						
					}
					
					
					for(Formula changeto : dist) {
						
						Formula copy = f.clone();
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	ASSOCIATIVITY(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			FormulaBuilder builder = FormulaBuilder.getBuilder();
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(true).isAction() && formula.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR), false)) {
				
				List<Formula> assoc = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					
					if(f.getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
						
						if(f.getNode(0).toFormula().isPremise() && !f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
							if(f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
								
								assoc.add(new Formula(f.getAttached(), 
												      f.getNode(0).toFormula().getNode(0),
												      Node.ActionType.AND.getAction(),
												      builder.action(f.getNode(0).toFormula().getNode(2).toFormula(), f.getNode(2).toFormula(), Node.ActionType.AND)));
								
							}
						} else if(f.getNode(2).toFormula().isPremise() && !f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
							if(f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
								
								assoc.add(new Formula(f.getAttached(), 
													  builder.action(f.getNode(0).toFormula(), f.getNode(2).toFormula().getNode(0).toFormula(), Node.ActionType.AND),
									    		      Node.ActionType.AND.getAction(),
									    		      f.getNode(2).toFormula().getNode(2)));
								
							}
						}
						
					} else {
						
						if(f.getNode(0).toFormula().isPremise() && !f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
							if(f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
								
								assoc.add(new Formula(f.getAttached(), 
												      f.getNode(0).toFormula().getNode(0),
												      Node.ActionType.OR.getAction(),
												      builder.action(f.getNode(0).toFormula().getNode(2).toFormula(), f.getNode(2).toFormula(), Node.ActionType.OR)));
								
							}
						} else if(f.getNode(2).toFormula().isPremise() && !f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
							if(f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR)) {
								
								assoc.add(new Formula(f.getAttached(), 
												      builder.action(f.getNode(0).toFormula(), f.getNode(2).toFormula().getNode(0).toFormula(), Node.ActionType.OR),
									    		      Node.ActionType.OR.getAction(),
									    		      f.getNode(2).toFormula().getNode(2)));
								
							}
						}
						
					}
					
					
					for(Formula changeto : assoc) {
						
						Formula copy = f.clone();
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	DOUBLE_NEGATION(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllFormulas(cache, (formula) -> true, false)) {
				
				final LinkedList<Action> newReAttached = new LinkedList<Action>();
				final LinkedList<Action> newAdAttached = new LinkedList<Action>();
				
				if(f.getAttached().size() > 0) {
					
					boolean founded = false;
					
					K : for(int i = 0; i < f.getAttached().size(); i++) {
						
						if(!founded && (i + 1) < f.getAttached().size() && f.getAttached().get(i).isAction() && f.getAttached().get(i + 1).isAction())
							if(f.getAttached().get(i).getAction().getType() == Node.ActionType.NO)
								if(f.getAttached().get(i + 1).getAction().getType() == Node.ActionType.NO) {

									founded = true;
									i += 1;
									continue K;
									
								}
						
						newReAttached.add(f1.getAttached().get(i));
						continue K;
						
					}
					
				}
				
				newAdAttached.addAll(f.getAttached());
				
				for(int i = 0; i < 2; i++)
					newAdAttached.addFirst(Node.ActionType.NO.getAction().setMaster(f).getAction());
				
				T : for(LinkedList<Action> cacheAttached : Arrays.asList(newReAttached, newAdAttached)) {
					
					Formula copy = f.clone();
					f.changeAttached(cacheAttached);
					
					if(cache.equals(f2))
						return true;
					
					f.changeAttached(copy.getAttached());
					continue T;
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	EXPORTATION(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			FormulaBuilder builder = FormulaBuilder.getBuilder();
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllFormulas(cache, (formula) -> formula.getPrimary(false).isAction(), false)) {
				
				List<Formula> exp = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					
					if(f.getNode(0).toFormula().isPremise()) {
						if(f.getNode(0).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.AND)) {
							
							exp.add(new Formula(f.getAttached(), 
											    f.getNode(0).toFormula().getNode(0),
											    Node.ActionType.SO.getAction(),
											    builder.action(f.getNode(0).toFormula().getNode(2).toFormula(), f.getNode(2).toFormula(), Node.ActionType.SO)));
							
						}
						
					}
					
					if(f.getNode(2).toFormula().isPremise()) {
						if(f.getNode(2).toFormula().getPrimary(true).getAction().getType().is(Node.ActionType.SO)) {
							
							exp.add(new Formula(f.getAttached(), 
												builder.action(f.getNode(0).toFormula(), f.getNode(2).toFormula().getNode(0).toFormula(), Node.ActionType.AND),
												Node.ActionType.SO.getAction(),
												f.getNode(2).toFormula().getNode(2)));
							
						}
					}
					
					for(Formula changeto : exp)
						for(Formula replaceto : cache.similarFormulas(f)) {
							
							Formula copy = replaceto.clone();
							replaceto.changeNodes(changeto.getNodes());
							
							if(cache.equals(f2))
								return true;
							
							replaceto.changeNodes(copy.getNodes());
							continue;
							
						}
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	MATERIAL_EQUIVALENCE(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			FormulaBuilder builder = FormulaBuilder.getBuilder();
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(false).isAction() && formula.getPrimary(false).getAction().getType().is(Node.ActionType.BICONDITIONAL, Node.ActionType.AND, Node.ActionType.OR), false)) {
				
				if(f.isPremise()) {
					
					List<Formula> matequiv = new ArrayList<Formula>();
					
					switch(f.getPrimary(false).getAction().getType()) {
					
						case BICONDITIONAL: {
							
							matequiv.add(new Formula(f.getAttached(), 
												     builder.action(f.getNodes().get(0).toFormula(), f.getNodes().get(2).toFormula(), Node.ActionType.SO),
												     Node.ActionType.AND.getAction(),
												     builder.action(f.getNodes().get(2).toFormula(), f.getNodes().get(0).toFormula(), Node.ActionType.SO)));
							matequiv.add(new Formula(f.getAttached(),
													 builder.action(f.getNodes().get(0).toFormula(), f.getNodes().get(2).toFormula(), Node.ActionType.AND),
													 Node.ActionType.OR.getAction(),
												     builder.action(f.getNodes().get(0).toFormula().clone().swapOpposite(), f.getNodes().get(2).toFormula().clone().swapOpposite(), Node.ActionType.AND)));
							
							break;
						}
						
						case AND: {
							
							if(f.getNode(0).isFormula() && f.getNode(2).isFormula())
								if(f.getNode(0).getFormula().getPrimary(false).getAction().getType().is(Node.ActionType.SO))
									if(f.getNode(2).getFormula().getPrimary(false).getAction().getType().is(Node.ActionType.SO))
										if(f.getNode(0).getFormula().getNode(0).equals(f.getNode(2).getFormula().getNode(2)))
											if(f.getNode(0).getFormula().getNode(2).equals(f.getNode(2).getFormula().getNode(0)))
												matequiv.add(new Formula(f.getAttached(),
														 				 f.getNode(0).getFormula().getNode(0).toFormula(),
														 				 Node.ActionType.BICONDITIONAL.getAction(),
														 				 f.getNode(0).getFormula().getNode(2).toFormula()));
							
							break;
						}
						
						case OR: {
							if(f.getNode(0).isFormula() && f.getNode(2).isFormula())
								if(f.getNode(0).getFormula().getPrimary(false).getAction().getType().is(Node.ActionType.AND))
									if(f.getNode(2).getFormula().getPrimary(false).getAction().getType().is(Node.ActionType.AND))
										if(f.getNode(0).getFormula().getNode(0).equals(f.getNode(2).getFormula().getNode(0).toFormula().clone().swapOpposite()))
											if(f.getNode(0).getFormula().getNode(2).equals(f.getNode(2).getFormula().getNode(2).toFormula().clone().swapOpposite()))
												matequiv.add(new Formula(f.getAttached(),
														 				 f.getNode(0).getFormula().getNode(0).toFormula(),
														 				 Node.ActionType.BICONDITIONAL.getAction(),
														 				 f.getNode(0).getFormula().getNode(2).toFormula()));
							break;
						}
						
						default: break;
					
					}
					
					for(Formula changeto : matequiv) {
						
						Formula copy = f.clone();
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	MATERIAL_IMPLICATION(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}

		@Override
		public boolean check(Formula f1, Formula f2) {
			
			Formula cache = f1.clone();
			LinkedList<Formula> premises = FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(false).isAction(), false);
			
			for(Formula f : premises) {
				
				List<Formula> impl = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					if(f.getPrimary(false).getAction().getType().is(Node.ActionType.SO)) {
						
						impl.add(new Formula(f.clone().getAttached(), 
										     f.getNode(0).toFormula().clone().swapOpposite(),
										     Node.ActionType.OR.getAction(),
										     f.getNode(2).toFormula().clone()));
						
					} else if(f.getPrimary(false).getAction().getType().is(Node.ActionType.OR)) {
						
						impl.add(new Formula(f.clone().getAttached(), 
							     			 f.getNode(0).toFormula().clone().swapOpposite(),
							     			 Node.ActionType.SO.getAction(),
							     			 f.getNode(2).toFormula().clone()));
						
					}
					
					for(Formula changeto : impl) {
						
						cache.containsReplacer(f);
						Formula copy = f.clone();
						
						f.changeAttached(changeto.getAttached());
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeAttached(copy.getAttached());
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
		}
		
	}),
	
	DEMORGAN(new ReplacementRuleActioner() {

		public Formula action(Formula f1, Formula f2) {
			
			if(check(f1, f2))
				return f2;
				
			return f1;
			
		}
		
		@Override
		public boolean check(Formula f1, Formula f2) {
			
			Formula cache = f1.clone();
			
			for(Formula f : FormulaBalancer.getAllPremises(cache, (formula) -> formula.getPrimary(false).isAction() && formula.getPrimary(false).getAction().getType().is(Node.ActionType.AND, Node.ActionType.OR), false)) {
				
				List<Formula> dm = new ArrayList<Formula>();
				
				if(f.isPremise()) {
					
					if(f.getPrimary(false).getAction().getType().is(Node.ActionType.AND)) {
						
						dm.add(new Formula(f.clone().swapOpposite().getAttached(), 
										   f.getNode(0).toFormula().clone().swapOpposite(),
										   Node.ActionType.OR.getAction(),
										   f.getNode(2).toFormula().clone().swapOpposite()));
						
					} else {
						
						dm.add(new Formula(f.clone().swapOpposite().getAttached(), 
								   f.getNode(0).toFormula().clone().swapOpposite(),
								   Node.ActionType.AND.getAction(),
								   f.getNode(2).toFormula().clone().swapOpposite()));
						
					}
					
					for(Formula changeto : dm) {
						
						cache.containsReplacer(f);
						Formula copy = f.clone();
						
						f.changeAttached(changeto.getAttached());
						f.changeNodes(changeto.getNodes());
						
						if(cache.equals(f2))
							return true;
						
						f.changeAttached(copy.getAttached());
						f.changeNodes(copy.getNodes());
						continue;
						
					}
					
				}
				
			}
			
			return false;
			
		}
		
	}),
	
	;
	
	private ReplacementRuleActioner actioner;
	
	ReplacementRuleType(ReplacementRuleActioner actioner) {
		this.actioner = actioner;
	}
	
	public ReplacementRuleActioner getActioner() {
		return actioner;
	}
	
	public static interface ReplacementRuleActioner {
		
		public Formula action(Formula f1, Formula f2);
		
		public boolean check(Formula f1, Formula f2);
		
	}

	@Override
	public CategoryType getCategory() {
		return CategoryType.REPLACEMENT_RULE;
	}
	
}
