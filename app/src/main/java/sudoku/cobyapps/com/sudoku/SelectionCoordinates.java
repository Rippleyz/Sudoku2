package sudoku.cobyapps.com.sudoku;

public class SelectionCoordinates {
    private int x;
    private int y;
    public static final int NOT_SELECTED = 0;
    SelectionCoordinates(){
        x = NOT_SELECTED;
        y = NOT_SELECTED;
    }
    public void setSelectionCoordinates(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
