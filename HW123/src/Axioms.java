import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anstanasia on 07.04.2016.
 */
public class Axioms {
    static List<String> axioms = new ArrayList<>();

    static void define() {
        axioms.add("A->B->A");
        axioms.add("(A->B)->(A->B->C)->(A->C)");
        axioms.add("A->B->A&B");
        axioms.add("A&B->A");
        axioms.add("A&B->B");
        axioms.add("A->A|B");
        axioms.add("B->A|B");
        axioms.add("(A->C)->(B->C)->(A|B->C)");
        axioms.add("(A->B)->(A->!B)->!A");
        axioms.add("!!A->A");
    }
}
