package tracer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class PriceJSON {
	
	// a JSON object wrapper which parses the web document and store values in java object using gson lib. The Adapter pattern is used here to adapt gson-built
	// JsonObject to PriceObject interface.
	
	String productUrl;
	JsonObject priceMember;
	
	public PriceJSON (String thisUrl) throws Exception {
		productUrl = thisUrl;
		
		String gsonInput = readUrl(productUrl);
		JsonParser jsonParser = new JsonParser();
		JsonObject priceObject = jsonParser.parse(gsonInput)
				.getAsJsonObject().get("listinginfo")
				.getAsJsonObject();
		
		JsonReader reader = new JsonReader(new StringReader(priceObject.toString()));
		
		reader.beginObject();
		
		String priceName = null;
		if (reader.hasNext()) { priceName = reader.nextName();}
		
		priceMember = priceObject.get(priceName).getAsJsonObject();
		
		reader.close();
	}
	
	public double getPrice() {	
		return priceMember.get("price").getAsDouble();
	}
	
	public double getFee() {
		return priceMember.get("fee").getAsDouble();
	}
	
	public int getCurrencyID() {
		return priceMember.get("currencyid").getAsInt();
	}

	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}

}
