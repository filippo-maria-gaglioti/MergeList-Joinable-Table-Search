package lucenex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Parser {
	
	final static private int LIMITMAXDATA=4;
	InputStream is;

	public Parser(InputStream is) {
		this.is = is;
		if (is == null) { 
			throw new NullPointerException("Cannot find resource file");
		}
	}

	public void parseFile(IndexCreator indexer) throws Exception {
		JSONTokener tokener = new JSONTokener(is);
		int countTables=0;	//conta quante tables ha analizzato
		while(!tokener.end()) {
			JSONObject object = new JSONObject(tokener);
			System.out.println("Id tabella: " + object.getString("id"));
			String id = object.getString("id");
			object.getJSONArray("cells");
			JSONArray cells = object.getJSONArray("cells");
			//StringBuilder textField = new StringBuilder();
			List<String> texedFieldList=new ArrayList<>();
			//scorre tutte le celle della tabella
			for(int i = 0; i < cells.length(); i++) {
				JSONObject cella = cells.getJSONObject(i);
				//se non è un header lo inserisco nel documento
				if(!cella.getBoolean("isHeader")) 
				{
					texedFieldList.add(cella.getString("cleanedText"));
					//textField = textField.append(";"+cella.getString("cleanedText") + " ");
					//System.out.print("Campi: " + cella.getString("cleanedText") + "\n");
				}
			}
			String valuesInTable=this.formatValueString(texedFieldList);
			System.out.println(valuesInTable);
			indexer.addData(id, valuesInTable);
			tokener.next();
			countTables++;
			System.out.println("Tables analyzed:"+ countTables);
			if (countTables>LIMITMAXDATA)
			{
				System.out.println("Limit exceeded");
				indexer.commitData();
				countTables=0;
			}
		}
		indexer.commitData();
		indexer.closeWriter();
	}
	
	//crea la sequenza di valori dei campi trasformado tutte le parole in loweCase
	private String formatValueString(List<String> texedFieldList) {
		String s="";
		int conta=0;
		for (String curr: texedFieldList)
		{
				if (conta==texedFieldList.size()-1)
				{
					s=s.concat(curr.toLowerCase());
				}
				else
				{
			
					s=s.concat(curr.toLowerCase()+";");
					
				}
				conta++;
			
		}
		return s;
	}

	public InputStream getInputStream() {
		return is;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}




}
