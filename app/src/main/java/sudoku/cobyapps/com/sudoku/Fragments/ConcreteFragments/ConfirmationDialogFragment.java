package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.DialogFragmentWithTwoButtons;

public class ConfirmationDialogFragment extends DialogFragmentWithTwoButtons {
    private static final String TITLE = "Confirm delete?";
    private static final String LEFT_BUTTON_TEXT = "Delete";
    private static final String RIGHT_BUTTON_TEXT = "Cancel";

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

}
