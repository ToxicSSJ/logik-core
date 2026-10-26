package io.github.toxicssj.logik.syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses formulas written as in Copi or with the usual ASCII and Unicode
 * symbols. The syntax is specified in {@code src/test/resources/corpus/README.md}:
 *
 * <pre>
 * formula    = biconditional
 * biconditional = conditional [ "↔" biconditional ]     ↔ &lt;-&gt; ≡
 * conditional   = disjunction [ "→" conditional ]       → -&gt; ⊃
 * disjunction   = conjunction { "|" conjunction }       | ∨
 * conjunction   = unary { "&amp;" unary }                   &amp; ∧ · •
 * unary      = "~" unary | quantifier unary | "(" formula ")" | "[" formula "]" | atom    ~ ¬
 * quantifier = "(" ("∀" | "∃") variable ")" | "(" variable ")" | ("∀" | "∃") variable
 * atom       = uppercase letter followed, with no space, by lowercase letters
 * </pre>
 *
 * <p>Errors are reported at the column where the first token that cannot
 * continue the formula starts. Tokens are read one at a time, so a character
 * that begins no token is only reported when the parser reaches it.
 */
public final class FormulaParser {

    private enum Kind {
        NOT, AND, OR, IMPLIES, IFF, FOR_ALL, EXISTS,
        OPEN_PAREN, CLOSE_PAREN, OPEN_BRACKET, CLOSE_BRACKET,
        ATOM, TERM, END, UNKNOWN
    }

    private record Token(Kind kind, String text, int column) {
        String describe() {
            return kind == Kind.END ? "the end of the input" : "'" + text + "'";
        }
    }

    /** A syntax error; it never leaves this class. */
    private static final class SyntaxError extends RuntimeException {
        final int column;

        SyntaxError(int column, String message) {
            super(message, null, false, false);
            this.column = column;
        }
    }

    private final int[] text;
    private final List<Token> tokens = new ArrayList<>();
    private int position;
    private int next;

    private FormulaParser(String text) {
        this.text = text.codePoints().toArray();
    }

    public static ParseResult<Formula> parse(String text) {
        FormulaParser parser = new FormulaParser(text);
        try {
            Formula formula = parser.biconditional();
            Token extra = parser.peek(0);
            if (extra.kind() != Kind.END) {
                throw parser.unexpected(extra, "expected a connective or the end of the formula");
            }
            return new ParseResult.Success<>(formula);
        } catch (SyntaxError e) {
            return new ParseResult.Failure<>(e.column, e.getMessage());
        }
    }

    private Formula biconditional() {
        Formula left = conditional();
        if (peek(0).kind() == Kind.IFF) {
            take();
            return new Formula.Iff(left, biconditional());
        }
        return left;
    }

    private Formula conditional() {
        Formula left = disjunction();
        if (peek(0).kind() == Kind.IMPLIES) {
            take();
            return new Formula.Implies(left, conditional());
        }
        return left;
    }

    private Formula disjunction() {
        Formula formula = conjunction();
        while (peek(0).kind() == Kind.OR) {
            take();
            formula = new Formula.Or(formula, conjunction());
        }
        return formula;
    }

    private Formula conjunction() {
        Formula formula = unary();
        while (peek(0).kind() == Kind.AND) {
            take();
            formula = new Formula.And(formula, unary());
        }
        return formula;
    }

    private Formula unary() {
        Token token = peek(0);
        switch (token.kind()) {
            case NOT -> {
                take();
                return new Formula.Not(unary());
            }
            case FOR_ALL, EXISTS -> {
                take();
                Variable variable = variable();
                return quantified(token, variable, unary());
            }
            case OPEN_PAREN -> {
                Token second = peek(1);
                if (second.kind() == Kind.FOR_ALL || second.kind() == Kind.EXISTS) {
                    take();
                    take();
                    Variable variable = variable();
                    expect(Kind.CLOSE_PAREN, ")");
                    return quantified(second, variable, unary());
                }
                if (isVariable(second) && peek(2).kind() == Kind.CLOSE_PAREN) {
                    take();
                    Variable variable = variable();
                    take();
                    return new Formula.ForAll(variable, unary());
                }
                take();
                Formula inner = biconditional();
                expect(Kind.CLOSE_PAREN, ")");
                return inner;
            }
            case OPEN_BRACKET -> {
                take();
                Formula inner = biconditional();
                expect(Kind.CLOSE_BRACKET, "]");
                return inner;
            }
            case ATOM -> {
                take();
                return atom(token.text());
            }
            default -> throw unexpected(token, "expected a formula");
        }
    }

    private static Formula quantified(Token quantifier, Variable variable, Formula body) {
        return quantifier.kind() == Kind.FOR_ALL
                ? new Formula.ForAll(variable, body)
                : new Formula.Exists(variable, body);
    }

    private static Formula atom(String text) {
        if (text.length() == 1) {
            return new Formula.Atom(text.charAt(0));
        }
        List<Term> terms = new ArrayList<>();
        for (int i = 1; i < text.length(); i++) {
            terms.add(Term.of(text.charAt(i)));
        }
        return new Formula.Predicate(text.charAt(0), terms);
    }

    private Variable variable() {
        Token token = peek(0);
        if (!isVariable(token)) {
            throw unexpected(token, "expected a variable (x, y or z)");
        }
        take();
        return new Variable(token.text().charAt(0));
    }

    private static boolean isVariable(Token token) {
        return token.kind() == Kind.TERM && Variable.isVariable(token.text().charAt(0));
    }

    private void expect(Kind kind, String symbol) {
        Token token = peek(0);
        if (token.kind() != kind) {
            throw unexpected(token, "expected '" + symbol + "'");
        }
        take();
    }

    private SyntaxError unexpected(Token token, String expectation) {
        if (token.kind() == Kind.UNKNOWN) {
            return new SyntaxError(token.column(), "unknown symbol " + token.describe());
        }
        return new SyntaxError(token.column(), expectation + ", found " + token.describe());
    }

    // --- Tokens, read on demand ---

    private Token peek(int ahead) {
        while (tokens.size() <= next + ahead) {
            Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
            if (last != null && (last.kind() == Kind.END || last.kind() == Kind.UNKNOWN)) {
                return last;
            }
            tokens.add(read());
        }
        return tokens.get(next + ahead);
    }

    private void take() {
        peek(0);
        next++;
    }

    private Token read() {
        while (position < text.length && Character.isWhitespace(text[position])) {
            position++;
        }
        int start = position;
        int column = start + 1;
        if (position >= text.length) {
            return new Token(Kind.END, "", column);
        }
        int c = text[position];
        if (c >= 'A' && c <= 'Z') {
            position++;
            while (position < text.length && text[position] >= 'a' && text[position] <= 'z') {
                position++;
            }
            return new Token(Kind.ATOM, new String(text, start, position - start), column);
        }
        if (c >= 'a' && c <= 'z') {
            position++;
            return new Token(Kind.TERM, Character.toString(c), column);
        }
        if (startsWith("<->")) {
            position += 3;
            return new Token(Kind.IFF, "<->", column);
        }
        if (startsWith("->")) {
            position += 2;
            return new Token(Kind.IMPLIES, "->", column);
        }
        Kind kind = switch (c) {
            case '~', '¬' -> Kind.NOT;
            case '&', '∧', '·', '•' -> Kind.AND;
            case '|', '∨' -> Kind.OR;
            case '→', '⊃' -> Kind.IMPLIES;
            case '↔', '≡' -> Kind.IFF;
            case '∀' -> Kind.FOR_ALL;
            case '∃' -> Kind.EXISTS;
            case '(' -> Kind.OPEN_PAREN;
            case ')' -> Kind.CLOSE_PAREN;
            case '[' -> Kind.OPEN_BRACKET;
            case ']' -> Kind.CLOSE_BRACKET;
            default -> Kind.UNKNOWN;
        };
        position++;
        return new Token(kind, Character.toString(c), column);
    }

    private boolean startsWith(String symbol) {
        if (position + symbol.length() > text.length) {
            return false;
        }
        for (int i = 0; i < symbol.length(); i++) {
            if (text[position + i] != symbol.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
