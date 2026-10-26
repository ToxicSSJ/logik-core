package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Formula.And;
import io.github.toxicssj.logik.syntax.Formula.Iff;
import io.github.toxicssj.logik.syntax.Formula.Implies;
import io.github.toxicssj.logik.syntax.Formula.Not;
import io.github.toxicssj.logik.syntax.Formula.Or;

/**
 * A propositional semantic tree (truth tree). Starting from a set of
 * formulas, it breaks each compound formula into simpler ones: a conjunction
 * adds both parts to its branch, a disjunction splits the branch in two. A
 * branch closes when it holds a formula and its negation. If every branch
 * closes, the formulas cannot all be true.
 *
 * <p>Formulas that start with a quantifier are not broken down; they are
 * treated as sentence letters, which is right for propositional reasoning
 * only.
 *
 * <p>The tree is built in a fixed order: the first formula that does not
 * split a branch is broken down first, then the first that does.
 */
public record Tableau(Tableau.Node root) {

    /**
     * A node of the tree: the formulas it adds to its branch, and its children.
     * A leaf is closed or open; an inner node is neither.
     */
    public record Node(List<Formula> formulas, List<Node> children, boolean closed) {
        public Node {
            formulas = List.copyOf(formulas);
            children = List.copyOf(children);
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        /** True if every branch below this node closes. */
        public boolean allClosed() {
            if (isLeaf()) {
                return closed;
            }
            for (Node child : children) {
                if (!child.allClosed()) {
                    return false;
                }
            }
            return true;
        }
    }

    /** The tree for {@code formulas}. */
    public static Tableau of(List<Formula> formulas) {
        List<Formula> pending = new ArrayList<>();
        for (Formula formula : formulas) {
            if (!isLiteral(formula)) {
                pending.add(formula);
            }
        }
        return new Tableau(grow(formulas, new ArrayList<>(formulas), pending));
    }

    /** True if every branch closes, so the formulas cannot all be true. */
    public boolean isClosed() {
        return root.allClosed();
    }

    /** True if the tree of the premises and the negated conclusion closes, so the argument is valid. */
    public static boolean proves(List<Formula> premises, Formula conclusion) {
        List<Formula> formulas = new ArrayList<>(premises);
        formulas.add(new Not(conclusion));
        return of(formulas).isClosed();
    }

    /**
     * @param added   the formulas this node adds to the branch
     * @param branch  every formula on the branch, this node's included
     * @param pending the compound formulas on the branch that are not broken down yet, in order
     */
    private static Node grow(List<Formula> added, List<Formula> branch, List<Formula> pending) {
        if (contradicts(branch)) {
            return new Node(added, List.of(), true);
        }
        int next = -1;
        for (int i = 0; i < pending.size() && next < 0; i++) {
            if (split(pending.get(i)) == null) {
                next = i;
            }
        }
        if (next < 0 && !pending.isEmpty()) {
            next = 0;
        }
        if (next < 0) {
            return new Node(added, List.of(), false);
        }
        Formula formula = pending.get(next);
        List<Formula> rest = new ArrayList<>(pending);
        rest.remove(next);
        List<List<Formula>> branches = split(formula);
        if (branches == null) {
            branches = List.of(parts(formula));
        }
        List<Node> children = new ArrayList<>();
        for (List<Formula> parts : branches) {
            List<Formula> longer = new ArrayList<>(branch);
            longer.addAll(parts);
            List<Formula> morePending = new ArrayList<>(rest);
            for (Formula part : parts) {
                if (!isLiteral(part)) {
                    morePending.add(part);
                }
            }
            children.add(grow(parts, longer, morePending));
        }
        return new Node(added, children, false);
    }

    /** The parts a non-splitting formula adds to its branch. */
    private static List<Formula> parts(Formula formula) {
        return switch (formula) {
            case Not(Not(Formula p)) -> List.of(p);
            case And(Formula p, Formula q) -> List.of(p, q);
            case Not(Or(Formula p, Formula q)) -> List.of(new Not(p), new Not(q));
            case Not(Implies(Formula p, Formula q)) -> List.of(p, new Not(q));
            default -> throw new IllegalArgumentException(formula + " splits its branch");
        };
    }

    /** The two branches a splitting formula opens, or null if it does not split. */
    private static List<List<Formula>> split(Formula formula) {
        return switch (formula) {
            case Or(Formula p, Formula q) -> List.of(List.of(p), List.of(q));
            case Not(And(Formula p, Formula q)) -> List.of(List.of(new Not(p)), List.of(new Not(q)));
            case Implies(Formula p, Formula q) -> List.of(List.of(new Not(p)), List.of(q));
            case Iff(Formula p, Formula q) -> List.of(List.of(p, q), List.of(new Not(p), new Not(q)));
            case Not(Iff(Formula p, Formula q)) -> List.of(List.of(p, new Not(q)), List.of(new Not(p), q));
            default -> null;
        };
    }

    /** Atoms, quantified formulas and their negations are not broken down. */
    private static boolean isLiteral(Formula formula) {
        Formula inner = formula instanceof Not(Formula operand) ? operand : formula;
        return inner instanceof Formula.Atom || inner instanceof Formula.Predicate || inner instanceof Formula.Quantified;
    }

    private static boolean contradicts(List<Formula> branch) {
        for (Formula formula : branch) {
            if (formula instanceof Not(Formula operand) && branch.contains(operand)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The tree as text: one formula per line, branches drawn with
     * {@code ├─} and {@code └─}, and each leaf marked {@code ×} if it closes
     * or {@code ○} if it stays open.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        write(root, "", "", out);
        return out.toString();
    }

    private static void write(Node node, String first, String indent, StringBuilder out) {
        List<Formula> formulas = node.formulas();
        for (int i = 0; i < formulas.size(); i++) {
            out.append(i == 0 ? first : indent).append(formulas.get(i)).append('\n');
        }
        if (node.isLeaf()) {
            out.append(formulas.isEmpty() ? first : indent).append(node.closed() ? "×" : "○").append('\n');
            return;
        }
        if (node.children().size() == 1) {
            write(node.children().get(0), indent, indent, out);
            return;
        }
        for (int i = 0; i < node.children().size(); i++) {
            boolean last = i == node.children().size() - 1;
            write(node.children().get(i), indent + (last ? "└─ " : "├─ "), indent + (last ? "   " : "│  "), out);
        }
    }
}
