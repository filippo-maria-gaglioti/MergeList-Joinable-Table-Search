package lucenex;

import java.util.List;

public class ValuesFormatter 
{
	public ValuesFormatter()
	{
		
	}
	public String formatValueString(List<String> texedFieldList) {
		String s="";
		int conta=0;
		for (String curr: texedFieldList)
		{
				if (conta==texedFieldList.size()-1)
				{
					s=s.concat(curr.toLowerCase());
				}
				else
				{
			
					s=s.concat(curr.toLowerCase()+";");
					
				}
				conta++;
			
		}
		return s;
	}

}
