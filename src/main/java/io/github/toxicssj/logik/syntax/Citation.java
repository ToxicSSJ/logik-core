package io.github.toxicssj.logik.syntax;

/** What a justification cites: one line, or a range of lines that forms a subproof. */
public sealed interface Citation {

    /** One line: {@code 3}. */
    record Line(int number) implements Citation {
        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }

    /** The lines from {@code first} to {@code last}, as CP and IP cite a subproof: {@code 3-5}. */
    record Range(int first, int last) implements Citation {
        @Override
        public String toString() {
            return first + "-" + last;
        }
    }
}
