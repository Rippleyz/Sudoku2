package sudoku.cobyapps.com.sudoku.Fragments;

public class OverWriteConfirmationFragment extends DialogFragmentWithTwoButtons {
    private static final String TITLE = "Overwrite to existing save?";
    private static final String LEFT_BUTTON_TEXT = "No";
    private static final String RIGHT_BUTTON_TEXT = "Yes";

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
