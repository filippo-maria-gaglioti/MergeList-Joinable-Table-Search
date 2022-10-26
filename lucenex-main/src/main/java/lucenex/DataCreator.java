package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


/**
 * Classe specializzata nella gestione della dinamica estrazione ed indicizzazione elemetni
 * Campi:
 * 1)Parser->logica di parsing del .JSON, tipo lucenex.Parser
 * 2)indexer->logica di indicizzazione, tipo lucenex.IndexCreator
 */

public class DataCreator {
	final static private String sampleData="/resources/sampleDataset.json";
	final static private String indexPath="/lucenex-main/indexedFiles";
	 private Parser parser;
	 private IndexCreator indexer;
	public DataCreator() 
	{
		this.parser=new Parser(DataCreator.class.getResourceAsStream(sampleData));
		this.indexer=null;
		
		
	}
	
	

	
	
	/**
	 * Metodo effettivo di indicizzazione termini
	 * Inizializza l'indexeter ed invoca il metodo parseFile del campo parser, passandoli l'indexter creato
	 * @throws Exception
	 */
	private void indexData() throws Exception
	{
		Path path = Paths.get(indexPath);
		try (Directory directory = FSDirectory.open(path)) {
			
			this.indexer = new IndexCreator(directory);
			
			this.parser.parseFile(this.indexer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Interfaccia della classe per indicizzare i termini
	 */
	public void run()
	{
		try {
			this.indexData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	
}
