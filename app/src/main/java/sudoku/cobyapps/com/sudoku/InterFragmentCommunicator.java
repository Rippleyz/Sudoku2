package sudoku.cobyapps.com.sudoku;

import android.support.v4.app.FragmentManager;

public interface InterFragmentCommunicator {
    void invalidateMenu ();
    void setCurrentMenu (int currentMenu);
    void saveCurrentSudokuToDatabase ();
    DatabaseAdapter getDatabaseAdapter ();
    FragmentManager getTheFragmentManager ();
    //SudokuCellDataComponent [][] getSudokuFromCursor(Cursor cursor, int position);
}
