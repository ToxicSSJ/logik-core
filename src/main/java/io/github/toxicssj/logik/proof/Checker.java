package io.github.toxicssj.logik.proof;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Justification;

/**
 * Checks a proof line by line. Each step is judged on its own, against the
 * lines it cites as they are written, so one wrong step does not hide the
 * others. The checker never throws on a wrong proof; it reports.
 */
public final class Checker {

    private Checker() {}

    public static Report check(Proof proof, Mode mode) {
        List<Report.LineCheck> steps = new ArrayList<>();
        for (int i = 0; i < proof.steps().size(); i++) {
            int number = proof.firstStep() + i;
            steps.add(new Report.LineCheck(number, checkStep(proof, number, mode)));
        }
        return new Report(steps, checkEnd(proof, mode));
    }

    private static Optional<String> checkStep(Proof proof, int number, Mode mode) {
        Step step = proof.steps().get(number - proof.firstStep());
        Justification justification = step.justification();
        Rule rule = Rule.byName(justification.rule());
        if (rule == null) {
            return Optional.of("there is no rule called " + justification.rule());
        }
        if (justification.citations().size() != rule.citations()) {
            return Optional.of(rule.name() + " cites " + (rule.citations() == 1 ? "one line" : "two lines"));
        }
        List<Cited> cited = new ArrayList<>();
        for (int line : justification.citations()) {
            if (line < 1) {
                return Optional.of("there is no line " + line);
            }
            if (line >= number) {
                return Optional.of("a line can only cite earlier lines, and line " + line + " is not before line " + number);
            }
            cited.add(new Cited(line, proof.line(line)));
        }
        Formula formula = step.formula();
        return switch (rule.kind()) {
            case INFERENCE -> InferenceRules.check(rule, cited, formula, mode);
            case REPLACEMENT -> ReplacementRules.check(rule, cited.get(0), formula, mode);
            case QUANTIFIER -> QuantifierRules.check(rule, cited.get(0), formula, mode, proof, number);
        };
    }

    private static Optional<String> checkEnd(Proof proof, Mode mode) {
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
