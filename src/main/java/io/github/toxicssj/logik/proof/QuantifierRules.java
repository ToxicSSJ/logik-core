package io.github.toxicssj.logik.proof;

import static io.github.toxicssj.logik.proof.Matching.same;
import static io.github.toxicssj.logik.proof.Matching.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.toxicssj.logik.syntax.Constant;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.Exists;
import io.github.toxicssj.logik.syntax.Formula.ForAll;
import io.github.toxicssj.logik.syntax.Term;
import io.github.toxicssj.logik.syntax.Variable;

/**
 * UI, EI, UG and EG, with Copi's restrictions:
 *
 * <ul>
 * <li>UI: {@code (∀x)Φx} gives {@code Φν} for any term ν that no quantifier
 *     inside Φ captures.</li>
 * <li>EI: {@code (∃x)Φx} gives {@code Φa} only for a constant {@code a} that
 *     occurs nowhere before: not in the premises, the conclusion or an earlier
 *     line.</li>
 * <li>UG: {@code Φy} gives {@code (∀x)Φx} only for a variable {@code y} that is
 *     free in no premise, no open assumption and no line obtained by EI, and
 *     every free {@code y} must become {@code x}.</li>
 * <li>EG: {@code Φν} gives {@code (∃x)Φx} when putting ν for {@code x} in Φx
 *     gives back Φν; some occurrences of ν may stay.</li>
 * </ul>
 */
final class QuantifierRules {

    private QuantifierRules() {}

    /** {@code open} holds the lines of the assumptions still open at line {@code number}. */
    static Optional<String> check(Rule rule, Cited cited, Formula line, Mode mode, Proof proof, int number,
            List<Integer> open) {
        return switch (rule) {
            case UI -> universalInstantiation(cited, line, mode);
            case EI -> existentialInstantiation(cited, line, mode, proof, number);
            case UG -> universalGeneralization(cited, line, mode, proof, number, open);
            case EG -> existentialGeneralization(cited, line, mode);
            default -> throw new IllegalArgumentException(rule + " is not a quantification rule");
        };
    }

    private static Optional<String> universalInstantiation(Cited cited, Formula line, Mode mode) {
        for (Formula view : views(cited.formula(), mode)) {
            if (view instanceof ForAll(Variable x, Formula body)) {
                for (Term term : terms(line, x)) {
                    if (instanceOf(body, x, term, line, mode)) {
                        return Optional.empty();
                    }
                }
                return Optional.of("UI puts one term for every free " + x + " in the body of line "
                        + cited.line() + ", and no quantifier may capture it; this line is not such an instance");
            }
        }
        return Optional.of("UI needs a line that starts with ∀");
    }

    private static Optional<String> existentialInstantiation(Cited cited, Formula line, Mode mode, Proof proof, int number) {
        for (Formula view : views(cited.formula(), mode)) {
            if (view instanceof Exists(Variable x, Formula body)) {
                if (!body.freeVariables().contains(x) && same(body, line, mode)) {
                    return Optional.empty();
                }
                String notNew = null;
                for (Constant constant : line.constants()) {
                    if (instanceOf(body, x, constant, line, mode)) {
                        String where = firstOccurrence(constant, proof, number);
                        if (where == null) {
                            return Optional.empty();
                        }
                        if (notNew == null) {
                            notNew = "EI needs a new constant, and " + constant + " occurs in " + where;
                        }
                    }
                }
                if (notNew != null) {
                    return Optional.of(notNew);
                }
                for (Variable variable : line.freeVariables()) {
                    if (instanceOf(body, x, variable, line, mode)) {
                        return Optional.of("EI must use a constant, not the variable " + variable);
                    }
                }
                return Optional.of("EI puts a new constant for every free " + x + " in the body of line "
                        + cited.line() + ", and this line is not such an instance");
            }
        }
        return Optional.of("EI needs a line that starts with ∃");
    }

    private static Optional<String> universalGeneralization(Cited cited, Formula line, Mode mode, Proof proof, int number,
            List<Integer> open) {
        Formula instance = cited.formula();
        for (Formula view : views(line, mode)) {
            if (view instanceof ForAll(Variable x, Formula body)) {
                String reason = null;
                List<Variable> variables = new ArrayList<>(instance.freeVariables());
                if (!variables.contains(x)) {
                    variables.add(x);
                }
                for (Variable y : variables) {
                    if (!instanceOf(body, x, y, instance, mode)) {
                        continue;
                    }
                    String restriction = !y.equals(x) && line.freeVariables().contains(y)
                            ? "UG must replace every free " + y + ", and " + y + " is still free in this line"
                            : restriction(y, proof, number, open);
                    if (restriction == null) {
                        return Optional.empty();
                    }
                    if (reason == null) {
                        reason = restriction;
                    }
                }
                if (reason != null) {
                    return Optional.of(reason);
                }
                for (Constant constant : instance.constants()) {
                    if (instanceOf(body, x, constant, instance, mode)) {
                        return Optional.of("UG generalizes a variable, and " + constant + " is a constant");
                    }
                }
                return Optional.of("line " + cited.line() + " is not the body of this line with one variable put for " + x);
            }
        }
        return Optional.of("UG gives a line that starts with ∀");
    }

    private static Optional<String> existentialGeneralization(Cited cited, Formula line, Mode mode) {
        Formula instance = cited.formula();
        for (Formula view : views(line, mode)) {
            if (view instanceof Exists(Variable x, Formula body)) {
                for (Term term : terms(instance, x)) {
                    if (instanceOf(body, x, term, instance, mode)) {
                        return Optional.empty();
                    }
                }
                return Optional.of("line " + cited.line() + " is not the body of this line with one term put for " + x);
            }
        }
        return Optional.of("EG gives a line that starts with ∃");
    }

    /** True if putting {@code term} for {@code x} in {@code body}, with no capture, gives {@code target}. */
    private static boolean instanceOf(Formula body, Variable x, Term term, Formula target, Mode mode) {
        Optional<Formula> instance = body.substitute(x, term);
        return instance.isPresent() && same(instance.get(), target, mode);
    }

    /** The terms that can have been put for {@code x}: those of {@code formula}, and {@code x} itself. */
    private static List<Term> terms(Formula formula, Variable x) {
        List<Term> terms = new ArrayList<>(formula.constants());
        terms.addAll(formula.freeVariables());
        if (!terms.contains(x)) {
            terms.add(x);
        }
        return terms;
    }

    /** Where {@code constant} first occurs among the premises, the conclusion and the steps before {@code number}. */
    private static String firstOccurrence(Constant constant, Proof proof, int number) {
        List<Formula> premises = proof.problem().premises();
        for (int i = 0; i < premises.size(); i++) {
            if (premises.get(i).constants().contains(constant)) {
                return "premise " + (i + 1);
            }
        }
        if (proof.problem().conclusion().constants().contains(constant)) {
            return "the conclusion";
        }
        for (int line = proof.firstStep(); line < number; line++) {
            if (proof.line(line).constants().contains(constant)) {
                return "line " + line;
            }
        }
        return null;
    }

    /** Why UG cannot generalize {@code y} at line {@code number}, or null if it can. */
    private static String restriction(Variable y, Proof proof, int number, List<Integer> open) {
        List<Formula> premises = proof.problem().premises();
        for (int i = 0; i < premises.size(); i++) {
            if (premises.get(i).freeVariables().contains(y)) {
                return "UG cannot generalize " + y + ", which is free in premise " + (i + 1);
            }
        }
        for (int assumption : open) {
            if (proof.line(assumption).freeVariables().contains(y)) {
                return "UG cannot generalize " + y + ", which is free in the assumption on line " + assumption;
            }
        }
        for (int line = proof.firstStep(); line < number; line++) {
            Step step = proof.steps().get(line - proof.firstStep());
            if (Rule.byName(step.justification().rule()) == Rule.EI && step.formula().freeVariables().contains(y)) {
                return "UG cannot generalize " + y + ", which is free in line " + line + ", obtained by EI";
            }
        }
        return null;
    }
}
