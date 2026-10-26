package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Term;
import io.github.toxicssj.logik.syntax.Variable;

/**
 * A finite interpretation: the domain {@code {0, …, size-1}}, the individual
 * each name denotes, and which tuples each letter holds of. A sentence letter
 * is a predicate with no arguments, true when it holds of the empty tuple.
 *
 * <p>Free variables are read as names of arbitrary individuals, like
 * constants, so a model gives them a denotation too.
 *
 * @param names      each constant or free variable and the individual it denotes, by letter
 * @param predicates each letter and the tuples it holds of, by letter
 */
public record Model(int size, List<Name> names, List<Extension> predicates) {

    /** A constant or free variable and the individual it denotes. */
    public record Name(char letter, int individual) {}

    /** A sentence or predicate letter, its number of arguments, and the tuples it holds of. */
    public record Extension(char letter, int arity, List<List<Integer>> tuples) {
        public Extension {
            List<List<Integer>> copies = new ArrayList<>();
            for (List<Integer> tuple : tuples) {
                copies.add(List.copyOf(tuple));
            }
            tuples = List.copyOf(copies);
        }
    }

    public Model {
        if (size < 1) {
            throw new IllegalArgumentException("a domain has at least one individual");
        }
        names = List.copyOf(names);
        predicates = List.copyOf(predicates);
    }

    /** True if {@code formula} holds in this model. */
    public boolean satisfies(Formula formula) {
        return holds(formula, new ArrayList<>(), new ArrayList<>());
    }

    /** {@code bound} and {@code values} pair each bound variable with its individual, innermost last. */
    private boolean holds(Formula formula, List<Variable> bound, List<Integer> values) {
        return switch (formula) {
            case Formula.Atom atom -> extension(atom.letter(), 0).tuples().contains(List.of());
            case Formula.Predicate predicate -> {
                List<Integer> tuple = new ArrayList<>();
                for (Term term : predicate.terms()) {
                    int binder = term instanceof Variable variable ? bound.lastIndexOf(variable) : -1;
                    tuple.add(binder >= 0 ? values.get(binder) : denotation(term.name()));
                }
                yield extension(predicate.letter(), tuple.size()).tuples().contains(tuple);
            }
            case Formula.Not not -> !holds(not.operand(), bound, values);
            case Formula.And and -> holds(and.left(), bound, values) && holds(and.right(), bound, values);
            case Formula.Or or -> holds(or.left(), bound, values) || holds(or.right(), bound, values);
            case Formula.Implies implies -> !holds(implies.left(), bound, values) || holds(implies.right(), bound, values);
            case Formula.Iff iff -> holds(iff.left(), bound, values) == holds(iff.right(), bound, values);
            case Formula.Quantified quantified -> {
                boolean universal = quantified.quantifier() == Formula.Quantifier.FOR_ALL;
                boolean result = universal;
                bound.add(quantified.variable());
                for (int individual = 0; individual < size && result == universal; individual++) {
                    values.add(individual);
                    result = holds(quantified.body(), bound, values);
                    values.remove(values.size() - 1);
                }
                bound.remove(bound.size() - 1);
                yield result;
            }
        };
    }

    private int denotation(char letter) {
        for (Name name : names) {
            if (name.letter() == letter) {
                return name.individual();
            }
        }
        throw new IllegalArgumentException("the model does not interpret " + letter);
    }

    private Extension extension(char letter, int arity) {
        for (Extension extension : predicates) {
            if (extension.letter() == letter && extension.arity() == arity) {
                return extension;
            }
        }
        throw new IllegalArgumentException("the model does not interpret " + letter + " with " + arity + " arguments");
    }

    /** For example {@code domain {0, 1}; a = 0; F = {(1)}; P = true}. */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("domain {");
        for (int i = 0; i < size; i++) {
            out.append(i > 0 ? ", " : "").append(i);
        }
        out.append('}');
        for (Name name : names) {
            out.append("; ").append(name.letter()).append(" = ").append(name.individual());
        }
        for (Extension extension : predicates) {
            out.append("; ").append(extension.letter()).append(" = ");
            if (extension.arity() == 0) {
                out.append(extension.tuples().isEmpty() ? "false" : "true");
                continue;
            }
            out.append('{');
            for (int i = 0; i < extension.tuples().size(); i++) {
                out.append(i > 0 ? ", " : "").append('(');
                List<Integer> tuple = extension.tuples().get(i);
                for (int j = 0; j < tuple.size(); j++) {
                    out.append(j > 0 ? ", " : "").append(tuple.get(j));
                }
                out.append(')');
            }
            out.append('}');
        }
        return out.toString();
    }
}
