package Prover.Tableau;

import Prover.Formula.Colour;
import Prover.Formula.Formula;
import Prover.Formula.Hue;

import java.util.concurrent.atomic.AtomicInteger;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.statusPrint;
import static Prover.Tableau.Pair.nH;
import static Prover.Tableau.Tableau.ExtendResult.FAILURE;
import static Prover.Tableau.Tableau.ExtendResult.SUCCESS;

public abstract class ConcurrentTableau extends Tableau{

    int tasks = 0;
    int parallelism;
    protected AtomicInteger lgRuns;
    protected AtomicInteger tableausBuilt;

    public ConcurrentTableau(Formula f) {
        super(f);
    }

    protected ExtendResult checkUpLinks(Node n, Hue hueToCheck, ConcurrentUpLinkTree checkedUpLinks, AtomicInteger lgRuns, AtomicInteger tableausBuilt, int taskID) {
        ANCESTOR_LOOP:
        for (Node a : n.ancestors) {
            if (n.z.getSuccessors(f.getAllColours()).contains(a.z)) {
                if (multiUpLinks) {
                    for (Hue h : a.z) {
                        if (hueToCheck.getSuccessors(f.getAllHues()).contains(h)) {
                            addUpLink(n, hueToCheck, a, h);
                            statusPrint(TABLEAU, MAX, "Task " + taskID +  " Adding uplink to " + a.getName() + " " + h.name + " and initiating LG with");
                            statusPrint(TABLEAU, MAX, infoString());
                            lgRuns.addAndGet(1);
                            if (LG.check(f, root)) {
                                tableausBuilt.addAndGet(1);
                                statusPrint(TABLEAU, MAX, "Task " + taskID +  " LG OK");
                                checkedUpLinks.add(n.z, hueToCheck, nH(a,h));
                                return SUCCESS;
                            } else {
                                statusPrint(TABLEAU, MAX, "Task " + taskID +  " LG failed");
                                removeUpLink(n, a, h);
                                continue ANCESTOR_LOOP;
                            }
                        }
                    }
                } else {
                    if (hueToCheck.getSuccessors(f.getAllHues()).contains(a.zOrder.get(0))) {
                        addUpLink(n, hueToCheck, a, a.zOrder.get(0));
                        statusPrint(TABLEAU, MAX, "Task " + taskID +  " Adding uplink to " + a.getName() + " " + a.zOrder.get(0).name + " and initiating LG with");
                        statusPrint(TABLEAU, MAX, infoString());
                        lgRuns.addAndGet(1);
                        if (LG.check(f, root)) {
                            tableausBuilt.addAndGet(1);
                            statusPrint(TABLEAU, MAX, "Task " + taskID +  " LG OK");
                            checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, a.getStandardNH());
                            return SUCCESS;
                        } else {
                            statusPrint(TABLEAU, MAX, "Task " + taskID +  " LG failed");
                            removeUpLink(n, a, a.zOrder.get(0));
                            continue ANCESTOR_LOOP;
                        }

                    }
                }
            }
        }
        statusPrint(TABLEAU, MAX,"Task " + taskID +  " All ancestors checked. No upLinks possible");
        if (this instanceof ConcurrentBreadthTableau) {
            checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, new ConcurrentUpLinkTree<Pair<Colour, Hue>>());
        } else {
            checkedUpLinks.add(n.z, hueToCheck, new ConcurrentUpLinkTree<Colour>());
        }
        return FAILURE;
    }

    @Override
    public String fullInfoString() {
        return infoString(root, null) + "\nTableaus built: " + tableausBuilt.toString() + " LG runs: " + lgRuns.toString() + " total tasks: " + tasks + " (parallelism: " + parallelism + ")";
    }


}
