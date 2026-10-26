package io.github.toxicssj.logik.proof;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Problem;

/**
 * A proof: the problem and the steps after the premises. Premises are lines 1
 * to k, and the steps are numbered from k + 1.
 */
public record Proof(Problem problem, List<Step> steps) {

    public Proof {
        Objects.requireNonNull(problem);
        steps = List.copyOf(steps);
    }

    /** Parses the problem and each step, written {@code formula ; justification}; throws on a syntax error. */
    public static Proof of(String problem, String... steps) {
        List<Step> parsed = new ArrayList<>();
        for (String step : steps) {
            int separator = step.indexOf(';');
            if (separator < 0) {
                throw new IllegalArgumentException("a step needs 'formula ; justification': " + step);
            }
            parsed.add(Step.of(step.substring(0, separator), step.substring(separator + 1)));
        }
        return new Proof(Problem.parse(problem).orThrow(), parsed);
    }

    /** The number of the first step. */
    public int firstStep() {
        return problem.premises().size() + 1;
    }

    /** The formula on line {@code number}, counting premises from 1. */
    public Formula line(int number) {
        int premises = problem.premises().size();
        return number <= premises ? problem.premises().get(number - 1) : steps.get(number - premises - 1).formula();
    }

    /** The number of the last line, premises included. */
    public int lastLine() {
        return problem.premises().size() + steps.size();
    }
}
