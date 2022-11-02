package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class MergeList {

	private String sep;
	
	public MergeList() {
		this.sep = new Separator().getSeparator();
	}

	public void runMergeListAlgo(String valuesToSearch) {

		Path path = Paths.get("C:\\indexedFiles"); 
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {

				long start = System.currentTimeMillis();
				HashMap<Integer, Integer> results = setQuery(valuesToSearch, reader);
				printMap(results, reader);
				long end = System.currentTimeMillis();
				float time = (float) (end - start)/1000/60;
				System.out.println("[TEMPO IMPIEGATO]: " + time + " minuti");
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**/
	private HashMap<Integer, Integer> setQuery(String sQuery, IndexReader reader) throws Exception {
		String[] lista = sQuery.split(this.sep); 

		//faccio il merge
		HashMap<Integer,Integer> sortbycount = merge(reader, lista);
		return sortbycount;
	}

	/*MERGE DELLE POSTING LIST DEI TERMINI DELLA QUERY*/
	private HashMap<Integer,Integer> merge(IndexReader reader, String[] lista) throws Exception {
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
					if(set2count.containsKey(docid)) {
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
	private HashMap<Integer, Integer> sort(HashMap<Integer, Integer> set2count) {
		LinkedHashMap<Integer, Integer> sortByCount = new LinkedHashMap<>();
		ArrayList<Integer> list = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : set2count.entrySet()) {
			list.add(entry.getValue());
		}

		Collections.sort(list, Collections.reverseOrder()); 
		for (int num : list) {
			for (Entry<Integer, Integer> entry : set2count.entrySet()) {
				if (entry.getValue().equals(num)) {
					sortByCount.put(entry.getKey(), num);
				}
			}
		}
		return sortByCount;
	}

	private void printMap(HashMap<Integer, Integer> map, IndexReader reader) throws IOException {
		System.out.println("[RISULTATI]\n");
		for(Integer i: map.keySet()) {
			if(map.get(i)>2) {
				System.out.print("Documento: " + i);
				System.out.print(" Occorrenze: " + map.get(i) + "\n");
				Document x = reader.document(i);
				System.out.println("Table_Name: " + x.get("titolo"));
				System.out.println("");
			}
		}
	}

}
