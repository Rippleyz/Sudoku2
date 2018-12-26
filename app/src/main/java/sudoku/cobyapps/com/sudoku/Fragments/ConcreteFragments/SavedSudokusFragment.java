package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;

import android.support.v7.widget.RecyclerView;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.FragmentWithRecyclerView;
import sudoku.cobyapps.com.sudoku.RecyclerViewAdapters.SavedSudokusRecyclerViewAdapter;

public class SavedSudokusFragment extends FragmentWithRecyclerView {
    public static final String TAG = "SavedSudokusFragment";
    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter(){
        return new SavedSudokusRecyclerViewAdapter(getActivity());
    }
}
