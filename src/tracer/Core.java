package tracer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.sound.sampled.*;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Core {

	final static double lowrate = 0.80;
	final static double midrate = 0.83;
	final static double highrate = 0.85;

	static public void main(String[] args) throws Exception, IOException{

		//DEF
		HashMap<Integer,Item> pricetable = new HashMap<Integer,Item>(); 
		System.out.println("Loading market price...");		
		pricetable = new PriceLoader().load(new File("price.txt"));
		System.out.println("=======Start monitoring...");
		
		//monitor loop
		while (true) {
			monitorprice(pricetable);
			Thread.sleep(10000);	
		}
	}
	
	public static void monitorprice(HashMap<Integer, Item> pricetable) throws Exception  {
		
	    File sound = new File("sound.wav");
	    AudioInputStream stream;
	    AudioFormat format;
	    DataLine.Info info;
	    Clip clip;
	    
	    //Set sound 
	    stream = AudioSystem.getAudioInputStream(sound);
	    format = stream.getFormat();
	    info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(stream);
	    
	    //check item loop
		for (int i = 0; i < pricetable.size(); i++){
	    
			String http = pricetable.get(i).url + "/render";
			double normalPrice = pricetable.get(i).price;
			String name = pricetable.get(i).name;
			
			//red price
			PriceJSON mypj = new PriceJSON(http);
			
			double rawPrice = mypj.getPrice() + mypj.getFee();
			int currencyID = mypj.getCurrencyID();
			
			// calculate the dollar value
			double finalPrice = marketPrice(currencyID, rawPrice);
			finalPrice =Double.parseDouble(new DecimalFormat("##.##").format(finalPrice));
			
			// calculate the appropriate offer price based on product value and flexible rate
			double price = offerPrice(normalPrice);
			price =Double.parseDouble(new DecimalFormat("##.##").format(price));
			
			if (finalPrice < price) {
				// handle unknown currency ID in the market
				if (finalPrice == 0.0) finalPrice = currencyID;
				else {
					URI theURI = new URI(pricetable.get(i).url);
					java.awt.Desktop.getDesktop().browse(theURI);
					clip.start();
				    System.out.println("==========Lowball detected: " + name + " - $" + finalPrice);
				}
			}			
	    }
	}

	private static double offerPrice(double normalPrice) {
		
		double offerThisPrice = 0;
		if (normalPrice < 2) offerThisPrice = normalPrice * lowrate;
		else if (normalPrice >= 2 && normalPrice < 7) offerThisPrice = normalPrice * midrate;
		else if (normalPrice >= 7) offerThisPrice = normalPrice * highrate;
		
		return offerThisPrice;
	}

	private static double marketPrice(int currencyID, double rawPrice) {
		// a currency exchange method to translate different currencies into US Dollars
		double finalPrice = 0;
		if (currencyID == 2001) finalPrice = rawPrice / 100; 
		else if (currencyID == 2005) finalPrice = rawPrice / 3217;
		else if (currencyID == 2007) finalPrice = rawPrice / 221;
		else if (currencyID == 2003) finalPrice = rawPrice / 73.6;
		else if (currencyID == 2002) finalPrice = rawPrice / 61.8;
		
		return finalPrice;
	}
}
