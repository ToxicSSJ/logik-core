package io.github.toxicssj.logik.corpus;

import java.util.List;

import io.github.toxicssj.logik.proof.Mode;

/**
 * A proof from the corpus.
 *
 * @param problem    premises and conclusion as {@code {P//Q=>C}}
 * @param steps      the lines after the premises
 * @param argumentInvalid true if the argument itself is not valid: some model
 *                   makes every premise true and the conclusion false
 * @param legacyNote why the 2018 engine disagrees with the flexible verdict, or null
 * @param location   file and line where the case starts, for error messages
 */
public record ProofCase(
        String id,
        String source,
        String problem,
        List<Step> steps,
        Verdict strict,
        Verdict flexible,
        boolean argumentInvalid,
        String legacyNote,
        String location) {

    /** One proof line: {@code number. formula ; justification}. */
    public record Step(int number, String formula, String justification) {}

    public ProofCase {
        steps = List.copyOf(steps);
    }

    public Verdict verdict(Mode mode) {
        return mode == Mode.STRICT ? strict : flexible;
    }

    public List<String> premises() {
        return premisesOf(problem);
    }

    public String conclusion() {
        return body(problem).split("=>", -1)[1].strip();
    }

    /** The premises of {@code {P//Q=>C}}; {@code {=>C}} has none. */
    static List<String> premisesOf(String problem) {
        String premises = body(problem).split("=>", -1)[0];
        if (premises.isBlank()) {
            return List.of();
        }
        return List.of(premises.split("//", -1)).stream()
                .map(String::strip)
                .toList();
    }

    private static String body(String problem) {
        return problem.substring(1, problem.length() - 1);
    }

    @Override
    public String toString() {
        return id;
    }
}
