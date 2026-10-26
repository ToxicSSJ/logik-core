package io.github.toxicssj.logik.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class JustificationTest {

    @Test
    void parsesCitationsAndRule() {
        assertEquals(Justification.of("CONJ", 2, 3), Justification.parse("2,3 CONJ").orThrow());
        assertEquals(Justification.of("SIMP", 1), Justification.parse(" 1 SIMP ").orThrow());
        assertEquals(Justification.of("MT", 10, 2), Justification.parse("10 , 2 MT").orThrow());
        assertEquals(Justification.of("ACP"), Justification.parse("ACP").orThrow());
        assertEquals(Justification.of("CONJ", 0, 2), Justification.parse("0,2 CONJ").orThrow());
        assertEquals("2,3 CONJ", Justification.of("CONJ", 2, 3).toString());
    }

    @Test
    void parsesRanges() {
        Justification cp = new Justification(List.of(new Citation.Range(3, 5)), "CP");
        assertEquals(cp, Justification.parse("3-5 CP").orThrow());
        assertEquals(cp, Justification.parse("3–5 CP").orThrow());
        assertEquals(cp, Justification.parse("3 - 5 CP").orThrow());
        assertEquals("3-5 CP", cp.toString());
        assertEquals(new Justification(List.of(new Citation.Line(1), new Citation.Range(2, 4)), "X"),
                Justification.parse("1,2-4 X").orThrow());
    }

    @Test
    void reportsErrorsWithTheirColumn() {
        assertEquals(failure(1, "expected a rule name"), Justification.parse(""));
        assertEquals(failure(4, "expected a line number"), Justification.parse("1, MP"));
        assertEquals(failure(2, "expected ',' or a space before the rule name"), Justification.parse("1MP"));
        assertEquals(failure(3, "expected ',' or a rule name"), Justification.parse("1 2 MP"));
        assertEquals(failure(6, "unexpected text after the rule name"), Justification.parse("1 MP 2"));
        assertEquals(failure(1, "line number too large"), Justification.parse("12345678901 MP"));
        assertEquals(failure(4, "expected the last line of the range"), Justification.parse("3- CP"));
    }

    private static ParseResult<Justification> failure(int column, String message) {
        return new ParseResult.Failure<>(column, message);
    }
}
