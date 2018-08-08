package Prover.Tableau;

import Prover.Formula.*;
import Prover.StatusMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Area.TASKS;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.NONE;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.*;
import Prover.Tableau.Pair.NodeHue;
import static Prover.Tableau.Tableau.ExtendResult.FAILURE;
import static Prover.Tableau.Tableau.ExtendResult.SUCCESS;


public class ConcurrentMultiBreadthTableau extends ConcurrentTableau {


    public ConcurrentMultiBreadthTableau(Formula f) {
        super(f);
        multiUpLinks = true;
        lgRuns = new AtomicInteger(0);
        tableausBuilt = new AtomicInteger(0);
    }

    public ExtendResult solve() {
        ExtendResult result = FAILURE;
        result.cUpLinks = new ConcurrentUpLinkTree<Colour>();
        for (int i = 0; i < maxBranchLength; i++) {
            result = checkLevel(i, false, result.cUpLinks);
            if (result == SUCCESS) {
                break;
            } else if (i != maxBranchLength - 1){
                result = checkLevel(i, true, result.cUpLinks);
            }
        }
        return result;
    }

    protected ExtendResult checkLevel(int level, boolean checkAll, ConcurrentUpLinkTree<Colour> checkedUpLinks) {
        ColourSet possibleColours = f.getFColours();

        ForkJoinPool pool = new ForkJoinPool();
        parallelism = pool.getParallelism();

        //TODO: check for empty possiblecolours
        ExtendResult result = FAILURE;
        sectionPrint(TABLEAU, SOME, "LEVEL " + level);
        //TODO: repetition checking: if same colour on higher or same level already uplinked, uplink to same node

        List<ColourCheckTask> taskList = new ArrayList<>();
        ColourSet coloursToCheck = new ColourSet();
        coloursToCheck.addAll(possibleColours);

        LOOP:
        while (!coloursToCheck.isEmpty() || !taskList.isEmpty()) {
            int freeThreads = parallelism - taskList.size();
            if (freeThreads < 0 ) {
                freeThreads = 0;
            }
            for (int i = 0; i < freeThreads; i++) {
                if (!coloursToCheck.isEmpty()) {
                    Colour c = coloursToCheck.first();
                    coloursToCheck.remove(c);

                    subSectionPrint(TABLEAU, SOME, "Fixing colour " + c.name);

                    ColourCheckTask task = new ColourCheckTask(c, level, checkAll, checkedUpLinks, tasks);
                    task.fork();
                    taskList.add(task);
                    tasks++;
                }
            }
            Iterator<ColourCheckTask> i = taskList.iterator();
            while (i.hasNext()) {
                ColourCheckTask task = i.next();
                if (task.isDone()) {
                    if (task.result == SUCCESS) {
                        statusPrint(TASKS, MAX, "Task " + task.taskID + " finished: SUCCESS");
                        root = task.join();
                        pool.shutdownNow();
                        result = SUCCESS;
                        break LOOP;
                    } else {
                        statusPrint(TASKS, MAX, "Task " + task.taskID + " finished: FAILURE");
                        task.cancel(true);
                        i.remove();
                    }
                } else {
                }
            }
        }
        if (!checkAll) {
            if (result == SUCCESS) {
                statusPrint(TABLEAU, SOME, "LEVEL " + level + " SUCCESS");
            } else {
                statusPrint(TABLEAU, SOME, "LEVEL " + level + " FAILURE");
                System.out.println( "LGRUNS " + lgRuns.toString() + " tableaus " + tableausBuilt.toString());
            }
        } else {
            statusPrint(TABLEAU, SOME, "LEVEL " + level + " CHECKALL FINISHED");
            System.out.println( "LGRUNS " + lgRuns.toString() + " tableaus " + tableausBuilt.toString());
        }
        return result;
    }


    class ColourCheckTask extends RecursiveTask<Node> {
        protected ConcurrentMultiBreadthTableau t;
        protected ConcurrentUpLinkTree<Colour> checkedUpLinks;
        protected ExtendResult result;
        private final Colour c;
        private final int level;
        private final int taskID;
        private final boolean checkAll;

        private ColourCheckTask(Colour c, int level, boolean checkAll, ConcurrentUpLinkTree<Colour> checkedUpLinks, int taskID) {
            t = new ConcurrentMultiBreadthTableau(f);
            this.c = c;
            this.level = level;
            this.checkAll = checkAll;
            this.checkedUpLinks = checkedUpLinks;
            this.taskID = taskID;
            result = SUCCESS;
        }

        protected Node compute() {
            Node n = t.addLeaf(null, null, c, c.first());
            t.root = n;
            statusPrint(TABLEAU, SOME, "Task " + taskID + " Starting with root " + c.name);

            ColourSet possibleChildColours = c.getSuccessors(f.getAllColours());
            if (!t.createDummies(n, possibleChildColours)) {
                t.removeNode(n);
                result = FAILURE;
                return null;
            }

            tableausBuilt.addAndGet(1);

            HUES_IN_NZ_LOOP:
            for (int i = 0; i < n.z.size(); i++) {
                Hue hueToCheck = n.zOrder.get(i);
                statusPrint(TABLEAU, SOME, "Task " + taskID + " Checking children/upLinks for " + hueToCheck.name);

                //remove dummy leaf
                Node dummyCopy = n.successors.get(i).node();
                t.removeNode(dummyCopy);

                //CHECK FOR ALREADY CHECKED UPLINKS HERE
                ConcurrentUpLinkTree<Colour> hueUpLinks = checkedUpLinks.getUpLinks(c, hueToCheck);
                if (hueUpLinks != null) {
                    NodeHue checkedChild = hueUpLinks.getNodeHue();

                    //CASE 1: we have a previously checked uplink. We attach it to the current tree
                    if (checkedChild != null) {
                        statusPrint(TABLEAU, SOME, "Task " + taskID + " Found previously checked uplink to " + checkedChild.node().getName());
                        t.restoreUpLink(n, hueToCheck, checkedChild);
                        tableausBuilt.addAndGet(1);
                        continue HUES_IN_NZ_LOOP;

                        //CASE 2: we have an empty uplinktree, indicating a previously failed uplink check if the level
                        //is 0. We attach a leaf and continue with the next hue
                    } else if (level == 0) {
                        if (hueUpLinks.getMap() != null && hueUpLinks.getMap().isEmpty()) {
                            statusPrint(TABLEAU, SOME, "Task " + taskID + " Previously checked result: no upLinks possible");
                            if (checkAll) {
                                t.addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                tableausBuilt.addAndGet(1);
                                result = FAILURE;
                                continue HUES_IN_NZ_LOOP;
                                //should not be able to reach this unless there has been a previous check;
                                //the method is meant to be used with checkAll off at each level first
                            } else {
                                throw new AssertionError("Task " + taskID + " Incorrect level check sequence");
                            }
                            //if the uplinktree exists and has no node, and the level is 0, the uplinktree map
                            //should be nonnull and empty
                        } else {
                            throw new AssertionError("Task " + taskID + " Incorrectly built UpLinkTree");
                        }

                        //CASE 3: we have an existing uplinktree (which is not a node), and the level is not 0.
                        //we recurse down to the lower levels
                    } else {
                        HueSet possibleChildHues = n.zOrder.get(i).getSuccessors(f.getAllHues());
                        ExtendResult lowerLevelResult = t.checkLevel(possibleChildColours, possibleChildHues, n, hueToCheck, level - 1, checkAll, hueUpLinks, lgRuns, tableausBuilt, taskID);
                        switch (lowerLevelResult) {
                            case SUCCESS:
                                continue HUES_IN_NZ_LOOP;
                            case FAILURE:
                                result = FAILURE;
                                if (checkAll) {
                                    tableausBuilt.addAndGet(1);
                                    t.addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                } else {
                                    t.removeNode(n);
                                    break HUES_IN_NZ_LOOP;
                                }
                            default: //TODO
                        }
                    }
                    //CASE 4: there is no existing uplinktree. The level should be 0. We check the upLinks
                } else {
                    //checked upLinks do not exist->if
                    if (level != 0) {
                        throw new AssertionError("Task + " + taskID + " Incorrectly built UpLinkTree");
                    } else {
                        switch (t.checkUpLinks(n, hueToCheck, checkedUpLinks, lgRuns, tableausBuilt, taskID)) {
                            case SUCCESS:
                                continue HUES_IN_NZ_LOOP;
                            case FAILURE:
                                result = FAILURE;
                                if (checkAll) {
                                    tableausBuilt.addAndGet(1);
                                    t.addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                } else {
                                    t.removeNode(n);
                                    break HUES_IN_NZ_LOOP;
                                }
                            default://TODO: error
                        }
                    }
                }
            }
            if (result == FAILURE && t.root != null) {
                t.removeNode(n);
            }
            return t.root;
        }
    }

    protected ExtendResult checkLevel(ColourSet possibleColours, HueSet possibleHues, Node parent, Hue parentHue, int level,
                                      boolean checkAll, ConcurrentUpLinkTree<Colour> checkedUpLinks, AtomicInteger lgRuns, AtomicInteger tableausBuilt, int taskID) {
        //TODO: check for empty possiblecolours
        ExtendResult result = FAILURE;
        sectionPrint(TABLEAU, SOME, "LEVEL " + level);
        //TODO: repetition checking: if same colour on higher or same level already uplinked, uplink to same node
        COLOUR_LOOP:
        for (Colour c : possibleColours) {
            subSectionPrint(TABLEAU, SOME, "Task " + taskID + "Fixing colour " + c.name);

            Hue hue = null;
            for (Hue h : possibleHues) {
                statusPrint(TABLEAU, MAX, "Task " + taskID + "Checking hue " + h.name);
                if (c.contains(h)) {
                    statusPrint(TABLEAU, SOME, "Task " + taskID + "Fixing successor for hue " + h.name);
                    hue = h;
                    break;
                }
            }

            if (hue == null) {
                continue COLOUR_LOOP;
            }

            Node n = addLeaf(parent, parentHue, c, hue);
            if (root == null) {
                root = n;
                statusPrint(TABLEAU, SOME, "Task " + taskID + "(This is the new root)");
            }

            ColourSet possibleChildColours = c.getSuccessors(f.getAllColours());
            if (!createDummies(n, possibleChildColours)) {
                removeNode(n);
                continue COLOUR_LOOP;
            }

            tableausBuilt.addAndGet(1);

            HUES_IN_NZ_LOOP:
            for (int i = 0; i < n.z.size(); i++) {
                Hue hueToCheck = n.zOrder.get(i);
                statusPrint(TABLEAU, SOME, "Task " + taskID + "Checking children/upLinks for " + hueToCheck.name);

                //remove dummy leaf
                Node dummyCopy = n.successors.get(i).node();
                removeNode(dummyCopy);

                //CHECK FOR ALREADY CHECKED UPLINKS HERE
                ConcurrentUpLinkTree<Colour> hueUpLinks = checkedUpLinks.getUpLinks(c, hueToCheck);
                if (hueUpLinks != null) {
                    NodeHue checkedChild = hueUpLinks.getNodeHue();

                    //CASE 1: we have a previously checked uplink. We attach it to the current tree
                    if (checkedChild != null) {
                        statusPrint(TABLEAU, SOME, "Task " + taskID + "Found previously checked uplink to " + checkedChild.node().getName());
                        restoreUpLink(n, hueToCheck, checkedChild);
                        tableausBuilt.addAndGet(1);
                        continue HUES_IN_NZ_LOOP;

                        //CASE 2: we have an empty uplinktree, indicating a previously failed uplink check if the level
                        //is 0. We attach a leaf and continue with the next hue
                    } else if (level == 0) {
                        if (hueUpLinks.getMap() != null && hueUpLinks.getMap().isEmpty()) {
                            statusPrint(TABLEAU, SOME, "Task " + taskID + "Previously checked result: no upLinks possible");
                            if (checkAll) {
                                addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                tableausBuilt.addAndGet(1);
                                continue HUES_IN_NZ_LOOP;
                                //should not be able to reach this unless there has been a previous check;
                                //the method is meant to be used with checkAll off at each level first
                            } else {
                                throw new AssertionError("Task " + taskID + "Incorrect level check sequence");
                            }
                            //if the uplinktree exists and has no node, and the level is 0, the uplinktree map
                            //should be nonnull and empty
                        } else {
                            throw new AssertionError("Task " + taskID + "Incorrectly built UpLinkTree");
                        }

                        //CASE 3: we have an existing uplinktree (which is not a node), and the level is not 0.
                        //we recurse down to the lower levels
                    } else {
                        HueSet possibleChildHues = n.zOrder.get(i).getSuccessors(f.getAllHues());
                        ExtendResult lowerLevelResult = checkLevel(possibleChildColours, possibleChildHues, n, hueToCheck, level - 1, checkAll, hueUpLinks, lgRuns, tableausBuilt, taskID);
                        switch (lowerLevelResult) {
                            case SUCCESS:
                                continue HUES_IN_NZ_LOOP;
                            case FAILURE:
                                if (checkAll) {
                                    tableausBuilt.addAndGet(1);
                                    addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                } else {
                                    removeNode(n);
                                    continue COLOUR_LOOP;
                                }
                            default: //TODO
                        }
                    }
                    //CASE 4: there is no existing uplinktree. The level should be 0. We check the upLinks
                } else {
                    //checked upLinks do not exist->if
                    if (level != 0) {
                        throw new AssertionError("Task " + taskID + "Incorrectly built UpLinkTree");
                    } else {
                        switch (checkUpLinks(n, hueToCheck, checkedUpLinks, lgRuns, tableausBuilt, taskID)) {
                            case SUCCESS:
                                continue HUES_IN_NZ_LOOP;
                            case FAILURE:
                                if (checkAll) {
                                    tableausBuilt.addAndGet(1);
                                    addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                } else {
                                    removeNode(n);
                                    continue COLOUR_LOOP;
                                }
                            default://TODO: error
                        }
                    }
                }
            }
            if (!checkAll) {
                result = SUCCESS;
                break COLOUR_LOOP;
            } else {
                removeNode(n);
            }
        }
        if (!checkAll) {
            if (result == SUCCESS) {
                statusPrint(TABLEAU, SOME,"Task " + taskID +  " LEVEL " + level + " SUCCESS");
            } else {
                statusPrint(TABLEAU, SOME,"Task " + taskID +  " LEVEL " + level + " FAILURE");
            }
        } else {
            statusPrint(TABLEAU, SOME, "Task " + taskID + " LEVEL " + level + "CHECKALL FINISHED");
        }
        return result;

    }

}
