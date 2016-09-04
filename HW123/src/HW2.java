import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anstanasia on 07.04.2016.
 */
public class HW2 {
    static List<ExprNode> assump = new ArrayList<>();
    static ExprNode a, b;

    public static void main(String[] args) throws IOException, ParseException {
        for (int testNum = 0; testNum < 3; testNum ++) {
            ProofChecker proofChecker = new ProofChecker();
            if (testNum == 0) {
                solve(proofChecker, "./tests/hw2/", "contra");
                proofChecker = new ProofChecker();
                check(proofChecker, "./tests/hw2/", "contra");
            } else {
                solve(proofChecker, "./tests/hw2/", "contra" + testNum);
                proofChecker = new ProofChecker();
                check(proofChecker, "./tests/hw2/", "contra" + testNum);
            }
        }
    }


    private static void solve(ProofChecker proofChecker, String testPath, String testName) throws ParseException {
        File inputFile = new File(testPath + testName + ".in");
        File outputFile = new File(testPath + testName + ".out");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {

                annotate(reader, writer, proofChecker, 0);
                changeProof(writer, proofChecker);

            } catch (IOException e) {
                System.err.println("Problems with output file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Problems with input file: " + e.getMessage());
        }
    }

    private static void check(ProofChecker proofChecker, String testPath, String testName) throws ParseException {
        File inputFile = new File(testPath + testName + ".out");
        File outputFile = new File(testPath + testName + ".ch");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {

                annotate(reader, writer, proofChecker, 1);

            } catch (IOException e) {
                System.err.println("Problems with output file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Problems with input file: " + e.getMessage());
        }
    }



    private static void annotate(BufferedReader reader, Writer writer, ProofChecker proofChecker, int t) throws IOException, ParseException{
        assump = new ArrayList<>();

        ExprParser exprParser = new ExprParser();
        String str;
        int curNum = 0;
        while ((str = reader.readLine()) != null) {
            if (curNum == 0 && str.indexOf("|-") >= 0) {

                int prev = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == ',') {
                        assump.add(exprParser.parse(str.substring(prev, i)));
                        prev = i + 1;
                    }
                    if (i < str.length() - 1 && str.charAt(i) == '|' && str.charAt(i + 1) == '-') {
                        a = exprParser.parse(str.substring(prev, i));
                        prev = i + 2;
                    }
                }
                b = exprParser.parse(str.substring(prev));

                curNum++;
     /*                   for (ExprNode a : assump) {
                            a.printTree("");
                            System.out.println("");
                        }
                        b.printTree("");
                        System.out.println("");
                        System.out.println("");
                        System.out.println("");
*/
                if (t == 1) {
                    assump.add(a);
                }

                if (assump.size() > 0) {
                    if (t == 1) {
                        writer.write("(0) ");
                    }
                    for (int i = 0; i < assump.size(); i++) {
                        writer.write(assump.get(i).var);
                        if (i != assump.size() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("|-");
                    writer.write(a.var + "->" + b.var + "\n");
                }

                continue;
            }

            ExprNode res = exprParser.parse(str);
            for (int i = 0; i < assump.size(); i++) {
                if (ExprNode.isEqual(assump.get(i), res)) {
                    res.proof = ExprNode.Proof.ASSUMP;
                    res.frst = i + 1;
                }
            }
            if (res.proof == ExprNode.Proof.NP && ExprNode.isEqual(a, res)) {
                res.proof = ExprNode.Proof.ASSUMP;
                res.frst = -1;
            }
            proofChecker.check(res);

            if (t == 1) {
                try {
                    writer.write("(" + res.num + ") " + str + " ");
                    if (res.proof == ExprNode.Proof.ASSUMP) {
                        writer.write("(Предп. " + res.frst + ")\n");
                    }
                    if (res.proof == ExprNode.Proof.AXIOM) {
                        writer.write("(Сх. акс. " + res.frst + ")\n");
                    }
                    if (res.proof == ExprNode.Proof.MP) {
                        writer.write("(M. P. " + res.frst + ", " + res.snd + ")\n");
                    }
                    if (res.proof == ExprNode.Proof.NP) {
                        writer.write("(Не доказано)\n");
                    }
                } catch (IOException e) {
                    System.err.println("Cannot write to output file");
                }
            }

            curNum++;
        }
    }


    static void changeProof(Writer writer, ProofChecker proofChecker) throws IOException {
        for (ExprNode expr : proofChecker.expressions) {
            if (expr.proof == ExprNode.Proof.AXIOM || expr.proof == ExprNode.Proof.ASSUMP) {
                if (expr.frst != -1) {
                    axiomCase(writer, expr);
                } else {
                    selfCase(writer);
                }
            }
            if (expr.proof == ExprNode.Proof.MP) {
                mpCase(writer, expr, proofChecker.expressions.get(expr.frst - 1));
            }
            if (expr.proof == ExprNode.Proof.NP) {
                writer.write("Expr " + expr.var + " not prooved");
                return;
            }
        }
    }

    static private void axiomCase(Writer writer, ExprNode expr) throws IOException {
        writer.write(expr.var + "\n");

        String sa = "(" + a.var + ")";
        String sexpr = "(" + expr.var + ")";


        writer.write(sexpr + "->(" + sa + "->" + sexpr + ")\n");
        writer.write(sa + "->" + sexpr + "\n");
    }

    static private void selfCase(Writer writer) throws IOException {
        String s1, s3, s4;
        String sa = "(" + a.var + ")";

        //a -> (a -> a)
        s1 = sa + "->(" + sa + "->" + sa + ")";
        //(a -> ((a -> a) -> a))
        s4 = "(" + sa + "->((" + sa + "->" + sa +")->" + sa + "))";
        //(a -> ((a -> a) -> a)) -> (a -> a)
        //s4 -> (a -> a)
        s3 = s4 + "->(" + sa + "->" + sa + ")";
        writer.write(s1 + "\n");
        //(s1) -> s3
        writer.write("(" + s1 + ")->" + s3 + "\n");
        writer.write(s3 + "\n");
        writer.write(s4 + "\n");
        writer.write(sa + "->" + sa + "\n");
    }

    static private void mpCase(Writer writer, ExprNode expr, ExprNode frst) throws IOException {
        String sa = "(" + a.var + ")";
        String sexpr = "(" + expr.var + ")";
        String sfrst = "(" + frst.var + ")";

        String s2 = "((" + sa + "->(" + sfrst + "->" + sexpr + "))->(" + sa + "->" + sexpr + "))";

        writer.write("(" + sa + "->" + sfrst + ")->" + s2 + "\n");
        writer.write(s2 + "\n");
        writer.write(sa + "->" + sexpr + "\n");
    }
}
