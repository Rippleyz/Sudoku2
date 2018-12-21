package sudoku.cobyapps.com.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseAdapter {
    private static SQLiteDatabase database;
    private static SQLiteHelper helper;
    private static DatabaseAdapter databaseAdapter = new DatabaseAdapter();
    private DatabaseAdapter (){

    }

    public static DatabaseAdapter getInstance(Context context){
        helper = new SQLiteHelper(context);
        database = helper.getWritableDatabase();
        return databaseAdapter;
    }

    public long insert(String sudoku){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.COLUMN_SUDOKU, sudoku);
        return database.insert(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS, null, contentValues);
    }

    public int delete(int id){
        return database.delete(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS,
                SQLiteHelper.COLUMN_ID+"=?", new String [] {id+""});
    }
    public int update (int id, int solution){
        ContentValues contentValues = new ContentValues();
        return database.update(SQLiteHelper.NAME_TABLE_SAVED_SUDOKUS,
                contentValues, SQLiteHelper.COLUMN_ID+"=?",
                new String [] {id+""});
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
    static class SQLiteHelper extends SQLiteOpenHelper {
        private static final String NAME_DATABASE = "Database";
        private static final int VERSION_DATABASE = 1;
        private static final String NAME_TABLE_SAVED_SUDOKUS = "NAME_TABLE_SAVED_SUDOKUS";
        private static final String COLUMN_ID = "COLUMN_ID";
        private static final String COLUMN_SUDOKU = "COLUMN_SUDOKU";
        public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, NAME_DATABASE, null, VERSION_DATABASE);
        }

        public SQLiteHelper (Context context){
            super(context, NAME_DATABASE, null, VERSION_DATABASE);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try{
                sqLiteDatabase.
                        execSQL("CREATE TABLE "+NAME_TABLE_SAVED_SUDOKUS+"" +
                                " ( "+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                                " "+COLUMN_SUDOKU+");");
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
