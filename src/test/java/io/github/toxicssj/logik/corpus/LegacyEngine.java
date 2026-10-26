package io.github.toxicssj.logik.corpus;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

import io.github.toxicssj.logik.propositional.builder.FormulaParser;
import io.github.toxicssj.logik.propositional.builder.ProofParser;
import io.github.toxicssj.logik.propositional.bundle.proof.Proof;
import io.github.toxicssj.logik.propositional.bundle.proof.ProofResult;
import io.github.toxicssj.logik.propositional.exception.PropositionalLogicException;

/**
 * Runs corpus cases through the 2018 engine. It goes away with the old
 * package at the end of phase 3.
 */
final class LegacyEngine {

    /** What the 2018 engine did with a case. */
    sealed interface Outcome {
        /** The proof was accepted, or the formula parsed to {@code text}. */
        record Accepted(String text) implements Outcome {}

        /** The engine reported an error the way it is meant to. */
        record Rejected(String message) implements Outcome {}

        /** The engine threw an unexpected exception. */
        record Crashed(RuntimeException exception) implements Outcome {}
    }

    private LegacyEngine() {}

    static Outcome check(ProofCase proofCase) {
        return quietly(() -> {
            Proof proof = ProofParser.fromString(proofCase.problem());
            for (ProofCase.Step step : proofCase.steps()) {
                proof.addLine(ProofParser.fromString(step.formula(), step.justification()));
            }
            ProofResult result = proof.build(false);
            return result.getType() == ProofResult.ResultType.CORRECT
                    ? new Outcome.Accepted("CORRECT")
                    : new Outcome.Rejected(result.getType().name());
        });
    }

    static Outcome parse(String input) {
        return quietly(() -> new Outcome.Accepted(FormulaParser.fromString(input).toString()));
    }

    /** The 2018 engine prints debugging output; keep it out of the test log. */
    private static synchronized Outcome quietly(Supplier<Outcome> action) {
        PrintStream out = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            return action.get();
        } catch (PropositionalLogicException e) {
            return new Outcome.Rejected(e.getMessage());
        } catch (RuntimeException e) {
            return new Outcome.Crashed(e);
        } finally {
            System.setOut(out);
        }
    }
}
