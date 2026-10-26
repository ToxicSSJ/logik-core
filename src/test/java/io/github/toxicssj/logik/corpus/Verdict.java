package io.github.toxicssj.logik.corpus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** What a proof checker must say about a proof. */
public sealed interface Verdict {

    /** Every step follows and the last line is the conclusion. */
    record Valid() implements Verdict {
        @Override
        public String toString() {
            return "valid";
        }
    }

    /** {@code line} is the first step that does not follow. */
    record InvalidAtLine(int line, String reason) implements Verdict {
        @Override
        public String toString() {
            return "invalid at " + line + ": " + reason;
        }
    }

    /** Every step follows, but the last line is not the conclusion. */
    record InvalidAtEnd(String reason) implements Verdict {
        @Override
        public String toString() {
            return "invalid at end: " + reason;
        }
    }

    default boolean isValid() {
        return this instanceof Valid;
    }

    Pattern INVALID = Pattern.compile("invalid at (\\d+|end): (.+)");

    /** Reads a verdict written as in the corpus, or returns null if the text is not one. */
    static Verdict parse(String text) {
        if (text.equals("valid")) {
            return new Valid();
        }
        Matcher m = INVALID.matcher(text);
        if (!m.matches()) {
            return null;
        }
        return m.group(1).equals("end")
                ? new InvalidAtEnd(m.group(2))
                : new InvalidAtLine(Integer.parseInt(m.group(1)), m.group(2));
    }
}
