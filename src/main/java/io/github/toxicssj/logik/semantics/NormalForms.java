package io.github.toxicssj.logik.semantics;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.And;
import io.github.toxicssj.logik.syntax.Formula.Exists;
import io.github.toxicssj.logik.syntax.Formula.ForAll;
import io.github.toxicssj.logik.syntax.Formula.Not;
import io.github.toxicssj.logik.syntax.Formula.Or;

/**
 * Negation, conjunctive and disjunctive normal forms. Each result is
 * equivalent to its input; CNF and DNF can be exponentially larger.
 */
public final class NormalForms {

    private NormalForms() {}

    /**
     * Negation normal form: only {@code ~ & |} and the quantifiers, with
     * {@code ~} applied to atoms only. Works with quantifiers too, turning
     * {@code ~(∀x)} into {@code (∃x)~} and {@code ~(∃x)} into {@code (∀x)~}.
     */
    public static Formula nnf(Formula formula) {
        return positive(formula);
    }

    /** Conjunctive normal form: a conjunction of disjunctions of literals. Needs a formula without quantifiers. */
    public static Formula cnf(Formula formula) {
        requireQuantifierFree(formula, "CNF");
        return conjunctive(nnf(formula));
    }

    /** Disjunctive normal form: a disjunction of conjunctions of literals. Needs a formula without quantifiers. */
    public static Formula dnf(Formula formula) {
        requireQuantifierFree(formula, "DNF");
        return disjunctive(nnf(formula));
    }

    /** True if {@code formula} is a conjunction of disjunctions of literals. */
    public static boolean isCnf(Formula formula) {
        if (formula instanceof And(Formula left, Formula right)) {
            return isCnf(left) && isCnf(right);
        }
        return isClause(formula, true);
    }

    /** True if {@code formula} is a disjunction of conjunctions of literals. */
    public static boolean isDnf(Formula formula) {
        if (formula instanceof Or(Formula left, Formula right)) {
            return isDnf(left) && isDnf(right);
        }
        return isClause(formula, false);
    }

    private static boolean isClause(Formula formula, boolean disjunctive) {
        if (disjunctive && formula instanceof Or(Formula left, Formula right)) {
            return isClause(left, true) && isClause(right, true);
        }
        if (!disjunctive && formula instanceof And(Formula left, Formula right)) {
            return isClause(left, false) && isClause(right, false);
        }
        return isLiteral(formula);
    }

    private static boolean isLiteral(Formula formula) {
        Formula atom = formula instanceof Not(Formula operand) ? operand : formula;
        return atom instanceof Formula.Atom || atom instanceof Formula.Predicate;
    }

    private static Formula positive(Formula formula) {
        return switch (formula) {
            case Formula.Atom atom -> atom;
            case Formula.Predicate predicate -> predicate;
            case Not not -> negative(not.operand());
            case And and -> new And(positive(and.left()), positive(and.right()));
            case Or or -> new Or(positive(or.left()), positive(or.right()));
            case Formula.Implies implies -> new Or(negative(implies.left()), positive(implies.right()));
            case Formula.Iff iff -> new And(
                    new Or(negative(iff.left()), positive(iff.right())),
                    new Or(negative(iff.right()), positive(iff.left())));
            case ForAll all -> new ForAll(all.variable(), positive(all.body()));
            case Exists some -> new Exists(some.variable(), positive(some.body()));
        };
    }

    /** The negation normal form of {@code ~formula}. */
    private static Formula negative(Formula formula) {
        return switch (formula) {
            case Formula.Atom atom -> new Not(atom);
            case Formula.Predicate predicate -> new Not(predicate);
            case Not not -> positive(not.operand());
            case And and -> new Or(negative(and.left()), negative(and.right()));
            case Or or -> new And(negative(or.left()), negative(or.right()));
            case Formula.Implies implies -> new And(positive(implies.left()), negative(implies.right()));
            case Formula.Iff iff -> new Or(
                    new And(positive(iff.left()), negative(iff.right())),
                    new And(negative(iff.left()), positive(iff.right())));
            case ForAll all -> new Exists(all.variable(), negative(all.body()));
            case Exists some -> new ForAll(some.variable(), negative(some.body()));
        };
    }

    /** CNF of a formula already in negation normal form. */
    private static Formula conjunctive(Formula formula) {
        return switch (formula) {
            case And and -> new And(conjunctive(and.left()), conjunctive(and.right()));
            case Or or -> distributeOr(conjunctive(or.left()), conjunctive(or.right()));
            default -> formula;
        };
    }

    /** CNF of {@code a | b}, where both are in CNF. */
    private static Formula distributeOr(Formula a, Formula b) {
        if (a instanceof And(Formula left, Formula right)) {
            return new And(distributeOr(left, b), distributeOr(right, b));
        }
        if (b instanceof And(Formula left, Formula right)) {
            return new And(distributeOr(a, left), distributeOr(a, right));
        }
        return new Or(a, b);
    }

    /** DNF of a formula already in negation normal form. */
    private static Formula disjunctive(Formula formula) {
        return switch (formula) {
            case Or or -> new Or(disjunctive(or.left()), disjunctive(or.right()));
            case And and -> distributeAnd(disjunctive(and.left()), disjunctive(and.right()));
            default -> formula;
        };
    }

    /** DNF of {@code a & b}, where both are in DNF. */
    private static Formula distributeAnd(Formula a, Formula b) {
        if (a instanceof Or(Formula left, Formula right)) {
            return new Or(distributeAnd(left, b), distributeAnd(right, b));
        }
        if (b instanceof Or(Formula left, Formula right)) {
            return new Or(distributeAnd(a, left), distributeAnd(a, right));
        }
        return new And(a, b);
    }

    private static void requireQuantifierFree(Formula formula, String form) {
        if (!Semantics.isQuantifierFree(formula)) {
            throw new IllegalArgumentException(form + " needs a formula without quantifiers: " + formula);
        }
    }
}
