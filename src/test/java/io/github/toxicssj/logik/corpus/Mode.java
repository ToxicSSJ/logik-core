package io.github.toxicssj.logik.corpus;

/** How closely a line must match the result of its rule. See the corpus README. */
public enum Mode {
    /** Copi's rules exactly as printed. */
    STRICT,
    /** Also up to double negation, commutation and renaming of bound variables. */
    FLEXIBLE
}
