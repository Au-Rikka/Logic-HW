import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Anstanasia on 06.04.2016.
 */
public class ProofChecker {
    List<ExprNode> axioms = new ArrayList<>();
    List<ExprNode> expressions = new ArrayList<>();

    List<Integer> MPFrst = new ArrayList<>();
    List<Integer> MPSnd = new ArrayList<>();
    List<ExprNode> MPExpr = new ArrayList<>();

    ProofChecker() throws ParseException {
        ExprParser exprParser = new ExprParser();
        for (String a : Axioms.axioms) {
            this.axioms.add(exprParser.parse(a));
        }

        int num = 1;
        for (ExprNode axiom : this.axioms) {
            axiom.num = num;
            num++;
  /*          if (expr == null) {
                System.out.println("!!!!!!!!!!! INVALID AXIOM EXPRESSION: number " + (-num));
            } else {
                expr.printTree("");
            }
            System.out.println("");
            System.out.println(""); */
        }

    }

    public void check(ExprNode expr) {
        expr.num = expressions.size() + 1;

        if (expr.proof == ExprNode.Proof.NP) {
            tryToProof(expr);
        }

        if (expr.proof != ExprNode.Proof.NP) {
            lookForMP(expr);
        }
        expressions.add(expr);
    }

    private void tryToProof(ExprNode expr) {
        for (ExprNode axiom : axioms) {
            if (checkAxiom(expr, axiom)) {
                return;
            }
        }

        checkMP(expr);
    }

    private Boolean checkAxiom(ExprNode expr, ExprNode ax) {
        Map<String, String> m = new HashMap<>();
        ArrayDeque<ExprNode> e = new ArrayDeque<>();
        ArrayDeque<ExprNode> a = new ArrayDeque<>();

        e.add(expr);
        a.add(ax);

        while (!a.isEmpty()) {
            ExprNode curE = e.pollFirst();
            ExprNode curA = a.pollFirst();

            if (curA.oper == ExprNode.Oper.NONE) {
                if (m.containsKey(curA.var)) {
                    if (!m.get(curA.var).equals(curE.var)) {
                        return false;
                    }
                } else {
                    m.put(curA.var, curE.var);
                }
            } else {
                if (curA.oper == curE.oper) {
                    e.add(curE.left);
                    a.add(curA.left);
                    if (curA.oper != ExprNode.Oper.NOT) {
                        e.add(curE.right);
                        a.add(curA.right);
                    }
                } else {
                    return false;
                }
            }
        }

        expr.proof = ExprNode.Proof.AXIOM;
        expr.frst = ax.num;
        return true;
    }

    private Boolean checkMP(ExprNode expr) {
        for (int i = 0; i < MPExpr.size(); i++) {
            if (ExprNode.isEqual(MPExpr.get(i).right, expr)) {
                expr.proof = ExprNode.Proof.MP;
                expr.frst = MPFrst.get(i);
                expr.snd = MPSnd.get(i);
                return true;
            }
        }

        return false;
    }

    private void lookForMP(ExprNode expr) {
        expressions.stream().filter(x -> x.proof != ExprNode.Proof.NP).forEach(x -> {
            addIfMP(x, expr);
            addIfMP(expr, x);
        });
    }

    private void addIfMP(ExprNode a, ExprNode b) {
        //if b is a short part (frst num)
        if (a.oper == ExprNode.Oper.IMPL && ExprNode.isEqual(a.left, b)) {
          //  System.out.println("MP FOUND:");
          //  b.printTree("");
          //  a.printTree("");
          //  System.out.println(b.num + " " + a.num);
          //  System.out.println("");

            MPFrst.add(b.num);
            MPSnd.add(a.num);
            MPExpr.add(a);
        }

    }
}
