package sudoku.cobyapps.com.sudoku;

import android.support.v4.app.FragmentManager;

public interface InterFragmentCommunicator {
    void invalidateMenu ();
    void setCurrentMenu (int currentMenu);
    void saveCurrentSudokuToDatabase ();
    SudokuDatabaseDataHolder getSudokuDatabaseDataHolder (SudokuCellDataComponent [][] dataComponents);
    DatabaseAdapter getDatabaseAdapter ();
    FragmentManager getTheFragmentManager ();
    SudokuCellDataComponent [][] getSudokuCellDataComponentFromDataStrings (String sudoku, String isGivens, String notes);
    void setCurrentSudoku (SudokuCellDataComponent [][] dataComponents);
    void invalidateCurrentSudoku();

}
