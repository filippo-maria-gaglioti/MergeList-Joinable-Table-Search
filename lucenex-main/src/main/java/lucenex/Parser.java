package lucenex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Classe specializzata nel parsing del file json di tabelle.
 * Per ogni tabella, analizza il contenuto, memorizza il nome della tabella ed estrae i dati delle colonne.
 * Genera una stringa value1;value2 contenente tutti i valori individuati da indicizzare nell'indice luceene.
 * 
 * Effettua il commit del writer ogni N tabelle lette, per ridurre la frequenza di commit e limitare lo 
 * spazio in memoria dei dati da indicizzare.
 * 
 * L'indicizzazione è delegata alla classe IndexCreator
 * 
 * @author 
 *
 */
public class Parser {

	final static private int LIMITMAXDATA = 1500;    //limite di tabelle da parsare prima del commit
	InputStream is;

	public Parser(InputStream is) {
		this.is = is;
		if (is == null) { 
			throw new NullPointerException("Cannot find resource file");
		}
	}

	/**
	 * Metodo di parsing del file .JSON.
	 * Analizza ciascuna tabella ed accede ai suoi elementi, estraendo id tabella e valore dei campi.
	 * I valore di campi estratto viene concatenato con separatore ; ed inviato all'indexter.
	 * 
	 * Gestione dinamica dei commit
	 * @param indexer
	 * @throws Exception
	 */
	public void parseFile(IndexCreator indexer) throws Exception {
		int countTables = 0;	//conta quante tables ha analizzato
		int totTables = 0;
		long start = System.currentTimeMillis();
		
		JSONTokener tokener = new JSONTokener(is);
		ValuesFormatter vf = new ValuesFormatter();
		while(!tokener.end()) {
			JSONObject object = new JSONObject(tokener);
			String id = object.getString("id");
			object.getJSONArray("cells");
			JSONArray cells = object.getJSONArray("cells");
			List<String> texedFieldList = new ArrayList<>();
			//scorre tutte le celle della tabella
			for(int i = 0; i < cells.length(); i++) {
				JSONObject cella = cells.getJSONObject(i);
				//se non è un header lo inserisco nel documento
				if(!cella.getBoolean("isHeader")) 
				{
					texedFieldList.add(cella.getString("cleanedText"));
				}
			}
			String valuesInTable = vf.formatValueString(texedFieldList);	//trasformo la lista di valori in una stringa val1;val2...
			indexer.addData(id, valuesInTable);							//indicizzo in locale (no commit)
			tokener.next();
			countTables++;
			totTables++;
			//se ho analizzato un numero di tabelle maggiore della soglia
			if (countTables > LIMITMAXDATA)
			{
				//commit dei dati presenti in centrale e azzero il numero di tabelle analizzate
				System.out.println("[COMMIT]: max number of tables processed before commit");
				System.out.println("[TABLES ANALYZED]: " + totTables);
				indexer.commitData();
				countTables = 0;
			}
		}
		
		indexer.commitData();
		indexer.closeWriter();
		long end = System.currentTimeMillis();
		float time = (float)((end - start)/1000)/60;
		System.out.println("[TABLES ANALYZED]: " + totTables);
		System.out.println("[TEMPO IMPIEGATO]: " + time + " minuti");
	}

	public InputStream getInputStream() {
		return is;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}

}
