package sudoku.cobyapps.com.sudoku;

public class IndividualFitnessScorePair {
    private int score;
    private String individual;
    IndividualFitnessScorePair(String individual, int score){
        this.score = score;
        this.individual = individual;
    }

    public String getIndividual() {
        return individual;
    }

    public int getScore() {
        return score;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
