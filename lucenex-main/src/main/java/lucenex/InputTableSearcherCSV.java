package lucenex;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputTableSearcherCSV 
{
	final static private String TABLES_BASIC_PATH="inputTables";
	final static private String EXTENSION=".csv";
	
	
	public InputTableSearcherCSV()
	{
		
	}
	
	public String getTablePath (String tableName)
	{
		//File dir= new File(TABLES_BASIC_PATH);
		Path path = Paths.get(TABLES_BASIC_PATH);
		File dir= new File(path.toString());
		File[] files=dir.listFiles();
		String out="";
		String currName="";
		for (File f: files)
		{	
			currName=f.getName();
			String currTableName=currName.substring(0, currName.length()-4);
			
			
			if (currName.contains(EXTENSION) && currTableName.equals(tableName))
			{
				
				out=currName;
			}
		}
		String outPath=TABLES_BASIC_PATH.concat("/");
		
		return outPath.concat(out);
	}
}
