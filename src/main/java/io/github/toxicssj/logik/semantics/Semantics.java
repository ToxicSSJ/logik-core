package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;

/**
 * Truth-functional questions about formulas without quantifiers, answered
 * exactly with truth tables. An atom like {@code Fa} counts as one more
 * sentence letter; that is exact too, because any valuation of such atoms can
 * be realized by a model that gives each name its own individual.
 *
 * <p>For formulas with quantifiers, use {@link ModelSearch}, which can show
 * that an argument is not valid but can only give evidence that it is.
 */
public final class Semantics {

    private Semantics() {}

    public static boolean isTautology(Formula formula) {
        for (TruthTable.Row row : TruthTable.of(formula).rows()) {
            if (!row.values().get(0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSatisfiable(Formula formula) {
        for (TruthTable.Row row : TruthTable.of(formula).rows()) {
            if (row.values().get(0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContradiction(Formula formula) {
        return !isSatisfiable(formula);
    }

    /** True if both formulas have the same value under every valuation. */
    public static boolean areEquivalent(Formula a, Formula b) {
        for (TruthTable.Row row : TruthTable.of(a, b).rows()) {
            if (!row.values().get(0).equals(row.values().get(1))) {
                return false;
            }
        }
        return true;
    }

    /** True if every valuation that makes all premises true makes the conclusion true. */
    public static boolean entails(List<Formula> premises, Formula conclusion) {
        return counterexample(premises, conclusion) == null;
    }

    /** A valuation that makes every premise true and the conclusion false, or null if there is none. */
    public static Valuation counterexample(List<Formula> premises, Formula conclusion) {
        List<Formula> formulas = new ArrayList<>(premises);
        formulas.add(conclusion);
        TruthTable table = TruthTable.of(formulas);
        for (TruthTable.Row row : table.rows()) {
            List<Boolean> values = row.values();
            boolean premisesTrue = !values.subList(0, premises.size()).contains(false);
            if (premisesTrue && !values.get(premises.size())) {
                return row.valuation();
            }
        }
        return null;
    }

    /** True if {@code formula} has no quantifier, so the methods of this class apply to it. */
    public static boolean isQuantifierFree(Formula formula) {
        return switch (formula) {
            case Formula.Atom atom -> true;
            case Formula.Predicate predicate -> true;
            case Formula.Not not -> isQuantifierFree(not.operand());
            case Formula.Binary binary -> isQuantifierFree(binary.left()) && isQuantifierFree(binary.right());
            case Formula.Quantified quantified -> false;
        };
    }
}
