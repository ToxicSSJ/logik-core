package com.magmaticcode.logikcore.propositional.bundle.proof;

import java.util.LinkedList;

import com.magmaticcode.logikcore.propositional.builder.FormulaBuilder;
import com.magmaticcode.logikcore.propositional.builder.FormulaCalculator;
import com.magmaticcode.logikcore.propositional.bundle.type.CategoryType;
import com.magmaticcode.logikcore.propositional.bundle.type.ImplicationRuleType;
import com.magmaticcode.logikcore.propositional.bundle.type.QuantifierGeneralizationRuleType;
import com.magmaticcode.logikcore.propositional.bundle.type.QuantifierInstantiationRuleType;
import com.magmaticcode.logikcore.propositional.exception.PropositionalLogicException;
import com.magmaticcode.logikcore.propositional.formula.Formula;
import com.magmaticcode.logikcore.propositional.node.Node;
import com.magmaticcode.logikcore.propositional.bundle.proof.ProofLine.ProofStep;
import com.magmaticcode.logikcore.propositional.bundle.type.ReplacementRuleType;

public class Proof {

	public static final FormulaBuilder builder = FormulaBuilder.getBuilder();
	public static final FormulaCalculator calculator = FormulaCalculator.getCalculator();
	
	private Formula conclusion;
	private LinkedList<Formula> premises;
	
	protected LinkedList<ProofLine> proofs = new LinkedList<ProofLine>();
	
	public Proof(LinkedList<Formula> premises, Formula conclusion) {
		
		this.setPremises(premises);
		this.setConclusion(conclusion);
		
	}
	
	public Proof addLine(ProofLine line) {
		
		proofs.add(line);
		return this;
		
	}
	
	public ProofResult build(boolean debug) {
		
		int index = 0;

		LinkedList<Formula> tested = new LinkedList<>(premises);
		
		if(proofs.isEmpty())
			throw new PropositionalLogicException("No hay pasos que seguir para hallar la conclusión.");
		
		if(debug) {
			
			for(index = 0; index < premises.size(); index++) {
				
				System.out.print("\t" + (index + 1) + ". " + premises.get(index).getFormula());
				
				if(index + 1 >= premises.size())
					System.out.print("\t\t\t\t\t\t ∴// " + conclusion);
				
				System.out.println();
				
			}
			
		}
		
		for(ProofLine proof : proofs) {
			
			LinkedList<ProofStep> steps = proof.getSteps();
			CategoryType.RuleType rule = proof.getType();
			
			for(ProofStep step : steps)
				if(step.getStep() > tested.size())
					throw new PropositionalLogicException("El paso actual referencia a un paso aún no existente.");
			
			T : switch(rule) {
			
				case CONJUNCTION: {
					
					Formula conjunction = calculator.conjunction(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1));
					
					if(proof.getFormula().equals(conjunction))
						tested.add(conjunction);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case SIMPLIFICATION: {
					
					Formula simplification = calculator.simplification(tested.get(steps.get(0).getStep() - 1));
					
					if(proof.getFormula().equals(simplification))
						tested.add(simplification);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case ADDITION: {
					
					Formula who = null;
					
					if(proof.getFormula().getPrimary(true).isAction())
						if(proof.getFormula().getPrimary(true).getAction().getType().is(Node.ActionType.OR))
							who = proof.getFormula().getNode(2).toFormula();
					
					Formula addition = calculator.addition(tested.get(steps.get(0).getStep() - 1), who);
					
					if(proof.getFormula().equals(addition))
						tested.add(addition);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case MODUS_PONENDO_PONENS: {
					
					Formula modusponens = calculator.implication(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1), ImplicationRuleType.MODUS_PONENDO_PONENS);
					
					if(proof.getFormula().equals(modusponens))
						tested.add(modusponens);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case MODUS_PONENDO_TOLLENS: {
					
					Formula modustollens = calculator.implication(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1), ImplicationRuleType.MODUS_PONENDO_TOLLENS);
					
					if(proof.getFormula().equals(modustollens))
						tested.add(modustollens);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case DISJUNCTIVE_SYLLOGISM: {
					
					Formula ds = calculator.implication(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1), ImplicationRuleType.DISJUNCTIVE_SYLLOGISM);
					
					if(proof.getFormula().equals(ds))
						tested.add(ds);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case HYPOTETICAL_SYLLOGISM: {
					
					Formula hs = calculator.implication(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1), ImplicationRuleType.HYPOTETICAL_SYLLOGISM);
					
					if(proof.getFormula().equals(hs))
						tested.add(hs);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case CONSTRUCTIVE_DILEMA: {
					
					Formula cd = calculator.implication(tested.get(steps.get(0).getStep() - 1), tested.get(steps.get(1).getStep() - 1), ImplicationRuleType.CONSTRUCTIVE_DILEMA);
					
					if(proof.getFormula().equals(cd))
						tested.add(cd);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case DEMORGAN: {
					
					Formula demorgan = ReplacementRuleType.DEMORGAN.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(demorgan.equals(proof.getFormula()))
						tested.add(demorgan);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case COMMUTATIVITY: {
					
					Formula comm = ReplacementRuleType.COMMUTATIVITY.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(comm.equals(proof.getFormula()))
						tested.add(comm);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case ASSOCIATIVITY: {
					
					Formula assoc = ReplacementRuleType.ASSOCIATIVITY.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(assoc.equals(proof.getFormula()))
						tested.add(assoc);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case DISTRIBUTION: {
					
					Formula dist = ReplacementRuleType.DISTRIBUTION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(dist.equals(proof.getFormula()))
						tested.add(dist);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case DOUBLE_NEGATION: {
					
					Formula dn = ReplacementRuleType.DOUBLE_NEGATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(dn.equals(proof.getFormula()))
						tested.add(dn);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case TRANSPOSITION: {
					
					Formula trans = ReplacementRuleType.TRANSPOSITION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(trans.equals(proof.getFormula()))
						tested.add(trans);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case MATERIAL_IMPLICATION: {
					
					Formula impl = ReplacementRuleType.MATERIAL_IMPLICATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(impl.equals(proof.getFormula()))
						tested.add(impl);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case MATERIAL_EQUIVALENCE: {
					
					Formula equiv = ReplacementRuleType.MATERIAL_EQUIVALENCE.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(equiv.equals(proof.getFormula()))
						tested.add(equiv);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case EXPORTATION: {
					
					Formula exp = ReplacementRuleType.EXPORTATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(exp.equals(proof.getFormula()))
						tested.add(exp);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case TAUTOLOGY: {
					
					Formula taut = ReplacementRuleType.TAUTOLOGY.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(taut.equals(proof.getFormula()))
						tested.add(taut);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case UNIVERSAL_INSTANTIATION: {
					
					Formula ui = QuantifierInstantiationRuleType.UNIVERSAL_INSTATIATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());
					
					if(ui.equals(proof.getFormula()))
						tested.add(ui);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}
				
				case EXISTENTIAL_INSTANTIATION: {
					
					Formula ei = QuantifierInstantiationRuleType.EXISTENTIAL_INSTATIATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula(), tested);
					
					if(ei.equals(proof.getFormula()))
						tested.add(ei);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");
					
					break T;
					
				}

				case UNIVERSAL_GENERALIZATION: {

					Formula ug = QuantifierGeneralizationRuleType.UNIVERSAL_GENERALIZATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());

					if(ug.equals(proof.getFormula()))
						tested.add(ug);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");

					break T;

				}

                case EXISTENTIAL_GENERALIZATION: {

					Formula eg = QuantifierGeneralizationRuleType.EXISTENTIAL_GENERALIZATION.getActioner().action(tested.get(steps.get(0).getStep() - 1), proof.getFormula());

					if(eg.equals(proof.getFormula()))
						tested.add(eg);
					else throw new PropositionalLogicException("El paso " + (tested.size() + 1) + " es incorrecto!");

					break T;

				}
				default: break T;
		
			}
			
			index++;
			
			if(debug)
				System.out.print("\t" + (index) + ". " + tested.getLast().getFormula() + "\t" + steps + " " + rule.name());
			
			if(tested.getLast().getFormula().equals(conclusion)) {
				
				if(debug)
					System.out.println("\t\t <------ SE HA LLEGADO A LA CONCLUSIÓN! ");
				
				return new ProofResult(ProofResult.ResultType.CORRECT);
				
			}
			
			if(debug)
				System.out.println();
			
		}
		
		return new ProofResult(ProofResult.ResultType.BAD_RULE);
		
	}

	public LinkedList<Formula> getPremises() {
		return premises;
	}

	public void setPremises(LinkedList<Formula> premises) {
		this.premises = premises;
	}

	public Formula getConclusion() {
		return conclusion;
	}

	public void setConclusion(Formula conclusion) {
		this.conclusion = conclusion;
	}
	
}
