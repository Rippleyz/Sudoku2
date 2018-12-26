package sudoku.cobyapps.com.sudoku.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.InterFragmentCommunicator;
import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;

public class SavedSudokusRecyclerViewAdapter extends RecyclerView.Adapter {
    private Cursor cursor;
    private InterFragmentCommunicator interFragmentCommunicator;
    private LayoutInflater inflater;
    public SavedSudokusRecyclerViewAdapter(Context context){
        interFragmentCommunicator = (MainActivity)context;
        cursor = interFragmentCommunicator.getDatabaseAdapter().query();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.list_element_with_text_view, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder)holder;
        cursor.moveToPosition(position);
        myHolder.getTextView().setText(cursor.getString(0));
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    class Holder extends RecyclerView.ViewHolder{
        private TextView textView;
        public Holder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
        public TextView getTextView() {
            return textView;
        }
    }
}
