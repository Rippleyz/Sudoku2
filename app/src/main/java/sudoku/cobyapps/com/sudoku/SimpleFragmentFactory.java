package sudoku.cobyapps.com.sudoku;

import android.support.v4.app.Fragment;

import sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments.CustomSudokuFragment;
import sudoku.cobyapps.com.sudoku.Fragments.ConcreteFragments.SavedSudokusFragment;

public class SimpleFragmentFactory {
    public static final String FRAGMENT_SAVED_SUDOKUS = "FRAGMENT_SAVED_SUDOKUS";
    public static final String FRAGMENT_CUSTOM_SUDOKU = "FRAGMENT_CUSTOM_SUDOKU";
    public static Fragment createFragment(String type){
        if(type.equalsIgnoreCase("FRAGMENT_SAVED_SUDOKUS")){
            return new SavedSudokusFragment();
        }else if(type.equalsIgnoreCase("FRAGMENT_CUSTOM_SUDOKU")){
            return new CustomSudokuFragment();
        }
        return null;
    }
}
