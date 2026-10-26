package io.github.toxicssj.logik.corpus;

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

    /** Reads {@code valid}, {@code invalid at N: reason} or {@code invalid at end: reason}; null if the text is none of them. */
    static Verdict parse(String text) {
        if (text.equals("valid")) {
            return new Valid();
        }
        String prefix = "invalid at ";
        int colon = text.indexOf(": ");
        if (!text.startsWith(prefix) || colon < 0 || colon + 2 >= text.length()) {
            return null;
        }
        String where = text.substring(prefix.length(), colon);
        String reason = text.substring(colon + 2);
        if (where.equals("end")) {
            return new InvalidAtEnd(reason);
        }
        if (where.isEmpty() || where.length() > 9) {
            return null;
        }
        for (int i = 0; i < where.length(); i++) {
            if (where.charAt(i) < '0' || where.charAt(i) > '9') {
                return null;
            }
        }
        return new InvalidAtLine(Integer.parseInt(where), reason);
    }
}
