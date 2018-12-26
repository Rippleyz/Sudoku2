package sudoku.cobyapps.com.sudoku.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.R;

public abstract class DialogFragmentWithOneButton extends DialogFragment implements View.OnClickListener {
    private TextView title;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_with_one_button, container, false);
        title = rootView.findViewById(R.id.title);
        button = rootView.findViewById(R.id.button);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        title.setText(getTitleText());
        button.setText(getButtonText());
        button.setOnClickListener(this);
        return rootView;
    }
    @Override
    public abstract void onClick(View view);
    protected abstract String getTitleText();
    protected abstract String getButtonText();
}
