package io.github.toxicssj.logik.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An argument to prove: premises and a conclusion, written
 * {@code {premise//premise=>conclusion}}.
 */
public record Problem(List<Formula> premises, Formula conclusion) {

    public Problem {
        premises = List.copyOf(premises);
        Objects.requireNonNull(conclusion);
    }

    /** Parses {@code {P//Q=>C}}. An empty premise part, as in {@code {=>C}}, means no premises. */
    public static ParseResult<Problem> parse(String text) {
        int open = firstNonSpace(text);
        if (open == text.length() || text.charAt(open) != '{') {
            return failure(text, open, "expected '{'");
        }
        int close = text.lastIndexOf('}');
        if (close < 0 || lastNonSpace(text) != close) {
            return failure(text, close < 0 ? text.length() : lastNonSpace(text) + 1, "expected '}' at the end");
        }
        int arrow = text.indexOf("=>", open);
        if (arrow < 0 || arrow > close) {
            return failure(text, close, "expected '=>' before the conclusion");
        }
        int secondArrow = text.indexOf("=>", arrow + 2);
        if (secondArrow >= 0 && secondArrow < close) {
            return failure(text, secondArrow, "only one '=>' is allowed");
        }

        List<Formula> premises = new ArrayList<>();
        if (!text.substring(open + 1, arrow).isBlank()) {
            int start = open + 1;
            while (true) {
                int separator = text.indexOf("//", start);
                int end = separator >= 0 && separator < arrow ? separator : arrow;
                switch (part(text, start, end)) {
                    case ParseResult.Failure<Formula> failure -> {
                        return new ParseResult.Failure<>(failure.column(), failure.message());
                    }
                    case ParseResult.Success<Formula> premise -> premises.add(premise.value());
                }
                if (end == arrow) {
                    break;
                }
                start = end + 2;
            }
        }
        return switch (part(text, arrow + 2, close)) {
            case ParseResult.Failure<Formula> failure -> new ParseResult.Failure<>(failure.column(), failure.message());
            case ParseResult.Success<Formula> conclusion -> new ParseResult.Success<>(new Problem(premises, conclusion.value()));
        };
    }

    /** Parses the formula in {@code text[start, end)}, with columns counted in the whole text. */
    private static ParseResult<Formula> part(String text, int start, int end) {
        ParseResult<Formula> result = FormulaParser.parse(text.substring(start, end));
        if (result instanceof ParseResult.Failure<Formula> failure) {
            return new ParseResult.Failure<>(column(text, start) - 1 + failure.column(), failure.message());
        }
        return result;
    }

    private static ParseResult<Problem> failure(String text, int index, String message) {
        return new ParseResult.Failure<>(column(text, index), message);
    }

    /** The 1-based column, in code points, of the char at {@code index}. */
    private static int column(String text, int index) {
        return text.codePointCount(0, index) + 1;
    }

    private static int firstNonSpace(String text) {
        int i = 0;
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }

    private static int lastNonSpace(String text) {
        int i = text.length() - 1;
        while (i >= 0 && Character.isWhitespace(text.charAt(i))) {
            i--;
        }
        return i;
    }

    /** Writes the problem back as {@code {P//Q=>C}}. */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("{");
        for (int i = 0; i < premises.size(); i++) {
            if (i > 0) {
                out.append("//");
            }
            out.append(premises.get(i));
        }
        return out.append("=>").append(conclusion).append('}').toString();
    }
}
