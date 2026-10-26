# logik-core [![CI](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml/badge.svg)](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml)

A propositional and predicate logic library: a formula parser and a natural-deduction proof checker that uses the rules from Copi's *Introduction to Logic*, in the style of LogicCoach.

## Status

The current code is the original from 2018, written during a first semester of Systems Engineering. It is kept under the [`legacy-2018`](https://github.com/ToxicSSJ/logik-core/tree/legacy-2018) tag and is being rewritten.

- 77 of the 96 proof tests pass.
- The other 19 are marked `@Disabled`, each with its reason: a known engine bug or a mistake in the test itself. The rewrite will turn them back on.

## Build and test

Requires Java 21 or later. The Maven Wrapper downloads the pinned Maven version:

```sh
./mvnw verify
```

GitHub Actions runs the tests on every push and pull request, on Java 21 and 25.

## License

[MIT](LICENSE).
