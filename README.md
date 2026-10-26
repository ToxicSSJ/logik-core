# logik-core [![CI](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml/badge.svg)](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml)

A propositional and predicate logic library: a formula parser and a natural-deduction proof checker that uses the rules from Copi's *Introduction to Logic*, in the style of LogicCoach.

## Status

The current code is the original from 2018, written during a first semester of Systems Engineering. It is kept under the [`legacy-2018`](https://github.com/ToxicSSJ/logik-core/tree/legacy-2018) tag and is being rewritten.

The tests are a corpus of plain data in [`src/test/resources/corpus`](src/test/resources/corpus): 201 proofs and 83 formulas, each with what a checker or parser must do with it. It holds the 96 proofs of the 2018 tests, some of them corrected, plus wrong proofs that must be rejected and correct ones that must be accepted. Its README describes the format and the rules.

The 2018 engine gets 117 of those 284 cases wrong. Each one carries a note with the reason and is reported as skipped.

## Build and test

Requires Java 21 or later. The Maven Wrapper downloads the pinned Maven version:

```sh
./mvnw verify
```

GitHub Actions runs the tests on every push and pull request, on Java 21 and 25.

## License

[MIT](LICENSE).
