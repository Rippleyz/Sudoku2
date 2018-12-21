package sudoku.cobyapps.com.sudoku.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sudoku.cobyapps.com.sudoku.CustomSudokuRecyclerViewAdapter;
import sudoku.cobyapps.com.sudoku.InterFragmentCommunicator;
import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;

public class CustomSudokuFragment extends Fragment {
    private RecyclerView recyclerView;
    private CustomSudokuRecyclerViewAdapter recyclerViewAdapter;
    private InterFragmentCommunicator interFragmentCommunicator;
    public static final String TAG = "CustomSudokuFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_sudoku_layout, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        interFragmentCommunicator = (MainActivity) getActivity();
        recyclerViewAdapter = new CustomSudokuRecyclerViewAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        super.onActivityCreated(savedInstanceState);
    }
    public CustomSudokuRecyclerViewAdapter getCustomSudokuRecyclerViewAdapter(){
        return recyclerViewAdapter;
    }

    @Override
    public void onDestroy() {
        interFragmentCommunicator.setCurrentMenu(MainActivity.MENU_ON_SUDOKU_PANEL);
        interFragmentCommunicator.invalidateMenu();
        super.onDestroy();
    }
}
