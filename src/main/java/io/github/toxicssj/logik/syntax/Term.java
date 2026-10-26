package io.github.toxicssj.logik.syntax;

/**
 * An argument of a predicate. Following Copi, {@code x}, {@code y} and
 * {@code z} are variables and {@code a} to {@code w} are constants.
 */
public sealed interface Term permits Variable, Constant {

    char name();

    /** The variable or constant written as {@code letter}, or null if it is neither. */
    static Term of(char letter) {
        if (Variable.isVariable(letter)) {
            return new Variable(letter);
        }
        if (Constant.isConstant(letter)) {
            return new Constant(letter);
        }
        return null;
    }
}
