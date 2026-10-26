package io.github.toxicssj.logik.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Why a proof line holds: what it cites and the name of its rule, written
 * {@code 2,3 CONJ}, {@code 3-5 CP} or {@code ACP}. The rule name is kept as
 * written; whether a rule with that name exists is for the proof checker to say.
 */
public record Justification(List<Citation> citations, String rule) {

    public Justification {
        citations = List.copyOf(citations);
        Objects.requireNonNull(rule);
    }

    /** A justification that cites single lines: {@code of("CONJ", 2, 3)}. */
    public static Justification of(String rule, int... lines) {
        List<Citation> citations = new ArrayList<>();
        for (int line : lines) {
            citations.add(new Citation.Line(line));
        }
        return new Justification(citations, rule);
    }

    /**
     * Parses a comma-separated list of citations, each a line number or a
     * range like {@code 3-5} (an en dash also works), then the rule name. A rule
     * name alone, as in {@code ACP}, cites nothing.
     */
    public static ParseResult<Justification> parse(String text) {
        int[] chars = text.codePoints().toArray();
        int i = skipSpaces(chars, 0);
        List<Citation> citations = new ArrayList<>();
        if (i < chars.length && isDigit(chars[i])) {
            while (true) {
                int start = i;
                i = skipDigits(chars, i);
                if (i - start > 9) {
                    return new ParseResult.Failure<>(start + 1, "line number too large");
                }
                int first = number(chars, start, i);
                int dash = skipSpaces(chars, i);
                if (dash < chars.length && (chars[dash] == '-' || chars[dash] == '–')) {
                    i = skipSpaces(chars, dash + 1);
                    if (i >= chars.length || !isDigit(chars[i])) {
                        return new ParseResult.Failure<>(i + 1, "expected the last line of the range");
                    }
                    int lastStart = i;
                    i = skipDigits(chars, i);
                    if (i - lastStart > 9) {
                        return new ParseResult.Failure<>(lastStart + 1, "line number too large");
                    }
                    citations.add(new Citation.Range(first, number(chars, lastStart, i)));
                } else {
                    citations.add(new Citation.Line(first));
                }
                int afterCitation = i;
                i = skipSpaces(chars, i);
                if (i < chars.length && chars[i] == ',') {
                    i = skipSpaces(chars, i + 1);
                    if (i >= chars.length || !isDigit(chars[i])) {
                        return new ParseResult.Failure<>(i + 1, "expected a line number");
                    }
                    continue;
                }
                if (i == afterCitation && i < chars.length) {
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

    private static int skipDigits(int[] chars, int i) {
        while (i < chars.length && isDigit(chars[i])) {
            i++;
        }
        return i;
    }

    private static int number(int[] chars, int start, int end) {
        return Integer.parseInt(new String(chars, start, end - start));
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
