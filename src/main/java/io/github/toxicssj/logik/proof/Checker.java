package io.github.toxicssj.logik.proof;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.toxicssj.logik.syntax.Citation;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Justification;

/**
 * Checks a proof line by line. Each step is judged on its own, against the
 * lines it cites as they are written, so one wrong step does not hide the
 * others. The checker never throws on a wrong proof; it reports.
 *
 * <p>An ACP or AIP line opens a subproof. CP and IP close the innermost open
 * one, on the line right after its last line, and cite it as a range. Once a
 * subproof is closed, no later line can cite a line inside it.
 */
public final class Checker {

    private Checker() {}

    /** A closed subproof: its assumption and its last line. */
    private record Subproof(int first, int last) {}

    public static Report check(Proof proof, Mode mode) {
        List<Report.LineCheck> steps = new ArrayList<>();
        List<Integer> open = new ArrayList<>();
        List<Subproof> closed = new ArrayList<>();
        for (int i = 0; i < proof.steps().size(); i++) {
            int number = proof.firstStep() + i;
            Step step = proof.steps().get(i);
            Rule rule = Rule.byName(step.justification().rule());
            steps.add(new Report.LineCheck(number, checkStep(proof, number, rule, mode, open, closed)));
            if (rule != null && rule.kind() == Rule.Kind.ASSUMPTION) {
                open.add(number);
            }
            if (rule != null && rule.kind() == Rule.Kind.DISCHARGE && closesInnermost(step.justification(), open, number)) {
                closed.add(new Subproof(open.remove(open.size() - 1), number - 1));
            }
        }
        return new Report(steps, checkEnd(proof, mode, open));
    }

    private static Optional<String> checkStep(Proof proof, int number, Rule rule, Mode mode,
            List<Integer> open, List<Subproof> closed) {
        Step step = proof.steps().get(number - proof.firstStep());
        Justification justification = step.justification();
        if (rule == null) {
            return Optional.of("there is no rule called " + justification.rule());
        }
        if (justification.citations().size() != rule.citations()) {
            return Optional.of(switch (rule.kind()) {
                case ASSUMPTION -> rule.name() + " cites no lines";
                case DISCHARGE -> rule.name() + " cites the subproof it closes, as a range like 3-5";
                default -> rule.name() + " cites " + (rule.citations() == 1 ? "one line" : "two lines");
            });
        }
        if (rule.kind() == Rule.Kind.ASSUMPTION) {
            return Optional.empty();
        }
        if (rule.kind() == Rule.Kind.DISCHARGE) {
            return discharge(proof, number, rule, justification.citations().get(0), mode, open, closed);
        }
        List<Cited> cited = new ArrayList<>();
        for (Citation citation : justification.citations()) {
            if (!(citation instanceof Citation.Line(int line))) {
                return Optional.of("only CP and IP cite a range of lines");
            }
            Optional<String> problem = citable(line, number, closed);
            if (problem.isPresent()) {
                return problem;
            }
            cited.add(new Cited(line, proof.line(line)));
        }
        Formula formula = step.formula();
        return switch (rule.kind()) {
            case INFERENCE -> InferenceRules.check(rule, cited, formula, mode);
            case REPLACEMENT -> ReplacementRules.check(rule, cited.get(0), formula, mode);
            case QUANTIFIER -> QuantifierRules.check(rule, cited.get(0), formula, mode, proof, number, open);
            case ASSUMPTION, DISCHARGE -> throw new IllegalStateException("handled above");
        };
    }

    /** Why line {@code number} cannot cite {@code line}, if it cannot. */
    private static Optional<String> citable(int line, int number, List<Subproof> closed) {
        if (line < 1) {
            return Optional.of("there is no line " + line);
        }
        if (line >= number) {
            return Optional.of("a line can only cite earlier lines, and line " + line + " is not before line " + number);
        }
        for (Subproof subproof : closed) {
            if (line >= subproof.first() && line <= subproof.last()) {
                return Optional.of("line " + line + " is inside the subproof on lines " + subproof.first() + "-"
                        + subproof.last() + ", which is closed");
            }
        }
        return Optional.empty();
    }

    /** CP: assumption p, last line q ∴ p → q. IP: assumption p, last line q & ~q ∴ ~p. */
    private static Optional<String> discharge(Proof proof, int number, Rule rule, Citation citation, Mode mode,
            List<Integer> open, List<Subproof> closed) {
        if (!(citation instanceof Citation.Range(int first, int last))) {
            return Optional.of(rule.name() + " cites the subproof it closes, as a range like 3-5");
        }
        if (first < 1 || first > last || last >= number) {
            return Optional.of("lines " + first + "-" + last + " are not a subproof before line " + number);
        }
        if (!open.contains(first)) {
            boolean wasClosed = false;
            for (Subproof subproof : closed) {
                wasClosed |= subproof.first() == first;
            }
            return Optional.of(wasClosed
                    ? "the subproof that starts on line " + first + " is already closed"
                    : "line " + first + " is not an assumption, so no subproof starts there");
        }
        int innermost = open.get(open.size() - 1);
        if (innermost != first) {
            return Optional.of("the assumption on line " + innermost + " is still open; close its subproof first");
        }
        if (last != number - 1) {
            return Optional.of(rule.name() + " closes the subproof right after its last line, so the range must end at line "
                    + (number - 1));
        }
        Formula assumption = proof.line(first);
        Formula result = proof.line(last);
        Formula line = proof.line(number);
        if (rule == Rule.CP) {
            Formula gives = new Formula.Implies(assumption, result);
            return Matching.same(line, gives, mode)
                    ? Optional.empty()
                    : Optional.of("CP on lines " + first + "-" + last + " gives " + gives);
        }
        if (!isContradiction(result, mode)) {
            return Optional.of("IP needs the subproof to end in a contradiction, p & ~p, and line " + last + " is not one");
        }
        Formula negation = new Formula.Not(assumption);
        if (Matching.same(line, negation, mode)
                || assumption instanceof Formula.Not(Formula denied) && Matching.same(line, denied, mode)) {
            return Optional.empty();
        }
        return Optional.of("IP on lines " + first + "-" + last + " gives " + negation
                + (assumption instanceof Formula.Not(Formula denied) ? " or " + denied : ""));
    }

    private static boolean isContradiction(Formula formula, Mode mode) {
        for (Formula view : Matching.views(formula, mode)) {
            if (view instanceof Formula.And(Formula p, Formula.Not(Formula q)) && Matching.same(p, q, mode)) {
                return true;
            }
        }
        return false;
    }

    private static boolean closesInnermost(Justification justification, List<Integer> open, int number) {
        return justification.citations().size() == 1
                && justification.citations().get(0) instanceof Citation.Range(int first, int last)
                && !open.isEmpty()
                && open.get(open.size() - 1) == first
                && last == number - 1;
    }

    private static Optional<String> checkEnd(Proof proof, Mode mode, List<Integer> open) {
        if (!open.isEmpty()) {
            return Optional.of("the assumption on line " + open.get(0) + " is never discharged");
        }
        int last = proof.lastLine();
        if (last == 0) {
            return Optional.of("the proof has no lines");
        }
        Formula conclusion = proof.problem().conclusion();
        if (Matching.same(proof.line(last), conclusion, mode)) {
            return Optional.empty();
        }
        return Optional.of("the last line is " + proof.line(last) + ", and the conclusion is " + conclusion);
    }
}
