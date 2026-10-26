package io.github.toxicssj.logik.corpus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Checks the corpus reader, so that a typo in the corpus fails loudly. */
class CorpusTest {

    @Test
    void readsTheWholeCorpus() {
        List<ProofCase> proofs = Corpus.proofCases();
        assertTrue(proofs.size() > 100, "proof cases: " + proofs.size());
        assertTrue(proofs.stream().anyMatch(c -> !c.strict().equals(c.flexible())), "no mode-dependent case");
        assertFalse(Corpus.parserCases().isEmpty());
    }

    @Test
    void readsAProof() {
        List<ProofCase> cases = Corpus.parseProofs("test", List.of(
                "# a comment",
                "case: modus-tollens",
                "source: test",
                "problem: {A → ~B//B=>~A}",
                "3. ~A ; 1,2 MT",
                "strict: invalid at 3: needs DN",
                "flexible: valid",
                "",
                "case: conclusion-not-reached",
                "source: test",
                "problem: {P//Q=>P & Q}",
                "3. P | R ; 1 ADD",
                "expect: invalid at end: not the conclusion",
                "legacy: a note"));

        assertEquals(2, cases.size());
        ProofCase first = cases.get(0);
        assertEquals("modus-tollens", first.id());
        assertEquals(List.of("A → ~B", "B"), first.premises());
        assertEquals("~A", first.conclusion());
        assertEquals(List.of(new ProofCase.Step(3, "~A", "1,2 MT")), first.steps());
        assertEquals(new Verdict.InvalidAtLine(3, "needs DN"), first.verdict(Mode.STRICT));
        assertEquals(new Verdict.Valid(), first.verdict(Mode.FLEXIBLE));
        assertEquals("test:2", first.location());

        ProofCase second = cases.get(1);
        assertEquals(new Verdict.InvalidAtEnd("not the conclusion"), second.strict());
        assertEquals(second.strict(), second.flexible());
        assertEquals("a note", second.legacyNote());
    }

    @Test
    void rejectsAWrongStepNumber() {
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: wrong-number", "source: test", "problem: {P//Q=>P & Q}",
                "4. P & Q ; 1,2 CONJ", "expect: valid")));
    }

    @Test
    void rejectsAVerdictOnAPremise() {
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: on-premise", "source: test", "problem: {P//Q=>P & Q}",
                "3. P & Q ; 1,2 CONJ", "expect: invalid at 2: a premise")));
    }

    @Test
    void rejectsAMissingOrDoubleVerdict() {
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: no-verdict", "source: test", "problem: {P=>P | Q}", "2. P | Q ; 1 ADD")));
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: only-strict", "source: test", "problem: {P=>P | Q}", "2. P | Q ; 1 ADD",
                "strict: valid")));
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: both", "source: test", "problem: {P=>P | Q}", "2. P | Q ; 1 ADD",
                "expect: valid", "strict: valid")));
    }

    @Test
    void rejectsUnknownKeys() {
        assertThrows(IllegalStateException.class, () -> Corpus.parseProofs("test", List.of(
                "case: typo", "source: test", "problem: {P=>P | Q}", "2. P | Q ; 1 ADD",
                "expcet: valid")));
    }

    @Test
    void readsParserCases() {
        List<ParserCase> cases = Corpus.parseParserCases("test", List.of(
                "# a comment",
                "accept \"P & Q & R\" ; ((P & Q) & R)",
                "reject \"\" ; 1 ; legacy: accepted"));

        assertEquals(List.of(
                new ParserCase(true, "P & Q & R", "((P & Q) & R)", null, "test:2"),
                new ParserCase(false, "", "1", "accepted", "test:3")), cases);
        assertEquals(1, cases.get(1).column());
    }

    @Test
    void rejectsAParserCaseWithoutColumn() {
        assertThrows(IllegalStateException.class,
                () -> Corpus.parseParserCases("test", List.of("reject \"(P\" ; (P")));
    }
}
