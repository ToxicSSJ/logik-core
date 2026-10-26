package io.github.toxicssj.logik.corpus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CorpusRunnerTest {

    @Test
    void agreesWithTheWholeCorpus() {
        CorpusRunner.Result result = CorpusRunner.run(Corpus.root());
        assertEquals(0, result.mismatches(), () -> mismatchedLines(result.output()));
        String output = result.output();
        assertTrue(output.contains("\nmodus-tollens strict: invalid at 3: MT with line 1 needs the negation of its consequent, ~~B, and gives ~A\n"));
        assertTrue(output.contains("\n\"(P & Q\": error at column 7: expected ')', found the end of the input\n"));
        assertTrue(output.contains("\nei-a-as-written: invalid, domain {0, 1}; a = 1; A = {(0)}\n"));
        assertTrue(output.endsWith("""
                == Summary ==
                proofs: 488 verdicts, 488 as expected
                parser: 83 cases, 83 as expected
                arguments: 244 cases, 244 as expected
                """));
    }

    @Test
    void printsTheSameOutputEveryTime() {
        assertEquals(CorpusRunner.run(Corpus.root()).output(), CorpusRunner.run(Corpus.root()).output());
    }

    private static String mismatchedLines(String output) {
        StringBuilder lines = new StringBuilder();
        for (String line : output.split("\n")) {
            if (line.contains("<-- expected")) {
                lines.append(line).append('\n');
            }
        }
        return lines.toString();
    }
}
