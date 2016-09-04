/**
 * Created by Anstanasia on 07.04.2016.
 */
public class ParseException extends Exception {
    public ParseException(String s, int i, String err) {
        super("Expression parse exception happend in line " + s + " at simbol " + i + ": " + err);
    }
}
