package io.github.toxicssj.logik.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.toxicssj.logik.corpus.Corpus;
import io.github.toxicssj.logik.corpus.ParserCase;
import io.github.toxicssj.logik.corpus.ProofCase;

/** Runs the new parser on the corpus: every parser case, and every formula of every proof. */
class SyntaxCorpusTest {

    static List<ParserCase> parserCases() {
        return Corpus.parserCases();
    }

    static List<ProofCase> proofCases() {
        return Corpus.proofCases();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("parserCases")
    void parser(ParserCase parserCase) {
        switch (FormulaParser.parse(parserCase.input())) {
            case ParseResult.Success<Formula> success -> {
                if (!parserCase.accept()) {
                    fail(parserCase.location() + ": expected an error at column " + parserCase.expected()
                            + ", parsed " + success.value());
                }
                Formula formula = success.value();
                assertEquals(parserCase.expected(), fullyParenthesized(formula), parserCase.location());
                assertRoundTrip(formula);
            }
            case ParseResult.Failure<Formula> failure -> {
                if (parserCase.accept()) {
                    fail(parserCase.location() + ": expected " + parserCase.expected() + ", got an error at " + failure);
                }
                assertEquals(parserCase.column(), failure.column(), parserCase.location() + ": " + failure);
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("proofCases")
    void proofRoundTrip(ProofCase proofCase) {
        Problem problem = Problem.parse(proofCase.problem()).orThrow();
        assertEquals(proofCase.premises().size(), problem.premises().size());
        assertEquals(problem, Problem.parse(problem.toString()).orThrow());
        for (Formula premise : problem.premises()) {
            assertRoundTrip(premise);
        }
        assertRoundTrip(problem.conclusion());
        for (ProofCase.Step step : proofCase.steps()) {
            assertRoundTrip(Formula.parse(step.formula()));
            Justification justification = Justification.parse(step.justification()).orThrow();
            assertEquals(justification, Justification.parse(justification.toString()).orThrow());
        }
    }

    private static void assertRoundTrip(Formula formula) {
        assertEquals(formula, Formula.parse(formula.toString()), () -> "round trip of " + formula);
    }

    /** The corpus form: like toString, but with the outermost parentheses too. */
    static String fullyParenthesized(Formula formula) {
        return formula instanceof Formula.Binary ? "(" + formula + ")" : formula.toString();
    }
}
