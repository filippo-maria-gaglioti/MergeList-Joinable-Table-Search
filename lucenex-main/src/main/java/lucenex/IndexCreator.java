package lucenex;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

public class IndexCreator {

	Directory directory;
	
	IndexWriter writer;

	public IndexCreator(Directory directory) throws Exception {
		this.directory = directory;
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		perFieldAnalyzers.put("", new StandardAnalyzer());
		perFieldAnalyzers.put("titolo", new StandardAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		this.writer = new IndexWriter(directory, config);
	}
	
	
	public void addData(String id, String vals) throws Exception {
		Document doc = new Document();
		doc.add(new TextField("titolo", id, Field.Store.YES));
		doc.add(new TextField("", vals, Field.Store.NO));
		writer.addDocument(doc);
		writer.commit();
	}
	
	public void closeWriter() throws Exception {
		this.writer.close();
	}
	
	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	public IndexWriter getWriter() {
		return writer;
	}

	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}

}
