package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.DialogFragmentWithTwoButtons;
import sudoku.cobyapps.com.sudoku.R;
import sudoku.cobyapps.com.sudoku.SimpleFragmentFactory;

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
        SavedSudokusFragment savedSudokusFragment =
                (SavedSudokusFragment)
                        SimpleFragmentFactory.createFragment(SimpleFragmentFactory.FRAGMENT_SAVED_SUDOKUS);
        FragmentManager fragmentManager = interFragmentCommunicator.getTheFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_layout, savedSudokusFragment, SavedSudokusFragment.TAG);
        fragmentTransaction.commit();
        dismiss();
    }
}
