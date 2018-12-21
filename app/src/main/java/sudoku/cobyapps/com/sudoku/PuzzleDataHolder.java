package sudoku.cobyapps.com.sudoku;

public class PuzzleDataHolder {
    private Byte [] puzzle;
    private Byte [] solution;
    private int difficultyScore;
    public Byte[] getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Byte[] puzzle) {
        this.puzzle = puzzle;
    }

    public Byte[] getSolution() {
        return solution;
    }

    public void setSolution(Byte[] solution) {
        this.solution = solution;
    }


    public PuzzleDataHolder(Byte[] puzzle, Byte[] solution){
        this.puzzle = puzzle;
        this.solution = solution;
    }

    public int getDifficultyScore() {
        return difficultyScore;
    }

    public void setDifficultyScore(int difficultyScore) {
        this.difficultyScore = difficultyScore;
    }
}
