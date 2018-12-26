package sudoku.cobyapps.com.sudoku;

public class Test {
	public static void main(String[] args) {
		StringTimer stringTimer = new StringTimer();
		IObserver iObserver = new StringObserver();
		stringTimer.addObserver(iObserver);
		stringTimer.execute();
	}
}
