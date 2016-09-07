import java.util.*;

/**
 * Created by Anastasia on 04.09.2016.
 */
public class ProofBuilder {

    //build proofs for all variables values, using assumptions (wtf???)
    //then transform them to a normal proof

    Set<String> variables;

    ProofBuilder() throws ParseException {}




    ArrayList<ExprNode> buildProof(ExprNode expr) {
        variables = expr.getVariables();

        HashMap<String, Boolean> map = new HashMap<>();
        for (String v :variables) {
            map.put(v, false);
        }
        do {
            buildProofwithAssumptions(expr, map);

            map = expr.getNextMap(map);
        } while (map != null);


        return expr;
    }



    
}
