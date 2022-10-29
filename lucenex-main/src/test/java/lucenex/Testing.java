package lucenex;

import org.junit.jupiter.api.Test;

public class Testing {
	
	/*TEST QUERY*/
	/*Colonna che non ha nessun termine presente nell'indice*/
	@Test
	public void testNoMatchForMerge() {
		QueryCreator qc = new QueryCreator("tab3", "NoMatch");
		qc.run();
	}
	
	/*Colonna che ha tutti i termine presenti nell'indice e ritorna un'unico documento*/
	@Test
	public void testPerfectMatchForMerge() {
		QueryCreator qc = new QueryCreator("tab3", "PerfectMatch");
		qc.run();
	}
	
	/*Colonna che ha tutti la metà dei termini presenti nell'indice e ritorna un'unico documento*/
	@Test
	public void testHalfMatchForMerge() {
		QueryCreator qc = new QueryCreator("tab3", "2Tables");
		qc.run();
	}
	
	/*Colonna che ha tutti i termine presenti nell'indice e ritorna due documenti*/
	@Test
	public void testPerfectMatchForMerge2Doc() {
		QueryCreator qc = new QueryCreator("tab3", "3TabMat");
		qc.run();
	}

}
