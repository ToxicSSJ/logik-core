package io.github.toxicssj.logik.corpus;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.proof.Checker;
import io.github.toxicssj.logik.proof.Mode;
import io.github.toxicssj.logik.proof.Proof;
import io.github.toxicssj.logik.proof.Report;
import io.github.toxicssj.logik.proof.Step;
import io.github.toxicssj.logik.semantics.Model;
import io.github.toxicssj.logik.semantics.ModelSearch;
import io.github.toxicssj.logik.semantics.Semantics;
import io.github.toxicssj.logik.semantics.Tableau;
import io.github.toxicssj.logik.semantics.TruthTable;
import io.github.toxicssj.logik.semantics.Valuation;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.FormulaParser;
import io.github.toxicssj.logik.syntax.Justification;
import io.github.toxicssj.logik.syntax.ParseResult;
import io.github.toxicssj.logik.syntax.Problem;

/**
 * Runs the whole corpus through the library and prints what it finds: the
 * checker's verdict on every proof in both modes, the parser's result on every
 * parser case, whether every argument is valid, and some truth tables and a
 * semantic tree.
 *
 * <p>The output is deterministic, so a port of the library to another
 * language can be checked by comparing its output with this one byte for byte.
 * Lines that disagree with the corpus end in {@code <-- expected ...}.
 *
 * <pre>java -jar logik-core.jar src/test/resources/corpus</pre>
 */
public final class CorpusRunner {

    /** The largest domain the countermodel search tries. */
    static final int MAX_DOMAIN = 3;

    private CorpusRunner() {}

    public static void main(String[] args) {
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        if (args.length != 1) {
            out.println("usage: java -jar logik-core.jar <corpus folder>");
            System.exit(2);
        }
        Result result = run(Path.of(args[0]));
        out.print(result.output());
        out.flush();
        System.exit(result.mismatches() == 0 ? 0 : 1);
    }

    /** The output, and how many results disagree with the corpus. */
    public record Result(String output, int mismatches) {}

    public static Result run(Path corpus) {
        StringBuilder out = new StringBuilder();
        List<ProofCase> proofs = CorpusReader.readProofs(corpus);
        List<ParserCase> parserCases = CorpusReader.readParserCases(corpus);

        out.append("== Proofs ==\n");
        int proofMismatches = 0;
        for (ProofCase proofCase : proofs) {
            for (Mode mode : Mode.values()) {
                Report report = Checker.check(proof(proofCase), mode);
                String got = verdict(report);
                String expected = proofCase.verdict(mode).toString();
                out.append(proofCase.id()).append(' ').append(mode.name().toLowerCase()).append(": ").append(got);
                if (!agrees(report, proofCase.verdict(mode))) {
                    out.append("  <-- expected ").append(expected);
                    proofMismatches++;
                }
                out.append('\n');
            }
        }

        out.append("\n== Parser ==\n");
        int parserMismatches = 0;
        for (ParserCase parserCase : parserCases) {
            out.append('"').append(parserCase.input()).append("\": ");
            boolean agrees;
            switch (FormulaParser.parse(parserCase.input())) {
                case ParseResult.Success<Formula> success -> {
                    String parsed = fullyParenthesized(success.value());
                    out.append(parsed);
                    agrees = parserCase.accept() && parsed.equals(parserCase.expected());
                }
                case ParseResult.Failure<Formula> failure -> {
                    out.append("error at ").append(failure);
                    agrees = !parserCase.accept() && failure.column() == parserCase.column();
                }
            }
            if (!agrees) {
                out.append("  <-- expected ").append(parserCase.accept()
                        ? parserCase.expected()
                        : "an error at column " + parserCase.expected());
                parserMismatches++;
            }
            out.append('\n');
        }

        out.append("\n== Arguments ==\n");
        int argumentMismatches = 0;
        for (ProofCase proofCase : proofs) {
            Problem problem = Problem.parse(proofCase.problem()).orThrow();
            String countermodel = countermodel(problem);
            out.append(proofCase.id()).append(": ");
            if (countermodel == null) {
                out.append("valid").append(quantifierFree(problem) ? "" : ", no countermodel with up to " + MAX_DOMAIN + " individuals");
            } else {
                out.append("invalid, ").append(countermodel);
            }
            if ((countermodel != null) != proofCase.argumentInvalid()) {
                out.append("  <-- expected ").append(proofCase.argumentInvalid() ? "invalid" : "valid");
                argumentMismatches++;
            }
            out.append('\n');
        }

        out.append("\n== Truth tables ==\n");
        appendTable(out, "P → Q", "~Q → ~P", "Q → P");
        appendTable(out, "~(P & Q)", "~P | ~Q", "~P & ~Q");
        appendTable(out, "(P → Q) & (Q → R)", "P → R");
        appendTable(out, "P ↔ Q", "(P → Q) & (Q → P)", "(P & Q) | (~P & ~Q)");

        out.append("\n== Semantic tree ==\n");
        out.append("(P → Q) & (Q → R), ~(P → R)\n");
        out.append(Tableau.of(List.of(Formula.parse("(P → Q) & (Q → R)"), Formula.parse("~(P → R)"))));

        int mismatches = proofMismatches + parserMismatches + argumentMismatches;
        out.append("\n== Summary ==\n");
        out.append("proofs: ").append(2 * proofs.size()).append(" verdicts, ")
                .append(2 * proofs.size() - proofMismatches).append(" as expected\n");
        out.append("parser: ").append(parserCases.size()).append(" cases, ")
                .append(parserCases.size() - parserMismatches).append(" as expected\n");
        out.append("arguments: ").append(proofs.size()).append(" cases, ")
                .append(proofs.size() - argumentMismatches).append(" as expected\n");
        return new Result(out.toString(), mismatches);
    }

    private static Proof proof(ProofCase proofCase) {
        List<Step> steps = new ArrayList<>();
        for (ProofCase.Step step : proofCase.steps()) {
            steps.add(new Step(Formula.parse(step.formula()), Justification.parse(step.justification()).orThrow()));
        }
        return new Proof(Problem.parse(proofCase.problem()).orThrow(), steps);
    }

    /** The report written as a corpus verdict: the first wrong step, else the end error, else valid. */
    private static String verdict(Report report) {
        if (report.firstError().isPresent()) {
            Report.LineCheck step = report.firstError().get();
            return "invalid at " + step.line() + ": " + step.error().get();
        }
        if (report.endError().isPresent()) {
            return "invalid at end: " + report.endError().get();
        }
        return "valid";
    }

    private static boolean agrees(Report report, Verdict expected) {
        return switch (expected) {
            case Verdict.Valid valid -> report.isValid();
            case Verdict.InvalidAtLine at -> report.firstError().isPresent()
                    && report.firstError().get().line() == at.line();
            case Verdict.InvalidAtEnd end -> report.firstError().isEmpty() && report.endError().isPresent();
        };
    }

    private static boolean quantifierFree(Problem problem) {
        for (Formula premise : problem.premises()) {
            if (!Semantics.isQuantifierFree(premise)) {
                return false;
            }
        }
        return Semantics.isQuantifierFree(problem.conclusion());
    }

    /** A countermodel of the argument as text, or null if there is none (up to {@link #MAX_DOMAIN} with quantifiers). */
    private static String countermodel(Problem problem) {
        if (quantifierFree(problem)) {
            Valuation valuation = Semantics.counterexample(problem.premises(), problem.conclusion());
            return valuation == null ? null : valuation.toString();
        }
        ModelSearch.Result result = ModelSearch.countermodel(problem.premises(), problem.conclusion(), MAX_DOMAIN);
        return result instanceof ModelSearch.Result.Found(Model model) ? model.toString() : null;
    }

    private static void appendTable(StringBuilder out, String... formulas) {
        List<Formula> parsed = new ArrayList<>();
        for (String formula : formulas) {
            parsed.add(Formula.parse(formula));
        }
        out.append(TruthTable.of(parsed)).append('\n');
    }

    /** The corpus form of a formula: like toString, with the outermost parentheses too. */
    private static String fullyParenthesized(Formula formula) {
        return formula instanceof Formula.Binary ? "(" + formula + ")" : formula.toString();
    }
}
