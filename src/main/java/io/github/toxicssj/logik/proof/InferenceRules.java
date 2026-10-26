package io.github.toxicssj.logik.proof;

import static io.github.toxicssj.logik.proof.Matching.same;
import static io.github.toxicssj.logik.proof.Matching.views;

import java.util.List;
import java.util.Optional;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.And;
import io.github.toxicssj.logik.syntax.Formula.Implies;
import io.github.toxicssj.logik.syntax.Formula.Not;
import io.github.toxicssj.logik.syntax.Formula.Or;

/**
 * The rules of inference. Each check is a pure function of the cited lines and
 * the new line: empty if the line follows, or the reason it does not. The two
 * cited lines of a rule may come in either order.
 */
final class InferenceRules {

    private InferenceRules() {}

    static Optional<String> check(Rule rule, List<Cited> cited, Formula line, Mode mode) {
        return switch (rule) {
            case CONJ -> conjunction(cited.get(0), cited.get(1), line, mode);
            case SIMP -> simplification(cited.get(0), line, mode);
            case ADD -> addition(cited.get(0), line, mode);
            case MP -> modusPonens(cited.get(0), cited.get(1), line, mode);
            case MT -> modusTollens(cited.get(0), cited.get(1), line, mode);
            case DS -> disjunctiveSyllogism(cited.get(0), cited.get(1), line, mode);
            case HS -> hypotheticalSyllogism(cited.get(0), cited.get(1), line, mode);
            case CD -> constructiveDilemma(cited.get(0), cited.get(1), line, mode);
            case DD -> destructiveDilemma(cited.get(0), cited.get(1), line, mode);
            case ABS -> absorption(cited.get(0), line, mode);
            default -> throw new IllegalArgumentException(rule + " is not a rule of inference");
        };
    }

    /** p, q ∴ p & q */
    private static Optional<String> conjunction(Cited a, Cited b, Formula line, Mode mode) {
        if (same(new And(a.formula(), b.formula()), line, mode) || same(new And(b.formula(), a.formula()), line, mode)) {
            return Optional.empty();
        }
        return Optional.of("CONJ joins lines " + a.line() + " and " + b.line() + " into "
                + new And(a.formula(), b.formula()));
    }

    /** p & q ∴ p */
    private static Optional<String> simplification(Cited c, Formula line, Mode mode) {
        boolean conjunction = false;
        for (Formula view : views(c.formula(), mode)) {
            if (view instanceof And(Formula left, Formula right)) {
                conjunction = true;
                if (same(left, line, mode)) {
                    return Optional.empty();
                }
            }
        }
        if (!conjunction) {
            return Optional.of("SIMP needs a conjunction, and line " + c.line() + " is not one");
        }
        return Optional.of(mode == Mode.STRICT
                ? "SIMP gives the left conjunct of line " + c.line() + ", " + ((And) c.formula()).left()
                : "SIMP gives one of the conjuncts of line " + c.line());
    }

    /** p ∴ p | q */
    private static Optional<String> addition(Cited c, Formula line, Mode mode) {
        boolean disjunction = false;
        for (Formula view : views(line, mode)) {
            if (view instanceof Or(Formula left, Formula right)) {
                disjunction = true;
                if (same(left, c.formula(), mode)) {
                    return Optional.empty();
                }
            }
        }
        if (!disjunction) {
            return Optional.of("ADD gives a disjunction");
        }
        return Optional.of(mode == Mode.STRICT
                ? "ADD gives a disjunction whose left side is line " + c.line()
                : "ADD gives a disjunction with line " + c.line() + " on one side");
    }

    /** p → q, p ∴ q */
    private static Optional<String> modusPonens(Cited a, Cited b, Formula line, Mode mode) {
        String reason = null;
        for (Cited[] pair : orders(a, b)) {
            for (Formula view : views(pair[0].formula(), mode)) {
                if (view instanceof Implies(Formula antecedent, Formula consequent)) {
                    if (same(pair[1].formula(), antecedent, mode) && same(line, consequent, mode)) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = "MP with line " + pair[0].line() + " needs its antecedent, " + antecedent
                                + ", and gives its consequent, " + consequent;
                    }
                }
            }
        }
        if (reason == null) {
            return Optional.of("MP needs a conditional, and neither line " + a.line() + " nor line " + b.line() + " is one");
        }
        return Optional.of(reason);
    }

    /** p → q, ~q ∴ ~p */
    private static Optional<String> modusTollens(Cited a, Cited b, Formula line, Mode mode) {
        String reason = null;
        for (Cited[] pair : orders(a, b)) {
            for (Formula view : views(pair[0].formula(), mode)) {
                if (view instanceof Implies(Formula antecedent, Formula consequent)) {
                    if (same(pair[1].formula(), new Not(consequent), mode) && same(line, new Not(antecedent), mode)) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = "MT with line " + pair[0].line() + " needs the negation of its consequent, "
                                + new Not(consequent) + ", and gives " + new Not(antecedent);
                    }
                }
            }
        }
        if (reason == null) {
            return Optional.of("MT needs a conditional, and neither line " + a.line() + " nor line " + b.line() + " is one");
        }
        return Optional.of(reason);
    }

    /** p | q, ~p ∴ q */
    private static Optional<String> disjunctiveSyllogism(Cited a, Cited b, Formula line, Mode mode) {
        String reason = null;
        for (Cited[] pair : orders(a, b)) {
            for (Formula view : views(pair[0].formula(), mode)) {
                if (view instanceof Or(Formula left, Formula right)) {
                    if (same(pair[1].formula(), new Not(left), mode) && same(line, right, mode)) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = mode == Mode.STRICT
                                ? "DS with line " + pair[0].line() + " needs " + new Not(left)
                                        + ", the negation of its left disjunct, and gives " + right
                                : "DS with line " + pair[0].line() + " needs the negation of one disjunct, and gives the other";
                    }
                }
            }
        }
        if (reason == null) {
            return Optional.of("DS needs a disjunction, and neither line " + a.line() + " nor line " + b.line() + " is one");
        }
        return Optional.of(reason);
    }

    /** p → q, q → r ∴ p → r */
    private static Optional<String> hypotheticalSyllogism(Cited a, Cited b, Formula line, Mode mode) {
        for (Cited[] pair : orders(a, b)) {
            for (Formula first : views(pair[0].formula(), mode)) {
                for (Formula second : views(pair[1].formula(), mode)) {
                    if (first instanceof Implies(Formula p, Formula q)
                            && second instanceof Implies(Formula q2, Formula r)
                            && same(q, q2, mode)
                            && same(line, new Implies(p, r), mode)) {
                        return Optional.empty();
                    }
                }
            }
        }
        if (!(a.formula() instanceof Implies) || !(b.formula() instanceof Implies)) {
            return Optional.of("HS needs two conditionals");
        }
        return Optional.of("HS needs p → q and q → r, and gives p → r");
    }

    /** (p → q) & (r → s), p | r ∴ q | s */
    private static Optional<String> constructiveDilemma(Cited a, Cited b, Formula line, Mode mode) {
        String reason = null;
        for (Cited[] pair : orders(a, b)) {
            for (Formula view : views(pair[0].formula(), mode)) {
                if (view instanceof And(Implies(Formula p, Formula q), Implies(Formula r, Formula s))) {
                    if (same(pair[1].formula(), new Or(p, r), mode) && same(line, new Or(q, s), mode)) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = "CD with line " + pair[0].line() + " needs " + new Or(p, r) + " and gives " + new Or(q, s);
                    }
                }
            }
        }
        if (reason == null) {
            return Optional.of("CD needs a conjunction of two conditionals");
        }
        return Optional.of(reason);
    }

    /** (p → q) & (r → s), ~q | ~s ∴ ~p | ~r */
    private static Optional<String> destructiveDilemma(Cited a, Cited b, Formula line, Mode mode) {
        String reason = null;
        for (Cited[] pair : orders(a, b)) {
            for (Formula view : views(pair[0].formula(), mode)) {
                if (view instanceof And(Implies(Formula p, Formula q), Implies(Formula r, Formula s))) {
                    Formula needs = new Or(new Not(q), new Not(s));
                    Formula gives = new Or(new Not(p), new Not(r));
                    if (same(pair[1].formula(), needs, mode) && same(line, gives, mode)) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = "DD with line " + pair[0].line() + " needs " + needs + " and gives " + gives;
                    }
                }
            }
        }
        if (reason == null) {
            return Optional.of("DD needs a conjunction of two conditionals");
        }
        return Optional.of(reason);
    }

    /** p → q ∴ p → (p & q) */
    private static Optional<String> absorption(Cited c, Formula line, Mode mode) {
        for (Formula view : views(c.formula(), mode)) {
            if (view instanceof Implies(Formula p, Formula q)) {
                Formula gives = new Implies(p, new And(p, q));
                if (same(line, gives, mode)) {
                    return Optional.empty();
                }
                return Optional.of("ABS turns line " + c.line() + " into " + gives);
            }
        }
        return Optional.of("ABS needs a conditional, and line " + c.line() + " is not one");
    }

    private static Cited[][] orders(Cited a, Cited b) {
        return new Cited[][] {{a, b}, {b, a}};
    }
}
