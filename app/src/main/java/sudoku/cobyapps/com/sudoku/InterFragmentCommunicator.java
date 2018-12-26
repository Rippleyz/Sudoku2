package sudoku.cobyapps.com.sudoku;

public interface InterFragmentCommunicator {
    void invalidateMenu ();
    void setCurrentMenu (int currentMenu);
    void saveCurrentSudokuToDatabase();
    DatabaseAdapter getDatabaseAdapter();
}
