package io.github.toxicssj.logik.corpus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the test corpus: {@code proofs/*.proofs} and {@code parser.cases}
 * under a corpus folder. The format is described in the corpus README. A
 * malformed case is an error that names its file and line.
 *
 * <p>The reader uses plain string handling, no regular expressions, so a port
 * to another language needs nothing beyond its standard library.
 */
public final class CorpusReader {

    private static final List<String> KEYS =
            List.of("case", "source", "problem", "expect", "strict", "flexible", "argument", "legacy");

    private CorpusReader() {}

    /** Every proof case under {@code corpus}, files in name order and cases in file order. */
    public static List<ProofCase> readProofs(Path corpus) {
        List<ProofCase> cases = new ArrayList<>();
        for (Path file : proofFiles(corpus.resolve("proofs"))) {
            cases.addAll(parseProofs(file.getFileName().toString(), readLines(file)));
        }
        List<String> ids = new ArrayList<>();
        for (ProofCase c : cases) {
            if (ids.contains(c.id())) {
                throw new IllegalStateException(c.location() + ": duplicate case id " + c.id());
            }
            ids.add(c.id());
        }
        return List.copyOf(cases);
    }

    /** Every parser case under {@code corpus}, in file order. */
    public static List<ParserCase> readParserCases(Path corpus) {
        Path file = corpus.resolve("parser.cases");
        return parseParserCases(file.getFileName().toString(), readLines(file));
    }

    public static List<ProofCase> parseProofs(String fileName, List<String> lines) {
        List<ProofCase> cases = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<ProofCase.Step> steps = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= lines.size(); i++) {
            String line = i < lines.size() ? lines.get(i).strip() : "";
            String where = fileName + ":" + (i + 1);
            if (line.startsWith("#")) {
                continue;
            }
            if (line.isEmpty()) {
                if (!keys.isEmpty() || !steps.isEmpty()) {
                    cases.add(toProofCase(keys, values, steps, fileName + ":" + start));
                    keys.clear();
                    values.clear();
                    steps.clear();
                }
                continue;
            }
            if (keys.isEmpty() && steps.isEmpty()) {
                start = i + 1;
            }
            ProofCase.Step step = step(line);
            if (step != null) {
                steps.add(step);
                continue;
            }
            int colon = line.indexOf(':');
            String key = colon < 0 ? "" : line.substring(0, colon);
            if (!KEYS.contains(key)) {
                throw new IllegalStateException(where + ": not a key or a proof line: " + line);
            }
            if (keys.contains(key)) {
                throw new IllegalStateException(where + ": repeated key " + key);
            }
            keys.add(key);
            values.add(line.substring(colon + 1).strip());
        }
        return cases;
    }

    /** A line like {@code 5. P & Q ; 2,3 CONJ}, or null if the line is not a step. */
    private static ProofCase.Step step(String line) {
        int dot = 0;
        while (dot < line.length() && isDigit(line.charAt(dot))) {
            dot++;
        }
        if (dot == 0 || dot > 9 || dot + 1 >= line.length() || line.charAt(dot) != '.'
                || !Character.isWhitespace(line.charAt(dot + 1))) {
            return null;
        }
        String rest = line.substring(dot + 1);
        int separator = rest.indexOf(';');
        if (separator < 0) {
            return null;
        }
        String formula = rest.substring(0, separator).strip();
        String justification = rest.substring(separator + 1).strip();
        if (formula.isEmpty() || justification.isEmpty()) {
            return null;
        }
        return new ProofCase.Step(Integer.parseInt(line.substring(0, dot)), formula, justification);
    }

    private static ProofCase toProofCase(List<String> keys, List<String> values, List<ProofCase.Step> steps, String where) {
        String id = required(keys, values, "case", where);
        if (!isId(id)) {
            throw new IllegalStateException(where + ": case id must be lowercase words joined by -: " + id);
        }
        String source = required(keys, values, "source", where);
        String problem = required(keys, values, "problem", where);
        int arrow = problem.indexOf("=>");
        if (!problem.startsWith("{") || !problem.endsWith("}") || arrow < 0 || problem.indexOf("=>", arrow + 2) >= 0) {
            throw new IllegalStateException(where + ": problem must look like {P//Q=>C}: " + problem);
        }
        int premises = ProofCase.premisesOf(problem).size();
        for (int k = 0; k < steps.size(); k++) {
            int expected = premises + k + 1;
            if (steps.get(k).number() != expected) {
                throw new IllegalStateException(where + ": case " + id + " numbers a step "
                        + steps.get(k).number() + ", but it is line " + expected);
            }
        }
        Verdict strict;
        Verdict flexible;
        if (keys.contains("expect")) {
            if (keys.contains("strict") || keys.contains("flexible")) {
                throw new IllegalStateException(where + ": case " + id + " has expect and also strict or flexible");
            }
            strict = verdict(value(keys, values, "expect"), premises, steps.size(), where);
            flexible = strict;
        } else {
            strict = verdict(required(keys, values, "strict", where), premises, steps.size(), where);
            flexible = verdict(required(keys, values, "flexible", where), premises, steps.size(), where);
        }
        String argument = value(keys, values, "argument");
        if (argument != null && !argument.equals("invalid")) {
            throw new IllegalStateException(where + ": argument: can only be invalid, not " + argument);
        }
        if (argument != null && (strict.isValid() || flexible.isValid())) {
            throw new IllegalStateException(where + ": case " + id + " is a valid proof of an invalid argument");
        }
        return new ProofCase(id, source, problem, steps, strict, flexible, argument != null,
                value(keys, values, "legacy"), where);
    }

    private static Verdict verdict(String text, int premises, int steps, String where) {
        Verdict verdict = Verdict.parse(text);
        if (verdict == null) {
            throw new IllegalStateException(where + ": not a verdict: " + text);
        }
        if (verdict instanceof Verdict.InvalidAtLine at && (at.line() <= premises || at.line() > premises + steps)) {
            throw new IllegalStateException(where + ": line " + at.line() + " is not a step of this proof");
        }
        return verdict;
    }

    private static String value(List<String> keys, List<String> values, String key) {
        int i = keys.indexOf(key);
        return i < 0 ? null : values.get(i);
    }

    private static String required(List<String> keys, List<String> values, String key, String where) {
        String value = value(keys, values, key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(where + ": missing " + key + ":");
        }
        return value;
    }

    /** Lowercase letters and digits, in words joined by single hyphens. */
    private static boolean isId(String id) {
        if (id.isEmpty() || id.startsWith("-") || id.endsWith("-") || id.contains("--")) {
            return false;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

    /**
     * Reads lines like {@code accept "P & Q" ; (P & Q)} or
     * {@code reject "(P & Q" ; 7 ; legacy: note}.
     */
    public static List<ParserCase> parseParserCases(String fileName, List<String> lines) {
        List<ParserCase> cases = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            String where = fileName + ":" + (i + 1);
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            boolean accept = line.startsWith("accept \"");
            if (!accept && !line.startsWith("reject \"")) {
                throw new IllegalStateException(where + ": not a parser case: " + line);
            }
            int open = line.indexOf('"');
            int close = line.indexOf('"', open + 1);
            if (close < 0 || !line.startsWith(" ; ", close + 1)) {
                throw new IllegalStateException(where + ": not a parser case: " + line);
            }
            String input = line.substring(open + 1, close);
            String rest = line.substring(close + 4);
            String legacy = null;
            int note = rest.indexOf(" ; legacy: ");
            if (note >= 0) {
                legacy = rest.substring(note + " ; legacy: ".length()).strip();
                rest = rest.substring(0, note);
            }
            String expected = rest.strip();
            if (expected.isEmpty()) {
                throw new IllegalStateException(where + ": not a parser case: " + line);
            }
            if (!accept && !isColumn(expected)) {
                throw new IllegalStateException(where + ": a rejected input needs an error column: " + expected);
            }
            cases.add(new ParserCase(accept, input, expected, legacy, where));
        }
        return List.copyOf(cases);
    }

    private static boolean isColumn(String text) {
        if (text.isEmpty() || text.length() > 9 || text.charAt(0) == '0') {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /** The .proofs files in {@code dir}, sorted by name as plain strings, so the order is the same everywhere. */
    private static List<Path> proofFiles(Path dir) {
        List<String> names = new ArrayList<>();
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(dir)) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (name.endsWith(".proofs")) {
                    names.add(name);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        names.sort(null);
        List<Path> files = new ArrayList<>();
        for (String name : names) {
            files.add(dir.resolve(name));
        }
        return files;
    }

    private static List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
