package io.github.toxicssj.logik.syntax;

/**
 * Writes formulas in canonical form: the symbols {@code ~ & | → ↔ ∀ ∃}, one
 * space around each binary connective, and parentheses around every binary
 * subformula except the outermost one.
 */
final class Printer {

    private Printer() {}

    static String print(Formula formula) {
        StringBuilder out = new StringBuilder();
        write(formula, false, out);
        return out.toString();
    }

    private static void write(Formula formula, boolean nested, StringBuilder out) {
        switch (formula) {
            case Formula.Atom atom -> out.append(atom.letter());
            case Formula.Predicate predicate -> {
                out.append(predicate.letter());
                for (Term term : predicate.terms()) {
                    out.append(term.name());
                }
            }
            case Formula.Not not -> {
                out.append('~');
                write(not.operand(), true, out);
            }
            case Formula.Binary binary -> {
                if (nested) {
                    out.append('(');
                }
                write(binary.left(), true, out);
                out.append(' ').append(binary.connective().symbol()).append(' ');
                write(binary.right(), true, out);
                if (nested) {
                    out.append(')');
                }
            }
            case Formula.Quantified quantified -> {
                out.append('(').append(quantified.quantifier().symbol()).append(quantified.variable().name()).append(')');
                write(quantified.body(), true, out);
            }
        }
    }
}
