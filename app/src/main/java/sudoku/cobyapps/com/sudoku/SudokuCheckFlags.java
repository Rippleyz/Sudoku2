package sudoku.cobyapps.com.sudoku;

import sudoku.cobyapps.com.sudoku.SudokuSolver.SudokuUtils;

/**
 * Flags which are used in conjunction with the {@link SudokuUtils}
 * <code>checkX()</code> family of functions to test whether a row is solved,
 * unsolved or broken.
 */
public enum SudokuCheckFlags {
	ERROR, UNSOLVED, SOLVED
}
