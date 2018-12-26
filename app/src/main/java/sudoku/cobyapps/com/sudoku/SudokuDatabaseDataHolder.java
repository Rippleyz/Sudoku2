package sudoku.cobyapps.com.sudoku;

public class SudokuDatabaseDataHolder {
    private String sudoku;
    private String isGivens;
    private String notes;
    SudokuDatabaseDataHolder(String sudoku, String isGivens, String notes){
        this.sudoku = sudoku;
        this.isGivens = isGivens;
        this.notes = notes;
    }

    public String getSudoku() {
        return sudoku;
    }

    public String getIsGivens() {
        return isGivens;
    }

    public String getNotes() {
        return notes;
    }
}
