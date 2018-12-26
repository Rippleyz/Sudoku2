package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;

import android.view.View;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.DialogFragmentWithTwoButtons;

public class QuitDialogFragment extends DialogFragmentWithTwoButtons {
    private static final String TITLE = "Confirm quit?";
    private static final String LEFT_BUTTON_TEXT = "Yes";
    private static final String RIGHT_BUTTON_TEXT = "No";

    @Override
    protected String getTitleText() {
        return TITLE;
    }

    @Override
    protected String getRightButtonText() {
        return RIGHT_BUTTON_TEXT;
    }

    @Override
    protected String getLeftButtonText() {
        return LEFT_BUTTON_TEXT;
    }

    @Override
    protected void onLeftButtonClick() {

    }

    @Override
    protected void onRightButtonClick() {

    }

    @Override
    public void onClick(View view) {

    }
}
