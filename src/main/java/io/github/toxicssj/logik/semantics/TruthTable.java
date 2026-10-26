package io.github.toxicssj.logik.semantics;

import java.util.ArrayList;
import java.util.List;

import io.github.toxicssj.logik.syntax.Formula;

/**
 * The truth table of one or more formulas without quantifiers: one row per
 * valuation of their atoms, from all true to all false.
 */
public record TruthTable(List<String> atoms, List<Formula> formulas, List<TruthTable.Row> rows) {

    /** One valuation and the value of each formula under it. */
    public record Row(Valuation valuation, List<Boolean> values) {
        public Row {
            values = List.copyOf(values);
        }
    }

    public TruthTable {
        atoms = List.copyOf(atoms);
        formulas = List.copyOf(formulas);
        rows = List.copyOf(rows);
    }

    public static TruthTable of(Formula... formulas) {
        return of(List.of(formulas));
    }

    public static TruthTable of(List<Formula> formulas) {
        List<String> atoms = Valuation.atomsOf(formulas);
        List<Row> rows = new ArrayList<>();
        for (Valuation valuation : Valuation.all(atoms)) {
            List<Boolean> values = new ArrayList<>();
            for (Formula formula : formulas) {
                values.add(valuation.satisfies(formula));
            }
            rows.add(new Row(valuation, values));
        }
        return new TruthTable(atoms, formulas, rows);
    }

    /** The table as text, with T and F, one column per atom and per formula. */
    @Override
    public String toString() {
        List<String> headers = new ArrayList<>(atoms);
        for (Formula formula : formulas) {
            headers.add(formula.toString());
        }
        List<List<String>> cells = new ArrayList<>();
        for (Row row : rows) {
            List<String> line = new ArrayList<>();
            for (boolean value : row.valuation().values()) {
                line.add(value ? "T" : "F");
            }
            for (boolean value : row.values()) {
                line.add(value ? "T" : "F");
            }
            cells.add(line);
        }
        StringBuilder out = new StringBuilder();
        appendLine(out, headers, headers);
        for (List<String> line : cells) {
            appendLine(out, line, headers);
        }
        return out.toString();
    }

    private void appendLine(StringBuilder out, List<String> cells, List<String> headers) {
        for (int i = 0; i < cells.size(); i++) {
            if (i >= atoms.size()) {
                out.append("| ");
            }
            String cell = cells.get(i);
            int width = headers.get(i).codePointCount(0, headers.get(i).length());
            int left = (width - cell.codePointCount(0, cell.length())) / 2;
            out.append(" ".repeat(left)).append(cell)
                    .append(" ".repeat(width - left - cell.codePointCount(0, cell.length())));
            if (i < cells.size() - 1) {
                out.append(' ');
            }
        }
        // No trailing spaces, so the output compares byte for byte.
        int end = out.length();
        while (end > 0 && out.charAt(end - 1) == ' ') {
            end--;
        }
        out.setLength(end);
        out.append('\n');
    }
}
