package lucenex;

import java.util.Scanner;

public class MainClass {

	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.println("INDEXING(1) or DO QUERY(2)? ");
		String choice = scanner.nextLine();
		if(choice.equals("1")) {
			DataCreator dc = new DataCreator();		//indicizzazione dei valori nel db
			dc.run();
		} else if(choice.equals("2")) {
			QueryCreator qc = new QueryCreator("NOME FILE", "CAMPO");
			qc.run();
		} else {
			System.out.println("[NO CHOICE]");
		}
	}
}
