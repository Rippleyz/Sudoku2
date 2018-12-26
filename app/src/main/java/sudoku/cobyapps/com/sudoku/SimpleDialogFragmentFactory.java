package sudoku.cobyapps.com.sudoku;


import android.support.v4.app.DialogFragment;

import sudoku.cobyapps.com.sudoku.Fragments.ConfirmationDialogFragment;
import sudoku.cobyapps.com.sudoku.Fragments.CongratulationsFragment;
import sudoku.cobyapps.com.sudoku.Fragments.OverWriteConfirmationFragment;
import sudoku.cobyapps.com.sudoku.Fragments.QuitDialogFragment;

public class SimpleDialogFragmentFactory {
    public static final String DIALOG_FRAGMENT_CONFIRM_DELETE = "DIALOG_FRAGMENT_CONFIRM_DELETE";
    public static final String DIALOG_FRAGMENT_CONGRATULATIONS = "DIALOG_FRAGMENT_CONGRATULATIONS";
    public static final String DIALOG_FRAGMENT_QUIT = "DIALOG_FRAGMENT_QUIT";
    public static final String DIALOG_FRAGMENT_OVERWRITE = "DIALOG_FRAGMENT_OVERWRITE";
    public static DialogFragment createDialogFragment(String type) {
        DialogFragment dialogFragment = null;
        if (type.equalsIgnoreCase(DIALOG_FRAGMENT_CONFIRM_DELETE))
            dialogFragment = new ConfirmationDialogFragment();
        else if (type.equalsIgnoreCase(DIALOG_FRAGMENT_CONGRATULATIONS))
            dialogFragment = new CongratulationsFragment();
        else if (type.equalsIgnoreCase(DIALOG_FRAGMENT_QUIT))
            dialogFragment = new QuitDialogFragment();
        else if (type.equalsIgnoreCase(DIALOG_FRAGMENT_OVERWRITE))
            dialogFragment = new OverWriteConfirmationFragment();
        return dialogFragment;
    }
}
