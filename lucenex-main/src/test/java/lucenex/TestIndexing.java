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

public class TestIndexing {

	// final static private String sampleData = "/resources/test.json";
	final static private String indexPath = "indexedFiles";

	DataCreator dc;

	@Before 
	public void setUp() throws Exception { 
		Path path = Paths.get(indexPath);
		try (Directory directory = FSDirectory.open(path)) {
			this.dc = new DataCreator(); dc.run();
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
	}
	
	@After
	public void deleteIndex() throws Exception {
		this.dc.getIndexer().deleteIndex();
	}

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
		builder.add(new Term("", "*lsn ‘tongue’"));
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
		builder.add(new Term("", "‘tongue’"));
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
