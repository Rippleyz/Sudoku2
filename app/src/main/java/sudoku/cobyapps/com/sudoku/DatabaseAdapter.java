package sudoku.cobyapps.com.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import sudoku.cobyapps.com.sudoku.Utilities.MyDatePicker;

public class DatabaseAdapter {
    private static SQLiteDatabase database;
    private static SQLiteHelper helper;
    private static DatabaseAdapter databaseAdapter = new DatabaseAdapter();
    private static Context mainContext;
    public static final int INDEX_ID = 0;
    public static final int INDEX_SUDOKU = 1;
    public static final int INDEX_NOTES = 2;
    public static final int INDEX_IS_GIVENS = 3;
    public static final int INDEX_DATE = 4;
    public static final int INDEX_TIME_ELAPSED = 5;
    private DatabaseAdapter (){

    }

    public static DatabaseAdapter getInstance(Context context){
        mainContext = context;
        helper = new SQLiteHelper(context);
        database = helper.getWritableDatabase();
        return databaseAdapter;
    }

    public long insert(SudokuDatabaseDataHolder databaseDataHolder){
        ContentValues contentValues = getContentValues(databaseDataHolder);
        return database.insert(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS, null, contentValues);
    }

    public int delete(int id){
        return database.delete(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS,
                SQLiteHelper.COLUMN_ID+"=?", new String [] {id+""});
    }
    public int update (int id, SudokuDatabaseDataHolder databaseDataHolder){
        ContentValues contentValues = getContentValues(databaseDataHolder);
        return database.update(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS,
                contentValues, SQLiteHelper.COLUMN_ID+"=?",
                new String [] {id+""});
        //TODO : GET TIME, TIME ELAPSED & UPDATE IT
    }
    public Cursor query(){
        return database.query(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS,
                null,
                null,
                null,
                null,
                null,
                null);
    }
    @NonNull
    private ContentValues getContentValues(SudokuDatabaseDataHolder databaseDataHolder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.COLUMN_SUDOKU, databaseDataHolder.getSudoku());
        contentValues.put(SQLiteHelper.COLUMN_IS_GIVENS, databaseDataHolder.getIsGivens());
        contentValues.put(SQLiteHelper.COLUMN_NOTES, databaseDataHolder.getNotes());
        contentValues.put(SQLiteHelper.COLUMN_DATE, MyDatePicker.getCurrentDate(mainContext));
        contentValues.put(SQLiteHelper.COLUMN_TIME_ELAPSED, "0");
        //TODO :TIME ELAPSED REPLACE WITH 0
        return contentValues;
    }
    static class SQLiteHelper extends SQLiteOpenHelper {
        private static final String NAME_DATABASE = "Database";
        private static final int VERSION_DATABASE = 1;
        private static final String NAME_TABLE_SAVED_SUDOKUS = "NAME_TABLE_SAVED_SUDOKUS";
        private static final String COLUMN_ID = "COLUMN_ID";
        private static final String COLUMN_SUDOKU = "COLUMN_SUDOKU";
        private static final String COLUMN_NOTES = "COLUMN_NOTES";
        private static final String COLUMN_IS_GIVENS = "COLUMN_IS_GIVENS";
        private static final String COLUMN_DATE = "COLUMN_DATE";
        private static final String COLUMN_TIME_ELAPSED = "COLUMN_TIME_ELAPSED";
        public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, NAME_DATABASE, null, VERSION_DATABASE);
        }

        public SQLiteHelper (Context context){
            super(context, NAME_DATABASE, null, VERSION_DATABASE);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try{
                sqLiteDatabase.execSQL("CREATE TABLE "+NAME_TABLE_SAVED_SUDOKUS+"" +
                        " ( "+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " "+COLUMN_SUDOKU+" TEXT, "+COLUMN_IS_GIVENS+" TEXT, "+COLUMN_NOTES+" TEXT," +
                        " "+COLUMN_DATE+" TEXT, "+COLUMN_TIME_ELAPSED+" TEXT);");
                Log.wtf("OK","SUCCESS");
            }catch (Exception e){
                Log.wtf("ERROR","CREATION FAILED");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            try {
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_SAVED_SUDOKUS);
                onCreate(sqLiteDatabase);
            } catch (SQLiteException e) {
                Log.wtf("ERROR","UPDATE FAILED");
            }
        }
    }
}
