package lucenex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class indexTables {
	
	public static void main(String[] args) throws Exception {

		Path path = Paths.get("indexedFiles");
		try (Directory directory = FSDirectory.open(path)) {
			String resourceName = "/tables/test.json";
			InputStream is = indexTables.class.getResourceAsStream(resourceName);
			if (is == null) {
				throw new NullPointerException("Cannot find resource file " + resourceName);
			}
			indexDocs(directory, is);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void indexDocs(Directory directory, InputStream is) throws Exception {
		JSONTokener tokener = new JSONTokener(is);
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(directory, config);
		writer.deleteAll();
		
		//scorre tutte le righe ---> ogni tabella
		while(!tokener.end()) {
			JSONObject object = new JSONObject(tokener);
			System.out.println("Id tabella: " + object.getString("id"));

			object.getJSONArray("cells");
			JSONArray cells = object.getJSONArray("cells");
			Document doc = new Document();
			StringBuilder textField = new StringBuilder();
			//scorre tutte le celle della tabella
			for(int i = 0; i < cells.length(); i++) {
				JSONObject cella = cells.getJSONObject(i);
				//se non è un header lo inserisco nel documento
				if(!cella.getBoolean("isHeader")) {
					textField = textField.append(cella.getString("cleanedText") + " ");
					System.out.print("Campi: " + cella.getString("cleanedText") + "\n");
				}
			}
			doc.add(new TextField("", textField.toString(), Field.Store.NO));
			writer.addDocument(doc);
			writer.commit();
			tokener.next();
		}
		writer.close();
	}

	
}
