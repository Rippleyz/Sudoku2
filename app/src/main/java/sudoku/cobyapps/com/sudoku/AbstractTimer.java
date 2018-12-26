package sudoku.cobyapps.com.sudoku;

import java.util.ArrayList;

public abstract class AbstractTimer {
	private ArrayList observers = new ArrayList();

	public void addObserver(IObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(IObserver observer) {
		observers.remove(observer);
	}

	public void notifyObservers() {
		for (Object observer : observers) {
			IObserver o = (IObserver) observer;
			o.update(this.getStringTime());
		}
	}

	public abstract String getStringTime();

	public abstract void execute();

}
