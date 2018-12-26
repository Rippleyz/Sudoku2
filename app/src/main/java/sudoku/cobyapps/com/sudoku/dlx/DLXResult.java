package sudoku.cobyapps.com.sudoku.dlx;

import java.util.Iterator;
import java.util.List;

public class DLXResult {

	private final List<List<Object> > resultData;

	public DLXResult(List<List<Object>> resultData) {
		this.resultData = resultData;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		for (final List<Object> row : resultData) {
			for (final Object label : row) {
				buffer.append(label.toString());
				buffer.append(' ');
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}

	public Iterator<List<Object>> rows() {
		return resultData.iterator();
	}

}
