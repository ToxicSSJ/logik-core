package io.github.toxicssj.logik.syntax;

/** An individual constant: a lowercase letter from {@code a} to {@code w}. */
public record Constant(char name) implements Term {

    public Constant {
        if (!isConstant(name)) {
            throw new IllegalArgumentException("not a constant: " + name);
        }
    }

    public static boolean isConstant(char letter) {
        return letter >= 'a' && letter <= 'w';
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
