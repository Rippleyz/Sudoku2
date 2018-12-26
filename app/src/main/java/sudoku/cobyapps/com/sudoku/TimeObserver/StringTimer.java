package sudoku.cobyapps.com.sudoku.TimeObserver;

import java.util.Timer;
import java.util.TimerTask;

public class StringTimer extends AbstractTimer {
	private Timer timer;
	private int time = 0;
	private String stringTime = "00:00";
	public StringTimer() {
		timer = new Timer();
	}

	private void modifyStringAsTime() {
		stringTime = String.format("%d%d:%d%d", (time % 3600) / 600, (time % 600) / 60, (time % 60) / 10, time % 10);
	}

	@Override
	public String getStringTime() {
		return stringTime;
	}
	@Override
	public void execute() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				++time;
				modifyStringAsTime();
				notifyObservers();
			}
		}, 0, 1000);
	}
}
