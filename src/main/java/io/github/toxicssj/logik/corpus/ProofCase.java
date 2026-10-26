package io.github.toxicssj.logik.corpus;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.proof.Mode;

/**
 * A proof from the corpus.
 *
 * @param problem         premises and conclusion as {@code {P//Q=>C}}
 * @param steps           the lines after the premises
 * @param argumentInvalid true if the argument itself is not valid: some model
 *                        makes every premise true and the conclusion false
 * @param legacyNote      why the 2018 engine got the flexible verdict wrong, or null
 * @param location        file and line where the case starts, for error messages
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
        String body = body(problem);
        return body.substring(body.indexOf("=>") + 2).strip();
    }

    /** The premises of {@code {P//Q=>C}}; {@code {=>C}} has none. */
    static List<String> premisesOf(String problem) {
        String body = body(problem);
        String premises = body.substring(0, body.indexOf("=>"));
        List<String> parts = new ArrayList<>();
        if (premises.isBlank()) {
            return parts;
        }
        int start = 0;
        while (true) {
            int separator = premises.indexOf("//", start);
            if (separator < 0) {
                parts.add(premises.substring(start).strip());
                return parts;
            }
            parts.add(premises.substring(start, separator).strip());
            start = separator + 2;
        }
    }

    private static String body(String problem) {
        return problem.substring(1, problem.length() - 1);
    }

    @Override
    public String toString() {
        return id;
    }
}
