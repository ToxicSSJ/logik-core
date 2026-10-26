# logik-core [![CI](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml/badge.svg)](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml)

A propositional and predicate logic library: a formula parser and a natural-deduction proof checker that uses the rules from Copi's *Introduction to Logic*, in the style of LogicCoach.

## Checking a proof

A proof is the problem, `{premise//premise=>conclusion}`, and the steps after the premises, each written `formula ; justification`. Premises are lines 1 to k, and the steps go on from k + 1.

```java
Proof proof = Proof.of("{(∀x)(Hx → Mx)//Hs=>Ms}",
        "Hs → Ms ; 1 UI",
        "Ms ; 3,2 MP");

Report report = Checker.check(proof, Mode.STRICT);
report.isValid();   // true
```

The checker judges every step against the lines it cites, as they are written, and says why a step does not follow. It never throws on a wrong proof.

```java
Report report = Checker.check(Proof.of("{~(~A & B)=>A | ~B}", "A | ~B ; 1 DM"), Mode.STRICT);
report.firstError();   // line 2: DM turns line 1 into ~~A | ~B, not into this line
report.endError();     // empty: the last line is the conclusion
```

A proof is correct when every step follows and its last line is the conclusion.

### Modes

- `Mode.STRICT` follows Copi's rules exactly as printed. `~(~A & B)` gives `~~A | ~B` by DM, and dropping the `~~` takes a DN step.
- `Mode.FLEXIBLE` also compares formulas up to double negation, commutation of `&`, `|` and `↔`, and renaming of bound variables. So SIMP may take the right conjunct, DS may drop either disjunct, and DM, IMPL, TRANS, MT and DS may skip the DN steps.

The restrictions on the quantifier rules are the same in both modes.

### Rules

| Kind | Rules |
|---|---|
| Inference | CONJ, SIMP, ADD, MP, MT, DS, HS, CD |
| Replacement | DN, DM, COM, ASSOC, DIST, TRANS, IMPL, EQUIV, EXP, TAUT |
| Quantification | UI, EI, UG, EG |

Rules of inference apply to whole lines, and the cited lines may come in any order. Rules of replacement rewrite one subformula, anywhere in the line. EI needs a constant that occurs nowhere before, conclusion included. UG needs a variable that is not free in any premise or any line obtained by EI.

## Formulas

The `syntax` package has immutable formulas and their parser.

```java
Formula f = Formula.parse("(x)(Hx ⊃ Mx)");     // Copi's notation, or ∀ → & | ~ and ASCII
f.toString();                                  // "(∀x)(Hx → Mx)"
f.alphaEquals(Formula.parse("(∀y)(Hy → My)")); // true
FormulaParser.parse("(P & Q");                 // Failure: column 7, expected ')'
```

The parser takes `~ ¬`, `& ∧ · •`, `| ∨`, `→ -> ⊃` and `↔ <-> ≡`, with that precedence. `&` and `|` group to the left, `→` and `↔` to the right. Sentence and predicate letters are uppercase. `x`, `y` and `z` are variables, and `a` to `w` are constants.

## The test corpus

The specification is plain data in [`src/test/resources/corpus`](src/test/resources/corpus): 202 proofs and 83 formulas, each with what the checker or the parser must do with it, in each mode. It holds the 96 proofs of the 2018 tests, some of them corrected, plus wrong proofs that must be rejected and correct ones that must be accepted. Its README describes the format and the rules in detail.

## History

The first version was written in 2018, during a first semester of Systems Engineering, and is kept under the [`legacy-2018`](https://github.com/ToxicSSJ/logik-core/tree/legacy-2018) tag. It accepted some wrong proofs and rejected some right ones, so it was rewritten in 2026 with its tests as the specification.

## Build and test

Requires Java 21 or later. The Maven Wrapper downloads the pinned Maven version:

```sh
./mvnw verify
```

GitHub Actions runs the tests on every push and pull request, on Java 21 and 25.

## License

[MIT](LICENSE).
