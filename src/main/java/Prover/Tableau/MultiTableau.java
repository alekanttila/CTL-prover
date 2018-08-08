package Prover.Tableau;

import Prover.Formula.Colour;
import Prover.Formula.Formula;
import Prover.Formula.Hue;

import java.util.concurrent.atomic.AtomicInteger;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.statusPrint;
import static Prover.Tableau.Tableau.ExtendResult.FAILURE;
import static Prover.Tableau.Tableau.ExtendResult.SUCCESS;

public abstract class MultiTableau extends Tableau{

    int tasks = 0;
    int parallelism;
    protected AtomicInteger lgRuns;
    protected AtomicInteger tableausBuilt;

    public MultiTableau(Formula f) {
        super(f);
    }

    protected ExtendResult checkUpLinks(Node n, Hue hueToCheck, ConcurrentUpLinkTree checkedUpLinks, AtomicInteger lgRuns, AtomicInteger tableausBuilt, int taskID) {
        ANCESTOR_LOOP:
        for (Node a : n.ancestors) {
            if (hueToCheck.getSuccessors(f.getAllHues()).contains(a.zOrder.get(0)) &&
                    n.z.getSuccessors(f.getAllColours()).contains(a.z)) {

                addUpLink(n, a, hueToCheck);
                statusPrint(TABLEAU, MAX,"Task " + taskID +  "Adding uplink to " + a.getName() + " and initiating LG with");
                statusPrint(TABLEAU, MAX, infoString());
                lgRuns.addAndGet(1);
                if (LG.check(f, root)) {
                    tableausBuilt.addAndGet(1);
                    statusPrint(TABLEAU, MAX,"Task " + taskID +  "LG OK");
                    checkedUpLinks.add(n.z, hueToCheck, n);
                    return SUCCESS;
                } else {
                    statusPrint(TABLEAU, MAX,"Task " + taskID +  "LG FAILED");
                    removeUpLink(n, a);
                    continue ANCESTOR_LOOP;
                }
            }
        }
        statusPrint(TABLEAU, MAX,"Task " + taskID + "All ancestors checked. No upLinks possible");
        checkedUpLinks.add(n.z, hueToCheck, new ConcurrentUpLinkTree<Colour>());
        return FAILURE;
    }

    @Override
    public String fullInfoString() {
        return infoString(root, null) + "\nTableaus built: " + tableausBuilt.toString() + " LG runs: " + lgRuns.toString() + " total tasks: " + tasks + " (parallelism: " + parallelism + ")";
    }


}
