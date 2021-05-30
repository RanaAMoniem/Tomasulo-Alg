import java.util.ArrayList;
import java.util.Hashtable;

public class StoreBufferList {
	public static Hashtable<String,StoreBuffer> list = new Hashtable<String, StoreBuffer>() ;
	String tagTitle;
	
	public StoreBufferList(String title, int n) {
		tagTitle = title;
		for (int i=0;i<n;i++) {
			String tag = tagTitle + (i+1);
			list.put(tag, new StoreBuffer());
		}
	}
	
	public String toString(){
		 list.forEach( (tag, buffer) -> System.out.println("Store Buffer List Items: " + "Tag: " + tag + "| Buffer Info: " + buffer.toString())); 
		return "";
	}
	
	public String returnFirstEmptySB() {
		ArrayList<String> emptyBuffers = new ArrayList<String>();
		list.forEach( (tag, buffer) -> {
			if(buffer.busyBit == false) {
				emptyBuffers.add(tag);
			}
		});
		return emptyBuffers.get(0);
	}
}
