package io.github.toxicssj.logik.syntax;

/**
 * The result of parsing text: the value, or the column of the first error.
 * Columns count Unicode code points from 1.
 */
public sealed interface ParseResult<T> {

    record Success<T>(T value) implements ParseResult<T> {}

    record Failure<T>(int column, String message) implements ParseResult<T> {
        @Override
        public String toString() {
            return "column " + column + ": " + message;
        }
    }

    /** The value, or an {@link IllegalArgumentException} that names the column of the error. */
    default T orThrow() {
        return switch (this) {
            case Success<T> success -> success.value();
            case Failure<T> failure -> throw new IllegalArgumentException(failure.toString());
        };
    }
}
