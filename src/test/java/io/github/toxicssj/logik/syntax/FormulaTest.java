package io.github.toxicssj.logik.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class FormulaTest {

    private static final Variable X = new Variable('x');
    private static final Variable Y = new Variable('y');
    private static final Constant A = new Constant('a');

    private static Formula f(String text) {
        return Formula.parse(text);
    }

    @Test
    void equalFormulasAreEqualAndHashAlike() {
        assertEquals(f("P & Q"), f("(P ∧ Q)"));
        assertEquals(f("P & Q").hashCode(), f("P · Q").hashCode());
        assertEquals(1, new HashSet<>(List.of(f("P & Q"), f("P&Q"))).size());
        assertNotEquals(f("P & Q"), f("Q & P"));
    }

    @Test
    void printsCanonically() {
        assertEquals("(P & Q) & R", f("P & Q & R").toString());
        assertEquals("P → (Q → R)", f("P -> Q -> R").toString());
        assertEquals("~(P | Q)", f("¬(P ∨ Q)").toString());
        assertEquals("(∀x)(Hx → Mx)", f("(x)(Hx ⊃ Mx)").toString());
        assertEquals("(∃x)Fx → Gx", f("∃x Fx → Gx").toString());
        assertEquals("~~(∀y)~Lay", f("~~(∀y)~Lay").toString());
    }

    @Test
    void buildsThePredicateStructure() {
        assertEquals(new Formula.Predicate('L', List.of(X, A)), f("Lxa"));
        assertEquals(new Formula.Atom('P'), f("P"));
        assertEquals(new Formula.ForAll(X, new Formula.Implies(
                new Formula.Predicate('F', List.of(X)), new Formula.Atom('Q'))), f("(∀x)(Fx → Q)"));
    }

    @Test
    void rejectsMalformedRecords() {
        assertThrows(IllegalArgumentException.class, () -> new Variable('a'));
        assertThrows(IllegalArgumentException.class, () -> new Constant('x'));
        assertThrows(IllegalArgumentException.class, () -> new Formula.Atom('p'));
        assertThrows(IllegalArgumentException.class, () -> new Formula.Predicate('F', List.of()));
        assertThrows(NullPointerException.class, () -> new Formula.Not(null));
    }

    @Test
    void findsFreeVariablesInOrder() {
        assertEquals(List.of(Y, X), List.copyOf(f("Lyx & (∀x)Fx").freeVariables()));
        assertEquals(List.of(Y), List.copyOf(f("(∀x)Lxy").freeVariables()));
        assertTrue(f("(∀x)(∃y)Lxy").isClosed());
        assertFalse(f("(∀y)Fx").isClosed());
    }

    @Test
    void findsConstants() {
        assertEquals(List.of(A), List.copyOf(f("P & (∀x)Hxa").constants()));
        assertEquals(List.of(new Constant('j'), new Constant('k')), List.copyOf(f("Ejjk").constants()));
    }

    @Test
    void substitutesFreeOccurrencesOnly() {
        assertEquals(Optional.of(f("Fa → (∃x)Gx")), f("Fx → (∃x)Gx").substitute(X, A));
        assertEquals(Optional.of(f("(∃y)Lay")), f("(∃y)Lxy").substitute(X, A));
        assertEquals(Optional.of(f("Lzy")), f("Lxy").substitute(X, new Variable('z')));
        assertEquals(Optional.of(f("(∀y)Fy")), f("(∀y)Fy").substitute(Y, A));
    }

    @Test
    void refusesToCaptureAVariable() {
        assertEquals(Optional.empty(), f("(∃y)Lxy").substitute(X, Y));
        assertEquals(Optional.of(f("(∃y)Fy & Lyy")), f("(∃y)Fy & Lxy").substitute(X, Y));
    }

    @Test
    void comparesUpToBoundVariableNames() {
        assertTrue(f("(∀x)Fx").alphaEquals(f("(∀y)Fy")));
        assertTrue(f("(∀x)(∃y)Lxy").alphaEquals(f("(∀y)(∃x)Lyx")));
        assertFalse(f("(∀x)(∃y)Lxy").alphaEquals(f("(∀y)(∃x)Lxy")));
        assertFalse(f("(∀y)Fx").alphaEquals(f("(∀x)Fx")));
        assertFalse(f("(∀x)Fx").alphaEquals(f("(∃x)Fx")));
        assertTrue(f("Fx").alphaEquals(f("Fx")));
        assertFalse(f("Fx").alphaEquals(f("Fy")));
        assertTrue(f("(∀x)(Fx → (∃x)Gx)").alphaEquals(f("(∀z)(Fz → (∃y)Gy)")));
    }

    @Test
    void reportsErrorsWithTheirColumn() {
        ParseResult<Formula> result = FormulaParser.parse("(P & Q");
        assertEquals(new ParseResult.Failure<Formula>(7, "expected ')', found the end of the input"), result);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Formula.parse("P ñ"));
        assertEquals("column 3: unknown symbol 'ñ'", e.getMessage());
    }

    @Test
    void countsColumnsInCodePoints() {
        // 𝑃 is outside the Basic Multilingual Plane: two UTF-16 chars, but one
        // code point, one column and one symbol in the message.
        assertEquals(new ParseResult.Failure<Formula>(2, "unknown symbol '𝑃'"), FormulaParser.parse("~𝑃"));
        assertEquals(new ParseResult.Failure<Problem>(7, "unknown symbol '𝑃'"), Problem.parse("{A//A&𝑃=>A}"));
    }
}
