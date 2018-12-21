package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class CustomSudokuRecyclerViewAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    CustomSudokuRecyclerViewAdapter(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_element_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            ((ViewHolder) holder).getEditText().setHint("Lower bound of number of unknowns : ");
        }else if(position == 1){
            ((ViewHolder) holder).getEditText().setHint("Upper bound of number of unknowns : ");
        }else if(position == 2){
            ((ViewHolder) holder).getEditText().setHint("Lower bound of difficulty score : ");
        }else{
            ((ViewHolder) holder).getEditText().setHint("Upper bound of difficulty score : ");
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private EditText editText;
        public ViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
        }

        public EditText getEditText() {
            return editText;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }
    }
}
