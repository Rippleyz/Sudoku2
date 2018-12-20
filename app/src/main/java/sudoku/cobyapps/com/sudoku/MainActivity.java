package sudoku.cobyapps.com.sudoku;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SudokuGridView sudokuGridView;
    private boolean isOnNote = false;
    private SudokuEngine sudokuEngine;
    private ArrayList<String> sudokus;
    private int next;
    private static final String KEY_CURRENT_SUDOKU = "KEY_CURRENT_SUDOKU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputStream inputStream = getResources().openRawResource(R.raw.puzzles50);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        SudokuDataHolder dataHolder = new SudokuDataHolder();
        next = 1;
        sudokus = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("sudoku.cobyapps.com.sudoku", MODE_PRIVATE);
        SudokuCellDataComponent[][] grid = new SudokuCellDataComponent[9][9];
        if (true || sharedPreferences.getString(KEY_CURRENT_SUDOKU, null) == null) {
            while (true) {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line == null) {
                    break;
                }
                sudokus.add(line);
            }

            for (int i = 0; i < 81; i++) {
                SudokuCellDataComponent sudokuCellDataComponent = new SudokuCellDataComponent(Integer.parseInt(sudokus.get(0).charAt(i) + ""), "");
                if (Integer.parseInt(sudokus.get(0).charAt(i) + "") != 0) {
                    sudokuCellDataComponent.setIsGiven(true);
                }
                grid[i / 9][i % 9] = sudokuCellDataComponent;
            }
        } else {
            sudokus = new ArrayList<String>();
            sudokus.add(sharedPreferences.getString(KEY_CURRENT_SUDOKU, null));
            for (int i = 0; i < 81; i++) {
                SudokuCellDataComponent sudokuCellDataComponent = new SudokuCellDataComponent(Integer.parseInt(sudokus.get(0).charAt(i) + ""), "");
                if (Integer.parseInt(sudokus.get(0).charAt(i) + "") != 0) {
                    sudokuCellDataComponent.setIsGiven(true);
                }
                grid[i / 9][i % 9] = sudokuCellDataComponent;
            }
        }
        dataHolder.setGrid(grid);
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(getResources().getInteger(R.integer.frame_id));
        sudokuGridView = new SudokuGridView(this, dataHolder);
        frameLayout.addView(sudokuGridView);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mainLayout.addView(frameLayout, new RelativeLayout.LayoutParams(width, width + 3));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height - (width + 3));
        layoutParams.addRule(RelativeLayout.BELOW, frameLayout.getId());
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setWeightSum(1f);
        mainLayout.addView(linearLayout, layoutParams);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        for (int i = 0; i < 4; i++) {
            LinearLayout columnLayout = new LinearLayout(this);
            columnLayout.setOrientation(LinearLayout.VERTICAL);
            columnLayout.setWeightSum(1f);
            for (int j = 0; j < 3; j++) {
                Button button = new Button(this);
                button.setOnClickListener(this);
                if (i != 3) {
                    button.setText(((3 * j) + i + 1) + "");
                } else {
                    if (j == 0) {
                        button.setText("Note");
                    } else if (j == 1) {
                        button.setText("Erase");
                    } else {
                        button.setText("New");
                    }
                }
                columnLayout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 / 3f));
            }
            linearLayout.addView(columnLayout, linearLayoutParams);
        }
    }

    @Override
    public void onClick(View view) {
        if (Character.isDigit(((Button) view).getText().charAt(0))
                && sudokuGridView.getSelectionCoordinates() != null
                && !sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()) {
            if (!isOnNote) {
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNumber(Integer.parseInt(((Button) view).getText() + ""));
                sudokuGridView.invalidate();
            } else if (!sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()) {
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNumber(SudokuDataHolder.NUMBER_EMPTY);
                String note = sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].getNotes();
                if (!note.equals("")) {
                    note += ",";
                }
                note += ((Button) view).getText() + "";
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNotes(note);
                sudokuGridView.invalidate();
            }
        } else if ((((Button) view).getText().toString().toLowerCase().equals("erase"))
                && !sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()) {
            sudokuGridView.getDataHolder().getGrid()
                    [sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].setNumber(SudokuDataHolder.NUMBER_EMPTY);
            sudokuGridView.getDataHolder().getGrid()
                    [sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].setNotes("");
            sudokuGridView.invalidate();
        } else if ((((Button) view).getText().toString().toLowerCase().equals("note"))) {
            if (!isOnNote) {
                view.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                isOnNote = true;
            } else {
                Button button = new Button(this);
                view.setBackground(button.getBackground());
                isOnNote = false;
                button = null;
            }
            sudokuGridView.invalidate();
        } else if ((((Button) view).getText().toString().toLowerCase().equals("new"))) {
            SudokuDataHolder holder = new SudokuDataHolder();
            SudokuCellDataComponent[][] grid = new SudokuCellDataComponent[9][9];
            for (int i = 0; i < 81; i++) {
                SudokuCellDataComponent dataComponent = new SudokuCellDataComponent(Integer.parseInt(sudokus.get(next).charAt(i) + ""), "");
                if (Integer.parseInt(sudokus.get(next).charAt(i) + "") != 0) {
                    dataComponent.setIsGiven(true);
                }
                grid[i / 9][i % 9] = dataComponent;
            }
            holder.setGrid(grid);
            sudokuGridView.setDataHolder(holder);
            sudokuGridView.invalidate();
            if (next + 1 == sudokus.size()) {
                next = 0;
            } else {
                next++;
            }
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences("sudoku.cobyapps.com.sudoku", MODE_PRIVATE).edit();
        String currentSudokuString = "";
        SudokuCellDataComponent[][] dataComponents = sudokuGridView.getDataHolder().getGrid();
        for (int i = 0; i < dataComponents.length; i++) {
            int row = i / 9;
            int column = i % 9;
            currentSudokuString += dataComponents[row][column].getNumber();
        }
        editor.putString(KEY_CURRENT_SUDOKU, currentSudokuString);
        editor.commit();
        super.onDestroy();
    }
}
