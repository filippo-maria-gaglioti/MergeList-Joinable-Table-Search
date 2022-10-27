package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class Testing {

	final static private String indexPath = "indexedFiles";

	DataCreator dc;

	@Before 
	public void setUp() throws Exception { 
		this.dc = new DataCreator();
		dc.run();
	}
	
	@After
	public void deleteIndex() throws Exception {
		this.dc.getIndexer().deleteIndex();
	}
	
	/*TEST INDEXING*/
	/* Nessuna Corrispondenza */
	@Test
	public void testNoMatch() throws Exception {
		Path path = Paths.get(indexPath);
		PhraseQuery.Builder builder = new PhraseQuery.Builder();
		builder.add(new Term("", "cavallo"));
		PhraseQuery pq = builder.build();
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {
				IndexSearcher searcher = new IndexSearcher(reader);
				runQuery(searcher, pq);
			} finally {
				directory.close();
			}
		}
	}

	/* Una corrispondenza */
	@Test
	public void testOneMatchOneTerm() throws Exception {
		Path path = Paths.get(indexPath);
		PhraseQuery.Builder builder = new PhraseQuery.Builder();
		builder.add(new Term("", "ingegneria dei dati"));
		PhraseQuery pq = builder.build();
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {
				IndexSearcher searcher = new IndexSearcher(reader);
				runQuery(searcher, pq);
			} finally {
				directory.close();
			}
		}
	}
	
	/*Una parola contenuta in una cella con più termini*/
	@Test
	public void testHalfMatch() throws Exception {
		Path path = Paths.get(indexPath);
		PhraseQuery.Builder builder = new PhraseQuery.Builder();
		builder.add(new Term("", "dei dati"));
		PhraseQuery pq = builder.build();
		try (Directory directory = FSDirectory.open(path)) {
			try (IndexReader reader = DirectoryReader.open(directory)) {
				IndexSearcher searcher = new IndexSearcher(reader);
				runQuery(searcher, pq);
			} finally {
				directory.close();
			}
		}
	}
	
	/*TEST QUERY*/
	/*Colonna che non ha nessun termine presente nell'indice*/
	@Test
	public void testNoMatchForMerge() {
		QueryCreator qc = new QueryCreator("esami", "colori");
		qc.run();
	}
	
	/*Colonna che ha tutti i termine presenti nell'indice e ritorna un'unico documento*/
	@Test
	public void testPerfectMatchForMerge() {
		QueryCreator qc = new QueryCreator("studenti", "Nome");
		qc.run();
	}
	
	/*Colonna che ha tutti la metà dei termini presenti nell'indice e ritorna un'unico documento*/
	@Test
	public void testHalfMatchForMerge() {
		QueryCreator qc = new QueryCreator("studenti", "test");
		qc.run();
	}
	
	/*Colonna che ha tutti i termine presenti nell'indice e ritorna due documenti*/
	@Test
	public void testPerfectMatchForMerge2Doc() {
		QueryCreator qc = new QueryCreator("esami", "test");
		qc.run();
	}
	
	/*Colonna che ha tutti i termine presenti nell'indice in tutti i documenti*/
	@Test
	public void testMergeAllDocs() {
		QueryCreator qc = new QueryCreator("esami", "prova");
		qc.run();
	}


	private void runQuery(IndexSearcher searcher, Query query) throws IOException {
		runQuery(searcher, query, false);
	}

	private void runQuery(IndexSearcher searcher, Query query, boolean explain) throws IOException {
		TopDocs hits = searcher.search(query, 10);
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("doc" + scoreDoc.doc + ":" + doc.get("titolo") + " (" + scoreDoc.score + ")");
			if (explain) {
				Explanation explanation = searcher.explain(query, scoreDoc.doc);
				System.out.println(explanation);
			}
		}
	}

}
