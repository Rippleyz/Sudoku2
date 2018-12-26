package sudoku.cobyapps.com.sudoku.Fragments;

import android.view.View;

public class CongratulationsFragment extends DialogFragmentWithOneButton {
    private static final String TITLE = "Congratulations";
    private static final String BUTTON_TEXT = "OK";

    @Override
    protected String getTitleText() {
        return TITLE;
    }

    @Override
    protected String getButtonText() {
        return BUTTON_TEXT;
    }

    @Override
    public void onClick(View view) {

    }
}
