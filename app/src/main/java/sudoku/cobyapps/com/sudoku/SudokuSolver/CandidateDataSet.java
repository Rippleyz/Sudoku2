package sudoku.cobyapps.com.sudoku.SudokuSolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CandidateDataSet extends HashSet<Byte> {
    private int primes [];
    CandidateDataSet (Set <Byte> set){
        primes = new int [] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        addAll(set);
    }
    @Override
    public int hashCode() {
        int product = 1;
        Iterator <Byte> iterator = iterator();
        while(iterator.hasNext()){
            product *= primes[iterator.next()];
        }
        return product;
    }
    public int hashCode(Set <Byte> set) {
        int product = 1;
        Iterator <Byte> iterator = set.iterator();
        while(iterator.hasNext()){
            product *= primes[iterator.next()];
        }
        return product;
    }
}
