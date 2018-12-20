package sudoku.cobyapps.com.sudoku;

public class SudokuCellDataComponent {
    private String notes;
    private int number;
    private boolean isGiven = false;
    public boolean getIsGiven() {
        return isGiven;
    }


    public void setIsGiven(boolean given) {
        isGiven = given;
    }
    SudokuCellDataComponent(){
        notes = "";
        number = SudokuDataHolder.NUMBER_EMPTY;
    }

    SudokuCellDataComponent(int number, String notes){
        this.notes = notes;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
