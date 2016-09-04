import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Anastasia on 04.04.2016.
 */
public class ExprNode {
    //type EXPR: DISJ | DISJ->EXPR
    //type DISJ: CONJ | DISJ|CONJ
    //type CONJ: NEG | CONJ&NEG
    //type NEG: bukva+cifry | !NEG | (EXPR)

    enum Type {EXPR, DISJ, CONJ, NEG}
    enum Oper {NONE, NOT, AND, OR, IMPL}
    enum Proof {AXIOM, MP, NP, ASSUMP}

    Type t;
    Oper oper;
    Proof proof;

    String var;
    ExprNode left, right;

    int num;
    int frst, snd;

    private Set<String> variables = new HashSet<>();



    public ExprNode() {
        this.proof = Proof.NP;
    }
    public ExprNode(Type t, Oper oper, String s) {
        this.proof = Proof.NP;
        this.t = t;
        this.oper = oper;
        this.var = s;
    }
    public ExprNode(Type t, Oper oper) {
        this.proof = Proof.NP;
        this.t = t;
        this.oper = oper;
    }



    void printTree(String tab) {
        System.out.print(tab);
        System.out.print(t.toString() + " ");
        System.out.print(oper.toString() + " ");
        if (oper == Oper.NONE) {
            System.out.println(var);
            return;
        }
        System.out.println();
        left.printTree(tab + "    ");
        if (oper == Oper.AND || oper == Oper.OR || oper == Oper.IMPL) {
            right.printTree(tab + "    ");
        }
    }



    //checks names of variables too
    static boolean isEqual(ExprNode e1, ExprNode e2) {
        if (e1.oper == e2.oper) {
            if (e1.oper == Oper.NONE) {
                return e1.var.equals(e2.var);
            }
            if (e1.oper == Oper.NOT) {
                return isEqual(e1.left, e2.left);
            }
            return isEqual(e1.left, e2.left) && isEqual(e1.right, e2.right);
        }
        return false;
    }



    public Boolean evaluate(HashMap<String, Boolean> m) {
        if (this.oper == Oper.NONE) {
            return m.get(this.var);
        }
        if (this.oper == Oper.NOT) {
            return !this.left.evaluate(m);
        }
        if (this.oper == Oper.AND) {
            return this.left.evaluate(m) && this.right.evaluate(m);
        }
        if (this.oper == Oper.IMPL) {
            if (this.left.evaluate(m) && !this.right.evaluate(m)) {
                return false;
            }
            return true;
        }
        if (this.oper == Oper.OR) {
            return this.left.evaluate(m) || this.right.evaluate(m);
        }

        return false;
    }



    public Set<String> getVariables() {
        if (variables.size() > 0) {
            return variables;
        }
        ArrayDeque<ExprNode> e = new ArrayDeque<>();
        e.add(this);
        while (!e.isEmpty()) {
            ExprNode curE = e.pollFirst();
            if (curE.oper == ExprNode.Oper.NONE) {
                variables.add(curE.var);
            } else {
                e.add(curE.left);
                if (curE.oper != ExprNode.Oper.NOT) {
                    e.add(curE.right);
                }
            }
        }
        return variables;
    }



    public HashMap<String, Boolean> getNextMap(HashMap<String, Boolean> m) {
        for (String v : variables) {
            if (m.get(v) == false) {
                m.put(v ,true);
                return  m;
            } else {
                m.put(v, false);
            }
        }

        return null;
    }

    //checks current expression for being tautology
    public HashMap<String, Boolean> isTaft() {
        HashMap<String, Boolean> map = new HashMap<>();
        getVariables();

        for (String v : variables) {
            map.put(v, false);
        }

        do {
            if (!this.evaluate(map)) {
                return map;
            }
            map = this.getNextMap(map);
        } while (map != null);

        return null;
    }
}
