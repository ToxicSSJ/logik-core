package io.github.toxicssj.logik.proof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class CheckerTest {

    @Test
    void acceptsACorrectProof() {
        Proof proof = Proof.of("{(∀x)(Hx → Mx)//Hs=>Ms}",
                "Hs → Ms ; 1 UI",
                "Ms ; 3,2 MP");
        Report report = Checker.check(proof, Mode.STRICT);
        assertTrue(report.isValid());
        assertEquals(List.of(new Report.LineCheck(3, Optional.empty()), new Report.LineCheck(4, Optional.empty())),
                report.steps());
    }

    @Test
    void reportsEveryWrongLineAndKeepsGoing() {
        Proof proof = Proof.of("{P → Q//Q//~R=>P & ~R}",
                "P ; 1,2 MP",
                "P & ~R ; 4,3 CONJ",
                "~R & P ; 5 COM");
        Report report = Checker.check(proof, Mode.STRICT);
        assertFalse(report.isValid());
        assertEquals(Optional.of("MP with line 1 needs its antecedent, P, and gives its consequent, Q"),
                report.steps().get(0).error());
        assertTrue(report.steps().get(1).isValid(), "line 5 is checked against line 4 as written");
        assertTrue(report.steps().get(2).isValid());
        assertEquals(4, report.firstError().orElseThrow().line());
        assertEquals(Optional.of("the last line is ~R & P, and the conclusion is P & ~R"), report.endError());
    }

    @Test
    void namesWhatAReplacementRuleGives() {
        Report report = Checker.check(Proof.of("{~(~A & B)=>A | ~B}", "A | ~B ; 1 DM"), Mode.STRICT);
        assertEquals(Optional.of("DM turns line 1 into ~~A | ~B, not into this line"), report.steps().get(0).error());
        assertTrue(Checker.check(Proof.of("{~(~A & B)=>A | ~B}", "A | ~B ; 1 DM"), Mode.FLEXIBLE).isValid());
    }

    @Test
    void namesTheConstantThatIsNotNew() {
        Report report = Checker.check(Proof.of("{(∃x)Fx//(∃x)Gx=>(∃x)(Fx & Gx)}",
                "Fa ; 1 EI",
                "Ga ; 2 EI",
                "Fa & Ga ; 3,4 CONJ",
                "(∃x)(Fx & Gx) ; 5 EG"), Mode.STRICT);
        assertEquals(new Report.LineCheck(4, Optional.of("EI needs a new constant, and a occurs in line 3")),
                report.firstError().orElseThrow());
    }

    @Test
    void acceptsRuleNamesInAnyCase() {
        assertTrue(Checker.check(Proof.of("{A → B//A=>B}", "B ; 1,2 mp"), Mode.STRICT).isValid());
    }

    @Test
    void reportsAnEmptyProof() {
        Report report = Checker.check(Proof.of("{=>P | ~P}"), Mode.STRICT);
        assertEquals(Optional.of("the proof has no lines"), report.endError());
    }

    @Test
    void rejectsAStepWithoutJustification() {
        assertThrows(IllegalArgumentException.class, () -> Proof.of("{A=>A}", "A"));
    }
}
