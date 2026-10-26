package io.github.toxicssj.logik.proof;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * What the checker found: one entry per step, and whether the proof ends
 * where it should. The proof is correct when every step is and there is no
 * error at the end.
 *
 * @param steps    one entry per step, in order
 * @param endError why the proof does not reach its conclusion, if it does not
 */
public record Report(List<LineCheck> steps, Optional<String> endError) {

    /** The verdict on one step: empty error if the step follows. */
    public record LineCheck(int line, Optional<String> error) {
        public LineCheck {
            Objects.requireNonNull(error);
        }

        public boolean isValid() {
            return error.isEmpty();
        }

        @Override
        public String toString() {
            return line + ": " + error.orElse("ok");
        }
    }

    public Report {
        steps = List.copyOf(steps);
        Objects.requireNonNull(endError);
    }

    public boolean isValid() {
        return firstError().isEmpty() && endError.isEmpty();
    }

    /** The first step that does not follow, if any. */
    public Optional<LineCheck> firstError() {
        for (LineCheck step : steps) {
            if (!step.isValid()) {
                return Optional.of(step);
            }
        }
        return Optional.empty();
    }
}
