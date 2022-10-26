package lucenex;

import java.io.IOException; 
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pattern.PatternTokenizerFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

/***
 * Classe specializzata nella creazione dell'indice lucene.
 * Indicizza due field diversi per doc.
 * 1. titolo-> id della tabella di appartenenza dei value (StringField)
 * 2. values-> valori di campo della tabella (TextField)
 * Utilizza due analyzer distinti per i due campi
 * 1. titolo-> StandardAnalyzer
 * 2. values->CustomAnalyzer composto da un PatternTokenizer dove crea un token ogni ;
 * Poichè nei campi delle tabelle possono esserci diversi valori (con spazi, virgole etc...) creiamo a partire 
 * da tali valori una stringa composta da tutti i valori suddivisi con ; (un valore non presente nei campi).
 * Cosi facendo limitiamo la dimensione dell'indice avendo 1 entry per valore di campo
 * @author Wissel
 *
 */
public class IndexCreator {

	Directory directory;
	
	IndexWriter writer;

	public IndexCreator(Directory directory) throws Exception {
		this.directory = directory;
		
		Map<String,String> terms2replace=new HashMap<>();
		terms2replace.put("pattern", ";");
		terms2replace.put("group", "-1");
		//Analyzer a= new WhitespaceAnalyzer();
		Analyzer a = CustomAnalyzer.builder().withTokenizer(PatternTokenizerFactory.class,terms2replace).build();
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		perFieldAnalyzers.put("", a);
		perFieldAnalyzers.put("titolo", new StandardAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCodec(new SimpleTextCodec());
		
		
		this.writer = new IndexWriter(directory, config);
		this.writer.deleteAll();
	}
	
	
	public void addData(String id, String vals) throws Exception {
		Document doc = new Document();
		doc.add(new StringField("titolo", id, Field.Store.YES));
		doc.add(new TextField("", vals, Field.Store.NO));
		writer.addDocument(doc);
		//writer.commit();
	}
	public void commitData() throws IOException
	{
		System.out.println("[COMMITED]");
		this.writer.commit();
	}
	
	public void closeWriter() throws Exception 
	{
		System.out.println("[CLOSING CHANNEL TO INDEX]");
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
