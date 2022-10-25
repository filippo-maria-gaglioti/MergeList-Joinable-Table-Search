package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class DataCreator {
	
	static Parser parser;
	static IndexCreator indexer;

	public static void main(String[] args) throws Exception {

		Path path = Paths.get("indexedFiles");
		try (Directory directory = FSDirectory.open(path)) {
			String resourceName = "/resources/sampleDataset.json"; 
			parser = new Parser(DataCreator.class.getResourceAsStream(resourceName));
			indexer = new IndexCreator(directory);
			parser.parseFile(indexer);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
