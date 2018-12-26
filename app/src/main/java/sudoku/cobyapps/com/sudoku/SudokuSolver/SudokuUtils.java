package sudoku.cobyapps.com.sudoku.SudokuSolver;

import java.io.PrintStream;

import sudoku.cobyapps.com.sudoku.SudokuCheckFlags;
import sudoku.cobyapps.com.sudoku.SudokuException;


/**
 * Utility methods useful for working with Sudoku.
 */
public final class SudokuUtils {

	/*
	 * Constant that provides a fast way to access cells by region, row and 
	 *  column number
	 */
	public static final byte[][] SUDOKU_GRID_REGION_MAP = new byte[9][9];
	public static final byte[][] SUDOKU_GRID_ROW_MAP = new byte[9][9];
	public static final byte[][] SUDOKU_GRID_COLUMN_MAP = new byte[9][9];

	static {
		for (int region = 0; region < 9; ++region) {
			final int startingRow = (region / 3) * 3;
			final int startingCol = (region % 3) * 3;
			for (int j = 0; j < 3; ++j) {
				final int row = startingRow + j;
				for (int i = 0; i < 3; ++i) {
					final int column = startingCol + i;
					final int offset = (row * 9) + column;
					SUDOKU_GRID_REGION_MAP[region][j*3+i] = (byte)offset;
				}
			}
		}
		for (int j = 0; j < 9; ++j) {
			for (int i = 0; i < 9; i++) {
				SUDOKU_GRID_ROW_MAP[j][i] = (byte)(9*j+i);
				SUDOKU_GRID_COLUMN_MAP[j][i] = (byte)(j + 9*i);
			}
		}
	}

	/**
	 * Returns whether or not the puzzle is complete.
	 *
	 * @param puzzle the puzzle. Must be valid
	 * @return
	 */
	public static final boolean isPuzzleComplete(byte[] puzzle) {
		if (!isPuzzleLegal(puzzle)) {
			throw new SudokuException("Puzzle is not legal");
		}
		return (countGivens(puzzle) == 81);
	}

	/**
	 * Tells whether or not the supplied puzzle is a valid Sudoku; valid means
	 * that any filled cells contain a value between 1 and 9, and that no
	 * column, row or region contains the same digit more than once.
	 *
	 * @param puzzle the puzzle to check
	 * @return a Boolean value indicating whether or not the puzzle is legal
	 */
	public static final boolean isPuzzleLegal(byte[] puzzle) {
		boolean isLegal = true;
		for (int i = 0; i < 9; ++i) {
			if ((checkColumn(puzzle, i) == SudokuCheckFlags.ERROR)
					|| (checkRow(puzzle, i) == SudokuCheckFlags.ERROR)
					|| (checkRegion(puzzle, i) == SudokuCheckFlags.ERROR)) {
				isLegal = false;
				break;
			}
		}
		return isLegal;
	}

	/**
	 * Scan a puzzle region to determine whether it is complete, incomplete
	 * or broken.
	 *
	 * @param puzzle the puzzle to check
	 * @param region the region number (0..8)
	 * @return the check result
	 */
	public static final SudokuCheckFlags checkRegion(byte[] puzzle, int region) {
		final int counters[] = new int[10];   // empty, plus 1..9
		for (int i = 0; i < 9; ++i) {
			final byte val = puzzle[SUDOKU_GRID_REGION_MAP[region][i]];
			if ((val < 0) || (val > 9)) {
				return SudokuCheckFlags.ERROR;
			}
			counters[val]++;
		}

		return scanPuzzleCheckCounters(counters);
	}

	/**
	 * Scan a puzzle row to determine whether it is complete, incomplete
	 * or broken.
	 *
	 * @param puzzle the puzzle to check
	 * @param row the row number (0..8)
	 * @return the check result
	 */
	public static final SudokuCheckFlags checkRow(byte[] puzzle, int row) {
		final int counters[] = new int[10];   // empty, plus 1..9
		for (int i = 0; i < 9; ++i) {
			final byte val = puzzle[row*9+i];
			if ((val < 0) || (val > 9)) {
				return SudokuCheckFlags.ERROR;
			}
			counters[val]++;
		}

		return scanPuzzleCheckCounters(counters);
	}

	/**
	 * Scan a puzzle column to determine whether it is complete, incomplete
	 * or broken.
	 *
	 * @param puzzle the puzzle to check
	 * @param column the column number (0..8)
	 * @return the check result
	 */
	public static final SudokuCheckFlags checkColumn(byte[] puzzle, int column) {
		final int counters[] = new int[10];   // empty, plus 1..9
		for (int i = 0; i < 9; ++i) {
			final byte val = puzzle[i*9+column];
			if ((val < 0) || (val > 9)) {
				return SudokuCheckFlags.ERROR;
			}
			counters[val]++;
		}

		return scanPuzzleCheckCounters(counters);
	}

	private static final SudokuCheckFlags scanPuzzleCheckCounters(int[] flags) {
		SudokuCheckFlags result = SudokuCheckFlags.SOLVED;
		for (int i = 1; i <= 9; ++i) {
			if (flags[i] == 0) {
				result = SudokuCheckFlags.UNSOLVED;
			} else if (flags[i] > 1) {
				result = SudokuCheckFlags.ERROR;
				break;
			}
		}
		return result;
	}

	/**
	 * Count the givens in the puzzle.
	 *
	 * @param puzzle the puzzle whose givens are to be counted
	 * @return number of givens
	 */
	public static final int countGivens(byte[] puzzle) {
		int count = 0;
		for (int i = 0; i < 81; ++i) {
			count += (puzzle[i] > 0 ? 1 : 0);
		}
		return count;
	}

	/**
	 * Prints out a puzzle to standard output.
	 * @param result a result generated by DLX.
	 */
	public static final void printPuzzle(PrintStream out, byte[] result) {
		for (int j = 0; j < 9; ++j) {
			if (j % 3 == 0)
				printHDiv(out);
			for (int i = 0; i < 9; ++i) {
				if (i % 3 == 0)
					out.print("| ");
				out.print(result[9*j+i]);
				out.print(' ');
			}
			out.println("|");
		}
		printHDiv(out);
	}

	private static final void printHDiv(PrintStream out) {
		out.println("+-------+-------+-------+");
	}

}