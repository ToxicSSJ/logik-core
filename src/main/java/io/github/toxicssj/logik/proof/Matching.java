package io.github.toxicssj.logik.proof;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.Binary;
import io.github.toxicssj.logik.syntax.Formula.Connective;
import io.github.toxicssj.logik.syntax.Formula.Not;
import io.github.toxicssj.logik.syntax.Formula.Quantified;
import io.github.toxicssj.logik.syntax.Term;
import io.github.toxicssj.logik.syntax.Variable;

/** How formulas are compared in each {@link Mode}. */
final class Matching {

    private Matching() {}

    /** Strict: equal. Flexible: equal up to double negation, commutation and bound-variable names. */
    static boolean same(Formula a, Formula b, Mode mode) {
        if (mode == Mode.STRICT) {
            return a.equals(b);
        }
        return key(a, new ArrayList<>()).equals(key(b, new ArrayList<>()));
    }

    /**
     * The ways a rule may read a formula. Strict: as written. Flexible: without
     * leading double negations, and with the sides of {@code &}, {@code |} or
     * {@code ↔} in either order.
     */
    static List<Formula> views(Formula formula, Mode mode) {
        if (mode == Mode.STRICT) {
            return List.of(formula);
        }
        Formula bare = formula;
        while (bare instanceof Not(Not(Formula inner))) {
            bare = inner;
        }
        if (bare instanceof Binary binary && binary.connective() != Connective.IMPLIES) {
            return List.of(bare, Binary.of(binary.connective(), binary.right(), binary.left()));
        }
        return List.of(bare);
    }

    /**
     * A text that two formulas share exactly when they are the same in flexible
     * mode: double negations dropped, bound variables numbered by binder, and the
     * sides of commutative connectives sorted.
     */
    private static String key(Formula formula, List<Variable> bound) {
        return switch (formula) {
            case Formula.Atom atom -> String.valueOf(atom.letter());
            case Formula.Predicate predicate -> {
                StringBuilder out = new StringBuilder().append(predicate.letter());
                for (Term term : predicate.terms()) {
                    int binder = term instanceof Variable variable ? bound.lastIndexOf(variable) : -1;
                    out.append(binder >= 0 ? "#" + binder + "." : term.name());
                }
                yield out.toString();
            }
            case Not(Not(Formula inner)) -> key(inner, bound);
            case Not not -> "~" + key(not.operand(), bound);
            case Binary binary -> {
                String left = key(binary.left(), bound);
                String right = key(binary.right(), bound);
                if (binary.connective() != Connective.IMPLIES && left.compareTo(right) > 0) {
                    String swap = left;
                    left = right;
                    right = swap;
                }
                yield "(" + left + " " + binary.connective().symbol() + " " + right + ")";
            }
            case Quantified quantified -> {
                bound.add(quantified.variable());
                String body = key(quantified.body(), bound);
                bound.remove(bound.size() - 1);
                yield "(" + quantified.quantifier().symbol() + ")" + body;
            }
        };
    }
}
