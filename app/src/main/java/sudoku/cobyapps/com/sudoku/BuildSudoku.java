package sudoku.cobyapps.com.sudoku;

import java.util.Random;

public class BuildSudoku {

    private int numberOfUnknowns;
    private int lowerBoundary;
    private int upperBoundary;

    private BuildSudoku(Builder builder) {
        this.numberOfUnknowns = builder.numberOfUnknowns;
        this.lowerBoundary = builder.lowerBoundary;
        this.upperBoundary = builder.upperBoundary;
    }

    public static class Builder {

        private int numberOfUnknowns ;
        private int lowerBoundary;
        private int upperBoundary;

        private Random random = new Random();

        public Builder() {
            numberOfUnknowns = 20;
            lowerBoundary = 5;
            upperBoundary = 120;
        }

        public Builder allRandom(){
            this.numberOfUnknowns = random.nextInt(61) + 1;
            this.lowerBoundary = random.nextInt(16) + 5;
            this.upperBoundary = random.nextInt(81) + 40;
            return this;
        }

        public Builder withNumberOfUnknowns(int numberOfUnknowns) {
            this.numberOfUnknowns = numberOfUnknowns;
            return this;
        }

        public Builder withLowerBound(int lowerBoundary) {
            this.lowerBoundary = lowerBoundary;
            return this;
        }

        public Builder withUpperBound(int upperBoundary) {
            this.upperBoundary = upperBoundary;
            return this;
        }

        public BuildSudoku build() {
            return new BuildSudoku(this);
        }
    }
}
