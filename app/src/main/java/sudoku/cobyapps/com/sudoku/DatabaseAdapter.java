package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseAdapter {

    class SQLiteHelper extends SQLiteOpenHelper {
        private static final String NAME_DATABASE = "Database";
        private static final int VERSION_DATABASE = 0;
        private static final String NAME_TABLE_SAVED_SUDOKUS = "NAME_TABLE_SAVED_SUDOKUS";
        private static final String COLUMN_ID = "COLUMN_ID";
        private static final String COLUMN_SUDOKU = "COLUMN_SUDOKU";
        private static final String COLUMN_SOLUTION = "COLUMN_SOLUTION";
        public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, NAME_DATABASE, null, VERSION_DATABASE);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
