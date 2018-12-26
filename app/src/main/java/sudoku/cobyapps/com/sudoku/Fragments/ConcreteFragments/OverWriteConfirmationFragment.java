package sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import sudoku.cobyapps.com.sudoku.Fragments.AbstractFragments.DialogFragmentWithTwoButtons;
import sudoku.cobyapps.com.sudoku.R;
import sudoku.cobyapps.com.sudoku.RecyclerViewAdapters.SavedSudokusRecyclerViewAdapter;
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
        interFragmentCommunicator.saveCurrentSudokuToDatabase();
        dismiss();
    }

    @Override
    protected void onRightButtonClick() {
        Bundle bundle = new Bundle();
        bundle.putInt(SavedSudokusFragment.KEY_RECYCLER_VIEW_STATE, SavedSudokusRecyclerViewAdapter.STATE_ON_OVERWRITE);
        SavedSudokusFragment savedSudokusFragment =
                (SavedSudokusFragment)
                        SimpleFragmentFactory.createFragment(SimpleFragmentFactory.FRAGMENT_SAVED_SUDOKUS);
        savedSudokusFragment.setArguments(bundle);
        FragmentManager fragmentManager = interFragmentCommunicator.getTheFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_layout, savedSudokusFragment, SavedSudokusFragment.TAG);
        fragmentTransaction.commit();
        dismiss();
    }
}
