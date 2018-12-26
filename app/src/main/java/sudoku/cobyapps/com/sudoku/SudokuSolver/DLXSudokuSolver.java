package sudoku.cobyapps.com.sudoku.SudokuSolver;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sudoku.cobyapps.com.sudoku.dlx.DLX;
import sudoku.cobyapps.com.sudoku.dlx.DLXResult;
import sudoku.cobyapps.com.sudoku.dlx.DLXResultProcessor;
import sudoku.cobyapps.com.sudoku.dlx.data.ColumnObject;

    public class DLXSudokuSolver implements SudokuSolver {
		private static final int BUF_SIZE = 324;
		private static final int COLUMN_CONTAINS_OFFSET = 81;
		private static final int ROW_CONTAINS_OFFSET = 162;
		private static final int REGION_CONTAINS_OFFSET = 243;
		private static Object[] LABELS;

		public static int getSolutionSpaceLimit() {
			return solutionSpaceLimit;
		}

		private static int solutionSpaceLimit = 0;
		private HashMap<Integer, Set<Byte>> candidates;
		private static final boolean IS_SINGLE_CANDIDATES_ENABLED = true;
		private static final boolean IS_HIDDEN_SINGLES_ENABLED = true;
		private static final boolean IS_POINTING_ENABLED = true;
		private static final boolean IS_CLAIMING_ENABLED = true;
		private static final boolean IS_NAKED_SUBSETS_ENABLED = true;
		private static final boolean IS_X_WINGS_ENABLED = true;
		private static final boolean IS_FINNED_X_WINGS_ENABLED = false;
		private static final boolean IS_SWORDFISH_ENABLED = false;
		private static final boolean IS_XY_WINGS_ENABLED = false;
		private static final int SINGLE_CANDIDATE_DIFFICULTY_COEFFICIENT = 1;
		private static final int HIDDEN_SINGLES_DIFFICULTY_COEFFICIENT = 5;
		private static final int POINTING_DIFFICULTY_COEFFICIENT = 8;
		private static final int CLAIMING_DIFFICULTY_COEFFICIENT = 12;
		private static final int NAKED_SUBSETS_DIFFICULTY_COEFFICIENT = 30;
		private static final int X_WINGS_DIFFICULTY_COEFFICIENT = 50;
		private static final int FINNED_X_WINGS_DIFFICULTY_COEFFICIENT = 80;
		private static final int SWORDFISH_DIFFICULTY_COEFFICIENT = 90;
		private static final int XY_WINGS_DIFFICULTY_COEFFICIENT = 150;
		private static final int INSUFFICIENT_ALGORITHMS_DIFFICULTY_COEFFICIENT = 300;
		public DLX getDlx() {
			return dlx;
		}

		public void setDlx(DLX dlx) {
			this.dlx = dlx;
		}

		private DLX dlx;
		public DLXSudokuSolver() {
			LABELS = generateLabels();
			candidates = new HashMap<Integer, Set<Byte>>();
			for (int i = 0; i < 81; i++) {
				candidates.put(i, new HashSet<Byte>());
			}
			dlx = new DLX();
		}

		public DLXSudokuSolver(int solutionSpaceLimit) {
			LABELS = generateLabels();
			this.solutionSpaceLimit = solutionSpaceLimit;
			candidates = new HashMap<Integer, Set<Byte>>();
			for (int i = 0; i < 81; i++) {
				candidates.put(i, new HashSet<Byte>());
			}
			dlx = new DLX();
		}
		public boolean isValidSudoku(byte[] puzzle) {

			if (!SudokuUtils.isPuzzleLegal(puzzle))
				return false;
			if (SudokuUtils.isPuzzleComplete(puzzle))
				return true;
			CountingOnlyDLXResultProcessor resultProcessor =
					new CountingOnlyDLXResultProcessor();
			solve(puzzle, resultProcessor);

			return (resultProcessor.getNumSolutions() == 1);
		}
		class CountingOnlyDLXResultProcessor implements DLXResultProcessor {
			private int numSolutions = 0;

			public boolean processResult(DLXResult result) {
				numSolutions++;
				return true;
			}

			public int getNumSolutions() {
				return numSolutions;
			}
		}
		public final void solve(byte[] puzzle) {
			final ColumnObject h = buildSparseArrayForPuzzle(puzzle);
			dlx.solve(h, true);
		}
		public final void solve(byte[] puzzle,
								DLXResultProcessor processor) {
			final ColumnObject h = buildSparseArrayForPuzzle(puzzle);
			dlx.solve(h, true, processor);
		}
		public final void printPuzzle(PrintStream out, DLXResult result) {
			byte[] puzzle = new byte[81];
			puzzle = decodeDLXResult(result);
			SudokuUtils.printPuzzle(out, puzzle);
		}
		public static final byte[] decodeDLXResult(DLXResult result) {
			final Iterator<List<Object>> rows = result.rows();
			final byte[] puzzle = new byte[81];
			while (rows.hasNext()) {
				final List<Object> row = rows.next();
				byte r = -1; // canary values
				byte c = -1;
				byte v = -1;
				for (final Object obj : row) {
					if (obj instanceof CellValueLabel) {
						final CellValueLabel cvl = (CellValueLabel) obj;
						v = cvl.value;
					} else if (obj instanceof CellCoordinatesLabel) {
						final CellCoordinatesLabel ccl =
								(CellCoordinatesLabel) obj;
						r = ccl.row;
						c = ccl.col;
					}
				}
				if ((v > -1) && (r > -1) && (c > -1)) {
					puzzle[r * 9 + c] = v;
				}
			}
			return puzzle;
		}
		public final ColumnObject buildSparseArrayForPuzzle(byte[] puzzle) {
			final byte[][] constraintSet = buildConstraintSets(puzzle);
			final ColumnObject obj = dlx.buildSparseMatrix(constraintSet, LABELS);
			sanityCheckSparseMatrix(obj);
			return obj;
		}


		final void sanityCheckSparseMatrix(ColumnObject obj) {
			ColumnObject curr = (ColumnObject) obj.R;
			while (curr != obj) {
				if (curr.size == 0) {
					System.err.println("Empty column found! " + obj.name.toString());
				}
				curr = (ColumnObject) curr.R;
			}
		}
		final byte[][] buildConstraintSets(byte[] puzzle) {
			final List<byte[]> constraintSets = new ArrayList<byte[]>(724);
			for (int j = 0; j < 9; ++j) {
				for (int i = 0; i < 9; ++i) {
					final byte curr = puzzle[j * 9 + i];
					if (curr > 0) {
						// Cell is already occupied by a 'given'
						final byte[] constraintSet = buildConstraintSet(j, i, (curr - 1));
						constraintSets.add(constraintSet);
					} else {
						// Generate all possibilities for this cell
						for (int val = 1; val <= 9; ++val) {
							if (isMoveLegal(puzzle, j, i, val)) {
								final byte[] constraintSet = buildConstraintSet(j, i, (val - 1));
								constraintSets.add(constraintSet);
							}
						}
					}
				}
			}
			final byte[][] arrays = constraintSets.toArray(
					new byte[][]{});
			return arrays;
		}
		final byte[] buildConstraintSet(int row, int column, int value) {

			final byte[] buffer = new byte[BUF_SIZE];
			buffer[((row * 9) + column)] = 1;

			buffer[COLUMN_CONTAINS_OFFSET + ((column * 9) + value)] = 1;

			buffer[ROW_CONTAINS_OFFSET + ((row * 9) + value)] = 1;

			final int region = getRegion(row, column);
			buffer[REGION_CONTAINS_OFFSET + ((region * 9) + value)] = 1;

			return buffer;
		}

		final int getRegion(int row, int column) {
			return (3 * (row / 3) + (column / 3));
		}

		final Object[] generateLabels() {
			final ColumnLabel[] labels = new ColumnLabel[BUF_SIZE];

			for (int i = 0; i < 9; ++i) {
				for (int j = 0; j < 9; ++j) {
					labels[i * 9 + j] = new CellCoordinatesLabel((byte) i, (byte) j);
				}
			}
			for (int i = 0; i < 9; ++i) {
				for (int j = 0; j < 9; ++j) {
					labels[COLUMN_CONTAINS_OFFSET + (i * 9 + j)] =
							new ColumnValueLabel((byte) (j + 1));
				}
			}
			for (int i = 0; i < 9; ++i) {
				for (int j = 0; j < 9; ++j) {
					labels[ROW_CONTAINS_OFFSET + (i * 9 + j)] =
							new RowValueLabel((byte) (j + 1));
				}
			}
			for (int i = 0; i < 9; ++i) {
				for (int j = 0; j < 9; ++j) {
					labels[REGION_CONTAINS_OFFSET + (i * 9 + j)] =
							new RegionValueLabel((byte) (j + 1));
				}
			}

			return labels;
		}

		final boolean isMoveLegal(byte[] puzzle, int row, int col, int value) {

			final boolean result = true;
			final int region = getRegion(row, col);
			for (int i = 0; i < 9; ++i) {
				if ((puzzle[row * 9 + i] == value) || (puzzle[i * 9 + col] == value)
						|| (puzzle[SudokuUtils.SUDOKU_GRID_REGION_MAP[region][i]]
						== value)) {
					return false;
				}
			}
			return result;
		}

		public int rateSudoku(Byte[] sudoku, Byte [] solution) throws Exception {
			for (int i = 0; i < 81; i++) {
				candidates.get(i).clear();
			}
			int difficultyScore = 0;
			boolean hasCandidatesSet = false;
			while (true) {
				if (!hasCandidatesSet) {
					for (int row = 0; row < 9; row++) {
						for (int column = 0; column < 9; column++) {
							if (sudoku[((row * 9) + column)] == 0) {
								Byte[] currentRow = getRow(row, sudoku);
								Byte[] currentColumn = getColumn(column, sudoku);
								Byte[] currentSegment = getSegment(row, column, sudoku);
								Set<Byte> missingNumbers = new HashSet<Byte>();
								Set<Byte> numbers = new HashSet<Byte>();
								missingNumbers.addAll(Arrays.asList(new Byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));
								numbers.addAll(Arrays.asList(currentRow));
								numbers.addAll(Arrays.asList(currentColumn));
								numbers.addAll(Arrays.asList(currentSegment));
								missingNumbers.removeAll(numbers);
								missingNumbers.remove(0);
								candidates.put(((row * 9) + column), missingNumbers);
							}
						}
					}
					hasCandidatesSet = true;
				}
				boolean proceedFurther = true;
				while (proceedFurther && IS_SINGLE_CANDIDATES_ENABLED) { // SINGLE CANDIDATES
					proceedFurther = false;
					for (int i = 0; i < candidates.size(); i++) {
						Set<Byte> currentCandidates = candidates.get(i);
						if (currentCandidates != null && currentCandidates.size() == 1) {
							byte numberToBePlaced = currentCandidates.toArray(new Byte[0])[0];
							sudoku[i] = numberToBePlaced;
							difficultyScore += SINGLE_CANDIDATE_DIFFICULTY_COEFFICIENT;
							candidates.get(i).clear();
							eliminateCandidatesInAffectedIndices(i, numberToBePlaced);
							proceedFurther = true;
							break;
						}
					}
				}
				proceedFurther = true;
				if (Arrays.toString(sudoku).contains("0") && IS_HIDDEN_SINGLES_ENABLED) {// HIDDEN SINGLES
					loop:
					for (int l = 0; l < 9; l++) {
						ArrayList<Set<Byte>> candidateSetsForRow = new ArrayList<Set<Byte>>();
						ArrayList<Set<Byte>> candidateSetsForColumn = new ArrayList<Set<Byte>>();
						ArrayList<Set<Byte>> candidateSetsForSegment = new ArrayList<Set<Byte>>();
						for (int row = l * 9; row < (l * 9) + 9; row++) {
							Set<Byte> currentCandidates = candidates.get(row);
							candidateSetsForRow.add(currentCandidates);
						}
						for (int i = 0; i < 9; i++) {
							if (candidateSetsForRow.get(i).size() != 0) {
								Set<Byte> candidatesSetToBeChecked = candidateSetsForRow.get(i);
								candidateSetsForRow.remove(i);
								Set<Byte> unionOfOtherCandidates = new HashSet<Byte>();
								for (int k = 0; k < 8; k++) {
									unionOfOtherCandidates.addAll(candidateSetsForRow.get(k));
								}
								Set<Byte> candidatesSetToBeCheckedTemp = new HashSet<Byte>();
								candidatesSetToBeCheckedTemp.addAll(candidatesSetToBeChecked);
								candidatesSetToBeChecked.removeAll(unionOfOtherCandidates);
								if (candidatesSetToBeChecked.size() == 1) {
									byte numberToBePlaced = candidatesSetToBeChecked.toArray(new Byte[0])[0];
									sudoku[(l * 9) + i] = numberToBePlaced;
									difficultyScore += HIDDEN_SINGLES_DIFFICULTY_COEFFICIENT;
									candidates.get((l * 9) + i).clear();
									proceedFurther = false;
									eliminateCandidatesInAffectedIndices((l * 9) + i, numberToBePlaced);
									break loop;
								}
								candidatesSetToBeChecked.addAll(candidatesSetToBeCheckedTemp);
								candidateSetsForRow.add(i, candidatesSetToBeChecked);
							}
						}
						for (int i = l; i <= l + 72; i += 9) {
							Set<Byte> currentCandidates = candidates.get(i);
							candidateSetsForColumn.add(currentCandidates);
						}
						for (int i = 0; i < 9; i++) {
							if (candidateSetsForColumn.get(i).size() != 0) {
								Set<Byte> candidatesSetToBeChecked = candidateSetsForColumn.get(i);
								candidateSetsForColumn.remove(i);
								Set<Byte> unionOfOtherCandidates = new HashSet<Byte>();
								for (int j = 0; j < 8; j++) {
									unionOfOtherCandidates.addAll(candidateSetsForColumn.get(j));
								}
								Set<Byte> candidatesSetToBeCheckedTemp = new HashSet<Byte>();
								candidatesSetToBeCheckedTemp.addAll(candidatesSetToBeChecked);
								candidatesSetToBeChecked.removeAll(unionOfOtherCandidates);
								if (candidatesSetToBeChecked.size() == 1) {
									byte numberToBePlaced = candidatesSetToBeChecked.toArray(new Byte[0])[0];
									sudoku[l + (i * 9)] = candidatesSetToBeChecked.toArray(new Byte[0])[0];
									difficultyScore += HIDDEN_SINGLES_DIFFICULTY_COEFFICIENT;
									proceedFurther = false;
									eliminateCandidatesInAffectedIndices(l + (i * 9), numberToBePlaced);
									break loop;
								}
								candidatesSetToBeChecked.addAll(candidatesSetToBeCheckedTemp);
								candidateSetsForColumn.add(i, candidatesSetToBeChecked);
							}
						}

						for (int i = 0; i < 9; i++) {
							int offset;
							if (i < 3) {
								offset = 0;
							} else if (i < 6) {
								offset = 9;
							} else {
								offset = 18;
							}
							int lOffset;
							if (l < 3) {
								lOffset = 0;
							} else if (l < 6) {
								lOffset = 27;
							} else {
								lOffset = 54;
							}
							Set<Byte> currentCandidates = candidates.get(lOffset + (l % 3) * 3 + offset + i % 3);
							candidateSetsForSegment.add(currentCandidates);
						}
						for (int i = 0; i < 9; i++) {
							if (candidateSetsForSegment.get(i).size() != 0) {
								Set<Byte> candidatesSetToBeChecked = candidateSetsForSegment.get(i);
								candidateSetsForSegment.remove(i);
								Set<Byte> unionOfOtherCandidates = new HashSet<Byte>();
								for (int k = 0; k < 8; k++) {
									unionOfOtherCandidates.addAll(candidateSetsForSegment.get(k));
								}
								Set<Byte> candidatesSetToBeCheckedTemp = new HashSet<Byte>();
								candidatesSetToBeCheckedTemp.addAll(candidatesSetToBeChecked);
								candidatesSetToBeChecked.removeAll(unionOfOtherCandidates);
								if (candidatesSetToBeChecked.size() == 1) {
									byte numberToBePlaced = candidatesSetToBeChecked.toArray(new Byte[0])[0];
									int offset;
									int iOffset;
									if (l < 3) {
										offset = 0;
									} else if (l < 6) {
										offset = 27;
									} else {
										offset = 54;
									}
									if (i < 3) {
										iOffset = 0;
									} else if (i < 6) {
										iOffset = 9;
									} else {
										iOffset = 18;
									}
									sudoku[offset + (l % 3) * 3 + iOffset + i % 3] = numberToBePlaced;
									difficultyScore += HIDDEN_SINGLES_DIFFICULTY_COEFFICIENT;
									candidates.get(offset + (l % 3) * 3 + iOffset + i % 3).clear();
									proceedFurther = false;
									eliminateCandidatesInAffectedIndices(offset + (l % 3) * 3 + iOffset + i % 3, numberToBePlaced);
									break loop;
								}
								candidatesSetToBeChecked.addAll(candidatesSetToBeCheckedTemp);
								candidateSetsForSegment.add(i, candidatesSetToBeChecked);
							}
						}
					}
				}


				if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_POINTING_ENABLED) { // POINTING
					loop3:
					for (int segment = 0; segment < 9; segment++) {
						int segmentOffset;
						if (segment < 3) {
							segmentOffset = 0;
						} else if (segment < 6) {
							segmentOffset = 27;
						} else {
							segmentOffset = 54;
						}
						int segmentStartingIndex = (segment % 3) * 3 + segmentOffset;
						Set<Integer> allIndices = new HashSet<Integer>();
						for (int i = 0; i < 9; i++) {
							int offset;
							if (i < 3) {
								offset = 0;
							} else if (i < 6) {
								offset = 9;
							} else {
								offset = 18;
							}
							allIndices.add(segmentStartingIndex + offset + i % 3);
						}
						for (int i = 0; i < 3; i++) {
							Set<Integer> rowRemainingIndices = new HashSet<Integer>();
							Set<Integer> columnRemainingIndices = new HashSet<Integer>();
							rowRemainingIndices.addAll(allIndices);
							columnRemainingIndices.addAll(allIndices);
							int rowStartingIndex = segmentStartingIndex + i * 9;
							int columnStartingIndex = segmentStartingIndex + i;
							Set<Byte> rowCandidates = new HashSet<Byte>();
							Set<Byte> columnCandidates = new HashSet<Byte>();
							for (int j = 0; j < 3; j++) {
								rowCandidates.addAll(candidates.get(rowStartingIndex + j));
								rowRemainingIndices.remove(rowStartingIndex + j);
								columnCandidates.addAll(candidates.get(columnStartingIndex + (9 * j)));
								columnRemainingIndices.remove(columnStartingIndex + (9 * j));
							}
							if (rowCandidates.size() > 0) {
								Set<Byte> unionOfRemainingIndices = new HashSet<Byte>();
								Integer[] rowRemainingIndicesArray = rowRemainingIndices.toArray(new Integer[0]);
								for (int j = 0; j < rowRemainingIndicesArray.length; j++) {
									unionOfRemainingIndices.addAll(candidates.get(rowRemainingIndicesArray[j]));
								}
								rowCandidates.removeAll(unionOfRemainingIndices);
								if (rowCandidates.size() > 0) {
									boolean hasCandidatesBeenRemoved = false;
									if (segment % 3 == 0) {
										for (int j = segmentStartingIndex + 3; j <= segmentStartingIndex + 8; j++) {
											Set<Byte> currentCandidates = candidates.get(j + (i * 9));
											Byte[] rowCandidatesArray = rowCandidates.toArray(new Byte[0]);
											for (int k = 0; k < rowCandidatesArray.length; k++) {
												if (currentCandidates.contains(rowCandidatesArray[k])) {
													currentCandidates.remove(rowCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									} else if (segment % 3 == 1) {
										for (int j = 0; j < 3; j++) {
											Set<Byte> currentCandidatesRightHandSide = candidates.get((segmentStartingIndex + 3 + j) + i * 9);
											Set<Byte> currentCandidatesLeftHandSide = candidates.get((segmentStartingIndex - j - 1) + i * 9);
											Byte[] rowCandidatesArray = rowCandidates.toArray(new Byte[0]);
											for (int k = 0; k < rowCandidatesArray.length; k++) {
												if (currentCandidatesRightHandSide.contains(rowCandidatesArray[k])) {
													currentCandidatesRightHandSide.remove(rowCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
												if (currentCandidatesLeftHandSide.contains(rowCandidatesArray[k])) {
													currentCandidatesLeftHandSide.remove(rowCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									} else {
										for (int j = 0; j < 6; j++) {
											Set<Byte> currentCandidates = candidates.get((segmentStartingIndex - 1 - j) + i * 9);
											Byte[] rowCandidatesArray = rowCandidates.toArray(new Byte[0]);
											for (int k = 0; k < rowCandidatesArray.length; k++) {
												if (currentCandidates.contains(rowCandidatesArray[k])) {
													currentCandidates.remove(rowCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									}
									if (hasCandidatesBeenRemoved) {
										difficultyScore += POINTING_DIFFICULTY_COEFFICIENT;
										proceedFurther = false;
										break loop3;
									}
								}
							}
							if (columnCandidates.size() > 0) {
								Set<Byte> unionOfRemainingIndices = new HashSet<Byte>();
								Integer[] columnRemainingIndicesArray = columnRemainingIndices.toArray(new Integer[0]);
								for (int j = 0; j < columnRemainingIndicesArray.length; j++) {
									unionOfRemainingIndices.addAll(candidates.get(columnRemainingIndicesArray[j]));
								}
								columnCandidates.removeAll(unionOfRemainingIndices);
								if (columnCandidates.size() > 0) {
									boolean hasCandidatesBeenRemoved = false;
									if (segment < 3) {
										for (int j = segmentStartingIndex + 27; j <= segmentStartingIndex + 72; j += 9) {
											Set<Byte> currentCandidates = candidates.get(j + i);
											Byte[] columnCandidatesArray = columnCandidates.toArray(new Byte[0]);
											for (int k = 0; k < columnCandidatesArray.length; k++) {
												if (currentCandidates.contains(columnCandidatesArray[k])) {
													currentCandidates.remove(columnCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									} else if (segment < 6) {
										for (int j = 0; j < 3; j++) {
											Set<Byte> currentCandidatesDownSide = candidates.get(segmentStartingIndex + 27 + (j * 9) + i);
											Set<Byte> currentCandidatesUpSide = candidates.get(segmentStartingIndex - 9 - (j * 9) + i);
											Byte[] columnCandidatesArray = columnCandidates.toArray(new Byte[0]);
											for (int k = 0; k < columnCandidatesArray.length; k++) {
												if (currentCandidatesDownSide.contains(columnCandidatesArray[k])) {
													currentCandidatesDownSide.remove(columnCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
												if (currentCandidatesUpSide.contains(columnCandidatesArray[k])) {
													currentCandidatesUpSide.remove(columnCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									} else {
										for (int j = 0; j < 6; j++) {
											Set<Byte> currentCandidates = candidates.get(((segmentStartingIndex - 9) - (j * 9) + i));
											Byte[] columnCandidatesArray = columnCandidates.toArray(new Byte[0]);
											for (int k = 0; k < columnCandidatesArray.length; k++) {
												if (currentCandidates.contains(columnCandidatesArray[k])) {
													currentCandidates.remove(columnCandidatesArray[k]);
													hasCandidatesBeenRemoved = true;
												}
											}
										}
									}
									if (hasCandidatesBeenRemoved) {
										difficultyScore += POINTING_DIFFICULTY_COEFFICIENT;
										proceedFurther = false;
										break loop3;
									}
								}
							}
						}
					}
				}
				if (Arrays.toString(sudoku).contains("0") && IS_CLAIMING_ENABLED) { //CLAIMING
					loop4:
					for (int segment = 0; segment < 9; segment++) {
						int segmentOffset;
						if (segment < 3) {
							segmentOffset = 0;
						} else if (segment < 6) {
							segmentOffset = 27;
						} else {
							segmentOffset = 54;
						}
						int segmentStartingIndex = (segment % 3) * 3 + segmentOffset;
						Set<Integer> allIndices = new HashSet<Integer>();
						for (int i = 0; i < 9; i++) {
							int offset;
							if (i < 3) {
								offset = 0;
							} else if (i < 6) {
								offset = 9;
							} else {
								offset = 18;
							}
							allIndices.add(segmentStartingIndex + offset + i % 3);
						}
						for (int i = 0; i < 3; i++) {
							Set<Integer> rowRemainingIndices = new HashSet<Integer>();
							Set<Integer> columnRemainingIndices = new HashSet<Integer>();
							rowRemainingIndices.addAll(allIndices);
							columnRemainingIndices.addAll(allIndices);
							int rowStartingIndex = segmentStartingIndex + i * 9;
							int columnStartingIndex = segmentStartingIndex + i;
							Set<Byte> rowCandidates = new HashSet<Byte>();
							Set<Byte> columnCandidates = new HashSet<Byte>();
							for (int j = 0; j < 3; j++) {
								rowCandidates.addAll(candidates.get(rowStartingIndex + j));
								rowRemainingIndices.remove(rowStartingIndex + j);
								columnCandidates.addAll(candidates.get(columnStartingIndex + (9 * j)));
								columnRemainingIndices.remove(columnStartingIndex + (9 * j));
							}
							if (rowCandidates.size() > 0) {
								boolean hasCandidatesBeenRemoved = false;
								if (segment % 3 == 0) {
									for (int j = 0; j < 6; j++) {
										rowCandidates.removeAll(candidates.get((segmentStartingIndex + 3) + j + (i * 9)));
										if (rowCandidates.size() == 0) {
											break;
										}
									}
								} else if (segment % 3 == 1) {
									for (int j = 0; j < 3; j++) {
										rowCandidates.removeAll(candidates.get((segmentStartingIndex + 3) + j + (i * 9)));
										rowCandidates.removeAll(candidates.get((segmentStartingIndex - 1) - j + (i * 9)));
										if (rowCandidates.size() == 0) {
											break;
										}
									}
								} else {
									for (int j = 0; j < 6; j++) {
										rowCandidates.removeAll(candidates.get((segmentStartingIndex - 1) - j + (i * 9)));
										if (rowCandidates.size() == 0) {
											break;
										}
									}
								}
								if (rowCandidates.size() > 0) {
									Integer[] rowRemainingIndicesArray = rowRemainingIndices.toArray(new Integer[0]);
									Byte[] remainingRowCandidates = rowCandidates.toArray(new Byte[0]);
									for (int j = 0; j < rowRemainingIndicesArray.length; j++) {
										Set<Byte> currentCellCandidates = candidates.get(rowRemainingIndicesArray[j]);
										for (int k = 0; k < remainingRowCandidates.length; k++) {
											if (currentCellCandidates.contains(remainingRowCandidates[k])) {
												currentCellCandidates.remove(remainingRowCandidates[k]);
												hasCandidatesBeenRemoved = true;
											}
										}
									}
								}
								if (hasCandidatesBeenRemoved) {
									difficultyScore += CLAIMING_DIFFICULTY_COEFFICIENT;
									proceedFurther = false;
									break loop4;
								}
							}
							if (columnCandidates.size() > 0) {
								boolean hasCandidatesBeenRemoved = false;
								if (segment < 3) {
									for (int j = 0; j < 6; j++) {
										columnCandidates.removeAll(candidates.get((segmentStartingIndex + 27) + (j * 9) + i));
										if (columnCandidates.size() == 0) {
											break;
										}
									}
								} else if (segment < 6) {
									for (int j = 0; j < 3; j++) {
										columnCandidates.removeAll(candidates.get((segmentStartingIndex + 27) + (j * 9) + i));
										columnCandidates.removeAll(candidates.get((segmentStartingIndex - 9) - (j * 9) + i));
										if (columnCandidates.size() == 0) {
											break;
										}
									}
								} else {
									for (int j = 0; j < 6; j++) {
										columnCandidates.removeAll(candidates.get((segmentStartingIndex - 9) - (j * 9) + i));
										if (columnCandidates.size() == 0) {
											break;
										}
									}
								}
								if (columnCandidates.size() > 0) {
									Integer[] columnRemainingIndicesArray = columnRemainingIndices.toArray(new Integer[0]);
									Byte[] remainingColumnCandidates = columnCandidates.toArray(new Byte[0]);
									for (int j = 0; j < columnRemainingIndicesArray.length; j++) {
										Set<Byte> currentCellCandidates = candidates.get(columnRemainingIndicesArray[j]);
										for (int k = 0; k < remainingColumnCandidates.length; k++) {
											if (currentCellCandidates.contains(remainingColumnCandidates[k])) {
												currentCellCandidates.remove(remainingColumnCandidates[k]);
												hasCandidatesBeenRemoved = true;
											}
										}
									}
								}
								if (hasCandidatesBeenRemoved) {
									difficultyScore += CLAIMING_DIFFICULTY_COEFFICIENT;
									proceedFurther = false;
									break loop4;
								}
							}
						}
					}
				}
				if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_NAKED_SUBSETS_ENABLED) { // NAKED SUBSETS
					for (int i = 0; i < 9; i++) {
						HashMap<CandidateDataSet, Integer> rowCandidatesHashMap = new HashMap<CandidateDataSet, Integer>();
						HashMap<CandidateDataSet, Integer> columnCandidatesHashMap = new HashMap<CandidateDataSet, Integer>();
						HashMap<CandidateDataSet, Integer> segmentCandidatesHashMap = new HashMap<CandidateDataSet, Integer>();
						for (int j = 0; j < 9; j++) {
							int jOffset;
							int iOffset;
							if (j < 3) {
								jOffset = 0;
							} else if (j < 6) {
								jOffset = 9;
							} else {
								jOffset = 18;
							}
							if (i < 3) {
								iOffset = 0;
							} else if (i < 6) {
								iOffset = 27;
							} else {
								iOffset = 54;
							}
							CandidateDataSet currentRowCandidate = new CandidateDataSet(candidates.get((i * 9) + j));
							CandidateDataSet currentColumnCandidate = new CandidateDataSet(candidates.get((j * 9) + i));
							CandidateDataSet currentSegmentCandidate = new CandidateDataSet(candidates.get(iOffset + jOffset + (j % 3) + (i % 3) * 3));
							Integer rowCandidateCount = rowCandidatesHashMap.get(currentRowCandidate);
							Integer columnCandidateCount = columnCandidatesHashMap.get(currentColumnCandidate);
							Integer segmentCandidateCount = segmentCandidatesHashMap.get(currentSegmentCandidate);
							if (rowCandidateCount == null) {
								rowCandidateCount = 1;
							} else {
								rowCandidateCount++;
							}
							if (columnCandidateCount == null) {
								columnCandidateCount = 1;
							} else {
								columnCandidateCount++;
							}
							if (segmentCandidateCount == null) {
								segmentCandidateCount = 1;
							} else {
								segmentCandidateCount++;
							}
							columnCandidatesHashMap.put(currentColumnCandidate, columnCandidateCount);
							rowCandidatesHashMap.put(currentRowCandidate, rowCandidateCount);
							segmentCandidatesHashMap.put(currentSegmentCandidate, segmentCandidateCount);
						}
						Iterator<CandidateDataSet> iterator = rowCandidatesHashMap.keySet().iterator();
						while (iterator.hasNext()) {
							CandidateDataSet currentKey = iterator.next();
							Integer currentValue = rowCandidatesHashMap.get(currentKey);
							if (currentValue == currentKey.size()) {
								for (int j = 0; j < 9; j++) {
									Set<Byte> currentRowCandidate = candidates.get((i * 9) + j);
									if (currentKey.hashCode() != currentKey.hashCode(currentRowCandidate)) {
										Byte[] currentKeyArray = currentKey.toArray(new Byte[0]);
										for (int k = 0; k < currentKeyArray.length; k++) {
											if (currentRowCandidate.contains(currentKeyArray[k])) {
												currentRowCandidate.remove(currentKeyArray[k]);
												proceedFurther = false;
											}
										}
									}
								}
							}
						}
						if (!proceedFurther) {
							difficultyScore += NAKED_SUBSETS_DIFFICULTY_COEFFICIENT;
							break;
						}
						iterator = columnCandidatesHashMap.keySet().iterator();
						while (iterator.hasNext()) {
							CandidateDataSet currentKey = iterator.next();
							Integer currentValue = columnCandidatesHashMap.get(currentKey);
							if (currentValue == currentKey.size()) {
								for (int j = 0; j < 9; j++) {
									Set<Byte> currentColumnCandidate = candidates.get((i + (j * 9)));
									if (currentKey.hashCode() != currentKey.hashCode(currentColumnCandidate)) {
										Byte[] currentKeyArray = currentKey.toArray(new Byte[0]);
										for (int k = 0; k < currentKeyArray.length; k++) {
											if (currentColumnCandidate.contains(currentKeyArray[k])) {
												currentColumnCandidate.remove(currentKeyArray[k]);
												proceedFurther = false;
											}
										}
									}
								}
							}
						}
						if (!proceedFurther) {
							difficultyScore += NAKED_SUBSETS_DIFFICULTY_COEFFICIENT;
							break;
						}
						iterator = segmentCandidatesHashMap.keySet().iterator();
						while (iterator.hasNext()) {
							CandidateDataSet currentKey = iterator.next();
							Integer currentValue = segmentCandidatesHashMap.get(currentKey);
							if (currentValue == currentKey.size()) {
								for (int j = 0; j < 9; j++) {
									int jOffset;
									int iOffset;
									if (j < 3) {
										jOffset = 0;
									} else if (j < 6) {
										jOffset = 9;
									} else {
										jOffset = 18;
									}
									if (i < 3) {
										iOffset = 0;
									} else if (i < 6) {
										iOffset = 27;
									} else {
										iOffset = 54;
									}
									Set<Byte> currentSegmentCandidate = candidates.get(iOffset + jOffset + (j % 3) + (i % 3) * 3);
									if (currentKey.hashCode() != currentKey.hashCode(currentSegmentCandidate)) {
										Byte[] currentKeyArray = currentKey.toArray(new Byte[0]);
										for (int k = 0; k < currentKeyArray.length; k++) {
											if (currentSegmentCandidate.contains(currentKeyArray[k])) {
												currentSegmentCandidate.remove(currentKeyArray[k]);
												proceedFurther = false;
											}
										}
									}
								}
							}
						}
						if (!proceedFurther) {
							difficultyScore += NAKED_SUBSETS_DIFFICULTY_COEFFICIENT;
							break;
						}
					}
				}
				if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_X_WINGS_ENABLED) { // X-WINGS
					loop:
					for (int i = 0; i < 3; i++) {
						for (int j = 3; j < 6; j++) {
							Set<Byte> candidatesInColumns = new HashSet<Byte>();
							Set<Byte> candidatesInRows = new HashSet<Byte>();
							for (int k = 0; k < 9; k++) {
								candidatesInColumns.addAll(candidates.get(i + (k * 9)));
								candidatesInColumns.addAll(candidates.get(j + (k * 9)));
								candidatesInRows.addAll(candidates.get((j * 9) + k));
								candidatesInRows.addAll(candidates.get((i * 9) + k));
							}
							Iterator<Byte> iterator = candidatesInColumns.iterator();
							while (iterator.hasNext()) {
								Byte currentCandidate = iterator.next();
								ArrayList<Integer> rowsSatisfyingCondition = new ArrayList<Integer>();
								for (int k = 0; k < 9; k++) {
									boolean isConditionSatisfied = true;
									for (int l = 0; l < 9; l++) {
										if ((l == i || l == j) && !candidates.get((k * 9) + l).contains(currentCandidate)) {
											isConditionSatisfied = false;
											break;
										}
										if ((l != i && l != j) && candidates.get((k * 9) + l).contains(currentCandidate)) {
											isConditionSatisfied = false;
											break;
										}
									}
									if (isConditionSatisfied) {
										rowsSatisfyingCondition.add(k);
									}
								}
								if (rowsSatisfyingCondition.size() == 2) {
									for (int l = 0; l < 9; l++) {
										if (!rowsSatisfyingCondition.contains(l)) {
											if (candidates.get(i + (l * 9)).contains(currentCandidate)) {
												proceedFurther = false;
												candidates.get(i + (l * 9)).remove(currentCandidate);
											}
											if (candidates.get(j + (l * 9)).contains(currentCandidate)) {
												proceedFurther = false;
												candidates.get(j + (l * 9)).remove(currentCandidate);
											}
										}
									}
									if (!proceedFurther) {
										difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
										break loop;
									}
								}
							}
							if (proceedFurther) {
								iterator = candidatesInColumns.iterator();
								while (iterator.hasNext()) {
									Byte currentCandidate = iterator.next();
									ArrayList<Integer> columnsSatisfyingCondition = new ArrayList<Integer>();
									for (int column = 0; column < 9; column++) {
										boolean isConditionSatisfied = true;
										for (int row = 0; row < 9; row++) {
											if ((row == i || row == j) && !candidates.get((row * 9) + column).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
											if ((row != i && row != j) && candidates.get((row * 9) + column).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
										}
										if (isConditionSatisfied) {
											columnsSatisfyingCondition.add(column);
										}
									}
									if (columnsSatisfyingCondition.size() == 2) {
										for (int l = 0; l < 9; l++) {
											if (!columnsSatisfyingCondition.contains(l)) {
												if (candidates.get((i * 9) + l).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get((i * 9) + l).remove(currentCandidate);
												}
												if (candidates.get((j * 9) + l).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get((j * 9) + l).remove(currentCandidate);
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop;
										}
									}
								}
							}
						}
					}
					if (proceedFurther) {
						loop:
						for (int i = 3; i < 6; i++) {
							for (int j = 6; j < 9; j++) {
								Set<Byte> candidatesInColumns = new HashSet<Byte>();
								for (int k = 0; k < 9; k++) {
									candidatesInColumns.addAll(candidates.get(i + (k * 9)));
									candidatesInColumns.addAll(candidates.get(j + (k * 9)));
								}
								Iterator<Byte> iterator = candidatesInColumns.iterator();
								while (iterator.hasNext()) {
									Byte currentCandidate = iterator.next();
									ArrayList<Integer> rowsSatisfyingCondition = new ArrayList<Integer>();
									for (int k = 0; k < 9; k++) {
										boolean isConditionSatisfied = true;
										for (int l = 0; l < 9; l++) {
											if ((l == i || l == j) && !candidates.get((k * 9) + l).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
											if ((l != i && l != j) && candidates.get((k * 9) + l).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
										}
										if (isConditionSatisfied) {
											rowsSatisfyingCondition.add(k);
										}
									}
									if (rowsSatisfyingCondition.size() == 2) {
										for (int l = 0; l < 9; l++) {
											if (!rowsSatisfyingCondition.contains(l)) {
												if (candidates.get(i + (l * 9)).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get(i + (l * 9)).remove(currentCandidate);
												}
												if (candidates.get(j + (l * 9)).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get(j + (l * 9)).remove(currentCandidate);
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop;
										}
									}
								}
								if (proceedFurther) {
									iterator = candidatesInColumns.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										ArrayList<Integer> columnsSatisfyingCondition = new ArrayList<Integer>();
										for (int column = 0; column < 9; column++) {
											boolean isConditionSatisfied = true;
											for (int row = 0; row < 9; row++) {
												if ((row == i || row == j) && !candidates.get((row * 9) + column).contains(currentCandidate)) {
													isConditionSatisfied = false;
													break;
												}
												if ((row != i && row != j) && candidates.get((row * 9) + column).contains(currentCandidate)) {
													isConditionSatisfied = false;
													break;
												}
											}
											if (isConditionSatisfied) {
												columnsSatisfyingCondition.add(column);
											}
										}
										if (columnsSatisfyingCondition.size() == 2) {
											for (int l = 0; l < 9; l++) {
												if (!columnsSatisfyingCondition.contains(l)) {
													if (candidates.get((i * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((i * 9) + l).remove(currentCandidate);
													}
													if (candidates.get((j * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((j * 9) + l).remove(currentCandidate);
													}
												}
											}
											if (!proceedFurther) {
												difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
												break loop;
											}
										}
									}
								}
							}
						}
					}
					if (proceedFurther) {
						loop:
						for (int i = 0; i < 3; i++) {
							for (int j = 6; j < 9; j++) {
								Set<Byte> candidatesInColumns = new HashSet<Byte>();
								for (int k = 0; k < 9; k++) {
									candidatesInColumns.addAll(candidates.get(i + (k * 9)));
									candidatesInColumns.addAll(candidates.get(j + (k * 9)));
								}
								Iterator<Byte> iterator = candidatesInColumns.iterator();
								while (iterator.hasNext()) {
									Byte currentCandidate = iterator.next();
									ArrayList<Integer> rowsSatisfyingCondition = new ArrayList<Integer>();
									for (int k = 0; k < 9; k++) {
										boolean isConditionSatisfied = true;
										for (int l = 0; l < 9; l++) {
											if ((l == i || l == j) && !candidates.get((k * 9) + l).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
											if ((l != i && l != j) && candidates.get((k * 9) + l).contains(currentCandidate)) {
												isConditionSatisfied = false;
												break;
											}
										}
										if (isConditionSatisfied) {
											rowsSatisfyingCondition.add(k);
										}
									}
									if (rowsSatisfyingCondition.size() == 2) {
										for (int l = 0; l < 9; l++) {
											if (!rowsSatisfyingCondition.contains(l)) {
												if (candidates.get(i + (l * 9)).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get(i + (l * 9)).remove(currentCandidate);
												}
												if (candidates.get(j + (l * 9)).contains(currentCandidate)) {
													proceedFurther = false;
													candidates.get(j + (l * 9)).remove(currentCandidate);
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop;
										}
									}
								}
								if (proceedFurther) {
									iterator = candidatesInColumns.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										ArrayList<Integer> columnsSatisfyingCondition = new ArrayList<Integer>();
										for (int column = 0; column < 9; column++) {
											boolean isConditionSatisfied = true;
											for (int row = 0; row < 9; row++) {
												if ((row == i || row == j) && !candidates.get((row * 9) + column).contains(currentCandidate)) {
													isConditionSatisfied = false;
													break;
												}
												if ((row != i && row != j) && candidates.get((row * 9) + column).contains(currentCandidate)) {
													isConditionSatisfied = false;
													break;
												}
											}
											if (isConditionSatisfied) {
												columnsSatisfyingCondition.add(column);
											}
										}
										if (columnsSatisfyingCondition.size() == 2) {
											for (int l = 0; l < 9; l++) {
												if (!columnsSatisfyingCondition.contains(l)) {
													if (candidates.get((i * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((i * 9) + l).remove(currentCandidate);
													}
													if (candidates.get((j * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((j * 9) + l).remove(currentCandidate);
													}
												}
											}
											if (!proceedFurther) {
												difficultyScore += X_WINGS_DIFFICULTY_COEFFICIENT;
												break loop;
											}
										}
									}
								}
							}
						}
					}
				}
				/*
				if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_HIDDEN_SUBSETS_ENABLED) { // HIDDEN SUBSETS
					loop7:
					for (int i = 0; i < 9; i++) {
						Set<Byte> candidatesInRow = new HashSet<Byte>();
						Set<Byte> candidatesInColumn = new HashSet<Byte>();
						Set<Byte> candidatesInSegment = new HashSet<Byte>();
						for (int j = 0; j < 9; j++) {
							int rowIndex = (i * 9) + j;
							int columnIndex = (j * 9) + i;
							int jOffset;
							int iOffset;
							if (j < 3) {
								jOffset = 0;
							} else if (j < 6) {
								jOffset = 9;
							} else {
								jOffset = 18;
							}
							if (i < 3) {
								iOffset = 0;
							} else if (i < 6) {
								iOffset = 27;
							} else {
								iOffset = 54;
							}
							candidatesInColumn.addAll(candidates.get(columnIndex));
							candidatesInRow.addAll(candidates.get(rowIndex));
							candidatesInSegment.addAll(candidates.get(iOffset + jOffset + (j % 3) + (i % 3) * 3));
						}
						int[] candidatesInRowArray = new int[candidatesInRow.size()];
						int[] candidatesInColumnArray = new int[candidatesInColumn.size()];
						int[] candidatesInSegmentArray = new int[candidatesInSegment.size()];
						int index = 0;
						Iterator<Byte> iterator = candidatesInRow.iterator();
						while (iterator.hasNext()) {
							candidatesInRowArray[index] = iterator.next();
							index++;
						}
						index = 0;
						iterator = candidatesInColumn.iterator();
						while (iterator.hasNext()) {
							candidatesInColumnArray[index] = iterator.next();
							index++;
						}
						index = 0;
						iterator = candidatesInSegment.iterator();
						while (iterator.hasNext()) {
							candidatesInSegmentArray[index] = iterator.next();
							index++;
						}
					}
				}
				 */
					if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_FINNED_X_WINGS_ENABLED) { //FINNED X_WINGS
						loop9:
						for (int i = 0; i < 3; i++) {
							for (int j = 6; j < 9; j++) {
								Set<Byte> candidatesInRows = new HashSet<Byte>();
								Set<Byte> candidatesInColumns = new HashSet<Byte>();
								for (int k = 0; k < 9; k++) {
									candidatesInRows.addAll(candidates.get(i + (k * 9)));
									candidatesInRows.addAll(candidates.get(j + (k * 9)));
									candidatesInColumns.addAll(candidates.get((j * 9) + k));
									candidatesInColumns.addAll(candidates.get((i * 9) + k));
								}
								Iterator<Byte> iterator = candidatesInRows.iterator();
								while (iterator.hasNext()) {
									Byte currentCandidate = iterator.next();
									Set<String> rowsContainingCommonCandidates = new HashSet<String>();
									String finCoordinates = "";
									for (int k = 0; k < 9; k++) {
										String candidatePlacementString = "";
										candidatePlacementString += k + "";
										if (candidates.get(i + (k * 9)).contains(currentCandidate)) {
											candidatePlacementString += "1";
										} else {
											candidatePlacementString += "0";
										}
										if (candidates.get(j + (k * 9)).contains(currentCandidate)) {
											candidatePlacementString += "1";
										} else {
											candidatePlacementString += "0";
										}
										if ((candidatePlacementString.charAt(1) + "").equals("1")
												&& (candidatePlacementString.charAt(2) + "").equals("1")) {
											rowsContainingCommonCandidates.add(candidatePlacementString);
										} else if (((candidatePlacementString.charAt(1) + "").equals("1")
												&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
											if (finCoordinates.equals("")) {
												finCoordinates = k + "" + i;
											} else {
												finCoordinates = "";
												break;
											}
										}
										if (((candidatePlacementString.charAt(1) + "").equals("0")
												&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
											if (finCoordinates.equals("")) {
												finCoordinates = k + "" + j;
											} else {
												finCoordinates = "";
												break;
											}
										}
									}
									if (rowsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
										Iterator<String> stringIterator = rowsContainingCommonCandidates.iterator();
										int row1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
										int row2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
										int segment1 = getSegmentIndex(row1, i);
										int segment2 = getSegmentIndex(row1, j);
										int segment3 = getSegmentIndex(row2, i);
										int segment4 = getSegmentIndex(row2, j);
										int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(0) + ""), Integer.parseInt(finCoordinates.charAt(1) + ""));
										if (segmentOfFin == segment1) {
											int offset = 0;
											if (segment1 < 3) {
												offset = 0;
											} else if (segment1 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment1 % 3) * 3;
											int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
											for (int k = 0; k < 3; k++) {
												if (segmentRowStartingIndex + k != i + row1 * 9) {
													if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
														candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment2) {
											int offset = 0;
											if (segment2 < 3) {
												offset = 0;
											} else if (segment2 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment2 % 3) * 3;
											int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
											for (int k = 0; k < 3; k++) {
												if (segmentRowStartingIndex + k != i + row1 * 9) {
													if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
														candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment3) {
											int offset = 0;
											if (segment3 < 3) {
												offset = 0;
											} else if (segment3 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment3 % 3) * 3;
											int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
											for (int k = 0; k < 3; k++) {
												if (segmentRowStartingIndex + k != i + row2 * 9) {
													if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
														candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment4) {
											int offset = 0;
											if (segment4 < 3) {
												offset = 0;
											} else if (segment4 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment4 % 3) * 3;
											int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
											for (int k = 0; k < 3; k++) {
												if (segmentRowStartingIndex + k != i + row2 * 9) {
													if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
														candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										}
									}
									if (!proceedFurther) {
										difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
										break loop9;
									}
								}
								iterator = candidatesInColumns.iterator();
								while (iterator.hasNext()) {
									Byte currentCandidate = iterator.next();
									Set<String> columnsContainingCommonCandidates = new HashSet<String>();
									String finCoordinates = "";
									for (int column = 0; column < 9; column++) {
										String candidatePlacementString = "";
										candidatePlacementString += column + "";
										if (candidates.get((i * 9) + column).contains(currentCandidate)) {
											candidatePlacementString += "1";
										} else {
											candidatePlacementString += "0";
										}
										if (candidates.get((j * 9) + column).contains(currentCandidate)) {
											candidatePlacementString += "1";
										} else {
											candidatePlacementString += "0";
										}
										if ((candidatePlacementString.charAt(1) + "").equals("1")
												&& (candidatePlacementString.charAt(2) + "").equals("1")) {
											columnsContainingCommonCandidates.add(candidatePlacementString);
										} else if (((candidatePlacementString.charAt(1) + "").equals("1")
												&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
											if (finCoordinates.equals("")) {
												finCoordinates = column + "" + i;
											} else {
												finCoordinates = "";
												break;
											}
										}
										if (((candidatePlacementString.charAt(1) + "").equals("0")
												&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
											if (finCoordinates.equals("")) {
												finCoordinates = column + "" + j;
											} else {
												finCoordinates = "";
												break;
											}
										}
									}
									if (columnsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
										Iterator<String> stringIterator = columnsContainingCommonCandidates.iterator();
										int column1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
										int column2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
										int segment1 = getSegmentIndex(i, column1);
										int segment2 = getSegmentIndex(j, column1);
										int segment3 = getSegmentIndex(i, column2);
										int segment4 = getSegmentIndex(j, column2);
										int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(1) + ""), Integer.parseInt(finCoordinates.charAt(0) + ""));
										if (segmentOfFin == segment1) {
											int offset;
											if (segment1 < 3) {
												offset = 0;
											} else if (segment1 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment1 % 3) * 3;
											int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
											for (int k = 0; k < 3; k++) {
												if (segmentColumnStartingIndex + k * 9 != column1 + i * 9) {
													if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
														candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment2) {
											int offset;
											if (segment2 < 3) {
												offset = 0;
											} else if (segment2 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment2 % 3) * 3;
											int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
											for (int k = 0; k < 3; k++) {
												if (segmentColumnStartingIndex + k * 9 != column1 + j * 9) {
													if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
														candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment3) {
											int offset;
											if (segment3 < 3) {
												offset = 0;
											} else if (segment3 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment3 % 3) * 3;
											int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
											for (int k = 0; k < 3; k++) {
												if (segmentColumnStartingIndex + k * 9 != column2 + i * 9) {
													if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
														candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										} else if (segmentOfFin == segment4) {
											int offset;
											if (segment4 < 3) {
												offset = 0;
											} else if (segment4 < 6) {
												offset = 27;
											} else {
												offset = 54;
											}
											int segmentStartingIndex = offset + (segment4 % 3) * 3;
											int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
											for (int k = 0; k < 3; k++) {
												if (segmentColumnStartingIndex + k * 9 != column2 + j * 9) {
													if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
														candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
														proceedFurther = false;
													}
												}
											}
										}
									}
									if (!proceedFurther) {
										difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
										break loop9;
									}
								}
							}
						}
						if (proceedFurther) {
							loop10:
							for (int i = 3; i < 6; i++) {
								for (int j = 6; j < 9; j++) {
									Set<Byte> candidatesInRows = new HashSet<Byte>();
									Set<Byte> candidatesInColumns = new HashSet<Byte>();
									for (int k = 0; k < 9; k++) {
										candidatesInRows.addAll(candidates.get(i + (k * 9)));
										candidatesInRows.addAll(candidates.get(j + (k * 9)));
										candidatesInColumns.addAll(candidates.get((j * 9) + k));
										candidatesInColumns.addAll(candidates.get((i * 9) + k));
									}
									Iterator<Byte> iterator = candidatesInRows.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										Set<String> rowsContainingCommonCandidates = new HashSet<String>();
										String finCoordinates = "";
										for (int k = 0; k < 9; k++) {
											String candidatePlacementString = "";
											candidatePlacementString += k + "";
											if (candidates.get(i + (k * 9)).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if (candidates.get(j + (k * 9)).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if ((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("1")) {
												rowsContainingCommonCandidates.add(candidatePlacementString);
											} else if (((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = k + "" + i;
												} else {
													finCoordinates = "";
													break;
												}
											}
											if (((candidatePlacementString.charAt(1) + "").equals("0")
													&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = k + "" + j;
												} else {
													finCoordinates = "";
													break;
												}
											}
										}
										if (rowsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
											Iterator<String> stringIterator = rowsContainingCommonCandidates.iterator();
											int row1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int row2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int segment1 = getSegmentIndex(row1, i);
											int segment2 = getSegmentIndex(row1, j);
											int segment3 = getSegmentIndex(row2, i);
											int segment4 = getSegmentIndex(row2, j);
											int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(0) + ""), Integer.parseInt(finCoordinates.charAt(1) + ""));
											if (segmentOfFin == segment1) {
												int offset = 0;
												if (segment1 < 3) {
													offset = 0;
												} else if (segment1 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment1 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row1 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment2) {
												int offset = 0;
												if (segment2 < 3) {
													offset = 0;
												} else if (segment2 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment2 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row1 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment3) {
												int offset = 0;
												if (segment3 < 3) {
													offset = 0;
												} else if (segment3 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment3 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row2 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment4) {
												int offset = 0;
												if (segment4 < 3) {
													offset = 0;
												} else if (segment4 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment4 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row2 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop10;
										}
									}
									iterator = candidatesInColumns.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										Set<String> columnsContainingCommonCandidates = new HashSet<String>();
										String finCoordinates = "";
										for (int column = 0; column < 9; column++) {
											String candidatePlacementString = "";
											candidatePlacementString += column + "";
											if (candidates.get((i * 9) + column).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if (candidates.get((j * 9) + column).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if ((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("1")) {
												columnsContainingCommonCandidates.add(candidatePlacementString);
											} else if (((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = column + "" + i;
												} else {
													finCoordinates = "";
													break;
												}
											}
											if (((candidatePlacementString.charAt(1) + "").equals("0")
													&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = column + "" + j;
												} else {
													finCoordinates = "";
													break;
												}
											}
										}
										if (columnsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
											Iterator<String> stringIterator = columnsContainingCommonCandidates.iterator();
											int column1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int column2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int segment1 = getSegmentIndex(i, column1);
											int segment2 = getSegmentIndex(j, column1);
											int segment3 = getSegmentIndex(i, column2);
											int segment4 = getSegmentIndex(j, column2);
											int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(1) + ""), Integer.parseInt(finCoordinates.charAt(0) + ""));
											if (segmentOfFin == segment1) {
												int offset;
												if (segment1 < 3) {
													offset = 0;
												} else if (segment1 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment1 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column1 + i * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment2) {
												int offset;
												if (segment2 < 3) {
													offset = 0;
												} else if (segment2 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment2 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column1 + j * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment3) {
												int offset;
												if (segment3 < 3) {
													offset = 0;
												} else if (segment3 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment3 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column2 + i * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment4) {
												int offset;
												if (segment4 < 3) {
													offset = 0;
												} else if (segment4 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment4 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column2 + j * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop10;
										}
									}
								}
							}
						}
						if (proceedFurther) {
							loop11:
							for (int i = 0; i < 3; i++) {
								for (int j = 6; j < 9; j++) {
									Set<Byte> candidatesInRows = new HashSet<Byte>();
									Set<Byte> candidatesInColumns = new HashSet<Byte>();
									for (int k = 0; k < 9; k++) {
										candidatesInRows.addAll(candidates.get(i + (k * 9)));
										candidatesInRows.addAll(candidates.get(j + (k * 9)));
										candidatesInColumns.addAll(candidates.get((j * 9) + k));
										candidatesInColumns.addAll(candidates.get((i * 9) + k));
									}
									Iterator<Byte> iterator = candidatesInRows.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										Set<String> rowsContainingCommonCandidates = new HashSet<String>();
										String finCoordinates = "";
										for (int k = 0; k < 9; k++) {
											String candidatePlacementString = "";
											candidatePlacementString += k + "";
											if (candidates.get(i + (k * 9)).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if (candidates.get(j + (k * 9)).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if ((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("1")) {
												rowsContainingCommonCandidates.add(candidatePlacementString);
											} else if (((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = k + "" + i;
												} else {
													finCoordinates = "";
													break;
												}
											}
											if (((candidatePlacementString.charAt(1) + "").equals("0")
													&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = k + "" + j;
												} else {
													finCoordinates = "";
													break;
												}
											}
										}
										if (rowsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
											Iterator<String> stringIterator = rowsContainingCommonCandidates.iterator();
											int row1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int row2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int segment1 = getSegmentIndex(row1, i);
											int segment2 = getSegmentIndex(row1, j);
											int segment3 = getSegmentIndex(row2, i);
											int segment4 = getSegmentIndex(row2, j);
											int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(0) + ""), Integer.parseInt(finCoordinates.charAt(1) + ""));
											if (segmentOfFin == segment1) {
												int offset = 0;
												if (segment1 < 3) {
													offset = 0;
												} else if (segment1 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment1 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row1 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment2) {
												int offset = 0;
												if (segment2 < 3) {
													offset = 0;
												} else if (segment2 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment2 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row1 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row1 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment3) {
												int offset = 0;
												if (segment3 < 3) {
													offset = 0;
												} else if (segment3 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment3 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row2 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment4) {
												int offset = 0;
												if (segment4 < 3) {
													offset = 0;
												} else if (segment4 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment4 % 3) * 3;
												int segmentRowStartingIndex = segmentStartingIndex + (row2 % 3) * 9;
												for (int k = 0; k < 3; k++) {
													if (segmentRowStartingIndex + k != i + row2 * 9) {
														if (candidates.get(segmentRowStartingIndex + k).contains(currentCandidate)) {
															candidates.get(segmentRowStartingIndex + k).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop11;
										}
									}
									iterator = candidatesInColumns.iterator();
									while (iterator.hasNext()) {
										Byte currentCandidate = iterator.next();
										Set<String> columnsContainingCommonCandidates = new HashSet<String>();
										String finCoordinates = "";
										for (int column = 0; column < 9; column++) {
											String candidatePlacementString = "";
											candidatePlacementString += column + "";
											if (candidates.get((i * 9) + column).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if (candidates.get((j * 9) + column).contains(currentCandidate)) {
												candidatePlacementString += "1";
											} else {
												candidatePlacementString += "0";
											}
											if ((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("1")) {
												columnsContainingCommonCandidates.add(candidatePlacementString);
											} else if (((candidatePlacementString.charAt(1) + "").equals("1")
													&& (candidatePlacementString.charAt(2) + "").equals("0"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = column + "" + i;
												} else {
													finCoordinates = "";
													break;
												}
											}
											if (((candidatePlacementString.charAt(1) + "").equals("0")
													&& (candidatePlacementString.charAt(2) + "").equals("1"))) {
												if (finCoordinates.equals("")) {
													finCoordinates = column + "" + j;
												} else {
													finCoordinates = "";
													break;
												}
											}
										}
										if (columnsContainingCommonCandidates.size() == 2 && !finCoordinates.equals("")) {
											Iterator<String> stringIterator = columnsContainingCommonCandidates.iterator();
											int column1 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int column2 = Integer.parseInt(stringIterator.next().charAt(0) + "");
											int segment1 = getSegmentIndex(i, column1);
											int segment2 = getSegmentIndex(j, column1);
											int segment3 = getSegmentIndex(i, column2);
											int segment4 = getSegmentIndex(j, column2);
											int segmentOfFin = getSegmentIndex(Integer.parseInt(finCoordinates.charAt(1) + ""), Integer.parseInt(finCoordinates.charAt(0) + ""));
											if (segmentOfFin == segment1) {
												int offset;
												if (segment1 < 3) {
													offset = 0;
												} else if (segment1 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment1 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column1 + i * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment2) {
												int offset;
												if (segment2 < 3) {
													offset = 0;
												} else if (segment2 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment2 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column1 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column1 + j * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment3) {
												int offset;
												if (segment3 < 3) {
													offset = 0;
												} else if (segment3 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment3 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column2 + i * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											} else if (segmentOfFin == segment4) {
												int offset;
												if (segment4 < 3) {
													offset = 0;
												} else if (segment4 < 6) {
													offset = 27;
												} else {
													offset = 54;
												}
												int segmentStartingIndex = offset + (segment4 % 3) * 3;
												int segmentColumnStartingIndex = segmentStartingIndex + column2 % 3;
												for (int k = 0; k < 3; k++) {
													if (segmentColumnStartingIndex + k * 9 != column2 + j * 9) {
														if (candidates.get(segmentColumnStartingIndex + k * 9).contains(currentCandidate)) {
															candidates.get(segmentColumnStartingIndex + k * 9).remove(currentCandidate);
															proceedFurther = false;
														}
													}
												}
											}
										}
										if (!proceedFurther) {
											difficultyScore += FINNED_X_WINGS_DIFFICULTY_COEFFICIENT;
											break loop11;
										}
									}
								}
							}
						}
					}
					if (Arrays.toString(sudoku).contains("0") && proceedFurther && IS_SWORDFISH_ENABLED) { //SWORDFISH
						loop8:
						for (int i = 0; i < 3; i++) {
							for (int j = 3; j < 6; j++) {
								for (int k = 6; k < 9; k++) {
									Set<Byte> candidatesInColumns = new HashSet<Byte>();
									Set<Byte> candidatesInRows = new HashSet<Byte>();
									for (int l = 0; l < 9; l++) {
										candidatesInColumns.addAll(candidates.get(i + l * 9));
										candidatesInColumns.addAll(candidates.get(j + l * 9));
										candidatesInColumns.addAll(candidates.get(k + l * 9));
										candidatesInRows.addAll(candidates.get((i * 9) + l));
										candidatesInRows.addAll(candidates.get((j * 9) + l));
										candidatesInRows.addAll(candidates.get((k * 9) + l));
									}
									Iterator<Byte> iterator = candidatesInColumns.iterator();
									while (iterator.hasNext()) {
										byte currentCandidate = iterator.next();
										Set<Integer> rowsSatisfyingCondition = new HashSet<Integer>();
										for (int l = 0; l < 9; l++) {
											boolean hasConditionSatisfied = true;
											for (int m = 0; m < 9; m++) {
												if (m != i && m != j && m != k) {
													if (candidates.get(m + l * 9).contains(currentCandidate)) {
														hasConditionSatisfied = false;
														break;
													}
												}
											}
											if (hasConditionSatisfied) {
												rowsSatisfyingCondition.add(l);
											}
										}
										if (rowsSatisfyingCondition.size() == 3) {
											for (int l = 0; l < 9; l++) {
												if (!rowsSatisfyingCondition.contains(l)) {
													if (candidates.get(i + (l * 9)).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get(i + (l * 9)).remove(currentCandidate);
													}
													if (candidates.get(j + (l * 9)).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get(j + (l * 9)).remove(currentCandidate);
													}
													if (candidates.get(k + (l * 9)).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get(k + (l * 9)).remove(currentCandidate);
													}
												}
											}
											if (!proceedFurther) {
												difficultyScore += SWORDFISH_DIFFICULTY_COEFFICIENT;
												break loop8;
											}
										}
									}
									iterator = candidatesInRows.iterator();
									while (iterator.hasNext()) {
										byte currentCandidate = iterator.next();
										Set<Integer> columnsSatisfyingCondition = new HashSet<Integer>();
										for (int l = 0; l < 9; l++) {
											boolean hasConditionSatisfied = true;
											for (int m = 0; m < 9; m++) {
												if (m != i && m != j && m != k) {
													if (candidates.get(m * 9 + l).contains(currentCandidate)) {
														hasConditionSatisfied = false;
														break;
													}
												}
											}
											if (hasConditionSatisfied) {
												columnsSatisfyingCondition.add(l);
											}
										}
										if (columnsSatisfyingCondition.size() == 3) {
											for (int l = 0; l < 9; l++) {
												if (!columnsSatisfyingCondition.contains(l)) {
													if (candidates.get((i * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((i * 9) + l).remove(currentCandidate);
													}
													if (candidates.get((j * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((j * 9) + l).remove(currentCandidate);
													}
													if (candidates.get((k * 9) + l).contains(currentCandidate)) {
														proceedFurther = false;
														candidates.get((k * 9) + l).remove(currentCandidate);
													}
												}
											}
											if (!proceedFurther) {
												difficultyScore += SWORDFISH_DIFFICULTY_COEFFICIENT;
												break loop8;
											}
										}
									}
								}
							}
						}
					}
					if (proceedFurther && Arrays.toString(sudoku).contains("0") && IS_XY_WINGS_ENABLED) {
						ArrayList<CandidateCoordinateDataHolder> cellsWithTwoCandidates = new ArrayList<CandidateCoordinateDataHolder>();
						for (int i = 0; i < 9; i++) {
							for (int j = 0; j < 9; j++) {
								if (candidates.get((i * 9) + j).size() == 2) {
									cellsWithTwoCandidates.add(new CandidateCoordinateDataHolder(candidates.get((i * 9) + j), i, j));
								}
							}
						}
						loop:
						for (int i = 0; i < cellsWithTwoCandidates.size(); i++) {
							for (int j = i + 1; j < cellsWithTwoCandidates.size(); j++) {
								for (int k = j + 1; k < cellsWithTwoCandidates.size(); k++) {
									Set<Byte> tempSet = new HashSet<Byte>();
									tempSet.addAll(cellsWithTwoCandidates.get(i).getCandidate());
									tempSet.retainAll(cellsWithTwoCandidates.get(j).getCandidate());
									if (tempSet.size() == 1) {
										tempSet.clear();
										tempSet.addAll(cellsWithTwoCandidates.get(i).getCandidate());
										tempSet.retainAll(cellsWithTwoCandidates.get(k).getCandidate());
										if (tempSet.size() == 1) {
											tempSet.clear();
											tempSet.addAll(cellsWithTwoCandidates.get(j).getCandidate());
											tempSet.retainAll(cellsWithTwoCandidates.get(k).getCandidate());
											if (tempSet.size() == 1) {
												tempSet.clear();
												tempSet.addAll(cellsWithTwoCandidates.get(i).getCandidate());
												tempSet.addAll(cellsWithTwoCandidates.get(j).getCandidate());
												if (tempSet.containsAll(cellsWithTwoCandidates.get(k).getCandidate())) {
													int segmentOfY = getSegmentIndex(cellsWithTwoCandidates.get(k).
															getRow(), cellsWithTwoCandidates.get(k).getColumn());
													int segmentOfXi = getSegmentIndex(cellsWithTwoCandidates.get(i)
															.getRow(), cellsWithTwoCandidates.get(i).getColumn());
													int segmentOfXj = getSegmentIndex(cellsWithTwoCandidates.get(j)
															.getRow(), cellsWithTwoCandidates.get(j).getColumn());
													if (((cellsWithTwoCandidates.get(k).getRow() == cellsWithTwoCandidates.get(i).getRow() ||
															cellsWithTwoCandidates.get(k).getColumn() == cellsWithTwoCandidates.get(i).getColumn() ||
															segmentOfY == segmentOfXi) && (cellsWithTwoCandidates.get(k).getRow() == cellsWithTwoCandidates.get(j).getRow() ||
															cellsWithTwoCandidates.get(k).getColumn() == cellsWithTwoCandidates.get(j).getColumn() ||
															segmentOfY == segmentOfXj)) &&
															(cellsWithTwoCandidates.get(i).getRow() != cellsWithTwoCandidates.get(j).getRow() &&
																	cellsWithTwoCandidates.get(i).getColumn() != cellsWithTwoCandidates.get(j).getColumn() &&
																	segmentOfXi != segmentOfXj)) {
														Set<Integer> affectedIndicesOfXi =
																getAffectedCells((cellsWithTwoCandidates.get(i).getRow() * 9) + cellsWithTwoCandidates.get(i).getColumn());
														Set<Integer> affectedIndicesOfXj =
																getAffectedCells((cellsWithTwoCandidates.get(j).getRow() * 9) + cellsWithTwoCandidates.get(j).getColumn());
														affectedIndicesOfXi.retainAll(affectedIndicesOfXj);
														Iterator<Integer> intersections = affectedIndicesOfXi.iterator();
														tempSet.clear();
														tempSet.addAll(cellsWithTwoCandidates.get(i).getCandidate());
														tempSet.retainAll(cellsWithTwoCandidates.get(j).getCandidate());
														int commonCandidate = tempSet.iterator().next();
														while (intersections.hasNext()) {
															int intersectionIndex = intersections.next();
															if (candidates.get(intersectionIndex).contains(Byte.parseByte(commonCandidate + ""))) {
																proceedFurther = false;
																candidates.get(intersectionIndex).remove(Byte.parseByte(commonCandidate + ""));
															}
														}
														if (!proceedFurther) {
															difficultyScore += XY_WINGS_DIFFICULTY_COEFFICIENT;
															break loop;
														}
													}
												} else {
													tempSet.clear();
													tempSet.addAll(cellsWithTwoCandidates.get(j).getCandidate());
													tempSet.addAll(cellsWithTwoCandidates.get(k).getCandidate());
													if (tempSet.containsAll(cellsWithTwoCandidates.get(i).getCandidate())) {
														int segmentOfY = getSegmentIndex(cellsWithTwoCandidates.get(i).
																getRow(), cellsWithTwoCandidates.get(i).getColumn());
														int segmentOfXk = getSegmentIndex(cellsWithTwoCandidates.get(k)
																.getRow(), cellsWithTwoCandidates.get(k).getColumn());
														int segmentOfXj = getSegmentIndex(cellsWithTwoCandidates.get(j)
																.getRow(), cellsWithTwoCandidates.get(j).getColumn());
														if (((cellsWithTwoCandidates.get(i).getRow() == cellsWithTwoCandidates.get(k).getRow() ||
																cellsWithTwoCandidates.get(i).getColumn() == cellsWithTwoCandidates.get(k).getColumn() ||
																segmentOfY == segmentOfXk) && (cellsWithTwoCandidates.get(i).getRow() == cellsWithTwoCandidates.get(j).getRow() ||
																cellsWithTwoCandidates.get(i).getColumn() == cellsWithTwoCandidates.get(j).getColumn() ||
																segmentOfY == segmentOfXj)) &&
																(cellsWithTwoCandidates.get(i).getRow() != cellsWithTwoCandidates.get(k).getRow() &&
																		cellsWithTwoCandidates.get(i).getColumn() != cellsWithTwoCandidates.get(k).getColumn() &&
																		segmentOfXk != segmentOfXj)) {
															Set<Integer> affectedIndicesOfXk =
																	getAffectedCells((cellsWithTwoCandidates.get(k).getRow() * 9) + cellsWithTwoCandidates.get(k).getColumn());
															Set<Integer> affectedIndicesOfXj =
																	getAffectedCells((cellsWithTwoCandidates.get(j).getRow() * 9) + cellsWithTwoCandidates.get(j).getColumn());
															affectedIndicesOfXk.retainAll(affectedIndicesOfXj);
															Iterator<Integer> intersections = affectedIndicesOfXk.iterator();
															tempSet.clear();
															tempSet.addAll(cellsWithTwoCandidates.get(k).getCandidate());
															tempSet.retainAll(cellsWithTwoCandidates.get(j).getCandidate());
															int commonCandidate = tempSet.iterator().next();
															while (intersections.hasNext()) {
																int intersectionIndex = intersections.next();
																if (candidates.get(intersectionIndex).contains(Byte.parseByte(commonCandidate + ""))) {
																	proceedFurther = false;
																	candidates.get(intersectionIndex).remove(Byte.parseByte(commonCandidate + ""));
																}
															}
															if (!proceedFurther) {
																difficultyScore += XY_WINGS_DIFFICULTY_COEFFICIENT;
																break loop;
															}
														}
													} else {
														tempSet.clear();
														tempSet.addAll(cellsWithTwoCandidates.get(i).getCandidate());
														tempSet.addAll(cellsWithTwoCandidates.get(k).getCandidate());
														if (tempSet.containsAll(cellsWithTwoCandidates.get(j).getCandidate())) {
															int segmentOfY = getSegmentIndex(cellsWithTwoCandidates.get(j).
																	getRow(), cellsWithTwoCandidates.get(j).getColumn());
															int segmentOfXk = getSegmentIndex(cellsWithTwoCandidates.get(k)
																	.getRow(), cellsWithTwoCandidates.get(k).getColumn());
															int segmentOfXi = getSegmentIndex(cellsWithTwoCandidates.get(i)
																	.getRow(), cellsWithTwoCandidates.get(i).getColumn());
															if (((cellsWithTwoCandidates.get(j).getRow() == cellsWithTwoCandidates.get(k).getRow() ||
																	cellsWithTwoCandidates.get(j).getColumn() == cellsWithTwoCandidates.get(k).getColumn() ||
																	segmentOfY == segmentOfXk) && (cellsWithTwoCandidates.get(j).getRow() == cellsWithTwoCandidates.get(i).getRow() ||
																	cellsWithTwoCandidates.get(j).getColumn() == cellsWithTwoCandidates.get(i).getColumn() ||
																	segmentOfY == segmentOfXi)) &&
																	(cellsWithTwoCandidates.get(k).getRow() != cellsWithTwoCandidates.get(i).getRow() &&
																			cellsWithTwoCandidates.get(k).getColumn() != cellsWithTwoCandidates.get(i).getColumn() &&
																			segmentOfXk != segmentOfXi)) {
																Set<Integer> affectedIndicesOfXk =
																		getAffectedCells((cellsWithTwoCandidates.get(k).getRow() * 9) + cellsWithTwoCandidates.get(k).getColumn());
																Set<Integer> affectedIndicesOfXi =
																		getAffectedCells((cellsWithTwoCandidates.get(i).getRow() * 9) + cellsWithTwoCandidates.get(i).getColumn());
																affectedIndicesOfXk.retainAll(affectedIndicesOfXi);
																Iterator<Integer> intersections = affectedIndicesOfXk.iterator();
																tempSet.clear();
																tempSet.addAll(cellsWithTwoCandidates.get(k).getCandidate());
																tempSet.retainAll(cellsWithTwoCandidates.get(i).getCandidate());
																int commonCandidate = tempSet.iterator().next();
																while (intersections.hasNext()) {
																	int intersectionIndex = intersections.next();
																	if (candidates.get(intersectionIndex).contains(Byte.parseByte(commonCandidate + ""))) {
																		proceedFurther = false;
																		candidates.get(intersectionIndex).remove(Byte.parseByte(commonCandidate + ""));
																	}
																}
																if (!proceedFurther) {
																	difficultyScore += XY_WINGS_DIFFICULTY_COEFFICIENT;
																	break loop;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					if (Arrays.equals(solution, sudoku)) {
						return difficultyScore;
					} else if (!Arrays.toString(sudoku).contains("0")) {
						System.exit(-1);
						throw new Exception("THERE'S SOMETHING WRONG WITH THE SOLVER");
					}
					if (proceedFurther) {
						for (int i = 0; i < sudoku.length; i++) {
							if(sudoku[i]==0){
								sudoku[i] = solution[i];
								difficultyScore += INSUFFICIENT_ALGORITHMS_DIFFICULTY_COEFFICIENT;
							}
						}
					}
					for (int i = 0; i < 81; i++) {
						if (candidates.get(i).size() != 0 && !candidates.get(i).contains(solution[i])) {
							throw new Exception("THERE'S SOMETHING WRONG WITH THE SOLVER");
						}
					}
				}
			}
			private static HashMap<Integer, ArrayList<int[]>> getCombinations ( int[] input, int k){
				HashMap<Integer, ArrayList<int[]>> result = new HashMap<Integer, ArrayList<int[]>>();
				for (int j = 1; j <= k; j++) {
					ArrayList<int[]> subsets = new ArrayList<int[]>();
					int[] s = new int[j];
					if (k <= input.length) {
						for (int i = 0; (s[i] = i) < j - 1; i++) ;
						subsets.add(getSubset(input, s));
						for (; ; ) {
							int i;
							for (i = j - 1; i >= 0 && s[i] == input.length - j + i; i--) ;
							if (i < 0) {
								break;
							}
							s[i]++;
							for (++i; i < j; i++) {
								s[i] = s[i - 1] + 1;
							}
							subsets.add(getSubset(input, s));
						}
					}
					result.put(j, subsets);
				}
				return result;
			}

			private static int[] getSubset (int[] input, int[] subset){
				int[] result = new int[subset.length];
				for (int i = 0; i < subset.length; i++)
					result[i] = input[subset[i]];
				return result;
			}


			private void eliminateCandidatesInAffectedIndices ( int i, byte numberToBePlaced){
				int affectedRow = i / 9;
				int affectedColumn = i % 9;
				int affectedSegment = getSegmentIndex(affectedRow, affectedColumn);
				Set<Integer> affectedIndices = new HashSet<Integer>();
				for (int j = 0; j < 9; j++) {
					affectedIndices.add((affectedRow * 9) + j);
					affectedIndices.add((affectedColumn) + j * 9);
					int segmentOffset;
					int jOffset;
					if (affectedSegment < 3) {
						segmentOffset = 0;
					} else if (affectedSegment < 6) {
						segmentOffset = 27;
					} else {
						segmentOffset = 54;
					}
					if (j < 3) {
						jOffset = 0;
					} else if (j < 6) {
						jOffset = 9;
					} else {
						jOffset = 18;
					}
					affectedIndices.add((affectedSegment % 3) * 3 + segmentOffset + (j % 3) + jOffset);
				}
				Integer[] affectedIndicesArray = affectedIndices.toArray(new Integer[0]);
				for (int j = 0; j < affectedIndicesArray.length; j++) {
					candidates.get(affectedIndicesArray[j]).remove(numberToBePlaced);
				}
			}
			private Set<Integer> getAffectedCells ( int i){
				int affectedRow = i / 9;
				int affectedColumn = i % 9;
				int affectedSegment = getSegmentIndex(affectedRow, affectedColumn);
				Set<Integer> affectedIndices = new HashSet<Integer>();
				for (int j = 0; j < 9; j++) {
					affectedIndices.add((affectedRow * 9) + j);
					affectedIndices.add((affectedColumn) + j * 9);
					int segmentOffset;
					int jOffset;
					if (affectedSegment < 3) {
						segmentOffset = 0;
					} else if (affectedSegment < 6) {
						segmentOffset = 27;
					} else {
						segmentOffset = 54;
					}
					if (j < 3) {
						jOffset = 0;
					} else if (j < 6) {
						jOffset = 9;
					} else {
						jOffset = 18;
					}
					affectedIndices.add((affectedSegment % 3) * 3 + segmentOffset + (j % 3) + jOffset);
				}
				return affectedIndices;
			}

			private static int getSegmentIndex ( int effectedRow, int effectedColumn){
				return (effectedRow/3) * 3 + effectedColumn/3;
			/*
				int segment;
				if (effectedColumn < 3) {
					if (effectedRow < 3) {
						segment = 0;
					} else if (effectedRow < 6) {
						segment = 3;
					} else {
						segment = 6;
					}
				} else if (effectedColumn < 6) {
					if (effectedRow < 3) {
						segment = 1;
					} else if (effectedRow < 6) {
						segment = 4;
					} else {
						segment = 7;
					}
				} else {
					if (effectedRow < 3) {
						segment = 2;
					} else if (effectedRow < 6) {
						segment = 5;
					} else {
						segment = 8;
					}
				}
				return segment;
			 */

			}
			public void setSolutionSpaceLimit (int solutionSpaceLimit){
				this.solutionSpaceLimit = solutionSpaceLimit;
			}

			public static Byte[] getRow (int row, Byte[] sudoku){
				return Arrays.copyOfRange(sudoku, row * 9, (row * 9) + 9);
			}
			public static Byte[] getColumn ( int column, Byte[] sudoku){
				Byte[] result = new Byte[9];
				for (int i = 0; i < 9; i++) {
					result[i] = sudoku[column + i * 9];
				}
				return result;
			}
			public static Byte[] getSegment ( int row, int column, Byte[] sudoku){
				Byte[] result = new Byte[9];
				int segment = getSegmentIndex(row, column);
				int segmentOffset;
				if (segment < 3) {
					segmentOffset = 0;
				} else if (segment < 6) {
					segmentOffset = 27;
				} else {
					segmentOffset = 54;
				}
				int segmentStartingIndex = segmentOffset + (segment % 3) * 3;
				for (int i = 0; i < 9; i++) {
					int offset;
					if (i < 3) {
						offset = 0;
					} else if (i < 6) {
						offset = 9;
					} else {
						offset = 18;
					}
					result[i] = sudoku[segmentStartingIndex + i % 3 + offset];
				}
				return result;
			}
		}

		abstract class ColumnLabel {
		}

		class CellCoordinatesLabel extends ColumnLabel {
			byte row, col;

			public CellCoordinatesLabel(byte row, byte col) {
				this.row = row;
				this.col = col;
			}

			@Override
			public String toString() {
				return "RC=(" + row + "," + col + ")";
			}
		}

		abstract class CellValueLabel extends ColumnLabel {
			byte value;

			public abstract String getType();

			@Override
			public String toString() {
				return getType() + "<-" + value;
			}
		}

		class RowValueLabel extends CellValueLabel {
			public RowValueLabel(byte value) {
				this.value = value;
			}

			@Override
			public String getType() {
				return "row";
			}
		}

		class ColumnValueLabel extends CellValueLabel {
			public ColumnValueLabel(byte value) {
				this.value = value;
			}

			@Override
			public String getType() {
				return "column";
			}
		}

		class RegionValueLabel extends CellValueLabel {
			public RegionValueLabel(byte value) {
				this.value = value;
			}

			@Override
			public String getType() {
				return "region";
			}
		}
