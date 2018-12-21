package sudoku.cobyapps.com.sudoku.Fragments;

import android.app.DialogFragment;

public class SimpleDialogFragmentFactory {

    public DialogFragment createFragment(String type) {
        DialogFragment dialogFragment = null;

        if (type.equalsIgnoreCase("confirmation"))
            dialogFragment = new ConfirmationDialogFragment();
        else if (type.equalsIgnoreCase("congratulations"))
            dialogFragment = new CongratulationsFragment();
        else if (type.equalsIgnoreCase("quit"))
            dialogFragment = new QuitDialogFragment();

        return dialogFragment;
    }
}
