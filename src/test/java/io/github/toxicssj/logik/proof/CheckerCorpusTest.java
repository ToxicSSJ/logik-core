package io.github.toxicssj.logik.proof;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.toxicssj.logik.corpus.Corpus;
import io.github.toxicssj.logik.corpus.ProofCase;
import io.github.toxicssj.logik.corpus.Verdict;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Justification;
import io.github.toxicssj.logik.syntax.Problem;

/** Runs every proof of the corpus through the checker, in both modes. */
class CheckerCorpusTest {

    static List<Arguments> cases() {
        List<Arguments> cases = new ArrayList<>();
        for (ProofCase proofCase : Corpus.proofCases()) {
            for (Mode mode : Mode.values()) {
                cases.add(Arguments.of(proofCase, mode));
            }
        }
        return cases;
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("cases")
    void corpus(ProofCase proofCase, Mode mode) {
        Report report = Checker.check(proof(proofCase), mode);
        Verdict expected = proofCase.verdict(mode);
        boolean agrees = switch (expected) {
            case Verdict.Valid valid -> report.isValid();
            case Verdict.InvalidAtLine at -> report.firstError().map(Report.LineCheck::line).equals(Optional.of(at.line()));
            case Verdict.InvalidAtEnd end -> report.firstError().isEmpty() && report.endError().isPresent();
        };
        if (!agrees) {
            fail(proofCase.location() + " in " + mode + " mode: expected " + expected + ", got " + describe(report));
        }
    }

    static Proof proof(ProofCase proofCase) {
        List<Step> steps = new ArrayList<>();
        for (ProofCase.Step step : proofCase.steps()) {
            steps.add(new Step(Formula.parse(step.formula()), Justification.parse(step.justification()).orThrow()));
        }
        return new Proof(Problem.parse(proofCase.problem()).orThrow(), steps);
    }

    private static String describe(Report report) {
        if (report.isValid()) {
            return "valid";
        }
        List<String> errors = new ArrayList<>();
        for (Report.LineCheck step : report.steps()) {
            step.error().ifPresent(error -> errors.add("line " + step.line() + ": " + error));
        }
        report.endError().ifPresent(error -> errors.add("end: " + error));
        return String.join("; ", errors);
    }
}
