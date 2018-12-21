package sudoku.cobyapps.com.sudoku.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sudoku.cobyapps.com.sudoku.R;

public class CustomSudokuFragment extends Fragment{
    private RecyclerView recyclerView;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_sudoku_layout, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        return rootView;
    }
}
