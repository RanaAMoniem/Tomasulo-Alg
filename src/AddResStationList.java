import java.util.ArrayList;
import java.util.Hashtable;

public class AddResStationList {
	String titleLetter;
	public static Hashtable<String,ResStation> list = new Hashtable<String, ResStation>() ;

	public AddResStationList(String title, int n) {
		titleLetter=title;
		for (int i=0;i<n;i++) {
			String tag = title + (i+1);
			list.put(tag, new ResStation());
		}
	}
	
	public String toString(){
		 list.forEach( (tag, station) -> System.out.println("Addition Reservation Station List Items: " + "Tag: " + tag + "| Buffer Info: " + station.toString())); 
		return "";
	}
	
	public String returnFirstEmptyRS() {
		ArrayList<String> emptyBuffers = new ArrayList<String>();
		list.forEach( (tag, buffer) -> {
			if(buffer.busy == false) {
				emptyBuffers.add(tag);
			}
		});
		return emptyBuffers.get(0);
	}
}
