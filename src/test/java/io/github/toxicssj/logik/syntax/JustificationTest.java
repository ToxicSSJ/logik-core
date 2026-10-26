package io.github.toxicssj.logik.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class JustificationTest {

    @Test
    void parsesCitationsAndRule() {
        assertEquals(new Justification(List.of(2, 3), "CONJ"), Justification.parse("2,3 CONJ").orThrow());
        assertEquals(new Justification(List.of(1), "SIMP"), Justification.parse(" 1 SIMP ").orThrow());
        assertEquals(new Justification(List.of(10, 2), "MT"), Justification.parse("10 , 2 MT").orThrow());
        assertEquals(new Justification(List.of(), "ACP"), Justification.parse("ACP").orThrow());
        assertEquals(new Justification(List.of(0, 2), "CONJ"), Justification.parse("0,2 CONJ").orThrow());
        assertEquals("2,3 CONJ", new Justification(List.of(2, 3), "CONJ").toString());
    }

    @Test
    void reportsErrorsWithTheirColumn() {
        assertEquals(failure(1, "expected a rule name"), Justification.parse(""));
        assertEquals(failure(4, "expected a line number"), Justification.parse("1, MP"));
        assertEquals(failure(2, "expected ',' or a space before the rule name"), Justification.parse("1MP"));
        assertEquals(failure(3, "expected ',' or a rule name"), Justification.parse("1 2 MP"));
        assertEquals(failure(6, "unexpected text after the rule name"), Justification.parse("1 MP 2"));
        assertEquals(failure(1, "line number too large"), Justification.parse("12345678901 MP"));
    }

    private static ParseResult<Justification> failure(int column, String message) {
        return new ParseResult.Failure<>(column, message);
    }
}
