package io.github.toxicssj.logik.proof;

/** How closely a line must match what its rule gives. */
public enum Mode {

    /** Copi's rules exactly as printed. */
    STRICT,

    /**
     * Everything strict mode accepts, comparing formulas up to double negation
     * ({@code ~~p} is {@code p}), commutation of {@code &}, {@code |} and
     * {@code ↔}, and renaming of bound variables. The quantifier restrictions
     * are the same as in strict mode.
     */
    FLEXIBLE
}
