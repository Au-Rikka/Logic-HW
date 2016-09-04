/**
 * Created by Anastasia on 21.02.2016.
 */
public class ExprParser {
    //type EXPR: DISJ | DISJ->EXPR
    //type DISJ: CONJ | DISJ|CONJ
    //type CONJ: NEG | CONJ&NEG
    //type NEG: bukva+cifry | !NEG | (EXPR)

    int i;
    String s;

    public ExprNode parse(String s) throws ParseException{
        this.s = deleteWhitespaces(s);
        this.i = 0;

        ExprNode res = parseExpr();

        if (i < this.s.length()) {
            throw new ParseException(this.s, i, "parsing finished before the end of string");
        }

        res.var = this.s;
        return res;
    }

    private ExprNode parseExpr() throws ParseException {
        ExprNode res = parseDisj();

        if (curChar() == '-') {
            i++;
            if (curChar() == '>') {
                i++;
                ExprNode newRes = new ExprNode(ExprNode.Type.EXPR, ExprNode.Oper.IMPL);
                newRes.left = res;
                newRes.right = parseExpr();
                res = newRes;
                res.var = res.left.var + "->" + res.right.var;
            } else {
                throw new ParseException(s, i, "unexpected symbol, should be >");
            }
        }

        return res;
    }

    private ExprNode parseDisj() throws ParseException {
        ExprNode res = parseConj();
        while (curChar() == '|') {
            i++;
            ExprNode newRes = new ExprNode(ExprNode.Type.DISJ, ExprNode.Oper.OR);
            newRes.left = res;
            newRes.right = parseConj();
            res = newRes;
            res.var = res.left.var + "|" + res.right.var;
        }
        return res;
    }

    private ExprNode parseConj() throws ParseException {
        ExprNode res = parseNeg();
        while (curChar() == '&') {
            i++;
            ExprNode newRes = new ExprNode(ExprNode.Type.CONJ, ExprNode.Oper.AND);
            newRes.left = res;
            newRes.right = parseNeg();
            res = newRes;
            res.var = res.left.var + "&" + res.right.var;
        }
        return res;
    }

    private ExprNode parseNeg() throws ParseException {
        ExprNode res;

        if (Character.isUpperCase(curChar())) {
            String var = "";
            while (Character.isUpperCase(curChar()) || Character.isDigit(curChar())) {
                var += curChar();
                i++;
            }
            res = new ExprNode(ExprNode.Type.NEG, ExprNode.Oper.NONE, var);
            return res;
        }
        if (curChar() == '(') {
            i++;
            res = parseExpr();
            if (curChar() == ')') {
                i++;
                return res;
            } else {
                throw new ParseException(s, i, "should be )");
            }
        }
        if (curChar() == '!') {
            i++;
            res = new ExprNode(ExprNode.Type.NEG, ExprNode.Oper.NOT);
            res.left = parseNeg();
            res.var = "!" + res.left.var;
            return res;
        }

        throw new ParseException(s, i, "unexpected symbol");
    }

    private static String deleteWhitespaces(String s) {
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                res += s.charAt(i);
            }
        }
        return res;
    }

    private char curChar() {
        if (i >= s.length()) {
            return '$';
        } else {
            return s.charAt(i);
        }
    }
}