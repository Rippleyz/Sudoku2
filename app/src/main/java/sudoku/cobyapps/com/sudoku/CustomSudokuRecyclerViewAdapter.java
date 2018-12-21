package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomSudokuRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final String LOWER_BOUND_OF_NUMBER_OF_UNKNOWNS = "Lower bound of unknowns : ";
    private static final String UPPER_BOUND_OF_NUMBER_OF_UNKNOWNS = "Upper bound of unknowns : ";
    private static final String LOWER_BOUND_OF_DIFFICULTY_SCORE = "Lower bound of difficulty score : ";
    private static final String UPPER_BOUND_OF_DIFFICULTY_SCORE = "Upper bound of difficulty score : ";
    private ArrayList <EditText> editTexts;
    private LayoutInflater inflater;
    public CustomSudokuRecyclerViewAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editTexts = new ArrayList <EditText> ();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_element_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            ((ViewHolder) holder).getTextView().setText(LOWER_BOUND_OF_NUMBER_OF_UNKNOWNS);
        }else if(position == 1){
            ((ViewHolder) holder).getTextView().setText(UPPER_BOUND_OF_NUMBER_OF_UNKNOWNS);
        }else if(position == 2){
            ((ViewHolder) holder).getTextView().setText(LOWER_BOUND_OF_DIFFICULTY_SCORE);
        }else{
            ((ViewHolder) holder).getTextView().setText(UPPER_BOUND_OF_DIFFICULTY_SCORE);
        }
        editTexts.add(((ViewHolder) holder).getEditText());
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private EditText editText;
        private TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
            textView = itemView.findViewById(R.id.textView);
        }

        public EditText getEditText() {
            return editText;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        public TextView getTextView() {
           return textView;
        }
    }
    public int getLowerBoundOfNumberOfUnknowns(){
        return Integer.parseInt(editTexts.get(0).getText().toString());
    }
    public int getUpperBoundOfNumberOfUnknowns(){
        return Integer.parseInt(editTexts.get(1).getText().toString());
    }
    public int getLowerBoundOfDifficultyScore (){
        return Integer.parseInt(editTexts.get(2).getText().toString());
    }
    public int getUpperBoundOfDifficultyScore(){
        return Integer.parseInt(editTexts.get(3).getText().toString());
    }
}
