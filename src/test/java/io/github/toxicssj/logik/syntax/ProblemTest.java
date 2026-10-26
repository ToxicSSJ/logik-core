package io.github.toxicssj.logik.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProblemTest {

    @Test
    void parsesPremisesAndConclusion() {
        Problem problem = Problem.parse("{A → B//A=>B}").orThrow();
        assertEquals(List.of(Formula.parse("A → B"), Formula.parse("A")), problem.premises());
        assertEquals(Formula.parse("B"), problem.conclusion());
        assertEquals("{A → B//A=>B}", problem.toString());
    }

    @Test
    void allowsSpacesAndNoPremises() {
        assertEquals(2, Problem.parse("  { Ax | Bx // ~Ax => (∀x)Bx }  ").orThrow().premises().size());
        assertEquals(List.of(), Problem.parse("{=>P | ~P}").orThrow().premises());
    }

    @Test
    void reportsErrorsInTheWholeText() {
        assertEquals(failure(1, "expected '{'"), Problem.parse("A=>B}"));
        assertEquals(failure(6, "expected '}' at the end"), Problem.parse("{A=>B"));
        assertEquals(failure(4, "expected '=>' before the conclusion"), Problem.parse("{AB}"));
        assertEquals(failure(6, "only one '=>' is allowed"), Problem.parse("{A=>B=>C}"));
        // Columns:                                              {A//B&&C=>C}
        //                                                       123456789...
        assertEquals(failure(7, "expected a formula, found '&'"), Problem.parse("{A//B&&C=>C}"));
        assertEquals(failure(3, "expected a variable (x, y or z), found the end of the input"),
                Problem.parse("{∀//A=>A}"));
        assertEquals(failure(8, "expected a formula, found the end of the input"), Problem.parse("{A//B=>}"));
        assertEquals(failure(2, "expected a formula, found the end of the input"), Problem.parse("{//A=>A}"));
    }

    private static ParseResult<Problem> failure(int column, String message) {
        return new ParseResult.Failure<>(column, message);
    }
}
