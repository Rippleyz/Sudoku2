package sudoku.cobyapps.com.sudoku;

import java.util.Random;

public class SudokuConfiguration {

    private int numberOfUnknownsLowerBound;
    private int numberOfUnknownsUpperBound;
    private int difficultyLowerBoundary;
    private int difficultyUpperBoundary;

    private SudokuConfiguration(Builder builder) {
        this.numberOfUnknownsLowerBound = builder.numberOfUnknownsLowerBound;
        this.numberOfUnknownsUpperBound = builder.numberOfUnknownsUpperBound;
        this.difficultyLowerBoundary = builder.difficultyLowerBound;
        this.difficultyUpperBoundary = builder.difficultyUpperBound;
    }

    public static class Builder {

        private int numberOfUnknownsLowerBound;
        private int numberOfUnknownsUpperBound;
        private int difficultyLowerBound;
        private int difficultyUpperBound;

        private Random random = new Random();


        // TODO: 20.12.2018 edit numbers
        public Builder() {
            numberOfUnknownsLowerBound = 1;
            numberOfUnknownsUpperBound = 64;
            difficultyLowerBound = 0;
            difficultyUpperBound = Integer.MAX_VALUE;
        }

        // TODO: 20.12.2018 edit numbers
        public Builder allRandom(){
            this.numberOfUnknownsLowerBound = random.nextInt(numberOfUnknownsLowerBound+1);
            this.numberOfUnknownsUpperBound = random.nextInt(numberOfUnknownsUpperBound+1);
            this.difficultyLowerBound = random.nextInt(difficultyLowerBound+1);
            this.difficultyUpperBound = random.nextInt(difficultyUpperBound+1);
            return this;
        }

        public Builder withNumberOfUnknownsLowerBound(int numberOfUnknownsLowerBound) throws Exception {
            if (numberOfUnknownsLowerBound >= 0 && numberOfUnknownsLowerBound < 65) {
                this.numberOfUnknownsLowerBound = numberOfUnknownsLowerBound;
                this.numberOfUnknownsLowerBound = numberOfUnknownsLowerBound;
            } else
                throw new Exception("Boundaries must be between 0 and 64");
            return this;
        }

        public Builder withNumberOfUnknownsUpperBound(int numberOfUnknownsUpperBound) throws Exception {
            if (numberOfUnknownsUpperBound >= 0 && numberOfUnknownsUpperBound < 65) {
                this.numberOfUnknownsUpperBound = numberOfUnknownsUpperBound;
                this.numberOfUnknownsUpperBound = numberOfUnknownsUpperBound;
            } else
                throw new Exception("Boundaries must be between 0 and 64");
            return this;
        }

        public Builder withDifficultyLowerBound(int difficultyLowerBound) throws Exception {
            if (difficultyLowerBound >= 0) {
                this.difficultyLowerBound = difficultyLowerBound;
            } else
                throw new Exception("Difficulty must be positive");
            return this;
        }

        public Builder withDifficultyUpperBound(int difficultyUpperBound) throws Exception {
            if (this.difficultyUpperBound >= 0) {
                this.difficultyUpperBound = difficultyUpperBound;
            } else {
                throw new Exception("Difficulty must be positive");
            }
            return this;
        }

        public SudokuConfiguration build() {
            return new SudokuConfiguration(this);
        }
    }
}
