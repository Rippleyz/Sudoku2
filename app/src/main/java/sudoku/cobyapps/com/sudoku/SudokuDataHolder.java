package sudoku.cobyapps.com.sudoku;

public class SudokuDataHolder {
    private SudokuCellDataComponent grid [][];
    public static int NUMBER_EMPTY = 0;
    SudokuDataHolder(){
        grid = new SudokuCellDataComponent [9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                grid[i][j] = new SudokuCellDataComponent();
            }
        }
    }

    public SudokuCellDataComponent [][] getGrid() {
        return grid;
    }

    public void setGrid(SudokuCellDataComponent[][] grid) {
        this.grid = grid;
    }
}
