package sudoku.cobyapps.com.sudoku.Fragments;

import android.support.v7.widget.RecyclerView;

import sudoku.cobyapps.com.sudoku.CustomSudokuRecyclerViewAdapter;
import sudoku.cobyapps.com.sudoku.MainActivity;

public class CustomSudokuFragment extends FragmentWithRecyclerView {
    public static final String TAG = "CustomSudokuFragment";
    @Override
    public void onDestroy() {
        interFragmentCommunicator.setCurrentMenu(MainActivity.MENU_ON_SUDOKU_PANEL);
        interFragmentCommunicator.invalidateMenu();
        super.onDestroy();
    }
    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter() {
        return new CustomSudokuRecyclerViewAdapter(getActivity());
    }
}
