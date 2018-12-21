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

public abstract class AbstractDialogFragment extends DialogFragment {
    private TextView title;
    private Button leftButton;
    private Button rightButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        title = rootView.findViewById(R.id.title);
        leftButton = rootView.findViewById(R.id.left_button);
        rightButton = rootView.findViewById(R.id.right_button);
        return rootView;
    }
}
