package sudoku.cobyapps.com.sudoku;

import java.util.Timer;
import java.util.TimerTask;

public class StringTimer extends AbstractTimer {
	private int time = 0;
	private String stringTime = "00:00";

	public StringTimer() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				++time;
				modifyStringAsTime();
			}
		}, 1000, 1000);
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
		while (true) {
			notifyObservers();
		}
	}
}
