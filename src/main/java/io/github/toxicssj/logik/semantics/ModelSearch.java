package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Term;

/**
 * Looks for a finite model of a set of formulas by trying every
 * interpretation over domains of size 1, 2, … in a fixed order, so the first
 * model found is always the same.
 *
 * <p>The number of interpretations grows fast with the domain and the arity
 * of the predicates, so the search stops at a size whose interpretations would
 * exceed a budget. Not finding a model is then evidence, not proof: a formula
 * set can have only infinite models, or only larger ones.
 */
public final class ModelSearch {

    /** What the search found. */
    public sealed interface Result {
        /** A model of every formula. */
        record Found(Model model) implements Result {}

        /** No model with a domain of up to {@code size} individuals. */
        record NoneUpTo(int size) implements Result {}
    }

    /** The default limit on the number of interpretations tried for one domain size. */
    public static final long DEFAULT_BUDGET = 1L << 22;

    private ModelSearch() {}

    /** A model of every formula, with a domain of at most {@code maxSize}, within the default budget. */
    public static Result find(List<Formula> formulas, int maxSize) {
        return find(formulas, maxSize, DEFAULT_BUDGET);
    }

    public static Result find(List<Formula> formulas, int maxSize, long budget) {
        List<Character> names = new ArrayList<>();
        List<Character> letters = new ArrayList<>();
        List<Integer> arities = new ArrayList<>();
        for (Formula formula : formulas) {
            collect(formula, new ArrayList<>(), names, letters, arities);
        }
        int searched = 0;
        for (int size = 1; size <= maxSize; size++) {
            if (!withinBudget(size, names.size(), arities, budget)) {
                break;
            }
            Model model = search(formulas, size, names, letters, arities);
            if (model != null) {
                return new Result.Found(model);
            }
            searched = size;
        }
        return new Result.NoneUpTo(searched);
    }

    /** A model where every premise holds and the conclusion does not, which shows the argument is not valid. */
    public static Result countermodel(List<Formula> premises, Formula conclusion, int maxSize) {
        List<Formula> formulas = new ArrayList<>(premises);
        formulas.add(new Formula.Not(conclusion));
        return find(formulas, maxSize);
    }

    private static boolean withinBudget(int size, int names, List<Integer> arities, long budget) {
        double count = Math.pow(size, names);
        for (int arity : arities) {
            count *= Math.pow(2, Math.pow(size, arity));
        }
        return count <= budget;
    }

    /** Tries every interpretation over a domain of {@code size}; null if none is a model. */
    private static Model search(List<Formula> formulas, int size, List<Character> names,
            List<Character> letters, List<Integer> arities) {
        int[] denotations = new int[names.size()];
        List<List<List<Integer>>> tuples = new ArrayList<>();
        long[] extensions = new long[arities.size()];
        for (int arity : arities) {
            tuples.add(allTuples(size, arity));
        }
        while (true) {
            Model model = model(size, names, denotations, letters, arities, tuples, extensions);
            boolean all = true;
            for (Formula formula : formulas) {
                if (!model.satisfies(formula)) {
                    all = false;
                    break;
                }
            }
            if (all) {
                return model;
            }
            if (!next(denotations, size, extensions, tuples)) {
                return null;
            }
        }
    }

    /** Moves to the next interpretation; false after the last one. */
    private static boolean next(int[] denotations, int size, long[] extensions, List<List<List<Integer>>> tuples) {
        for (int i = denotations.length - 1; i >= 0; i--) {
            if (++denotations[i] < size) {
                return true;
            }
            denotations[i] = 0;
        }
        for (int i = extensions.length - 1; i >= 0; i--) {
            if (++extensions[i] < (1L << tuples.get(i).size())) {
                return true;
            }
            extensions[i] = 0;
        }
        return false;
    }

    private static Model model(int size, List<Character> names, int[] denotations, List<Character> letters,
            List<Integer> arities, List<List<List<Integer>>> tuples, long[] extensions) {
        List<Model.Name> named = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            named.add(new Model.Name(names.get(i), denotations[i]));
        }
        List<Model.Extension> predicates = new ArrayList<>();
        for (int i = 0; i < arities.size(); i++) {
            List<List<Integer>> holds = new ArrayList<>();
            List<List<Integer>> all = tuples.get(i);
            for (int t = 0; t < all.size(); t++) {
                if (((extensions[i] >> t) & 1) == 1) {
                    holds.add(all.get(t));
                }
            }
            predicates.add(new Model.Extension(letters.get(i), arities.get(i), holds));
        }
        return new Model(size, named, predicates);
    }

    /** Every tuple of {@code arity} individuals, in lexicographic order. */
    private static List<List<Integer>> allTuples(int size, int arity) {
        List<List<Integer>> tuples = new ArrayList<>();
        tuples.add(List.of());
        for (int position = 0; position < arity; position++) {
            List<List<Integer>> longer = new ArrayList<>();
            for (List<Integer> tuple : tuples) {
                for (int individual = 0; individual < size; individual++) {
                    List<Integer> extended = new ArrayList<>(tuple);
                    extended.add(individual);
                    longer.add(extended);
                }
            }
            tuples = longer;
        }
        return tuples;
    }

    /** Collects the names (constants and free variables) and the letters with their arities, in order of appearance. */
    private static void collect(Formula formula, List<Character> bound, List<Character> names,
            List<Character> letters, List<Integer> arities) {
        switch (formula) {
            case Formula.Atom atom -> addLetter(atom.letter(), 0, letters, arities);
            case Formula.Predicate predicate -> {
                addLetter(predicate.letter(), predicate.terms().size(), letters, arities);
                for (Term term : predicate.terms()) {
                    if (!bound.contains(term.name()) && !names.contains(term.name())) {
                        names.add(term.name());
                    }
                }
            }
            case Formula.Not not -> collect(not.operand(), bound, names, letters, arities);
            case Formula.Binary binary -> {
                collect(binary.left(), bound, names, letters, arities);
                collect(binary.right(), bound, names, letters, arities);
            }
            case Formula.Quantified quantified -> {
                bound.add(quantified.variable().name());
                collect(quantified.body(), bound, names, letters, arities);
                bound.remove(bound.size() - 1);
            }
        }
    }

    private static void addLetter(char letter, int arity, List<Character> letters, List<Integer> arities) {
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i) == letter && arities.get(i) == arity) {
                return;
            }
        }
        letters.add(letter);
        arities.add(arity);
    }
}
