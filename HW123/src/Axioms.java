import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Anstanasia on 07.04.2016.
 */
public class Axioms {
    static List<String> axioms = new ArrayList<>(asList(
            "A->B->A",
            "(A->B)->(A->B->C)->(A->C)",
            "A->B->A&B",
            "A&B->A",
            "A&B->B",
            "A->A|B",
            "B->A|B",
            "(A->C)->(B->C)->(A|B->C)",
            "(A->B)->(A->!B)->!A",
            "!!A->A"
    ));
}
