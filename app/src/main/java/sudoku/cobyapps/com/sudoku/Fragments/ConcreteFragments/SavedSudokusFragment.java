package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;

import android.support.v7.widget.RecyclerView;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.FragmentWithRecyclerView;
import sudoku.cobyapps.com.sudoku.RecyclerViewAdapters.SavedSudokusRecyclerViewAdapter;

public class SavedSudokusFragment extends FragmentWithRecyclerView {
    public static final String TAG = "SavedSudokusFragment";
    public static final String KEY_RECYCLER_VIEW_STATE = "KEY_RECYCLER_VIEW_STATE";
    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter() throws Exception {
        return new SavedSudokusRecyclerViewAdapter(getActivity(), getArguments().getInt(KEY_RECYCLER_VIEW_STATE));
    }
}
