
/*
package PermutationBreadthTableau;

public class Comparescraps {
    public int compareTo(Formula f) {
        int result = 0;
        int lengthDiff = this.length - f.length;
        if (lengthDiff < -1 || lengthDiff > 1) {
            result = lengthDiff;
        } else if (lengthDiff == -1 || lengthDiff == 1) {
            //the following are included as special cases so as to order formulae so that the negation of an
            // atom (or TRUE) always immediately follows the atom so that ordered sets of formulae are easier
            // to compare
            if (this.getC()== NOT && (f.getArity() == ATOMIC || f.getArity() == BOOL)) {
                int idDiff = this.sf1.compareTo(f);
                if (idDiff == 0) {
                    result = 1;
                } else {
                    result = idDiff;
                }
            } else if ((this.getArity() == ATOMIC || this.getArity() == BOOL) && f.getC()== NOT) {
                int idDiff = f.sf1.compareTo(this);
                if (idDiff == 0) {
                    result = -1;
                } else {
                    result = idDiff;
                }
            } else {
                result = lengthDiff;
            }
        } else {
            if (this.equals(f)) {
                result = 0;
            } else {
                int connectiveDiff = this.c.compareTo(f.c);
                if (connectiveDiff != 0) {
                    result = connectiveDiff;
                } else {
                    switch (this.getArity()) {
                        case BINARY:
                            int sf1Diff = this.sf1.compareTo(f.sf1);
                            if (sf1Diff != 0) {
                                result = sf1Diff;
                            } else {
                                result = this.getSf2().compareTo(f.getSf2());
                            }
                            break;
                        case UNARY:
                            result = this.sf1.compareTo(f.sf1);
                            break;
                        case ATOMIC:
                            result = this.id.compareTo(f.id);
                            break;
                        default:
                            //if arity is TRUE, we should have gotten this.equals(f) = true
                            //TODO:error
                    }
                }
            }
        }
        return result;
    }
}
 public int compareTo(FormulaSet fS){
        int result = 0;
        Iterator<Formula> i = this.iterator();
        Iterator<Formula> i2 = fS.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            if (i2.hasNext()) {
                Formula f2 = i2.next();
                int fDiff = f.compareTo(f2);
                if (fDiff != 0) {
                    result = fDiff;
                    break;
                }
            } else {
                //if this set has the same first elements as fS but is smaller:
                result = -1;
            }
        }
        return result;
    }
    //TODO: equality?!!! should be thru members but double check
    */

