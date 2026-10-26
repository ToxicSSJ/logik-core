package io.github.toxicssj.logik.corpus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Reads the test corpus in {@code src/test/resources/corpus}. The format is
 * described in the README there. Any malformed case is an error that names its
 * file and line.
 */
public final class Corpus {

    private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");
    private static final Pattern KEY = Pattern.compile("([a-z]+):\\s*(.*)");
    private static final Pattern STEP = Pattern.compile("(\\d+)\\.\\s+([^;]+?)\\s*;\\s*(\\S.*)");
    private static final Pattern PARSER_CASE =
            Pattern.compile("(accept|reject) \"([^\"]*)\" ; (.+?)(?: ; legacy: (.+))?");
    private static final Set<String> KEYS =
            Set.of("case", "source", "problem", "expect", "strict", "flexible", "argument", "legacy");

    private Corpus() {}

    /** Every proof case, files in name order and cases in file order. */
    public static List<ProofCase> proofCases() {
        List<ProofCase> cases = new ArrayList<>();
        for (Path file : files(root().resolve("proofs"), ".proofs")) {
            cases.addAll(parseProofs(file.getFileName().toString(), readLines(file)));
        }
        Set<String> ids = new HashSet<>();
        for (ProofCase c : cases) {
            if (!ids.add(c.id())) {
                throw new IllegalStateException(c.location() + ": duplicate case id " + c.id());
            }
        }
        return List.copyOf(cases);
    }

    /** Every parser case, in file order. */
    public static List<ParserCase> parserCases() {
        Path file = root().resolve("parser.cases");
        return parseParserCases(file.getFileName().toString(), readLines(file));
    }

    static List<ProofCase> parseProofs(String fileName, List<String> lines) {
        List<ProofCase> cases = new ArrayList<>();
        Map<String, String> fields = new LinkedHashMap<>();
        List<ProofCase.Step> steps = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= lines.size(); i++) {
            String line = i < lines.size() ? lines.get(i).strip() : "";
            String where = fileName + ":" + (i + 1);
            if (line.startsWith("#")) {
                continue;
            }
            if (line.isEmpty()) {
                if (!fields.isEmpty() || !steps.isEmpty()) {
                    cases.add(toProofCase(fields, steps, fileName + ":" + start));
                    fields.clear();
                    steps.clear();
                }
                continue;
            }
            if (fields.isEmpty() && steps.isEmpty()) {
                start = i + 1;
            }
            Matcher step = STEP.matcher(line);
            Matcher key = KEY.matcher(line);
            if (step.matches()) {
                steps.add(new ProofCase.Step(Integer.parseInt(step.group(1)), step.group(2), step.group(3)));
            } else if (key.matches() && KEYS.contains(key.group(1))) {
                if (fields.putIfAbsent(key.group(1), key.group(2).strip()) != null) {
                    throw new IllegalStateException(where + ": repeated key " + key.group(1));
                }
            } else {
                throw new IllegalStateException(where + ": not a key or a proof line: " + line);
            }
        }
        return cases;
    }

    private static ProofCase toProofCase(Map<String, String> fields, List<ProofCase.Step> steps, String where) {
        String id = required(fields, "case", where);
        if (!ID.matcher(id).matches()) {
            throw new IllegalStateException(where + ": case id must be lowercase words joined by -: " + id);
        }
        String source = required(fields, "source", where);
        String problem = required(fields, "problem", where);
        if (!problem.startsWith("{") || !problem.endsWith("}") || problem.split("=>", -1).length != 2) {
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
        if (fields.containsKey("expect")) {
            if (fields.containsKey("strict") || fields.containsKey("flexible")) {
                throw new IllegalStateException(where + ": case " + id + " has expect and also strict or flexible");
            }
            strict = verdict(fields.get("expect"), premises, steps.size(), where);
            flexible = strict;
        } else {
            strict = verdict(required(fields, "strict", where), premises, steps.size(), where);
            flexible = verdict(required(fields, "flexible", where), premises, steps.size(), where);
        }
        String argument = fields.get("argument");
        if (argument != null && !argument.equals("invalid")) {
            throw new IllegalStateException(where + ": argument: can only be invalid, not " + argument);
        }
        if (argument != null && (strict.isValid() || flexible.isValid())) {
            throw new IllegalStateException(where + ": case " + id + " is a valid proof of an invalid argument");
        }
        return new ProofCase(id, source, problem, steps, strict, flexible, argument != null, fields.get("legacy"), where);
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

    private static String required(Map<String, String> fields, String key, String where) {
        String value = fields.get(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(where + ": missing " + key + ":");
        }
        return value;
    }

    static List<ParserCase> parseParserCases(String fileName, List<String> lines) {
        List<ParserCase> cases = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            String where = fileName + ":" + (i + 1);
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Matcher m = PARSER_CASE.matcher(line);
            if (!m.matches()) {
                throw new IllegalStateException(where + ": not a parser case: " + line);
            }
            boolean accept = m.group(1).equals("accept");
            String expected = m.group(3).strip();
            if (!accept && !expected.matches("[1-9]\\d*")) {
                throw new IllegalStateException(where + ": a rejected input needs an error column: " + expected);
            }
            cases.add(new ParserCase(accept, m.group(2), expected, m.group(4), where));
        }
        return List.copyOf(cases);
    }

    private static Path root() {
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

    private static List<Path> files(Path dir, String extension) {
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.filter(p -> p.getFileName().toString().endsWith(extension))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
