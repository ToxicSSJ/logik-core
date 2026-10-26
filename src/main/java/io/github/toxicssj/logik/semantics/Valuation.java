package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;

/**
 * A truth value for each atom of a formula without quantifiers. An atom is a
 * sentence letter, like {@code P}, or a predicate applied to terms, like
 * {@code Fa}; it is named by how it is written.
 *
 * @param atoms  the atoms, in alphabetical order
 * @param values the value of each atom, in the same order
 */
public record Valuation(List<String> atoms, List<Boolean> values) {

    public Valuation {
        atoms = List.copyOf(atoms);
        values = List.copyOf(values);
        if (atoms.size() != values.size()) {
            throw new IllegalArgumentException("one value per atom");
        }
    }

    public boolean valueOf(String atom) {
        int i = atoms.indexOf(atom);
        if (i < 0) {
            throw new IllegalArgumentException("no value for " + atom);
        }
        return values.get(i);
    }

    /** The truth value of {@code formula}, which must have no quantifiers. */
    public boolean satisfies(Formula formula) {
        return switch (formula) {
            case Formula.Atom atom -> valueOf(atom.toString());
            case Formula.Predicate predicate -> valueOf(predicate.toString());
            case Formula.Not not -> !satisfies(not.operand());
            case Formula.And and -> satisfies(and.left()) && satisfies(and.right());
            case Formula.Or or -> satisfies(or.left()) || satisfies(or.right());
            case Formula.Implies implies -> !satisfies(implies.left()) || satisfies(implies.right());
            case Formula.Iff iff -> satisfies(iff.left()) == satisfies(iff.right());
            case Formula.Quantified quantified ->
                throw new IllegalArgumentException("a valuation cannot evaluate " + quantified + "; use a Model");
        };
    }

    /** For example {@code P = T, Q = F}. */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < atoms.size(); i++) {
            out.append(i > 0 ? ", " : "").append(atoms.get(i)).append(" = ").append(values.get(i) ? "T" : "F");
        }
        return out.toString();
    }

    /** Every valuation of {@code atoms}, starting with all true and ending with all false, as Copi lists them. */
    static List<Valuation> all(List<String> atoms) {
        List<Valuation> valuations = new ArrayList<>();
        int rows = 1 << atoms.size();
        for (int row = 0; row < rows; row++) {
            List<Boolean> values = new ArrayList<>();
            for (int i = 0; i < atoms.size(); i++) {
                int bit = atoms.size() - 1 - i;
                values.add(((row >> bit) & 1) == 0);
            }
            valuations.add(new Valuation(atoms, values));
        }
        return valuations;
    }

    /** The atoms of {@code formulas}, in alphabetical order. Throws if any formula has a quantifier. */
    static List<String> atomsOf(List<Formula> formulas) {
        List<String> atoms = new ArrayList<>();
        for (Formula formula : formulas) {
            collect(formula, atoms);
        }
        atoms.sort(null);
        return atoms;
    }

    private static void collect(Formula formula, List<String> atoms) {
        switch (formula) {
            case Formula.Atom atom -> add(atom.toString(), atoms);
            case Formula.Predicate predicate -> add(predicate.toString(), atoms);
            case Formula.Not not -> collect(not.operand(), atoms);
            case Formula.Binary binary -> {
                collect(binary.left(), atoms);
                collect(binary.right(), atoms);
            }
            case Formula.Quantified quantified ->
                throw new IllegalArgumentException(quantified + " has a quantifier; truth tables need formulas without them");
        }
    }

    private static void add(String atom, List<String> atoms) {
        if (!atoms.contains(atom)) {
            atoms.add(atom);
        }
    }
}
