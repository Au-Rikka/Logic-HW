import java.io.*;
import java.util.Map;

/**
 * Created by Anstanasia on 07.04.2016.
 */
public class HW3 {

    public static void main(String[] args) throws IOException, ParseException {
        Axioms.define();

        ProofChecker proofChecker = new ProofChecker(Axioms.axioms);
        solve(proofChecker, "./tests/hw3/", "false" + 1);

        for (int testNum = 1; testNum <= 7; testNum++) {
            proofChecker = new ProofChecker(Axioms.axioms);
            solve(proofChecker, "./tests/hw3/", "true" + testNum);
        }
    }


    private static void solve(ProofChecker proofChecker, String testPath, String testName) throws ParseException {
        File inputFile = new File(testPath + testName + ".in");
        File outputFile = new File(testPath + testName + ".out");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {

                String str = reader.readLine();
                if (str == null) {
                    return;
                }
                ExprParser exprParser = new ExprParser();
                ExprNode expr = exprParser.parse(str);

                if (checkTrue(writer, proofChecker, expr)) {
                    //TODO: построить доказательство
                    writer.write("всегда правда");
                }

            } catch (IOException e) {
                System.err.println("Problems with output file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Problems with input file: " + e.getMessage());
        }
    }

    private static boolean checkTrue(Writer writer, ProofChecker proofChecker, ExprNode expr) throws IOException {
        Map<String, Boolean> m = expr.isTaft();

        if (m == null) {
            return true;
        } else {
            writer.write("Высказывание ложно при ");
            int i = 0;
            for (String key : m.keySet()) {
                String val;
                if (m.get(key)) {
                    val = "И";
                } else {
                    val = "Л";
                }
                if (i == 0) {
                    writer.write(key + "=" + val);
                } else {
                    writer.write(", " + key + "=" + val);
                }
            }

            return false;
        }
    }
}
