package io.github.toxicssj.logik.corpus;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/** The corpus in {@code src/test/resources/corpus}, as the tests see it on the classpath. */
public final class Corpus {

    private Corpus() {}

    public static List<ProofCase> proofCases() {
        return CorpusReader.readProofs(root());
    }

    public static List<ParserCase> parserCases() {
        return CorpusReader.readParserCases(root());
    }

    /** The corpus folder. */
    public static Path root() {
        URL url = Corpus.class.getResource("/corpus");
        if (url == null) {
            throw new IllegalStateException("the corpus folder is not on the test classpath");
        }
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
