package sudoku.cobyapps.com.sudoku;

public class StringObserver implements IObserver {
	@Override
	public void update(String generatedString) {
		System.out.print("\r" + generatedString);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
