package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class SavedSudokusRecyclerViewAdapter extends RecyclerView.Adapter {
    private Cursor cursor;
    private InterFragmentCommunicator interFragmentCommunicator;
    SavedSudokusRecyclerViewAdapter (Context context){
        interFragmentCommunicator = (MainActivity)context;
        cursor = interFragmentCommunicator.getDatabaseAdapter().query();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}
