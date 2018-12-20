package sudoku.cobyapps.com.sudoku.Fragments;

import android.os.Bundle;

public class QuitDialogFragment extends DialogFragmentWithTwoButtons {
    private static final String TITLE = "Confirm quit?";
    private static final String LEFT_BUTTON_TEXT = "Yes";
    private static final String RIGHT_BUTTON_TEXT = "No";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        title.setText(TITLE);
        leftButton.setText(LEFT_BUTTON_TEXT);
        rightButton.setText(RIGHT_BUTTON_TEXT);
        super.onActivityCreated(savedInstanceState);
    }
}
