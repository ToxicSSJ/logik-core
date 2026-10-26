# Test corpus

This folder is the specification of logik-core. It says which proofs a proof checker must accept, which it must reject and at which line, and how the formula parser must read or reject each input. It is plain data, so the Java checker and the Rust port produced by Transmuto can run the same cases. `CheckerCorpusTest` runs every proof in both modes, and `SyntaxCorpusTest` runs every parser case.

| Path | Content |
|---|---|
| `proofs/2018-*.proofs` | The proofs from the original 2018 test suite, some of them corrected (see below). |
| `proofs/soundness.proofs` | Wrong proofs that a checker must reject. |
| `proofs/completeness.proofs` | Correct proofs that a checker must accept. |
| `proofs/added-rules.proofs` | DD, ABS and QN, the rules added in 2026. |
| `proofs/subproofs.proofs` | Conditional and indirect proof, and the scope of their subproofs. |
| `parser.cases` | Inputs the formula parser must accept, with the structure it must build, or reject, with the column of the error. |

All files are UTF-8. Lines that start with `#` are comments. Files are read in name order and cases in file order.

## Proof cases

Cases are separated by blank lines:

```
case: modus-tollens
source: 2018 test modusTollensTest
problem: {A → ~B//B=>~A}
3. ~A ; 1,2 MT
strict: invalid at 3: MT needs ~~B, the negation of the consequent ~B
flexible: valid
```

| Key | Meaning |
|---|---|
| `case` | Unique id, lowercase letters, digits and `-`. Required. |
| `source` | Where the case comes from. Required. |
| `problem` | `{premise//premise=>conclusion}`, the syntax of the 2018 `ProofParser`. Required. |
| `N. formula ; justification` | One proof line. `N` is its line number: premises are lines 1 to k, so the first step is k + 1. The justification is what the line cites, comma-separated, and the rule name, as in `2,3 CONJ`, `2-4 CP` or `ACP`. A case may have no steps, and a problem may have no premises, as in `{=>P | ~P}`, so that the first step is line 1. Spaces after `N.` are ignored, so the lines of a subproof can be indented. |
| `expect` | Verdict in both modes. |
| `strict`, `flexible` | Verdict in one mode. A case has either `expect` or both of these. |
| `legacy` | Why the 2018 engine, kept under the `legacy-2018` tag, got the flexible verdict wrong. History only: no test runs that engine any more. |

A verdict is one of:

- `valid`: every step follows from the lines it cites by its rule, and the last line is the conclusion.
- `invalid at N: reason`: line `N` is the first step that does not follow.
- `invalid at end: reason`: every step follows, but the last line is not the conclusion.

The reason is for people. A checker does not have to produce that text.

### Rules

Copi's rules, with his names: CONJ, SIMP, ADD, MP, MT, DS, HS, CD, DD, ABS (rules of inference), DN, DM, COM, ASSOC, DIST, TRANS, IMPL, EQUIV, EXP, TAUT, QN (rules of replacement), UI, EI, UG, EG (quantification) and ACP, AIP, CP, IP (subproofs). The 2018 engine had all of them but DD, ABS, QN and the subproof rules.

- A rule of inference applies to whole lines. A rule of replacement rewrites one subformula, anywhere in the line.
- The order of the cited lines does not matter: `2,1 MP` is as good as `1,2 MP`.
- A step must cite as many lines as its rule takes: two for CONJ, MP, MT, DS, HS, CD and DD, none for ACP and AIP, one range for CP and IP, and one line for the others. A line can only cite earlier lines.
- A rule that does not apply never accepts a line, even if that line repeats an earlier one.
- UI: `(∀x)Φx` gives `Φν` for any constant or variable `ν`, as long as no occurrence of `ν` ends up bound by a quantifier inside `Φ`.
- EI: `(∃x)Φx` gives `Φa` only for a constant `a` that does not occur in the premises, the conclusion or any earlier line. EI to a variable is not allowed. Without the conclusion part, `(∃x)Ax ⊢ Aa` would be provable.
- UG: `Φy` gives `(∀x)Φx` only if `y` is a variable, `y` is not free in any premise, any open assumption or any line obtained by EI, and every free `y` in `Φy` becomes `x`.
- QN: `~(∀x)Φ ≡ (∃x)~Φ`, `~(∃x)Φ ≡ (∀x)~Φ`, `(∀x)Φ ≡ ~(∃x)~Φ` and `(∃x)Φ ≡ ~(∀x)~Φ`, as a rule of replacement.
- EG: `Φν` gives `(∃x)Φx` when replacing `x` by `ν` in `Φx` gives back `Φν`. It may generalize some occurrences of `ν` and leave others: `Faa` gives `(∃x)Fxa`.

### Subproofs

An ACP or AIP line assumes its formula and opens a subproof. CP or IP closes the innermost open subproof on the line right after its last line, and cites it as a range from the assumption to that last line, as in `2-4 CP`.

- CP gives `p → q`, where `p` is the assumption and `q` the last line of the subproof.
- IP needs the last line of the subproof to be a contradiction, `q & ~q`, and gives `~p`, the negation of the assumption, or `q` when the assumption is `~q`.
- Once a subproof is closed, no later line can cite a line inside it.
- Every subproof must be closed before the proof ends.

### Modes

**Strict** mode is Copi's system as printed: a line must match the rule exactly. `~(~A & B)` gives `~~A | ~B` by DM, and getting `A | ~B` takes a further DN step. COM covers `&` and `|` only.

**Flexible** mode accepts everything strict mode accepts, and compares formulas up to:

1. double negation: `~~p` and `p` are the same formula;
2. commutation: `p & q`, `p | q` and `p ↔ q` are the same as `q & p`, `q | p` and `q ↔ p`;
3. renaming of bound variables: `(∀x)Fx` and `(∀y)Fy` are the same formula.

So in flexible mode SIMP may take the right conjunct, DS may drop either disjunct, and DM, IMPL, TRANS, MT and DS may skip the DN steps. The quantifier restrictions are the same in both modes.

## Corrections to the 2018 tests

Some 2018 tests had a typo, broke a rule, or proved an argument that is not valid. Those keep their id in their corrected form, and the proof as it was written stays as a separate case, with the suffix `-as-written`, that must be rejected. Each corrected case has a `# Fixed:` comment.

## Parser cases

One case per line, with fields separated by ` ; `:

```
accept "P & Q & R" ; ((P & Q) & R)
reject "(P & Q" ; 7
accept "¬P" ; ~P ; legacy: the 2018 parser drops ¬ and reads P
```

The input goes between double quotes. For `accept`, the second field is the formula fully parenthesized: every binary connective gets its own parentheses, with the symbols `~ & | → ↔`, and quantifiers are written `(∀x)` and `(∃x)`. For `reject`, it is the column where the error is reported, counted in Unicode code points from 1. The optional third field is a `legacy:` note, as in proof cases.

The parser reads one token at a time, from left to right, and reports the column where the first token that cannot continue the formula starts. A character that does not begin any token, like the `-` in `P - Q`, is reported only when the parser reaches it. So `a = b` fails at column 1, because a formula cannot start with the term `a`, not at the `=` in column 3. An error at the end of the input is reported at the column after the last character.

### Syntax

| Connective | Accepted symbols | Precedence | Grouping |
|---|---|---|---|
| negation | `~` `¬` | 1 (binds tightest) | |
| conjunction | `&` `∧` `·` `•` | 2 | left: `P & Q & R` is `(P & Q) & R` |
| disjunction | `\|` `∨` | 3 | left |
| conditional | `→` `->` `⊃` | 4 | right: `P → Q → R` is `P → (Q → R)` |
| biconditional | `↔` `<->` `≡` | 5 | right |

- Parentheses `( )` and brackets `[ ]` group.
- A sentence letter or predicate is one uppercase letter, followed by its terms: `P`, `Fa`, `Lxy`.
- Terms are one lowercase letter. `x`, `y` and `z` are variables, and `a` to `w` are constants. So `v` is a constant, not "or".
- Quantifiers are `(∀x)`, `(∃x)` or Copi's `(x)` for the universal one. `∀x` and `∃x` without parentheses are also accepted. A quantifier binds only the formula that follows it, like `~`: `(∀x)Fx → Gx` is `((∀x)Fx → Gx)`.
