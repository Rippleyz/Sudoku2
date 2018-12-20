package sudoku.cobyapps.com.sudoku.Fragments;

import android.os.Bundle;

public class CongratulationsFragment extends DialogFragmentWithOneButton {
    private static final String TITLE = "Congratulations";
    private static final String BUTTON_TEXT = "OK";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        title.setText(TITLE);
        button.setText(BUTTON_TEXT);
        super.onActivityCreated(savedInstanceState);
    }
}
