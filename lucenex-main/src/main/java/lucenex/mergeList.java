package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.pattern.PatternTokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class mergeList {
	
	
	public mergeList()
	{
		
	}
	
	public void runMergeListAlgo(String valuesToSearch)
	{

		Path path = Paths.get("indexedFiles"); 
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {

				
				HashMap<Integer,Integer> results = setQuery(valuesToSearch, reader);
				printMap(results,reader);

			} catch (Exception e) {
				e.printStackTrace();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**/
	private static HashMap<Integer, Integer> setQuery(String sQuery, IndexReader reader) throws Exception {
		
		Map<String,String> terms2replace=new HashMap<>();
		terms2replace.put("pattern", ";");
		terms2replace.put("group", "-1");
		
		Analyzer a = CustomAnalyzer.builder().withTokenizer(PatternTokenizerFactory.class,terms2replace).build();
		QueryParser parser = new QueryParser("", a);
		Query query = parser.parse(sQuery);

	
		
		
		String[] lista= sQuery.split(";"); 
		

		//faccio il merge
		HashMap<Integer,Integer> sortbycount = merge(reader, lista);
		return sortbycount;
	}

	/*MERGE DELLE POSTING LIST DEI TERMINI DELLA QUERY*/
	private static HashMap<Integer,Integer> merge(IndexReader reader, String[] lista) throws Exception {
		HashMap<Integer, Integer> set2count = new HashMap<>();
		for(String s: lista) {
			//per ogni token della query ricavo la posting list
			PostingsEnum posting = MultiTerms.getTermPostingsEnum(reader, "", new BytesRef(s));
			//se esiste
			if(posting != null) {
				
				int docid;
				//scorro la posting list
				while ( ( docid = posting.nextDoc() ) != PostingsEnum.NO_MORE_DOCS ) {
					//se documento già presente aggiorno
					if(set2count.containsKey(docid)) 
					{	
					
						set2count.put(docid, set2count.get(docid) + 1);
					} 
					//altrimenti inserisco
					else {
						
						set2count.put(docid, 1);
					}
				}
			}
		}
		//ritorno la lista ordinata per i valori
		return sort(set2count);
	}

	/* ORDINA LA LISTA CON I RISULTATI*/
	private static HashMap<Integer, Integer> sort(HashMap<Integer, Integer> set2count) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(set2count.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {  
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		HashMap<Integer,Integer> sortByCount = new HashMap<>();  
		for (Entry<Integer, Integer> entry : list) {  
			sortByCount.put(entry.getKey(), entry.getValue());  
		}  
		return sortByCount;
	}

	private static void printMap(HashMap<Integer, Integer> map, IndexReader reader) throws IOException {
		System.out.println("[RESULTS]\n");
		for(Integer i: map.keySet()) {
			System.out.print("Documento: " + i);
			System.out.print(" Occorrenze: " + map.get(i) + "\n");
			Document x = reader.document(i);
			System.out.println("Table_Name: "+x.get("titolo"));
			System.out.println("");
			
}
	}
}
