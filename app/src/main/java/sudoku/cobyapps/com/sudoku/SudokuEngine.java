package sudoku.cobyapps.com.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class SudokuEngine {
    private static final int POPULATION_SIZE = 30;
    private ArrayList<IndividualFitnessScorePair> environment;
    private static final double MUTATION_PROBABILITY = 0.5;
    private String sudokuString;
    private Random random;
    private Set<String> newGenerations;
    private int age = 0;
    private String bestIndividual = "";
    private HashMap <Integer,Set<Integer>> candidates;
    private HashMap <String,Set<Integer>> candidatesWithRowAndColumn;
    SudokuEngine() {
        newGenerations = new HashSet<String>();
        candidates = new HashMap<Integer,Set<Integer>>();
        candidatesWithRowAndColumn = new HashMap <String,Set<Integer>>();
        Set<String> newGenerations = new HashSet<String>();
        environment = new ArrayList<IndividualFitnessScorePair>();
        random = new Random();
    }

    public String generateOpenSudoku() throws Exception {
        createEnvironment();
        int generations = 0;
        while (true) {
            generations++;
            sortByFitnessScore();
            mate();
            sortByFitnessScore();
            if (environment.get(0).getScore() == 0) {
                return environment.get(0).getIndividual();
            } else {
                //System.out.print("\r");
                // System.out.print(generations+" : "+environment.get(0).getScore());
            }
            eliminate();
            if (generations == 200000) {
                createEnvironment();
                generations = 0;
            }
        }
    }

    public String solve(String sudokuString) throws Exception {
        this.sudokuString = sudokuString;
        boolean stop  = true;
        while(stop){
            stop = false;
            for(int i = 0 ; i < 81 ; i++){
                if(sudokuString.charAt(i)=='0'){
                    int segment = i/9;
                    int currentColumn = 0;
                    int currentRow = 0;
                    int rowOffset = 0;
                    if(segment%3==0){
                        currentColumn = (i - segment * 9)%3;
                    }else if(segment%3==1){
                        currentColumn = (i - segment * 9)%3 + 3;
                    }else{
                        currentColumn = (i - segment * 9)%3 + 6;
                    }
                    if(segment > 2 && segment < 6){
                        rowOffset += 3;
                    }else if(segment > 5){
                        rowOffset += 6;
                    }
                    if(i - segment*9 < 3){
                        currentRow = rowOffset;
                    }else if(i - segment*9 < 6){
                        currentRow= rowOffset + 1;
                    }else{
                        currentRow = rowOffset + 2;
                    }
                    String currentColumnString = getColumn(sudokuString,currentColumn);
                    String currentRowString = getRow(sudokuString,currentRow);
                    String currentSegmentString = sudokuString.substring(segment*9,(segment*9)+9);
                    HashSet <Integer> numbers = new HashSet<Integer>();
                    for(int j = 0 ; j < 9 ; j++){
                        numbers.add(Integer.parseInt(currentColumnString.charAt(j)+""));
                        numbers.add(Integer.parseInt(currentRowString.charAt(j)+""));
                        numbers.add(Integer.parseInt(currentSegmentString.charAt(j)+""));
                    }
                    numbers.remove(0);
                    HashSet <Integer> missingNumbers = new HashSet<Integer>();
                    missingNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                    missingNumbers.removeAll(numbers);
                    if(missingNumbers.size()==1){
                        int missingNumber = missingNumbers.toArray(new Integer[0])[0];
                        sudokuString = sudokuString.substring(0,i)+
                                missingNumber+
                                sudokuString.substring(i+1,sudokuString.length());
                        stop = true;
                    }
                }
            }
            if(!stop){
                for(int i = 0 ; i < 81 ; i++){
                    if(sudokuString.charAt(i)=='0'){
                        int segment = i/9;
                        int currentColumn = 0;
                        int currentRow = 0;
                        int rowOffset = 0;
                        if(segment%3==0){
                            currentColumn = (i - segment * 9)%3;
                        }else if(segment%3==1){
                            currentColumn = (i - segment * 9)%3 + 3;
                        }else{
                            currentColumn = (i - segment * 9)%3 + 6;
                        }
                        if(segment > 2 && segment < 6){
                            rowOffset += 3;
                        }else if(segment > 5){
                            rowOffset += 6;
                        }
                        if(i - segment*9 < 3){
                            currentRow = rowOffset;
                        }else if(i - segment*9 < 6){
                            currentRow= rowOffset + 1;
                        }else{
                            currentRow = rowOffset + 2;
                        }
                        String currentColumnString = getColumn(sudokuString,currentColumn);
                        String currentRowString = getRow(sudokuString,currentRow);
                        String currentSegmentString = sudokuString.substring(segment*9,(segment*9)+9);
                        HashSet <Integer> numbers = new HashSet<Integer>();
                        for(int j = 0 ; j < 9 ; j++){
                            numbers.add(Integer.parseInt(currentColumnString.charAt(j)+""));
                            numbers.add(Integer.parseInt(currentRowString.charAt(j)+""));
                            numbers.add(Integer.parseInt(currentSegmentString.charAt(j)+""));
                        }
                        numbers.remove(0);
                        HashSet <Integer> missingNumbers = new HashSet<Integer>();
                        missingNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                        missingNumbers.removeAll(numbers);
                        candidates.put(i,missingNumbers);
                        candidatesWithRowAndColumn.put(currentRow+""+currentColumn,missingNumbers);
                    }
                }
            }
        }
        environment = new ArrayList<IndividualFitnessScorePair>();
        for (int j = 0; j < POPULATION_SIZE; j++) {
            String individual = "";
            for (int segment = 0; segment < 9; segment++) {
                String currentSegment = sudokuString.substring((segment * 9), (segment * 9) + 9);
                HashSet<Integer> numbersInCurrentRow = new HashSet<Integer>();
                HashSet<Integer> numbers = new HashSet<Integer>();
                numbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                for (int i = 0; i < currentSegment.length(); i++) {
                    if (!(currentSegment.charAt(i) + "").equals("0")) {
                        numbersInCurrentRow.add(Integer.parseInt(currentSegment.charAt(i) + ""));
                    }
                }
                numbers.removeAll(numbersInCurrentRow);
                String missingNumbers = numbers.toString().replace("[", "").
                        replace(", ", "").
                        replace("]", "");
                for (int i = 0; i < currentSegment.length(); i++) {
                    if ((currentSegment.charAt(i) + "").equals("0")) {
                        int missingNumberReplacementIndex = random.nextInt(missingNumbers.length());
                        String missingNumberReplacement = missingNumbers.charAt(missingNumberReplacementIndex) + "";
                        missingNumbers = missingNumbers.substring(0, missingNumberReplacementIndex)
                                + missingNumbers.substring(missingNumberReplacementIndex + 1, missingNumbers.length());
                        currentSegment = currentSegment.substring(0, i)
                                + missingNumberReplacement + currentSegment.substring(i + 1, currentSegment.length());
                    }
                }
                individual += currentSegment;
            }
            environment.add(new IndividualFitnessScorePair(individual, getFitnessScore(individual)));
            newGenerations.add(individual);
        }
        int generations = 0;
        while (true) {
            generations++;
            sortByFitnessScore();
            mate();
            if (environment.get(0).getScore() == 0) {
               // System.out.println(environment.get(0).getIndividual() + "");
                return environment.get(0).getIndividual();
            } else {
                //System.out.print("\r");
               // System.out.print(generations + " : " + environment.get(0).getScore());
            }
            if (bestIndividual.equals(environment.get(0).getIndividual())) {
                age++;
                environment.get(0).setScore(environment.get(0).getScore() + age);
            } else {
                age = 0;
                bestIndividual = environment.get(0).getIndividual();
            }
            sortByFitnessScore();
            if(generations == 500){
                int m = 0;
            }
            eliminate();
            newGenerations.clear();
            for (int i = 0; i < environment.size(); i++) {
                newGenerations.add(environment.get(i).getIndividual());
            }

            //System.out.print("\r");
            //System.out.print(age);
        }
    }

    private void eliminate() {
        int numberOfIterations = environment.size() - POPULATION_SIZE;
        for (int i = 0; i < numberOfIterations; i++) {
            environment.remove(environment.size() - 1);
        }
    }

    private void createEnvironment() {
        String initialIndividualStateComponent = "123456789";
        for (int i = 0; i < POPULATION_SIZE; i++) {
            String individual = "";
            for (int j = 0; j < 9; j++) {
                individual += getShuffledString(initialIndividualStateComponent);
            }
            environment.add(new IndividualFitnessScorePair(individual, getFitnessScore(individual)));
        }
    }

    private void sortByFitnessScore() {
        Collections.sort(environment, new Comparator<IndividualFitnessScorePair>() {
            @Override
            public int compare(IndividualFitnessScorePair individualFitnessScorePair,
                               IndividualFitnessScorePair individualFitnessScorePair2) {
                return individualFitnessScorePair.getScore() - individualFitnessScorePair2.getScore();
            }
        });
    }

    private String getShuffledString(String input) {
        String result = "";
        for (int i = 0; i < 9; i++) {
            int randomIndex = random.nextInt(input.length());
            result += input.charAt(randomIndex);
            input = input.substring(0, randomIndex) + input.substring(randomIndex + 1, input.length());
        }
        return result;
    }

    private int getFitnessScore(String individual) {
        int fitnessScore = 0;
        for (int i = 0; i < 9; i++) {
            Set<Integer> currentRowSet = new HashSet<Integer>();
            Set<Integer> currentColumnSet = new HashSet<Integer>();
            int rowIndex = i * 3;
            int columnIndex = i;
            if (i > 2 && i <= 5) {
                rowIndex += 18;
                columnIndex += 6;
            } else if (i > 5) {
                rowIndex += 36;
                columnIndex += 12;
            }
            currentRowSet.addAll(Arrays.asList(Integer.parseInt(individual.charAt(rowIndex) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 1) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 2) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 9) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 10) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 11) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 18) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 19) + ""),
                    Integer.parseInt(individual.charAt(rowIndex + 20) + "")));
            currentColumnSet.addAll(Arrays.asList(Integer.parseInt(individual.charAt(columnIndex) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 3) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 6) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 27) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 30) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 33) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 54) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 57) + ""),
                    Integer.parseInt(individual.charAt(columnIndex + 60) + "")));
            fitnessScore += (9 - currentRowSet.size()) + (9 - currentColumnSet.size());
            String currentColumnString = Arrays.toString(currentColumnSet.toArray(new Integer[0]))
                    .replace("[", "").replace("]", "")
                    .replace(",", "").replace(" ", "");
            String currentRowString = Arrays.toString(currentRowSet.toArray(new Integer[0]))
                    .replace("[", "").replace("]", "")
                    .replace(",", "").replace(" ", "");
            for (int m = 1; m < currentColumnString.length() + 1; m++) {
                fitnessScore += findNumberOfOccurances(getColumn(individual, i), m) - 1;
            }
            for (int m = 1; m < currentRowString.length() + 1; m++) {
                fitnessScore += findNumberOfOccurances(getRow(individual, i), m) - 1;
            }
        }
        return fitnessScore;
    }
    private void mate() throws Exception {
        for (int individiual1 = 0; individiual1 < POPULATION_SIZE; individiual1++) {
            for (int individiual2 = individiual1 + 1; individiual2 < POPULATION_SIZE; individiual2++) {
                if (random.nextDouble() < (2 * POPULATION_SIZE - (individiual1 + individiual2) / ((2 * POPULATION_SIZE) - 1))) {
                    int segment1 = random.nextInt(9);
                    int segment2 = segment1;
                    String segment1String = environment.get(individiual1).getIndividual().substring(segment1 * 9, (segment1 * 9) + 9);
                    String segment2String = environment.get(individiual2).getIndividual().substring(segment2 * 9, (segment2 * 9) + 9);
                    String offspring1 = mutate((environment.get(individiual1).getIndividual().substring(0, segment1 * 9) +
                            environment.get(individiual1).getIndividual().substring((segment1 * 9) + 9, environment.get(individiual1)
                                    .getIndividual().length())).substring(0, segment1 * 9) + segment2String +
                            environment.get(individiual1).getIndividual().substring((segment1 * 9) + 9, environment.get(individiual1)
                                    .getIndividual().length()));

                    String offspring2 = mutate((environment.get(individiual2).getIndividual().substring(0,segment2*9)+
                            environment.get(individiual2).getIndividual().substring((segment2*9)+9,environment.get(individiual2)
                                    .getIndividual().length())).substring(0,segment2*9)+segment1String+
                            environment.get(individiual2).getIndividual().substring((segment2*9)+9,environment.get(individiual2)
                                    .getIndividual().length()));
                    if (!newGenerations.contains(offspring1)) {
                        environment.add(new IndividualFitnessScorePair(offspring1, getFitnessScore(offspring1)));
                        newGenerations.add(offspring1);
                    }
                    if(!newGenerations.contains(offspring2)){
                        environment.add(new IndividualFitnessScorePair(offspring2,getFitnessScore(offspring2)));
                        newGenerations.add(offspring2);
                    }
                }
            }
        }
    }
    /*
    for (int segment1 = 0; segment1 < 9; segment1++) {
                    int segment2 = segment1;
                    for (int individiual1 = 0; individiual1 < POPULATION_SIZE; individiual1++) {
                        for (int individiual2 = individiual1 + 1; individiual2 < POPULATION_SIZE; individiual2++) {
                            if (random.nextDouble() < (2 * POPULATION_SIZE - (individiual1 + individiual2) / ((2 * POPULATION_SIZE) - 1))) {
                                String segment1String = environment.get(individiual1).getIndividual().substring(segment1 * 9, (segment1 * 9) + 9);
                                String segment2String = environment.get(individiual2).getIndividual().substring(segment2 * 9, (segment2 * 9) + 9);
                                String offspring1 = mutate((environment.get(individiual1).getIndividual().substring(0, segment1 * 9) +
                                        environment.get(individiual1).getIndividual().substring((segment1 * 9) + 9, environment.get(individiual1)
                                                .getIndividual().length())).substring(0, segment1 * 9) + segment2String +
                                        environment.get(individiual1).getIndividual().substring((segment1 * 9) + 9, environment.get(individiual1)
                                                .getIndividual().length()));

                                String offspring2 = mutate((environment.get(individiual2).getIndividual().substring(0,segment2*9)+
                                        environment.get(individiual2).getIndividual().substring((segment2*9)+9,environment.get(individiual2)
                                                .getIndividual().length())).substring(0,segment2*9)+segment1String+
                                        environment.get(individiual2).getIndividual().substring((segment2*9)+9,environment.get(individiual2)
                                                .getIndividual().length()));
                                if (!newGenerations.contains(offspring1)) {
                                    environment.add(new IndividualFitnessScorePair(offspring1, getFitnessScore(offspring1)));
                                    newGenerations.add(offspring1);
                                }
                                if(!newGenerations.contains(offspring2)){
                                    environment.add(new IndividualFitnessScorePair(offspring2,getFitnessScore(offspring2)));
                                    newGenerations.add(offspring2);
                                }
                            }
                        }
                    }
            }
     */
    private String mutate(String individual) throws Exception {
        for(int segment = 0; segment < 9 ; segment++){
            if (random.nextDouble() < MUTATION_PROBABILITY) {
                int swapIndex1 = (segment * 9) + random.nextInt(9);
                int swapIndex2 = (segment * 9) + random.nextInt(9);
                while(swapIndex1==swapIndex2){
                    swapIndex2 = (segment * 9) + random.nextInt(9);
                }
                if (sudokuString == null || ((((sudokuString.charAt(swapIndex1) + "").equals("0"))
                        && ((sudokuString.charAt(swapIndex2) + "").equals("0"))) && candidates.get(swapIndex2).contains(Integer.parseInt(individual.charAt(swapIndex1)+""))&&
                        candidates.get(swapIndex1).contains(Integer.parseInt(individual.charAt(swapIndex2)+"")))) {
                    int temp = Integer.parseInt(individual.charAt(swapIndex1) + "");
                    String tempIndividual = ((individual.substring(0, swapIndex1)
                            + individual.substring(swapIndex1 + 1, individual.length())).substring(0, swapIndex1)
                            + individual.charAt(swapIndex2)
                            + individual.substring(swapIndex1 + 1, individual.length()));
                    individual = tempIndividual.substring(0, swapIndex2) + temp + tempIndividual.substring(swapIndex2 + 1, tempIndividual.length());
                    int columnOfSwapIndex1;
                    int columnOfSwapIndex2;
                    int rowOfSwapIndex1;
                    int rowOfSwapIndex2;
                    int rowOffset = 0;
                    if(segment%3==0){
                        columnOfSwapIndex1 = (swapIndex1 - segment * 9)%3;
                        columnOfSwapIndex2 = (swapIndex2 - segment * 9)%3;
                    }else if(segment%3==1){
                        columnOfSwapIndex1 = (swapIndex1 - (segment * 9))%3 + 3;
                        columnOfSwapIndex2 = (swapIndex2 - (segment * 9))%3 + 3;
                    }else{
                        columnOfSwapIndex1 = (swapIndex1 - (segment * 9))%3 + 6;
                        columnOfSwapIndex2 = (swapIndex2 - (segment * 9))%3 + 6;
                    }
                    if(segment > 2 && segment < 6){
                        rowOffset += 3;
                    }else if(segment > 5){
                        rowOffset += 6;
                    }
                    if(swapIndex1 - segment*9 < 3){
                        rowOfSwapIndex1 = rowOffset;
                    }else if(swapIndex1 - segment*9 < 6){
                        rowOfSwapIndex1 = rowOffset + 1;
                    }else{
                        rowOfSwapIndex1 = rowOffset + 2;
                    }
                    if(swapIndex2 - segment*9 < 3){
                        rowOfSwapIndex2 = rowOffset;
                    }else if(swapIndex2 - segment*9 < 6){
                        rowOfSwapIndex2 = rowOffset + 1;
                    }else{
                        rowOfSwapIndex2 = rowOffset + 2;
                    }
                    int numberOfOccurancesOfSwapIndex1InItsColumn = findNumberOfOccurances(getColumn(individual,columnOfSwapIndex1)
                            ,Integer.parseInt(individual.charAt(swapIndex1)+""));
                    if(numberOfOccurancesOfSwapIndex1InItsColumn > 2 ||
                            findNumberOfOccurances(getRow(individual,rowOfSwapIndex1)
                                    ,Integer.parseInt(individual.charAt(swapIndex1)+""))
                                    + numberOfOccurancesOfSwapIndex1InItsColumn > 3){
                        temp = Integer.parseInt(individual.charAt(swapIndex1)+"");
                        tempIndividual = ((individual.substring(0,swapIndex1)
                                +individual.substring(swapIndex1+1,individual.length())).substring(0,swapIndex1)
                                +individual.charAt(swapIndex2)
                                +individual.substring(swapIndex1+1,individual.length()));
                        individual = tempIndividual.substring(0,swapIndex2)+temp+tempIndividual.substring(swapIndex2+1,tempIndividual.length());
                    }else{
                        int numberOfOccurancesOfSwapIndex2InItsColumn = findNumberOfOccurances(getColumn(individual,columnOfSwapIndex2)
                                ,Integer.parseInt(individual.charAt(swapIndex2)+""));
                        if(numberOfOccurancesOfSwapIndex2InItsColumn > 2 ||
                                findNumberOfOccurances(getRow(individual,rowOfSwapIndex2)
                                        ,Integer.parseInt(individual.charAt(swapIndex2)+""))
                                        + numberOfOccurancesOfSwapIndex2InItsColumn > 3){
                            temp = Integer.parseInt(individual.charAt(swapIndex1)+"");
                            tempIndividual = ((individual.substring(0,swapIndex1)
                                    +individual.substring(swapIndex1+1,individual.length())).substring(0,swapIndex1)
                                    +individual.charAt(swapIndex2)
                                    +individual.substring(swapIndex1+1,individual.length()));
                            individual = tempIndividual.substring(0,swapIndex2)+temp+tempIndividual.substring(swapIndex2+1,tempIndividual.length());
                        }
                    }
                }
            }
        }
        return individual;
    }
    public String getRow (String individual , int row){
        int rowStartingIndex = row*3;
        if(row > 2 && row < 6){
            rowStartingIndex += 18;
        }else if (row > 5){
            rowStartingIndex += 36;
        }
        return individual.charAt(rowStartingIndex)+""
                +individual.charAt(rowStartingIndex+1)+""
                +individual.charAt(rowStartingIndex+2)+""
                +individual.charAt(rowStartingIndex+9)+""
                +individual.charAt(rowStartingIndex+10)+""
                +individual.charAt(rowStartingIndex+11)+""
                +individual.charAt(rowStartingIndex+18)+""
                +individual.charAt(rowStartingIndex+19)+""
                +individual.charAt(rowStartingIndex+20)+"";
    }

    private String getColumn (String individual, int column){
        int columnStartingIndex = column;
        if(column > 2 && column < 6){
            columnStartingIndex += 6;
        }else if (column > 5){
            columnStartingIndex += 12;
        }
        return individual.charAt(columnStartingIndex)+""
                +individual.charAt(columnStartingIndex+3)+""
                +individual.charAt(columnStartingIndex+6)+""
                +individual.charAt(columnStartingIndex+27)+""
                +individual.charAt(columnStartingIndex+30)+""
                +individual.charAt(columnStartingIndex+33)+""
                +individual.charAt(columnStartingIndex+54)+""
                +individual.charAt(columnStartingIndex+57)+""
                +individual.charAt(columnStartingIndex+60)+"";
    }
    private int findNumberOfOccurances(String string, int number){
        int result = 0;
        for(int i = 0 ; i < string.length() ; i++){
            if(Integer.parseInt(string.charAt(i)+"")==number){
                result++;
            }
        }
        return result;
    }
    public String generateSudoku(String openSudoku, int numberOfEmptyCells){
        for(int i = 0 ; i < numberOfEmptyCells ; i++){
            int indexOfEmptyCell = random.nextInt(81);
            openSudoku = openSudoku.substring(0,indexOfEmptyCell)
                    +"0"+openSudoku.substring(indexOfEmptyCell+1,openSudoku.length());
        }
        return openSudoku;
    }
    public boolean backTrack (String sudokuStrink){
        if(sudokuString==null){
            this.sudokuString = sudokuStrink;
            boolean stop  = true;
            while(stop){
                stop = false;
                for(int i = 0 ; i < 81 ; i++){
                    if(sudokuString.charAt(i)=='0'){
                        int segment = i/9;
                        int currentColumn = 0;
                        int currentRow = 0;
                        int rowOffset = 0;
                        if(segment%3==0){
                            currentColumn = (i - segment * 9)%3;
                        }else if(segment%3==1){
                            currentColumn = (i - segment * 9)%3 + 3;
                        }else{
                            currentColumn = (i - segment * 9)%3 + 6;
                        }
                        if(segment > 2 && segment < 6){
                            rowOffset += 3;
                        }else if(segment > 5){
                            rowOffset += 6;
                        }
                        if(i - segment*9 < 3){
                            currentRow = rowOffset;
                        }else if(i - segment*9 < 6){
                            currentRow= rowOffset + 1;
                        }else{
                            currentRow = rowOffset + 2;
                        }
                        String currentColumnString = getColumn(sudokuString,currentColumn);
                        String currentRowString = getRow(sudokuString,currentRow);
                        String currentSegmentString = sudokuString.substring(segment*9,(segment*9)+9);
                        HashSet <Integer> numbers = new HashSet<Integer>();
                        for(int j = 0 ; j < 9 ; j++){
                            numbers.add(Integer.parseInt(currentColumnString.charAt(j)+""));
                            numbers.add(Integer.parseInt(currentRowString.charAt(j)+""));
                            numbers.add(Integer.parseInt(currentSegmentString.charAt(j)+""));
                        }
                        numbers.remove(0);
                        HashSet <Integer> missingNumbers = new HashSet<Integer>();
                        missingNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                        missingNumbers.removeAll(numbers);
                        if(missingNumbers.size()==1){
                            int missingNumber = missingNumbers.toArray(new Integer[0])[0];
                            sudokuString = sudokuString.substring(0,i)+
                                    missingNumber+
                                    sudokuString.substring(i+1,sudokuString.length());
                            stop = true;
                        }
                    }
                }
                if(!stop){
                    for(int i = 0 ; i < 81 ; i++){
                        if(sudokuString.charAt(i)=='0'){
                            int segment = i/9;
                            int currentColumn = 0;
                            int currentRow = 0;
                            int rowOffset = 0;
                            if(segment%3==0){
                                currentColumn = (i - segment * 9)%3;
                            }else if(segment%3==1){
                                currentColumn = (i - segment * 9)%3 + 3;
                            }else{
                                currentColumn = (i - segment * 9)%3 + 6;
                            }
                            if(segment > 2 && segment < 6){
                                rowOffset += 3;
                            }else if(segment > 5){
                                rowOffset += 6;
                            }
                            if(i - segment*9 < 3){
                                currentRow = rowOffset;
                            }else if(i - segment*9 < 6){
                                currentRow= rowOffset + 1;
                            }else{
                                currentRow = rowOffset + 2;
                            }
                            String currentColumnString = getColumn(sudokuString,currentColumn);
                            String currentRowString = getRow(sudokuString,currentRow);
                            String currentSegmentString = sudokuString.substring(segment*9,(segment*9)+9);
                            HashSet <Integer> numbers = new HashSet<Integer>();
                            for(int j = 0 ; j < 9 ; j++){
                                numbers.add(Integer.parseInt(currentColumnString.charAt(j)+""));
                                numbers.add(Integer.parseInt(currentRowString.charAt(j)+""));
                                numbers.add(Integer.parseInt(currentSegmentString.charAt(j)+""));
                            }
                            numbers.remove(0);
                            HashSet <Integer> missingNumbers = new HashSet<Integer>();
                            missingNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                            missingNumbers.removeAll(numbers);
                            candidates.put(i,missingNumbers);
                            candidatesWithRowAndColumn.put(currentRow+""+currentColumn,missingNumbers);
                        }
                    }
                }
            }
        }
        if(!sudokuString.contains("0")){
            return true;
        }
        int [] rowColumn = new int [3];
        if(!findEmpty(rowColumn)){
            return true;
        }
        for(int i = 1 ; i < 10 ; i++){
            String currentColumnString = getColumn(sudokuString,rowColumn[1]);
            String currentRowString = getRow(sudokuString,rowColumn[0]);
            String currentSegmentString = sudokuString.substring(rowColumn[2]*9,(rowColumn[2]*9)+9);
            HashSet <Integer> numbers = new HashSet<Integer>();
            for(int j = 0 ; j < 9 ; j++){
                numbers.add(Integer.parseInt(currentColumnString.charAt(j)+""));
                numbers.add(Integer.parseInt(currentRowString.charAt(j)+""));
                numbers.add(Integer.parseInt(currentSegmentString.charAt(j)+""));
            }
            numbers.remove(0);
            HashSet <Integer> missingNumbers = new HashSet<Integer>();
            missingNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            missingNumbers.removeAll(numbers);
            if(missingNumbers.contains(i)){
                int rowStartingIndex = rowColumn[0]*3;
                if(rowColumn[0] > 2 && rowColumn[0] < 6){
                    rowStartingIndex += 18;
                }else if (rowColumn[0] > 5){
                    rowStartingIndex += 36;
                }
                int columnStartingIndex = rowColumn[1];
                if(rowColumn[1] > 2 && rowColumn[1] < 6){
                    columnStartingIndex += 6;
                }else if (rowColumn[1] > 5){
                    columnStartingIndex += 12;
                }
                int index = rowStartingIndex + columnStartingIndex;
                sudokuString = sudokuString.substring(0,index)+i+sudokuString.substring(index+1,sudokuString.length());
                if(backTrack(sudokuString)){
                    //System.out.print("\r");
                    //System.out.println(sudokuString);
                    return true;
                }else{
                    sudokuString = sudokuString.substring(0,index)+0+sudokuString.substring(index+1,sudokuString.length());
                }
            }
        }
        //System.out.println(sudokuString);
        return false;
    }

    public boolean findEmpty(int [] rowColumn){
        int i = sudokuString.indexOf('0');
        if(i==-1){
            return false;
        }
        int segment = i/9;
        int currentColumn = 0;
        int currentRow = 0;
        int rowOffset = 0;
        if(segment%3==0){
            currentColumn = (i - segment * 9)%3;
        }else if(segment%3==1){
            currentColumn = (i - segment * 9)%3 + 3;
        }else{
            currentColumn = (i - segment * 9)%3 + 6;
        }
        if(segment > 2 && segment < 6){
            rowOffset += 3;
        }else if(segment > 5){
            rowOffset += 6;
        }
        if(i - segment*9 < 3){
            currentRow = rowOffset;
        }else if(i - segment*9 < 6){
            currentRow= rowOffset + 1;
        }else{
            currentRow = rowOffset + 2;
        }
        rowColumn[0] = currentRow;
        rowColumn[1] = currentColumn;
        rowColumn[2] = segment;
        return true;
    }
    public void printSudokustring(String sudokuString){
        for(int i = 0 ; i < 9 ; i++){
           // System.out.println(getRow(sudokuString,i));
        }
    }
}
