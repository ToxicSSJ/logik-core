package io.github.toxicssj.logik.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.toxicssj.logik.corpus.Corpus;
import io.github.toxicssj.logik.corpus.ProofCase;
import io.github.toxicssj.logik.syntax.Formula;
import io.github.toxicssj.logik.syntax.Problem;

/**
 * Checks the corpus against semantics. A case marked valid must have a valid
 * argument, and a case has a countermodel exactly when it says
 * {@code argument: invalid}. Without quantifiers, truth tables decide this
 * exactly, and the semantic tree must agree with them; with quantifiers, the
 * search covers domains of up to three individuals, as far as its budget goes.
 */
class SemanticCorpusTest {

    static List<ProofCase> proofCases() {
        return Corpus.proofCases();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("proofCases")
    void argument(ProofCase proofCase) {
        Problem problem = Problem.parse(proofCase.problem()).orThrow();
        List<Formula> formulas = new ArrayList<>(problem.premises());
        formulas.add(problem.conclusion());
        boolean quantifierFree = true;
        for (Formula formula : formulas) {
            quantifierFree &= Semantics.isQuantifierFree(formula);
        }

        String countermodel;
        if (quantifierFree) {
            Valuation valuation = Semantics.counterexample(problem.premises(), problem.conclusion());
            countermodel = valuation == null ? null : valuation.toString();
            assertEquals(valuation == null, Tableau.proves(problem.premises(), problem.conclusion()),
                    proofCase.location() + ": the semantic tree disagrees with the truth table");
        } else {
            ModelSearch.Result result = ModelSearch.countermodel(problem.premises(), problem.conclusion(), 3);
            countermodel = result instanceof ModelSearch.Result.Found(Model model) ? model.toString() : null;
        }

        boolean validProof = proofCase.strict().isValid() || proofCase.flexible().isValid();
        if (validProof && countermodel != null) {
            fail(proofCase.location() + ": a valid proof of an argument with a countermodel: " + countermodel);
        }
        if (proofCase.argumentInvalid() && countermodel == null) {
            fail(proofCase.location() + ": says argument: invalid, but no countermodel was found");
        }
        if (!proofCase.argumentInvalid() && countermodel != null) {
            fail(proofCase.location() + ": the argument has a countermodel, so the case needs argument: invalid: "
                    + countermodel);
        }
    }
}
