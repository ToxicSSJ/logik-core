package io.github.toxicssj.logik.proof;

import static io.github.toxicssj.logik.proof.Matching.same;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.And;
import io.github.toxicssj.logik.syntax.Formula.Binary;
import io.github.toxicssj.logik.syntax.Formula.Iff;
import io.github.toxicssj.logik.syntax.Formula.Implies;
import io.github.toxicssj.logik.syntax.Formula.Not;
import io.github.toxicssj.logik.syntax.Formula.Or;
import io.github.toxicssj.logik.syntax.Formula.Quantified;

/**
 * The rules of replacement. A step rewrites one subformula of the cited line
 * with one of the rule's equivalences, in either direction.
 *
 * <p>The check lists every formula one rewrite away from the cited line and
 * looks for the new line among them. Since the rules are equivalences, it also
 * looks for the cited line among the rewrites of the new line; in strict mode
 * that finds nothing new, but in flexible mode it lets {@code B | C} become
 * {@code ~(~B & ~C)} by DM, which needs {@code ~~B | ~~C} written out first.
 */
final class ReplacementRules {

    private ReplacementRules() {}

    static Optional<String> check(Rule rule, Cited cited, Formula line, Mode mode) {
        List<Formula> rewrites = everywhere(rule, cited.formula(), mode);
        for (Formula rewritten : rewrites) {
            if (same(rewritten, line, mode)) {
                return Optional.empty();
            }
        }
        for (Formula rewritten : everywhere(rule, line, mode)) {
            if (same(rewritten, cited.formula(), mode)) {
                return Optional.empty();
            }
        }
        if (rewrites.isEmpty()) {
            return Optional.of(rule.name() + " does not apply to any part of line " + cited.line());
        }
        // DN and TAUT apply everywhere; for the others, naming the results helps.
        List<String> results = new ArrayList<>();
        for (Formula rewritten : rewrites) {
            if (!results.contains(rewritten.toString())) {
                results.add(rewritten.toString());
            }
        }
        if (rule != Rule.DN && rule != Rule.TAUT && results.size() <= 2) {
            return Optional.of(rule.name() + " turns line " + cited.line() + " into " + String.join(" or ", results)
                    + ", not into this line");
        }
        return Optional.of("one " + rule.name() + " step does not turn line " + cited.line() + " into this line");
    }

    /** Every formula that differs from {@code formula} by one rewrite of {@code rule}. */
    static List<Formula> everywhere(Rule rule, Formula formula, Mode mode) {
        List<Formula> out = new ArrayList<>(atTop(rule, formula, mode));
        switch (formula) {
            case Formula.Atom atom -> { }
            case Formula.Predicate predicate -> { }
            case Not not -> {
                for (Formula operand : everywhere(rule, not.operand(), mode)) {
                    out.add(new Not(operand));
                }
            }
            case Binary binary -> {
                for (Formula left : everywhere(rule, binary.left(), mode)) {
                    out.add(Binary.of(binary.connective(), left, binary.right()));
                }
                for (Formula right : everywhere(rule, binary.right(), mode)) {
                    out.add(Binary.of(binary.connective(), binary.left(), right));
                }
            }
            case Quantified quantified -> {
                for (Formula body : everywhere(rule, quantified.body(), mode)) {
                    out.add(Quantified.of(quantified.quantifier(), quantified.variable(), body));
                }
            }
        }
        return out;
    }

    /** The rewrites of {@code f} itself, not of its parts. */
    private static List<Formula> atTop(Rule rule, Formula f, Mode mode) {
        List<Formula> out = new ArrayList<>();
        switch (rule) {
            case DN -> {
                // p ≡ ~~p
                out.add(new Not(new Not(f)));
                if (f instanceof Not(Not(Formula p))) {
                    out.add(p);
                }
            }
            case COM -> {
                // p & q ≡ q & p, p | q ≡ q | p; flexible mode also allows ↔
                if (f instanceof And(Formula p, Formula q)) {
                    out.add(new And(q, p));
                }
                if (f instanceof Or(Formula p, Formula q)) {
                    out.add(new Or(q, p));
                }
                if (mode == Mode.FLEXIBLE && f instanceof Iff(Formula p, Formula q)) {
                    out.add(new Iff(q, p));
                }
            }
            case ASSOC -> {
                // p & (q & r) ≡ (p & q) & r, and the same for |
                if (f instanceof And(Formula p, And(Formula q, Formula r))) {
                    out.add(new And(new And(p, q), r));
                }
                if (f instanceof And(And(Formula p, Formula q), Formula r)) {
                    out.add(new And(p, new And(q, r)));
                }
                if (f instanceof Or(Formula p, Or(Formula q, Formula r))) {
                    out.add(new Or(new Or(p, q), r));
                }
                if (f instanceof Or(Or(Formula p, Formula q), Formula r)) {
                    out.add(new Or(p, new Or(q, r)));
                }
            }
            case DIST -> {
                // p & (q | r) ≡ (p & q) | (p & r)
                if (f instanceof And(Formula p, Or(Formula q, Formula r))) {
                    out.add(new Or(new And(p, q), new And(p, r)));
                }
                if (f instanceof Or(And(Formula p, Formula q), And(Formula p2, Formula r)) && same(p, p2, mode)) {
                    out.add(new And(p, new Or(q, r)));
                }
                // p | (q & r) ≡ (p | q) & (p | r)
                if (f instanceof Or(Formula p, And(Formula q, Formula r))) {
                    out.add(new And(new Or(p, q), new Or(p, r)));
                }
                if (f instanceof And(Or(Formula p, Formula q), Or(Formula p2, Formula r)) && same(p, p2, mode)) {
                    out.add(new Or(p, new And(q, r)));
                }
            }
            case DM -> {
                // ~(p & q) ≡ ~p | ~q, ~(p | q) ≡ ~p & ~q
                if (f instanceof Not(And(Formula p, Formula q))) {
                    out.add(new Or(new Not(p), new Not(q)));
                }
                if (f instanceof Not(Or(Formula p, Formula q))) {
                    out.add(new And(new Not(p), new Not(q)));
                }
                if (f instanceof Or(Not(Formula p), Not(Formula q))) {
                    out.add(new Not(new And(p, q)));
                }
                if (f instanceof And(Not(Formula p), Not(Formula q))) {
                    out.add(new Not(new Or(p, q)));
                }
            }
            case TRANS -> {
                // p → q ≡ ~q → ~p
                if (f instanceof Implies(Formula p, Formula q)) {
                    out.add(new Implies(new Not(q), new Not(p)));
                }
                if (f instanceof Implies(Not(Formula q), Not(Formula p))) {
                    out.add(new Implies(p, q));
                }
            }
            case IMPL -> {
                // p → q ≡ ~p | q
                if (f instanceof Implies(Formula p, Formula q)) {
                    out.add(new Or(new Not(p), q));
                }
                if (f instanceof Or(Not(Formula p), Formula q)) {
                    out.add(new Implies(p, q));
                }
            }
            case EQUIV -> {
                // p ↔ q ≡ (p → q) & (q → p), p ↔ q ≡ (p & q) | (~p & ~q)
                if (f instanceof Iff(Formula p, Formula q)) {
                    out.add(new And(new Implies(p, q), new Implies(q, p)));
                    out.add(new Or(new And(p, q), new And(new Not(p), new Not(q))));
                }
                if (f instanceof And(Implies(Formula p, Formula q), Implies(Formula q2, Formula p2))
                        && same(p, p2, mode) && same(q, q2, mode)) {
                    out.add(new Iff(p, q));
                }
                if (f instanceof Or(And(Formula p, Formula q), And(Not(Formula p2), Not(Formula q2)))
                        && same(p, p2, mode) && same(q, q2, mode)) {
                    out.add(new Iff(p, q));
                }
            }
            case EXP -> {
                // (p & q) → r ≡ p → (q → r)
                if (f instanceof Implies(And(Formula p, Formula q), Formula r)) {
                    out.add(new Implies(p, new Implies(q, r)));
                }
                if (f instanceof Implies(Formula p, Implies(Formula q, Formula r))) {
                    out.add(new Implies(new And(p, q), r));
                }
            }
            case TAUT -> {
                // p ≡ p & p, p ≡ p | p
                out.add(new And(f, f));
                out.add(new Or(f, f));
                if (f instanceof And(Formula p, Formula q) && same(p, q, mode)) {
                    out.add(p);
                }
                if (f instanceof Or(Formula p, Formula q) && same(p, q, mode)) {
                    out.add(p);
                }
            }
            default -> throw new IllegalArgumentException(rule + " is not a rule of replacement");
        }
        return out;
    }
}
