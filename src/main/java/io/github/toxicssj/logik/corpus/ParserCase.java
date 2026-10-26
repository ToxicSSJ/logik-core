package io.github.toxicssj.logik.corpus;

/**
 * A formula parser case from the corpus.
 *
 * @param expected   for an accepted input, the formula fully parenthesized;
 *                   for a rejected one, the column of the error
 * @param legacyNote why the 2018 parser disagrees, or null
 * @param location   file and line of the case, for error messages
 */
public record ParserCase(boolean accept, String input, String expected, String legacyNote, String location) {

    /** Column of the error, counted in code points from 1. Only for rejected inputs. */
    public int column() {
        if (accept) {
            throw new IllegalStateException(location + ": an accepted input has no error column");
        }
        return Integer.parseInt(expected);
    }

    @Override
    public String toString() {
        return (accept ? "accept \"" : "reject \"") + input + "\"";
    }
}
