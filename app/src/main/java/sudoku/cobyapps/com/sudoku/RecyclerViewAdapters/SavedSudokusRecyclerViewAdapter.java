package sudoku.cobyapps.com.sudoku.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.DatabaseAdapter;
import sudoku.cobyapps.com.sudoku.InterFragmentCommunicator;
import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;
import sudoku.cobyapps.com.sudoku.SudokuCellDataComponent;

public class SavedSudokusRecyclerViewAdapter extends RecyclerView.Adapter {
    private Cursor cursor;
    private InterFragmentCommunicator interFragmentCommunicator;
    private LayoutInflater inflater;
    private static final int STATE_ON_OVERWRITE = 0;
    private static final int STATE_ON_LOAD = 1;
    private int state;
    public SavedSudokusRecyclerViewAdapter(Context context, int state) throws Exception {
        interFragmentCommunicator = (MainActivity)context;
        cursor = interFragmentCommunicator.getDatabaseAdapter().query();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(state == STATE_ON_LOAD || state == STATE_ON_OVERWRITE){
            this.state = state;
        }else{
            throw new Exception("Invalid State");
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.list_element_with_text_view, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Holder myHolder = (Holder)holder;
        cursor.moveToPosition(position);
        myHolder.getTextView().setText(cursor.getString(DatabaseAdapter.INDEX_DATE));
        myHolder.setDatabaseId(cursor.getInt(DatabaseAdapter.INDEX_ID));
        myHolder.getRelativeLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == STATE_ON_OVERWRITE){


                }else if (state == STATE_ON_LOAD){
                    DatabaseAdapter databaseAdapter = interFragmentCommunicator.getDatabaseAdapter();
                    int databaseId = myHolder.getDatabaseId();
                    Cursor cursor = databaseAdapter.query(databaseId);
                    cursor.moveToNext();
                    String sudoku = cursor.getString(DatabaseAdapter.INDEX_SUDOKU);
                    String notes = cursor.getString(DatabaseAdapter.INDEX_NOTES);
                    String isGivens = cursor.getString(DatabaseAdapter.INDEX_IS_GIVENS);
                    SudokuCellDataComponent
                            [][] sudokuCellDataComponents
                            = interFragmentCommunicator
                            .getSudokuCellDataComponentFromDataStrings(sudoku, notes, isGivens);


                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    class Holder extends RecyclerView.ViewHolder{
        private TextView textView;
        private RelativeLayout relativeLayout;
        private int databaseId;
        public Holder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
        public TextView getTextView() {
            return textView;
        }

        public RelativeLayout getRelativeLayout() {
            return relativeLayout;
        }
        public void setDatabaseId (int databaseId){
            this.databaseId = databaseId;
        }
        public int getDatabaseId (){
            return databaseId;
        }
    }
}
