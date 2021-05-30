

public class RegisterEntry {
	String state; // C for constant read value, E for Empty put value (can not read)
	double value;
	
	public RegisterEntry(String s, double v) {
		state = s;
		value = v;
	}
	
	public String toString(){
		return ( " | State: " + state + " | Value: " + value);
	}
}
