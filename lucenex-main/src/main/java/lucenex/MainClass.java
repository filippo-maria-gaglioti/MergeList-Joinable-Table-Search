package lucenex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.codecs.Codec;
import org.apache.lucene.queryparser.surround.query.SimpleTerm;

import com.opencsv.exceptions.CsvException;

public class MainClass 
{
	public static void main(String[] args) throws FileNotFoundException, IOException, CsvException
	{
		
		
		DataCreator dc=new DataCreator();		//indicizzazione dei valori nel db
		dc.run();			
			
		System.out.println("======QUERY========");
		String toSearch="studenti";					//presa in input del nome della tabella .csv da cui prendere i dati di query per il join
		String fileName= new InputTableSearcherCSV().getTablePath(toSearch); //acquisizoione del path della tabella da cercare
		
		System.out.println("[FILE NAME TO QUEY]: "+fileName);
		
		TableInputParserCSV tip=new TableInputParserCSV();
		List<String>out=tip.getValuesOfFieldInTable(fileName, "matricola");	//acquisizione dati dal file e dalla colonna definita
		ValuesFormatter vf= new ValuesFormatter();
		String valuesToSearch=vf.formatValueString(out);		//formattazione elementi estartti-> val1;val2;...;valn
		
		System.out.println("[VALUES TO SEARCH]: "+valuesToSearch);
		mergeList mg= new mergeList();						
		mg.runMergeListAlgo(valuesToSearch);		//esecuzione algo mergeList
		
	}

}
