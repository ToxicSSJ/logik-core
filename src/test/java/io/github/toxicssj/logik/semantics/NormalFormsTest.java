package io.github.toxicssj.logik.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.toxicssj.logik.corpus.Corpus;
import io.github.toxicssj.logik.corpus.ProofCase;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Problem;

/** Property tests: each normal form has its shape and is equivalent to the formula, under every valuation. */
class NormalFormsTest {

    /** 500 random formulas over P, Q, R and S, always the same ones. */
    static List<Formula> randomFormulas() {
        Random random = new Random(20261026L);
        List<Formula> formulas = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            formulas.add(random(random, 4));
        }
        return formulas;
    }

    /** Every formula without quantifiers in the corpus: premises, conclusions and steps. */
    static List<Formula> corpusFormulas() {
        List<Formula> formulas = new ArrayList<>();
        for (ProofCase proofCase : Corpus.proofCases()) {
            Problem problem = Problem.parse(proofCase.problem()).orThrow();
            List<Formula> all = new ArrayList<>(problem.premises());
            all.add(problem.conclusion());
            for (ProofCase.Step step : proofCase.steps()) {
                all.add(Formula.parse(step.formula()));
            }
            for (Formula formula : all) {
                if (Semantics.isQuantifierFree(formula) && !formulas.contains(formula)) {
                    formulas.add(formula);
                }
            }
        }
        return formulas;
    }

    @ParameterizedTest
    @MethodSource({"randomFormulas", "corpusFormulas"})
    void normalFormsAreEquivalent(Formula formula) {
        Formula cnf = NormalForms.cnf(formula);
        Formula dnf = NormalForms.dnf(formula);
        Formula nnf = NormalForms.nnf(formula);
        assertTrue(NormalForms.isCnf(cnf), () -> "not in CNF: " + cnf);
        assertTrue(NormalForms.isDnf(dnf), () -> "not in DNF: " + dnf);
        assertTrue(Semantics.areEquivalent(formula, cnf), () -> formula + " and its CNF " + cnf);
        assertTrue(Semantics.areEquivalent(formula, dnf), () -> formula + " and its DNF " + dnf);
        assertTrue(Semantics.areEquivalent(formula, nnf), () -> formula + " and its NNF " + nnf);
    }

    @Test
    void examples() {
        Formula f = Formula.parse("P → (Q & R)");
        assertEquals(Formula.parse("(~P | Q) & (~P | R)"), NormalForms.cnf(f));
        assertEquals(Formula.parse("~P | (Q & R)"), NormalForms.dnf(f));
        assertEquals(Formula.parse("(∃x)~Fx | (∀y)Gy"), NormalForms.nnf(Formula.parse("(∀x)Fx → (∀y)Gy")));
    }

    @Test
    void cnfNeedsAFormulaWithoutQuantifiers() {
        assertThrows(IllegalArgumentException.class, () -> NormalForms.cnf(Formula.parse("(∀x)Fx")));
    }

    private static Formula random(Random random, int depth) {
        int kind = depth == 0 ? 0 : random.nextInt(7);
        return switch (kind) {
            case 0, 1 -> new Formula.Atom("PQRS".charAt(random.nextInt(4)));
            case 2 -> new Formula.Not(random(random, depth - 1));
            case 3 -> new Formula.And(random(random, depth - 1), random(random, depth - 1));
            case 4 -> new Formula.Or(random(random, depth - 1), random(random, depth - 1));
            case 5 -> new Formula.Implies(random(random, depth - 1), random(random, depth - 1));
            default -> new Formula.Iff(random(random, depth - 1), random(random, depth - 1));
        };
    }
}
