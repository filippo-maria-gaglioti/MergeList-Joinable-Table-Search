package lucenex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.opencsv.exceptions.CsvException;

public class QueryCreator {

	String toSearch;          //nome della tabella su cui effettuare la query
	String campo;
	
	public QueryCreator(String toSearch, String campo) {
		this.toSearch = toSearch;
		this.campo = campo;
	}
	
	public void doQuery() throws FileNotFoundException, IOException, CsvException {
		System.out.println("======QUERY========");
		String fileName = new InputTableSearcherCSV().getTablePath(toSearch); //acquisizione del path della tabella da cercare

		System.out.println("[FILE NAME TO QUEY]: " + fileName);

		TableInputParserCSV tip = new TableInputParserCSV();
		List<String>out = tip.getValuesOfFieldInTable(fileName, campo);	//acquisizione dati dal file e dalla colonna definita
		ValuesFormatter vf = new ValuesFormatter();
		String valuesToSearch = vf.formatValueString(out);		//formattazione elementi estartti-> val1;val2;...;valn

		System.out.println("[VALUES TO SEARCH]: " + valuesToSearch);
		MergeList mg = new MergeList();						
		mg.runMergeListAlgo(valuesToSearch);		//esecuzione algo mergeList
	}

	public void run() {
		try {
			this.doQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
