import java.util.ArrayDeque;
import java.util.HashMap;

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
    int num;
    long hash;
    ExprNode left, right;
    int frst, snd;

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
        System.out.println("");
        left.printTree(tab + "    ");
        if (oper == Oper.AND || oper == Oper.OR || oper == Oper.IMPL) {
            right.printTree(tab + "    ");
        }
    }

    long doHash() {
        long h = this.var.hashCode();
        return h;
    }

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

    public HashMap<String, Boolean> getNextMap(HashMap<String, Boolean> m) {
        for (String key : m.keySet()) {
            if (m.get(key) == true) {
                m.put(key, false);
            } else {
                m.put(key, true);
                return m;
            }
        }

        return null;
    }



    public HashMap<String, Boolean> isTaft() {
        ArrayDeque<ExprNode> e = new ArrayDeque<>();
        HashMap<String, Boolean> m = new HashMap<>();

        e.add(this);

        while (!e.isEmpty()) {
            ExprNode curE = e.pollFirst();
            if (curE.oper == ExprNode.Oper.NONE) {
                if (!m.containsKey(curE.var)) {
                    m.put(curE.var, false);
                }
            } else {
                e.add(curE.left);
                if (curE.oper != ExprNode.Oper.NOT) {
                    e.add(curE.right);
                }
            }
        }

        do {
            if (!this.evaluate(m)) {
                return m;
            }
            m = this.getNextMap(m);
        } while (m != null);

        return null;
    }
}
