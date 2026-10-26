package io.github.toxicssj.logik.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.toxicssj.logik.syntax.Formula;

class SemanticsTest {

    private static Formula f(String text) {
        return Formula.parse(text);
    }

    @Test
    void printsATruthTable() {
        assertEquals("""
                P Q | P → Q | Q → P
                T T |   T   |   T
                T F |   F   |   T
                F T |   T   |   F
                F F |   T   |   T
                """, TruthTable.of(f("P → Q"), f("Q → P")).toString());
    }

    @Test
    void decidesTruthFunctionalQuestions() {
        assertTrue(Semantics.isTautology(f("P | ~P")));
        assertFalse(Semantics.isTautology(f("P → Q")));
        assertTrue(Semantics.isContradiction(f("P & ~P")));
        assertTrue(Semantics.isSatisfiable(f("P & ~Q")));
        assertTrue(Semantics.areEquivalent(f("P → Q"), f("~Q → ~P")));
        assertFalse(Semantics.areEquivalent(f("P → Q"), f("Q → P")));
        assertTrue(Semantics.entails(List.of(f("P → Q"), f("P")), f("Q")));
        assertEquals("P = F, Q = T", Semantics.counterexample(List.of(f("P → Q"), f("Q")), f("P")).toString());
        assertNull(Semantics.counterexample(List.of(f("P | Q"), f("~P")), f("Q")));
    }

    @Test
    void treatsPredicatesWithoutQuantifiersAsAtoms() {
        assertEquals(List.of("Fa", "Fb"), TruthTable.of(f("Fa → Fb")).atoms());
        assertTrue(Semantics.entails(List.of(f("Fa & Gx")), f("Gx")));
    }

    @Test
    void refusesQuantifiersInTruthTables() {
        assertThrows(IllegalArgumentException.class, () -> TruthTable.of(f("(∀x)Fx")));
    }

    @Test
    void findsACountermodel() {
        ModelSearch.Result result = ModelSearch.countermodel(List.of(f("(∃x)Fx")), f("Fa"), 3);
        Model model = assertInstanceOf(ModelSearch.Result.Found.class, result).model();
        assertEquals("domain {0, 1}; a = 1; F = {(0)}", model.toString());
        assertTrue(model.satisfies(f("(∃x)Fx")));
        assertFalse(model.satisfies(f("Fa")));
    }

    @Test
    void findsNoCountermodelForAValidArgument() {
        assertEquals(new ModelSearch.Result.NoneUpTo(3),
                ModelSearch.countermodel(List.of(f("(∀x)(Hx → Mx)"), f("Hs")), f("Ms"), 3));
        assertEquals(new ModelSearch.Result.NoneUpTo(3),
                ModelSearch.countermodel(List.of(f("(∃x)(∀y)Lxy")), f("(∀y)(∃x)Lxy"), 3));
    }

    @Test
    void showsTheQuantifierSwapIsInvalid() {
        ModelSearch.Result result = ModelSearch.countermodel(List.of(f("(∀y)(∃x)Lxy")), f("(∃x)(∀y)Lxy"), 3);
        assertEquals("domain {0, 1}; L = {(0, 1), (1, 0)}",
                assertInstanceOf(ModelSearch.Result.Found.class, result).model().toString());
    }

    @Test
    void stopsWhenTheBudgetRunsOut() {
        // A ternary predicate over three individuals has 2^27 extensions.
        assertEquals(new ModelSearch.Result.NoneUpTo(2),
                ModelSearch.find(List.of(f("(∀x)Axxx"), f("~(∀x)Axxx")), 3));
    }

    @Test
    void drawsASemanticTree() {
        Tableau tableau = Tableau.of(List.of(f("P → Q"), f("P"), f("~Q")));
        assertTrue(tableau.isClosed());
        assertEquals("""
                P → Q
                P
                ~Q
                ├─ ~P
                │  ×
                └─ Q
                   ×
                """, tableau.toString());
    }

    @Test
    void leavesABranchOpenForAnInvalidArgument() {
        assertFalse(Tableau.proves(List.of(f("P → Q"), f("Q")), f("P")));
        assertTrue(Tableau.proves(List.of(f("P | Q"), f("~P")), f("Q")));
        assertTrue(Tableau.proves(List.of(), f("(P → Q) ↔ (~Q → ~P)")));
    }
}
