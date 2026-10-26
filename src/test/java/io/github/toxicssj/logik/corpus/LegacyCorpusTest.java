package io.github.toxicssj.logik.corpus;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.toxicssj.logik.corpus.LegacyEngine.Outcome;

/**
 * Checks the 2018 engine against the corpus. A case where the engine is known
 * to be wrong carries a {@code legacy:} note and is reported as skipped; if the
 * engine ever agrees with such a case, the test fails so the note gets removed.
 */
class LegacyCorpusTest {

    static List<ProofCase> proofCases() {
        return Corpus.proofCases();
    }

    static List<ParserCase> parserCases() {
        return Corpus.parserCases();
    }

    /** The 2018 engine has no modes; it is closest to flexible mode. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("proofCases")
    void proof(ProofCase proofCase) {
        Verdict expected = proofCase.flexible();
        Outcome outcome = LegacyEngine.check(proofCase);
        boolean agrees = expected.isValid()
                ? outcome instanceof Outcome.Accepted
                : outcome instanceof Outcome.Rejected;
        report(agrees, proofCase.legacyNote(), proofCase.location(),
                "expected " + expected + ", got " + describe(outcome));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("parserCases")
    void parser(ParserCase parserCase) {
        Outcome outcome = LegacyEngine.parse(parserCase.input());
        boolean agrees = parserCase.accept()
                ? outcome instanceof Outcome.Accepted(String text) && text.equals(parserCase.expected())
                : outcome instanceof Outcome.Rejected;
        String expected = parserCase.accept() ? parserCase.expected() : "an error at column " + parserCase.expected();
        report(agrees, parserCase.legacyNote(), parserCase.location(),
                "expected " + expected + ", got " + describe(outcome));
    }

    private static void report(boolean agrees, String legacyNote, String location, String mismatch) {
        if (legacyNote == null) {
            if (!agrees) {
                fail(location + ": " + mismatch);
            }
        } else {
            if (agrees) {
                fail(location + ": the 2018 engine now gets this case right; remove its legacy note");
            }
            Assumptions.abort("known 2018 bug: " + legacyNote);
        }
    }

    private static String describe(Outcome outcome) {
        return switch (outcome) {
            case Outcome.Accepted(String text) -> "accepted (" + text + ")";
            case Outcome.Rejected(String message) -> "rejected (" + message + ")";
            case Outcome.Crashed(RuntimeException e) -> "a crash (" + e + ")";
        };
    }
}
