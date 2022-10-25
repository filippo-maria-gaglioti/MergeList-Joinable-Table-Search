package lucenex;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Parser {

	InputStream is;

	public Parser(InputStream is) {
		this.is = is;
		if (is == null) { 
			throw new NullPointerException("Cannot find resource file");
		}
	}

	public void parseFile(IndexCreator indexer) throws Exception {
		JSONTokener tokener = new JSONTokener(is);
		while(!tokener.end()) {
			JSONObject object = new JSONObject(tokener);
			System.out.println("Id tabella: " + object.getString("id"));
			String id = object.getString("id");
			object.getJSONArray("cells");
			JSONArray cells = object.getJSONArray("cells");
			StringBuilder textField = new StringBuilder();
			//scorre tutte le celle della tabella
			for(int i = 0; i < cells.length(); i++) {
				JSONObject cella = cells.getJSONObject(i);
				//se non è un header lo inserisco nel documento
				if(!cella.getBoolean("isHeader")) {
					textField = textField.append(cella.getString("cleanedText") + " ");
					System.out.print("Campi: " + cella.getString("cleanedText") + "\n");
				}
			}
			indexer.addData(id, textField.toString());
			tokener.next();
		}
		indexer.closeWriter();
	}

	public InputStream getInputStream() {
		return is;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}




}
