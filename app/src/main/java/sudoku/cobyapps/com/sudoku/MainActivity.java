package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments.CustomSudokuFragment;
import sudoku.cobyapps.com.sudoku.RecyclerViewAdapters.CustomSudokuRecyclerViewAdapter;
import sudoku.cobyapps.com.sudoku.TimeObserver.IObserver;
import sudoku.cobyapps.com.sudoku.TimeObserver.StringTimer;
import sudoku.cobyapps.com.sudoku.TimeObserver.TimerPanel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        InterFragmentCommunicator, SudokuDataConverter{
    private SudokuGridView sudokuGridView;
    private boolean isOnNote = false;
    private int next;
    private static final String KEY_CURRENT_SUDOKU = "KEY_CURRENT_SUDOKU";
    private static final String KEY_IS_GIVEN = "KEY_IS_GIVEN";
    private static final String KEY_NOTES = "KEY_NOTES";
    public static final int MENU_ON_SUDOKU_PANEL = 0;
    public static final int MENU_ON_CUSTOM_DIFFICULTY = 1;
    public static final int MENU_NEW_SUDOKU = 2;
    private int currentMenu;
    private DatabaseAdapter databaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentMenu = MENU_ON_SUDOKU_PANEL;
        InputStream inputStream = getResources().openRawResource(R.raw.puzzles50);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        SudokuDataHolder dataHolder = new SudokuDataHolder();
        next = 1;
        SharedPreferences sharedPreferences = getSharedPreferences("sudoku.cobyapps.com.sudoku", MODE_PRIVATE);
        SudokuCellDataComponent [][] currentSudoku = new SudokuCellDataComponent[9][9];
        if(sharedPreferences.getString(KEY_CURRENT_SUDOKU,null)==null){
            while(true){
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(line==null){
                    break;
                }
                for(int i = 0 ; i < 81 ; i++) {
                    SudokuCellDataComponent dataComponent = new SudokuCellDataComponent();
                    dataComponent.setNumber(Integer.parseInt(line.charAt(i)+""));
                    if(Integer.parseInt(line.charAt(i)+"") == 0){
                        dataComponent.setIsGiven(false);
                    }else{
                        dataComponent.setIsGiven(true);
                    }
                    currentSudoku[i/9][i%9] = dataComponent;
                }
            }
        }else{
            String currentSudokuString = sharedPreferences.getString(KEY_CURRENT_SUDOKU,null);
            String isGivensString = sharedPreferences.getString(KEY_IS_GIVEN, null);
            String notesString = sharedPreferences.getString(KEY_NOTES, null);
            String [] notesForEachCell=null;
            if(notesString!=null){
                notesForEachCell = notesString.split(" / ");
                notesForEachCell[80] = notesForEachCell[80]
                        .replace("/", "")
                        .replace(" ", "");
            }
            for(int i = 0; i < 81 ; i++){
                SudokuCellDataComponent sudokuCellDataComponent = new SudokuCellDataComponent();
                if(isGivensString != null && isGivensString.charAt(i)=='1'){
                    sudokuCellDataComponent.setIsGiven(true);
                }else{
                    sudokuCellDataComponent.setIsGiven(false);
                }
                if(notesForEachCell!=null){
                    sudokuCellDataComponent.setNotes(notesForEachCell[i]);
                }
                sudokuCellDataComponent.setNumber(Integer.parseInt(currentSudokuString.charAt(i)+""));
                currentSudoku[i/9][i%9] = sudokuCellDataComponent;
            }
        }
        dataHolder.setGrid(currentSudoku);

        RelativeLayout mainLayout = findViewById(R.id.main_layout);

        RelativeLayout timerPanelLayout = (RelativeLayout)
                ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.timer_panel,mainLayout,false);

        StringTimer timer = new StringTimer();
        IObserver iObserver = new TimerPanel(timerPanelLayout, this);
        timer.addObserver(iObserver);
        timer.execute();
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(getResources().getInteger(R.integer.frame_id));
        sudokuGridView = new SudokuGridView(this,dataHolder);
        frameLayout.addView(sudokuGridView);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mainLayout.addView(timerPanelLayout, timerPanelLayout.getLayoutParams());
        RelativeLayout.LayoutParams layoutParamsForFrameLayout = new RelativeLayout.LayoutParams(width,width+3);
        layoutParamsForFrameLayout.addRule(RelativeLayout.BELOW, timerPanelLayout.getId());
        mainLayout.addView(frameLayout, layoutParamsForFrameLayout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height-(width+3));
        layoutParams.addRule(RelativeLayout.BELOW,frameLayout.getId());
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setWeightSum(1f);
        mainLayout.addView(linearLayout,layoutParams);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0.25f);
        for(int i = 0 ; i < 4 ; i++){
            LinearLayout columnLayout = new LinearLayout(this);
            columnLayout.setOrientation(LinearLayout.VERTICAL);
            columnLayout.setWeightSum(1f);
            for(int j = 0; j < 3; j++){
                Button button = new Button(this);
                button.setOnClickListener(this);
                if(i!=3){
                    button.setText(((3*j)+i+1)+"");
                }else{
                    if(j==0){
                        button.setText("Note");
                    }else if(j==1){
                        button.setText("Erase");
                    }else{
                        button.setText("New");
                    }
                }
                columnLayout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1/3f));
            }
            linearLayout.addView(columnLayout,linearLayoutParams);
        }
        databaseAdapter = DatabaseAdapter.getInstance(this);
        }

    @Override
    public void onClick(View view) {
        if(Character.isDigit(((Button)view).getText().charAt(0))
                && sudokuGridView.getSelectionCoordinates()!=null
                && !sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()){
            if(!isOnNote){
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNumber(Integer.parseInt(((Button)view).getText()+""));
                sudokuGridView.invalidate();
            }else if(!sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()){
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNumber(SudokuDataHolder.NUMBER_EMPTY);
                String note = sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].getNotes();
                if(!note.equals("")){
                    note += ",";
                }
                note += ((Button)view).getText()+"";
                sudokuGridView.getDataHolder().getGrid()
                        [sudokuGridView.getSelectionCoordinates().getX()]
                        [sudokuGridView.getSelectionCoordinates().getY()].setNotes(note);
                sudokuGridView.invalidate();
            }
        }
        else if((((Button)view).getText().toString().toLowerCase().equals("erase"))
                && !sudokuGridView.getDataHolder().getGrid()[sudokuGridView.getSelectionCoordinates().getX()]
                [sudokuGridView.getSelectionCoordinates().getY()].getIsGiven()){
            sudokuGridView.getDataHolder().getGrid()
                    [sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].setNumber(SudokuDataHolder.NUMBER_EMPTY);
            sudokuGridView.getDataHolder().getGrid()
                    [sudokuGridView.getSelectionCoordinates().getX()]
                    [sudokuGridView.getSelectionCoordinates().getY()].setNotes("");
            sudokuGridView.invalidate();
        }
        else if((((Button)view).getText().toString().toLowerCase().equals("note"))){
            if(!isOnNote){
                view.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                isOnNote = true;
            }else{
                Button button = new Button(this);
                view.setBackground(button.getBackground());
                isOnNote = false;
                button = null;
            }
            sudokuGridView.invalidate();
        }else if((((Button)view).getText().toString().toLowerCase().equals("new"))){
            currentMenu = MENU_NEW_SUDOKU;
            invalidateOptionsMenu();
            Toolbar toolbar = (Toolbar) getActionBar(getWindow().getDecorView());
            toolbar.showOverflowMenu();
            getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
                @Override
                public void onMenuVisibilityChanged(boolean isVisible) {
                    if(!isVisible && currentMenu == MENU_NEW_SUDOKU){
                        currentMenu = MENU_ON_SUDOKU_PANEL;
                        invalidateMenu();
                    }
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        saveCurrentSudokuToSharedPreferences();
        super.onDestroy();
    }

    private void saveCurrentSudokuToSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("sudoku.cobyapps.com.sudoku",MODE_PRIVATE).edit();
        SudokuDatabaseDataHolder dataHolder = getSudokuDatabaseDataHolder(sudokuGridView.getDataHolder().getGrid());
        editor.putString(KEY_CURRENT_SUDOKU,dataHolder.getSudoku());
        editor.putString(KEY_IS_GIVEN, dataHolder.getIsGivens());
        editor.putString(KEY_NOTES, dataHolder.getNotes());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_difficulty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.easy){

        }else if(item.getItemId() == R.id.medium){

        }else if(item.getItemId() == R.id.difficult){

        }else if(item.getItemId() == R.id.expert){

        }else if(item.getItemId() == R.id.custom){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_layout, new CustomSudokuFragment(), CustomSudokuFragment.TAG);
            fragmentTransaction.addToBackStack(CustomSudokuFragment.TAG);
            fragmentTransaction.commit();
            currentMenu = MENU_ON_CUSTOM_DIFFICULTY;
            invalidateOptionsMenu();
        }else if (item.getItemId() == R.id.generate){
            CustomSudokuFragment customSudokuFragment = (CustomSudokuFragment)
                    getSupportFragmentManager().findFragmentByTag(CustomSudokuFragment.TAG);
            CustomSudokuRecyclerViewAdapter recyclerViewAdapter =
                    (CustomSudokuRecyclerViewAdapter) customSudokuFragment.getCustomSudokuRecyclerViewAdapter();
        }else if (item.getItemId() == R.id.save_sudoku){
            if(databaseAdapter.query().getCount() > 0){
                SimpleDialogFragmentFactory.
                        createDialogFragment(SimpleDialogFragmentFactory.DIALOG_FRAGMENT_OVERWRITE)
                        .show(getSupportFragmentManager(), null);
            }else{
                saveCurrentSudokuToDatabase();
            }
        }else if (item.getItemId() == R.id.load_sudoku){
            Cursor cursor = databaseAdapter.query();
            String string = "";
            while (cursor.moveToNext()){
                string += cursor.getString(1)+"\n"+cursor.getString(2)+"\n"+cursor.getString(3);
            }
            Log.wtf("DATA",string);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentMenu == MENU_ON_CUSTOM_DIFFICULTY){
            menu.clear();
            getMenuInflater().inflate(R.menu.custom_sudoku_menu, menu);
        }else if(currentMenu == MENU_ON_SUDOKU_PANEL){
            menu.clear();
            getMenuInflater().inflate(R.menu.options_menu_difficulty, menu);
        }else if(currentMenu == MENU_NEW_SUDOKU){
            menu.clear();
            getMenuInflater().inflate(R.menu.new_sudoku_menu, menu);
        }
        return true;
    }

    @Override
    public void invalidateMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void setCurrentMenu(int currentMenu) {
        this.currentMenu = currentMenu;
    }

    @Override
    public void saveCurrentSudokuToDatabase() {
        databaseAdapter.insert(getSudokuDatabaseDataHolder(sudokuGridView.getDataHolder().getGrid()));
    }


    public static ViewGroup getActionBar(View view) {
        try {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                if (viewGroup instanceof android.support.v7.widget.Toolbar) {
                    return viewGroup;
                }
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    ViewGroup actionBar = getActionBar(viewGroup.getChildAt(i));
                    if (actionBar != null) {
                        return actionBar;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public SudokuDatabaseDataHolder getSudokuDatabaseDataHolder (SudokuCellDataComponent [][] dataComponents) {
        String sudoku = "";
        String notes = "";
        String isGivens = "";
        for(int i = 0 ; i < 81 ; i++){
            int row = i/9;
            int column = i%9;
            SudokuCellDataComponent dataComponent = dataComponents[row][column];
            if(dataComponent.getIsGiven()){
                isGivens += "1";
            }else{
                isGivens += "0";
            }
            sudoku += dataComponent.getNumber();
            notes += dataComponent.getNotes() + " /";
            if(i!=80){
                notes += " ";
            }
        }
        return new SudokuDatabaseDataHolder(sudoku, isGivens, notes);
    }
    public DatabaseAdapter getDatabaseAdapter (){
        return databaseAdapter;
    }

    @Override
    public FragmentManager getTheFragmentManager() {
        return getSupportFragmentManager();
    }
}
