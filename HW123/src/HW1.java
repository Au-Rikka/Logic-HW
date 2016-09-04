import java.io.*;

/**
 * Created by Anastasia on 03.04.2016.
 */
public class HW1 {
    public static void main(String[] args) throws IOException, ParseException {
        Axioms.define();

        for (int testNum = 1; testNum <= 6; testNum ++) {
            if (testNum != 2) {
                ProofChecker proofChecker = new ProofChecker(Axioms.axioms);
                solve(proofChecker, "./tests/hw1/", "good" + testNum);
            }
        }
        for (int testNum = 1; testNum <= 6; testNum ++) {
                ProofChecker proofChecker = new ProofChecker(Axioms.axioms);
                solve(proofChecker, "./tests/hw1/", "wrong" + testNum);
        }
    }

    private static void solve(ProofChecker proofChecker, String testPath, String testName) throws ParseException {
        File inputFile = new File(testPath + testName + ".in");
        File outputFile = new File(testPath + testName + ".out");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {

                rewriteWithAnnotations(reader, writer, proofChecker);

            } catch (IOException e) {
                System.err.println("Problems with output file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Problems with input file: " + e.getMessage());
        }
    }

    private static void rewriteWithAnnotations(BufferedReader reader, Writer writer, ProofChecker proofChecker) throws IOException, ParseException{
        ExprParser exprParser = new ExprParser();
        String str;
        int curNum = 1;
        while ((str = reader.readLine()) != null) {

            ExprNode res = exprParser.parse(str);
            proofChecker.check(res);

            try {
                writer.write("(" + curNum + ") " + str + " ");
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

            curNum++;
        }
    }
}
