package tracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class PriceLoader {
	
	public HashMap<Integer, Item> load(File file) throws Exception {
		
		HashMap<Integer,Item> pricelist = new HashMap<Integer,Item>();

		BufferedReader br = new BufferedReader (new FileReader(file));
		
		String line = null;
		String[] tmp_content = null;
		int index = 0;
				
		while ((line = br.readLine()) != null) {
			
			tmp_content = line.split(",");

			pricelist.put(index, new Item(tmp_content[0],tmp_content[1],Double.parseDouble(tmp_content[2])));
			System.out.println(tmp_content[0] + " : " + Double.parseDouble(tmp_content[2]));
			index++;
		}
		br.close();
		return pricelist;
		
	}
	
}
