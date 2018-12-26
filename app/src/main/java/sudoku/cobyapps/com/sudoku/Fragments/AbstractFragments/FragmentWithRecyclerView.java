package sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sudoku.cobyapps.com.sudoku.InterFragmentCommunicator;
import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;

public abstract class FragmentWithRecyclerView extends Fragment {
    private RecyclerView recyclerView;
    protected RecyclerView.Adapter recyclerViewAdapter;
    protected InterFragmentCommunicator interFragmentCommunicator;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_with_recycler_view, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        interFragmentCommunicator = (MainActivity)getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(getRecyclerViewAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        super.onActivityCreated(savedInstanceState);
    }
    public RecyclerView.Adapter getCustomSudokuRecyclerViewAdapter(){
        return recyclerViewAdapter;
    }
    protected abstract RecyclerView.Adapter getRecyclerViewAdapter();
}
