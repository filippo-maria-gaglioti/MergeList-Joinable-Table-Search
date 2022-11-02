package lucenex;

import java.util.List;

public class ValuesFormatter {
	
	private String sep;

	public ValuesFormatter() {
		this.sep = new Separator().getSeparator();
	}

	public String formatValueString(List<String> texedFieldList) {
		String s = "";
		int conta = 0;
		for (String curr: texedFieldList) {
			if (conta == texedFieldList.size() - 1) {
				s = s.concat(curr.toLowerCase() + this.sep);
			} else {
				s = s.concat(curr.toLowerCase() + this.sep);
			}
			conta++;
		}
		return s;
	}

}
