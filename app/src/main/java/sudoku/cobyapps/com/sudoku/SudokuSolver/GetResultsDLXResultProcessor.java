package sudoku.cobyapps.com.sudoku.SudokuSolver;

import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;

import java.util.ArrayList;

public class GetResultsDLXResultProcessor implements DLXResultProcessor {
    private int numSolutions = 0;
    private ArrayList<byte[]> results;
    private DLXSudokuSolver sudokuSolver;
    public GetResultsDLXResultProcessor(DLXSudokuSolver sudokuSolver){
        results = new ArrayList<byte[]>();
        this.sudokuSolver = sudokuSolver;
    }
    public boolean processResult(DLXResult result) {
        numSolutions++;
        results.add(sudokuSolver.decodeDLXResult(result));
        if(results.size() == sudokuSolver.getSolutionSpaceLimit()){
            sudokuSolver.getDlx().setTerminate(true);
        }
        return true;
    }
    public int getNumSolutions() {
        return numSolutions;
    }

    public ArrayList<byte[]> getResults() {
        return results;
    }
}