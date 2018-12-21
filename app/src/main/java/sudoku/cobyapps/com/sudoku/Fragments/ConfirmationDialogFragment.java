package sudoku.cobyapps.com.sudoku.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class ConfirmationDialogFragment extends DialogFragmentWithTwoButtons {
    private static final String TITLE = "Confirm delete?";
    private static final String LEFT_BUTTON_TEXT = "Delete";
    private static final String RIGHT_BUTTON_TEXT = "Cancel";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        title.setText(TITLE);
        leftButton.setText(LEFT_BUTTON_TEXT);
        rightButton.setText(RIGHT_BUTTON_TEXT);
        super.onActivityCreated(savedInstanceState);
    }
}
