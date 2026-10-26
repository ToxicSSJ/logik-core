package io.github.toxicssj.logik.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A formula of propositional or predicate logic.
 *
 * <p>Formulas are immutable. Two formulas are {@code equals} when they have the
 * same structure and the same variable names, so {@code (∀x)Fx} and
 * {@code (∀y)Fy} differ; {@link #alphaEquals} treats them as the same.
 *
 * <p>{@code toString} writes every binary subformula in parentheses except the
 * outermost one, as Copi does, so parsing it gives back an equal formula.
 */
public sealed interface Formula permits Formula.Atom, Formula.Predicate, Formula.Not, Formula.Binary, Formula.Quantified {

    /** A sentence letter: {@code P}. */
    record Atom(char letter) implements Formula {
        public Atom {
            requireUppercase(letter);
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** A predicate letter applied to one or more terms: {@code Fa}, {@code Lxy}. */
    record Predicate(char letter, List<Term> terms) implements Formula {
        public Predicate {
            requireUppercase(letter);
            terms = List.copyOf(terms);
            if (terms.isEmpty()) {
                throw new IllegalArgumentException("a predicate needs at least one term; use Atom for " + letter);
            }
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** {@code ~operand}. */
    record Not(Formula operand) implements Formula {
        public Not {
            Objects.requireNonNull(operand);
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** The binary connectives, from the one that binds tightest. */
    enum Connective {
        AND("&"), OR("|"), IMPLIES("→"), IFF("↔");

        private final String symbol;

        Connective(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    /** A formula built with a binary connective. */
    sealed interface Binary extends Formula permits And, Or, Implies, Iff {
        Formula left();

        Formula right();

        Connective connective();

        /** A formula with the same connective and the given sides. */
        static Binary of(Connective connective, Formula left, Formula right) {
            return switch (connective) {
                case AND -> new And(left, right);
                case OR -> new Or(left, right);
                case IMPLIES -> new Implies(left, right);
                case IFF -> new Iff(left, right);
            };
        }
    }

    /** {@code left & right}. */
    record And(Formula left, Formula right) implements Binary {
        public And {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
        }

        @Override
        public Connective connective() {
            return Connective.AND;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** {@code left | right}. */
    record Or(Formula left, Formula right) implements Binary {
        public Or {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
        }

        @Override
        public Connective connective() {
            return Connective.OR;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** {@code left → right}: left is the antecedent, right the consequent. */
    record Implies(Formula left, Formula right) implements Binary {
        public Implies {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
        }

        @Override
        public Connective connective() {
            return Connective.IMPLIES;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** {@code left ↔ right}. */
    record Iff(Formula left, Formula right) implements Binary {
        public Iff {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
        }

        @Override
        public Connective connective() {
            return Connective.IFF;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** The two quantifiers. */
    enum Quantifier {
        FOR_ALL("∀"), EXISTS("∃");

        private final String symbol;

        Quantifier(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    /** A formula that starts with a quantifier. */
    sealed interface Quantified extends Formula permits ForAll, Exists {
        Variable variable();

        Formula body();

        Quantifier quantifier();

        /** A formula with the same quantifier, binding {@code variable} in {@code body}. */
        static Quantified of(Quantifier quantifier, Variable variable, Formula body) {
            return switch (quantifier) {
                case FOR_ALL -> new ForAll(variable, body);
                case EXISTS -> new Exists(variable, body);
            };
        }
    }

    /** {@code (∀x)body}. */
    record ForAll(Variable variable, Formula body) implements Quantified {
        public ForAll {
            Objects.requireNonNull(variable);
            Objects.requireNonNull(body);
        }

        @Override
        public Quantifier quantifier() {
            return Quantifier.FOR_ALL;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** {@code (∃x)body}. */
    record Exists(Variable variable, Formula body) implements Quantified {
        public Exists {
            Objects.requireNonNull(variable);
            Objects.requireNonNull(body);
        }

        @Override
        public Quantifier quantifier() {
            return Quantifier.EXISTS;
        }

        @Override
        public String toString() {
            return Printer.print(this);
        }
    }

    /** Parses {@code text}, or throws {@link IllegalArgumentException} with the column of the error. */
    static Formula parse(String text) {
        return FormulaParser.parse(text).orThrow();
    }

    /** The variables with a free occurrence, in order of first occurrence. */
    default Set<Variable> freeVariables() {
        Set<Variable> free = new LinkedHashSet<>();
        collectFree(this, new ArrayList<>(), free);
        return Collections.unmodifiableSet(free);
    }

    /** True if no variable occurs free. */
    default boolean isClosed() {
        return freeVariables().isEmpty();
    }

    /** The constants that occur, in order of first occurrence. */
    default Set<Constant> constants() {
        Set<Constant> constants = new LinkedHashSet<>();
        collectConstants(this, constants);
        return Collections.unmodifiableSet(constants);
    }

    /**
     * This formula with every free occurrence of {@code variable} replaced by
     * {@code term}. Empty if a quantifier inside would capture {@code term}, as
     * when putting {@code y} for {@code x} in {@code (∃y)Lxy}.
     */
    default Optional<Formula> substitute(Variable variable, Term term) {
        return Optional.ofNullable(substitute(this, variable, Objects.requireNonNull(term)));
    }

    /** True if both formulas are equal up to the names of their bound variables. */
    default boolean alphaEquals(Formula other) {
        return alphaEquals(this, other, new ArrayList<>(), new ArrayList<>());
    }

    private static void requireUppercase(char letter) {
        if (letter < 'A' || letter > 'Z') {
            throw new IllegalArgumentException("not an uppercase letter: " + letter);
        }
    }

    private static void collectFree(Formula formula, List<Variable> bound, Set<Variable> free) {
        switch (formula) {
            case Atom atom -> { }
            case Predicate predicate -> {
                for (Term term : predicate.terms()) {
                    if (term instanceof Variable variable && !bound.contains(variable)) {
                        free.add(variable);
                    }
                }
            }
            case Not not -> collectFree(not.operand(), bound, free);
            case Binary binary -> {
                collectFree(binary.left(), bound, free);
                collectFree(binary.right(), bound, free);
            }
            case Quantified quantified -> {
                bound.add(quantified.variable());
                collectFree(quantified.body(), bound, free);
                bound.remove(bound.size() - 1);
            }
        }
    }

    private static void collectConstants(Formula formula, Set<Constant> constants) {
        switch (formula) {
            case Atom atom -> { }
            case Predicate predicate -> {
                for (Term term : predicate.terms()) {
                    if (term instanceof Constant constant) {
                        constants.add(constant);
                    }
                }
            }
            case Not not -> collectConstants(not.operand(), constants);
            case Binary binary -> {
                collectConstants(binary.left(), constants);
                collectConstants(binary.right(), constants);
            }
            case Quantified quantified -> collectConstants(quantified.body(), constants);
        }
    }

    /** Null when {@code term} would be captured. */
    private static Formula substitute(Formula formula, Variable variable, Term term) {
        return switch (formula) {
            case Atom atom -> atom;
            case Predicate predicate -> {
                List<Term> terms = new ArrayList<>();
                for (Term t : predicate.terms()) {
                    terms.add(t.equals(variable) ? term : t);
                }
                yield new Predicate(predicate.letter(), terms);
            }
            case Not not -> {
                Formula operand = substitute(not.operand(), variable, term);
                yield operand == null ? null : new Not(operand);
            }
            case Binary binary -> {
                Formula left = substitute(binary.left(), variable, term);
                Formula right = substitute(binary.right(), variable, term);
                yield left == null || right == null ? null : Binary.of(binary.connective(), left, right);
            }
            case Quantified quantified -> {
                if (quantified.variable().equals(variable) || !quantified.body().freeVariables().contains(variable)) {
                    yield quantified;
                }
                if (quantified.variable().equals(term)) {
                    yield null;
                }
                Formula body = substitute(quantified.body(), variable, term);
                yield body == null ? null : Quantified.of(quantified.quantifier(), quantified.variable(), body);
            }
        };
    }

    /** {@code bound1} and {@code bound2} hold the variables bound on the way down, innermost last. */
    private static boolean alphaEquals(Formula f, Formula g, List<Variable> bound1, List<Variable> bound2) {
        return switch (f) {
            case Atom atom -> atom.equals(g);
            case Predicate p -> {
                if (!(g instanceof Predicate q) || p.letter() != q.letter() || p.terms().size() != q.terms().size()) {
                    yield false;
                }
                for (int i = 0; i < p.terms().size(); i++) {
                    if (!sameTerm(p.terms().get(i), q.terms().get(i), bound1, bound2)) {
                        yield false;
                    }
                }
                yield true;
            }
            case Not not -> g instanceof Not other && alphaEquals(not.operand(), other.operand(), bound1, bound2);
            case Binary b -> g instanceof Binary c
                    && b.connective() == c.connective()
                    && alphaEquals(b.left(), c.left(), bound1, bound2)
                    && alphaEquals(b.right(), c.right(), bound1, bound2);
            case Quantified q -> {
                if (!(g instanceof Quantified r) || q.quantifier() != r.quantifier()) {
                    yield false;
                }
                bound1.add(q.variable());
                bound2.add(r.variable());
                boolean same = alphaEquals(q.body(), r.body(), bound1, bound2);
                bound1.remove(bound1.size() - 1);
                bound2.remove(bound2.size() - 1);
                yield same;
            }
        };
    }

    private static boolean sameTerm(Term s, Term t, List<Variable> bound1, List<Variable> bound2) {
        if (s instanceof Variable v && t instanceof Variable w) {
            int i = bound1.lastIndexOf(v);
            int j = bound2.lastIndexOf(w);
            return i == j && (i >= 0 || v.equals(w));
        }
        return s.equals(t);
    }
}
