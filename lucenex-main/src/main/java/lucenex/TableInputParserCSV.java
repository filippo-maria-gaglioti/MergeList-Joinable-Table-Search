package lucenex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class TableInputParserCSV {

	public TableInputParserCSV() {}

	/**
	 * Dato il reader del file csv e il nome della colonna da cui estrarre i dati, estraiamo i dati
	 * da utilizzare nella query
	 * @param columnIndex
	 * @param reader
	 * @return
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	private List<String> extractData(String columnIndex, CSVReader reader) throws CsvValidationException, IOException {
		String [] nextLine;
		int idPosition;
		List <String> output=new ArrayList<>();

		idPosition = Integer.parseInt(columnIndex);

		while ((nextLine = reader.readNext()) != null) {
			output.add(nextLine[idPosition]);
		}
		return output;
	}

	/**
	 * Applicando la levi dist vediamo quale risulta essere la colonna da cui estrarre i dati
	 * @param headers
	 * @param field
	 * @return
	 */
	private List<String> getHeaderToSearch(String[] headers, String field) {
		String out="";	//il campo da trovare
		String index="0";
		int conta=0;	//indice di colonna
		int min=100000;	//il valore minimo iniziale

		LevenshteinDistance j= new LevenshteinDistance();

		for (String s: headers) {
			int curr=0;
			curr=j.apply(field, s.toLowerCase());

			if (curr<min) {
				min=curr;
				out=s;
				index=Integer.toString(conta);
			}
			conta++;
		}
		List<String> output=new ArrayList<>();
		output.add(out);
		output.add(index);
		return output;
	}

	public List<String> getValuesOfFieldInTable(String fileName,String field) throws FileNotFoundException, IOException, CsvException {
		List<String> out;
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); //definiamo ; come separatore
		try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
				.withCSVParser(csvParser)
				.build()) {
			String[] r = reader.readNext();			//contiente tutti gli header in una Stringa
			List<String> headerToSearch = this.getHeaderToSearch(r, field);	//cerchiamo l'indice di header da analizzare
			System.out.println("[COLUMN TO QUERY]: "+headerToSearch.get(0));
			out=this.extractData(headerToSearch.get(1),reader);
		}
		return out;
	}

}
