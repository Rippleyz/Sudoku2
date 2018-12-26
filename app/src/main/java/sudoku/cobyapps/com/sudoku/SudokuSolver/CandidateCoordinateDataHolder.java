package sudoku.cobyapps.com.sudoku.SudokuSolver;

import java.util.Set;

public class CandidateCoordinateDataHolder {
    private Set<Byte> candidate;
    private int row;
    private int column;
    CandidateCoordinateDataHolder(Set<Byte> candidate, int row, int column){
        this.column = column;
        this.row = row;
        this.candidate = candidate;
    }
    public Set<Byte> getCandidate() {
        return candidate;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
