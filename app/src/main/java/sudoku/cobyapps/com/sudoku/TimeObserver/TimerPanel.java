package sudoku.cobyapps.com.sudoku.TimeObserver;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sudoku.cobyapps.com.sudoku.MainActivity;
import sudoku.cobyapps.com.sudoku.R;

public class TimerPanel implements IObserver{
    private TextView textView;
    private Context context;
    public TimerPanel(RelativeLayout layout, Context context){
        textView = layout.findViewById(R.id.timer_text_view);
        this.context = context;
    }

    @Override
    public void update(final String generatedString) {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(generatedString);
            }
        });
    }
}
