package sudoku.cobyapps.com.sudoku.dlx;


public class NullResultProcessor implements DLXResultProcessor {

	boolean keepGoing = false;

	public NullResultProcessor() {
		// Do nothing
	}

	public NullResultProcessor(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}

	public boolean processResult(DLXResult result) {
		return keepGoing;
	}

}
