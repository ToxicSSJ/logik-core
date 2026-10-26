package io.github.toxicssj.logik.syntax;

/** An individual variable: {@code x}, {@code y} or {@code z}. */
public record Variable(char name) implements Term {

    public Variable {
        if (!isVariable(name)) {
            throw new IllegalArgumentException("not a variable: " + name);
        }
    }

    public static boolean isVariable(char letter) {
        return letter >= 'x' && letter <= 'z';
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
