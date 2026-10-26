package io.github.toxicssj.logik.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Why a proof line holds: the lines it cites and the name of its rule, written
 * {@code 2,3 CONJ}. The rule name is kept as written; whether a rule with
 * that name exists is for the proof checker to say.
 */
public record Justification(List<Integer> citations, String rule) {

    public Justification {
        citations = List.copyOf(citations);
        Objects.requireNonNull(rule);
    }

    /** Parses {@code 2,3 CONJ}, {@code 1 SIMP}, or a rule name alone. */
    public static ParseResult<Justification> parse(String text) {
        int[] chars = text.codePoints().toArray();
        int i = skipSpaces(chars, 0);
        List<Integer> citations = new ArrayList<>();
        if (i < chars.length && isDigit(chars[i])) {
            while (true) {
                int start = i;
                while (i < chars.length && isDigit(chars[i])) {
                    i++;
                }
                if (i - start > 9) {
                    return new ParseResult.Failure<>(start + 1, "line number too large");
                }
                citations.add(Integer.parseInt(new String(chars, start, i - start)));
                int afterNumber = i;
                i = skipSpaces(chars, i);
                if (i < chars.length && chars[i] == ',') {
                    i = skipSpaces(chars, i + 1);
                    if (i >= chars.length || !isDigit(chars[i])) {
                        return new ParseResult.Failure<>(i + 1, "expected a line number");
                    }
                    continue;
                }
                if (i == afterNumber && i < chars.length) {
                    return new ParseResult.Failure<>(i + 1, "expected ',' or a space before the rule name");
                }
                break;
            }
        }
        int start = i;
        while (i < chars.length && isLetter(chars[i])) {
            i++;
        }
        if (i == start) {
            return new ParseResult.Failure<>(i + 1, citations.isEmpty() ? "expected a rule name" : "expected ',' or a rule name");
        }
        String rule = new String(chars, start, i - start);
        i = skipSpaces(chars, i);
        if (i < chars.length) {
            return new ParseResult.Failure<>(i + 1, "unexpected text after the rule name");
        }
        return new ParseResult.Success<>(new Justification(citations, rule));
    }

    private static int skipSpaces(int[] chars, int i) {
        while (i < chars.length && Character.isWhitespace(chars[i])) {
            i++;
        }
        return i;
    }

    private static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(int c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    /** Writes the justification back as {@code 2,3 CONJ}. */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < citations.size(); i++) {
            out.append(i > 0 ? "," : "").append(citations.get(i));
        }
        return out.append(citations.isEmpty() ? "" : " ").append(rule).toString();
    }
}
