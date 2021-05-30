import java.util.ArrayList;
import java.util.Hashtable;

public class LoadBufferList {
	public static Hashtable<String,LoadBuffer> list = new Hashtable<String, LoadBuffer>() ;
	String tagTitle ;
	
	public LoadBufferList(String title, int n) {
		tagTitle = title;
		for (int i=0; i<n; i++) {
			String tag = title + (i+1);
			list.put(tag, new LoadBuffer());
		}
	}
	
	public String toString(){
		 list.forEach( (tag, buffer) -> System.out.println("Load Buffer List Items: " + "Tag: " + tag + "| Buffer Info: " + buffer.toString())); 
		return "";
	}
	
	public String returnFirstEmptyLB() {
		ArrayList<String> emptyBuffers = new ArrayList<String>();
		list.forEach( (tag, buffer) -> {
			if(buffer.busyBit == false) {
				emptyBuffers.add(tag);
			}
		});
		return emptyBuffers.get(0);
	}
}
