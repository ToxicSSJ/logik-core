package io.github.toxicssj.logik.proof;

/** The rules a proof line can cite, with Copi's abbreviations. */
public enum Rule {

    // Rules of inference: they apply to whole lines.
    CONJ("Conjunction", Kind.INFERENCE, 2),
    SIMP("Simplification", Kind.INFERENCE, 1),
    ADD("Addition", Kind.INFERENCE, 1),
    MP("Modus Ponens", Kind.INFERENCE, 2),
    MT("Modus Tollens", Kind.INFERENCE, 2),
    DS("Disjunctive Syllogism", Kind.INFERENCE, 2),
    HS("Hypothetical Syllogism", Kind.INFERENCE, 2),
    CD("Constructive Dilemma", Kind.INFERENCE, 2),

    // Rules of replacement: they rewrite one subformula.
    DN("Double Negation", Kind.REPLACEMENT, 1),
    DM("De Morgan's Theorems", Kind.REPLACEMENT, 1),
    COM("Commutation", Kind.REPLACEMENT, 1),
    ASSOC("Association", Kind.REPLACEMENT, 1),
    DIST("Distribution", Kind.REPLACEMENT, 1),
    TRANS("Transposition", Kind.REPLACEMENT, 1),
    IMPL("Material Implication", Kind.REPLACEMENT, 1),
    EQUIV("Material Equivalence", Kind.REPLACEMENT, 1),
    EXP("Exportation", Kind.REPLACEMENT, 1),
    TAUT("Tautology", Kind.REPLACEMENT, 1),

    // Quantification rules.
    UI("Universal Instantiation", Kind.QUANTIFIER, 1),
    EI("Existential Instantiation", Kind.QUANTIFIER, 1),
    UG("Universal Generalization", Kind.QUANTIFIER, 1),
    EG("Existential Generalization", Kind.QUANTIFIER, 1);

    /** What a rule does. */
    public enum Kind { INFERENCE, REPLACEMENT, QUANTIFIER }

    private final String fullName;
    private final Kind kind;
    private final int citations;

    Rule(String fullName, Kind kind, int citations) {
        this.fullName = fullName;
        this.kind = kind;
        this.citations = citations;
    }

    public String fullName() {
        return fullName;
    }

    public Kind kind() {
        return kind;
    }

    /** How many lines a step with this rule cites. */
    public int citations() {
        return citations;
    }

    /** The rule with this abbreviation, ignoring case, or null if there is none. */
    public static Rule byName(String name) {
        for (Rule rule : values()) {
            if (rule.name().equalsIgnoreCase(name)) {
                return rule;
            }
        }
        return null;
    }
}
