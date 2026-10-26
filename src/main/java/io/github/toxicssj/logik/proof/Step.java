package io.github.toxicssj.logik.proof;

import java.util.Objects;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Justification;

/** A proof line after the premises: a formula and why it holds. */
public record Step(Formula formula, Justification justification) {

    public Step {
        Objects.requireNonNull(formula);
        Objects.requireNonNull(justification);
    }

    /** Parses both parts, as in {@code Step.of("A & B", "2,3 CONJ")}; throws on a syntax error. */
    public static Step of(String formula, String justification) {
        return new Step(Formula.parse(formula), Justification.parse(justification).orThrow());
    }

    @Override
    public String toString() {
        return formula + " ; " + justification;
    }
}
