package sudoku.cobyapps.com.sudoku.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.R;

public class DialogFragmentWithOneButton extends DialogFragment {
    protected TextView title;
    protected Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_with_one_button, container, false);
        title = rootView.findViewById(R.id.title);
        button = rootView.findViewById(R.id.button);
        return rootView;
    }
}
