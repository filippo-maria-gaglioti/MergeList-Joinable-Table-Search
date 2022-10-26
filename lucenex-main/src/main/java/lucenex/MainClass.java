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
		
		
		DataCreator dc=new DataCreator();
		dc.run();
		
		System.out.println("======QUERY========");
		String toSearch="studenti";
		String fileName= new InputTableSearcherCSV().getTablePath(toSearch);
		
		System.out.println("[FILE NAME TO QUEY]: "+fileName);
		
		TableInputParserCSV tip=new TableInputParserCSV();
		List<String>out=tip.getValuesOfFieldInTable(fileName, "matricola");
		ValuesFormatter vf= new ValuesFormatter();
		String valuesToSearch=vf.formatValueString(out);
		
		System.out.println("[VALUES TO SEARCH]: "+valuesToSearch);
		mergeList mg= new mergeList();
		mg.runMergeListAlgo(valuesToSearch);
		
	}

}
