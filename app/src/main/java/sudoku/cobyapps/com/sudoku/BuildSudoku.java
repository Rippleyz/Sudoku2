package sudoku.cobyapps.com.sudoku;

import java.util.Random;

public class BuildSudoku {

    private int numberOfUnknownsLowerBound;
    private int numberOfUnknownsUpperBound;
    private int difficultyLowerBoundary;
    private int difficultyUpperBoundary;

    private BuildSudoku(Builder builder) {
        this.numberOfUnknownsLowerBound = builder.numberOfUnknownsLowerBound;
        this.numberOfUnknownsUpperBound = builder.numberOfUnknownsUpperBound;
        this.difficultyLowerBoundary = builder.difficultyLowerBoundary;
        this.difficultyUpperBoundary = builder.difficultyUpperBoundary;
    }

    public static class Builder {

        private int numberOfUnknownsLowerBound;
        private int numberOfUnknownsUpperBound;
        private int difficultyLowerBoundary;
        private int difficultyUpperBoundary;

        private Random random = new Random();


        // TODO: 20.12.2018 edit numbers
        public Builder() {
            numberOfUnknownsLowerBound = 20;
            numberOfUnknownsUpperBound = 40;
            difficultyLowerBoundary = 5;
            difficultyUpperBoundary = 120;
        }

        // TODO: 20.12.2018 edit numbers
        public Builder allRandom(){
            this.numberOfUnknownsLowerBound = random.nextInt(10) + 20;
            this.numberOfUnknownsUpperBound = random.nextInt(21) + 40;
            this.difficultyLowerBoundary = random.nextInt(16) + 5;
            this.difficultyUpperBoundary = random.nextInt(81) + 40;
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
                this.difficultyLowerBoundary = difficultyLowerBound;
            } else
                throw new Exception("Difficulty must be positive");
            return this;
        }

        public Builder withDifficultyUpperBound(int difficultyUpperBound) throws Exception {
            if (difficultyUpperBoundary >= 0) {
                this.difficultyUpperBoundary = difficultyUpperBound;
            } else {
                throw new Exception("Difficulty must be positive");
            }
            return this;
        }

        public BuildSudoku build() {
            return new BuildSudoku(this);
        }
    }
}
