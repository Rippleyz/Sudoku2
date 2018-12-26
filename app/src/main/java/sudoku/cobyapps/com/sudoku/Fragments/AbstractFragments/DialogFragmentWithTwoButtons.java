package sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.InterFragmentCommunicator;
import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;

public abstract class DialogFragmentWithTwoButtons extends DialogFragment implements View.OnClickListener {
    protected TextView title;
    protected Button leftButton;
    protected Button rightButton;
    protected InterFragmentCommunicator interFragmentCommunicator;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        interFragmentCommunicator = (MainActivity)getActivity();
        View rootView = inflater.inflate(R.layout.fragment_layout_with_two_button, container, false);
        title = rootView.findViewById(R.id.title);
        leftButton = rootView.findViewById(R.id.left_button);
        rightButton = rootView.findViewById(R.id.right_button);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        title.setText(getTitleText());
        leftButton.setText(getLeftButtonText());
        rightButton.setText(getRightButtonText());

        return rootView;
    }
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.left_button){
            onLeftButtonClick();
        }else if (view.getId() == R.id. right_button){
            onRightButtonClick();
        }
    }
    protected abstract String getTitleText();
    protected abstract String getRightButtonText();
    protected abstract String getLeftButtonText();
    protected abstract void onLeftButtonClick();
    protected abstract void onRightButtonClick();

}
